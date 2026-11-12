"use strict";

// ====================================================================
// Helpers
// ====================================================================
const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => Array.from(root.querySelectorAll(selector));

const state = {
  data: null,              // payload /api/status gần nhất
  tab: "nvhn",             // tab bảng worker
  stateFilter: "ALL",
  search: "",
  selected: new Set(),     // tên worker được chọn (theo tab hiện tại)
  buildActive: false,
  settingsDirty: false,
  busy: new Set(),         // action key đang chạy -> disable nút
  rowCache: new Map(),     // `${tab}:${name}` -> {tr, cells, sig}
};

function esc(value) {
  return String(value ?? "").replace(/[&<>"']/g, (c) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
  })[c]);
}

async function api(path, options = {}) {
  const headers = new Headers(options.headers || {});
  const init = { ...options, headers };
  if (options.json !== undefined) {
    headers.set("Content-Type", "application/json");
    init.body = JSON.stringify(options.json);
    delete init.json;
  }
  const response = await fetch(path, init);
  let body = {};
  try { body = await response.json(); } catch (_) { body = {}; }
  if (!response.ok) {
    const detail = Array.isArray(body.detail)
      ? body.detail.map((d) => d.msg || JSON.stringify(d)).join("; ")
      : body.detail;
    throw new Error(detail || `HTTP ${response.status}`);
  }
  return body;
}

function toast(message, type = "ok") {
  const box = document.createElement("div");
  box.className = `toast ${type}`;
  box.innerHTML = `<span>${esc(message)}</span><button class="x" aria-label="Đóng">✕</button>`;
  box.querySelector(".x").addEventListener("click", () => box.remove());
  $("#toasts").append(box);
  // Lỗi giữ lại cho tới khi người dùng đóng; thông báo thường tự ẩn.
  if (type !== "error") setTimeout(() => box.remove(), 5000);
}

function formatAge(seconds) {
  if (seconds == null) return "—";
  if (seconds < 60) return `${seconds}s trước`;
  if (seconds < 3600) return `${Math.floor(seconds / 60)}m trước`;
  return `${Math.floor(seconds / 3600)}h${String(Math.floor((seconds % 3600) / 60)).padStart(2, "0")} trước`;
}

function formatTimestamp(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString("vi-VN");
}

function compact(n) {
  if (n == null) return "—";
  if (n >= 1e6) return `${(n / 1e6).toFixed(2)}M`;
  if (n >= 1e4) return `${Math.round(n / 1e3)}k`;
  return String(n);
}

function duration(seconds) {
  if (seconds == null || !Number.isFinite(seconds) || seconds < 0) return "—";
  const m = Math.floor(seconds / 60);
  const s = Math.round(seconds % 60);
  return m >= 60 ? `${Math.floor(m / 60)}h${String(m % 60).padStart(2, "0")}m` : `${m}m${String(s).padStart(2, "0")}s`;
}

// Dialog xác nhận. Trả về {ok, option}.
function confirmAction({ title, text, ok = "Xác nhận", danger = true, option = null }) {
  const dialog = $("#confirm-dialog");
  $("#confirm-title").textContent = title;
  $("#confirm-text").textContent = text;
  const okButton = $("#confirm-ok");
  okButton.textContent = ok;
  okButton.className = `btn ${danger ? "danger" : "primary"}`;
  const optionLabel = $("#confirm-option");
  const optionInput = optionLabel.querySelector("input");
  if (option) {
    optionLabel.classList.remove("hidden");
    optionLabel.querySelector("span").textContent = option.label;
    optionInput.checked = Boolean(option.checked);
  } else {
    optionLabel.classList.add("hidden");
  }
  return new Promise((resolve) => {
    dialog.addEventListener("close", () => {
      resolve({ ok: dialog.returnValue === "ok", option: optionInput.checked });
    }, { once: true });
    dialog.returnValue = "cancel";
    dialog.showModal();
  });
}

// Chạy một action có khóa nút + toast kết quả.
async function runAction(key, button, fn, successMessage) {
  if (state.busy.has(key)) return;
  state.busy.add(key);
  const label = button ? button.innerHTML : null;
  if (button) {
    button.disabled = true;
    button.classList.add("loading");
  }
  try {
    const result = await fn();
    if (successMessage) toast(typeof successMessage === "function" ? successMessage(result) : successMessage);
    await refreshStatus();
    return result;
  } catch (error) {
    toast(error.message, "error");
    return undefined;
  } finally {
    state.busy.delete(key);
    if (button) {
      button.classList.remove("loading");
      button.innerHTML = label;
    }
    applyControlState();
  }
}

// ====================================================================
// Status polling + overview
// ====================================================================
let refreshing = false;

async function refreshStatus() {
  if (refreshing) return;
  refreshing = true;
  try {
    state.data = await api("/api/status");
    renderOverview();
    renderSettings();
    renderWorkers();
    applyControlState();
    if ($("#log-drawer").classList.contains("open")) renderLogInfo();
    $("#last-refresh").textContent = `cập nhật ${new Date().toLocaleTimeString("vi-VN")}`;
    const job = state.data.active_job;
    if (job && !buildStream) watchBuild(job, false);
  } catch (error) {
    $("#last-refresh").textContent = `lỗi kết nối: ${error.message}`;
  } finally {
    refreshing = false;
  }
}

function statBlock(items) {
  return items.map(([label, value, cls = ""]) =>
    `<div class="stat ${cls}"><span>${esc(label)}</span><strong>${esc(value)}</strong></div>`).join("");
}

function setPill(el, text, cls) {
  el.textContent = text;
  el.className = `pill ${cls || ""}`;
}

function renderOverview() {
  const d = state.data;
  const sup = d.supervisor || {};
  const totals = d.totals || {};
  const tt = d.ta_thu_runtime || { totals: {}, workers: [] };
  const ttSup = d.ta_thu || {};
  state.buildActive = Boolean(d.active_job);

  $("#server-endpoint").textContent = d.server || sup.server || "—";
  const phase = d.current_phase === "ta_thu" ? ["Pha: Tà Thú", "warn"] : ["Pha: NVHN", "ok"];
  setPill($("#phase-pill"), state.buildActive ? `Đang build (${d.active_job.status})` : phase[0],
    state.buildActive ? "info" : phase[1]);

  // NVHN
  setPill($("#nvhn-state"), sup.running ? "ĐANG CHẠY" : "ĐÃ DỪNG", sup.running ? "ok" : "off");
  $("#nvhn-stats").innerHTML = statBlock([
    ["Chạy", totals.running ?? 0, "ok"],
    ["Dừng", totals.stopped ?? 0],
    ["Tạm dừng", totals.paused ?? 0],
    ["Xong", totals.done ?? 0, "info"],
    ["Tổng", totals.total ?? 0],
  ]);
  $("#nvhn-detail").textContent = [
    sup.running ? `Supervisor PID ${sup.pid}` : (sup.stale_pid ? "Supervisor: PID cũ" : "Supervisor dừng"),
    `tự khởi động ${sup.desired ? "bật" : "tắt"}`,
    `giãn cách ${sup.worker_start_delay_seconds ?? 30}s`,
    sup.proxy_enabled ? `proxy: chia đều ${sup.proxy_count} proxy` : "proxy tắt",
  ].join(" · ");

  // Tà Thú
  const ttTotals = tt.totals || {};
  const ttWorkers = tt.workers || [];
  const sum = (key) => ttWorkers.reduce((acc, w) => acc + (w[key] || 0), 0);
  setPill($("#tathu-state"), ttSup.running ? "ĐANG CHẠY" : "ĐÃ DỪNG", ttSup.running ? "ok" : "off");
  $("#tathu-stats").innerHTML = statBlock([
    ["Chạy", ttTotals.running ?? 0, "ok"],
    ["Xong", ttTotals.done ?? 0, "info"],
    ["Kết liễu", sum("kills"), "ok"],
    ["Bị cướp", sum("lost"), "warn"],
    ["Chết", sum("deaths"), "bad"],
  ]);
  $("#tathu-detail").textContent = [
    ttSup.running ? `Supervisor PID ${ttSup.pid}` : "Supervisor dừng",
    `tự khôi phục ${ttSup.desired ? "bật" : "tắt"}`,
    `${ttTotals.total ?? ttWorkers.length} worker`,
  ].join(" · ");

  // Hệ thống
  const schedule = d.schedule || {};
  setPill($("#schedule-pill"), schedule.enabled ? "Hẹn giờ bật" : "Hẹn giờ tắt", schedule.enabled ? "ok" : "off");
  $("#system-stats").innerHTML = statBlock([
    ["Account", d.account?.count ?? 0],
    ["Chạy kế tiếp", schedule.enabled && schedule.next_run_at ? new Date(schedule.next_run_at).toLocaleString("vi-VN", { hour: "2-digit", minute: "2-digit", day: "2-digit", month: "2-digit" }) : "—"],
  ]);

  $("#tab-count-nvhn").textContent = totals.total ?? (d.workers || []).length;
  $("#tab-count-ta_thu").textContent = ttTotals.total ?? ttWorkers.length;
}

function applyControlState() {
  const d = state.data;
  if (!d) return;
  const sup = d.supervisor || {};
  const ttSup = d.ta_thu || {};
  const build = state.buildActive;
  const set = (id, disabled, why = "") => {
    const el = $(id);
    if (!el || el.classList.contains("loading")) return;
    el.disabled = disabled;
    if (why) el.dataset.why = why; else delete el.dataset.why;
  };
  set("#btn-build-run", build, build ? "Đang build" : "");
  set("#btn-build", build, build ? "Đang build" : "");
  set("#btn-nvhn-start", build || sup.running, sup.running ? "NVHN đang chạy" : "");
  set("#btn-nvhn-stop", build || !sup.running, !sup.running ? "NVHN không chạy" : "");
  set("#btn-tathu-start", build || ttSup.running || sup.running,
    sup.running ? "Stop NVHN trước khi chạy Tà Thú" : (ttSup.running ? "Tà Thú đang chạy" : ""));
  set("#btn-tathu-stop", build || !ttSup.running, !ttSup.running ? "Tà Thú không chạy" : "");
  const anyWorker = (d.totals?.running || 0) + (d.ta_thu_runtime?.totals?.running || 0) > 0;
  set("#btn-stop-all", build || !(sup.running || ttSup.running || sup.desired || ttSup.desired || anyWorker));
  $("#account-file").disabled = build;
  $("#account-form button").disabled = build;
  for (const button of $$("#bulk-bar [data-bulk]")) {
    if (button.dataset.bulk !== "clear") button.disabled = build;
  }
}

// ====================================================================
// Settings (một form chung cho Supervisor + Hẹn giờ)
// ====================================================================
function renderSettings() {
  const d = state.data;
  const sup = d.supervisor || {};
  const sch = d.schedule || {};
  $("#settings-summary").textContent =
    `· ${sch.worker_count ?? "?"} worker · giãn cách ${sup.worker_start_delay_seconds ?? 30}s · ` +
    `proxy ${sup.proxy_enabled ? "bật" : "tắt"} · ` +
    `lịch ${sch.enabled ? `${sch.start_time}, mỗi ${sch.repeat_hours}h build lại` : "tắt"}`;
  renderProxyInfo();
  $("#schedule-info").textContent = sch.enabled
    ? `Lần chạy kế tiếp: ${formatTimestamp(sch.next_run_at)} · lần trước: ${formatTimestamp(sch.last_run_at)}`
    : `Lần chạy trước: ${formatTimestamp(sch.last_run_at)}`;
  if (state.settingsDirty) return;
  $("#cfg-worker-count").value = sch.worker_count ?? 10;
  $("#cfg-delay").value = sup.worker_start_delay_seconds ?? 30;
  $("#cfg-use-proxy").checked = Boolean(sup.proxy_enabled);
  renderProxyInfo();
  $("#cfg-schedule-enabled").checked = Boolean(sch.enabled);
  $("#cfg-auto-tathu").checked = sch.auto_ta_thu !== false;
  $("#cfg-start-time").value = sch.start_time || "01:00";
  $("#cfg-repeat").value = sch.repeat_hours || 6;
}

// Proxy: chia đều kiểu uplevel; backend coi proxy_workers_per_proxy > 0 là "bật".
function renderProxyInfo() {
  const sup = state.data?.supervisor || {};
  const count = sup.proxy_count ?? 0;
  const workers = Number.parseInt($("#cfg-worker-count").value, 10) || 0;
  const info = $("#proxy-info");
  if (!$("#cfg-use-proxy").checked) {
    info.textContent = `Tắt: worker chạy bằng mạng của máy. proxy-tk.txt hiện có ${count} proxy.`;
  } else if (!count) {
    info.textContent = "proxy-tk.txt chưa có proxy nào: Start sẽ báo lỗi.";
  } else {
    const per = Math.ceil(workers / count);
    info.textContent = `${workers} worker chia đều cho ${Math.min(count, workers)} proxy ` +
      `(${count} proxy trong file) → tối đa ${per} worker/proxy, theo khối liên tiếp worker-01…`;
  }
}

function readSettings() {
  const int = (id) => Number.parseInt($(id).value, 10);
  const cfg = {
    workerCount: int("#cfg-worker-count"),
    delay: int("#cfg-delay"),
    repeat: int("#cfg-repeat"),
    // Không còn restart định kỳ riêng: chạy lại từ đầu do lịch đảm nhiệm.
    restart: 0,
    proxy: $("#cfg-use-proxy").checked ? 1 : 0,
  };
  const checks = [
    ["workerCount", 1, 500, "Số worker"], ["delay", 0, 3600, "Giãn cách"], ["repeat", 1, 72, "Lặp lại"],
  ];
  for (const [key, min, max, label] of checks) {
    if (!Number.isInteger(cfg[key]) || cfg[key] < min || cfg[key] > max) {
      throw new Error(`${label} phải từ ${min} đến ${max}`);
    }
  }
  return cfg;
}

function markSettingsDirty(dirty) {
  state.settingsDirty = dirty;
  $("#settings-dirty").classList.toggle("hidden", !dirty);
}

async function saveSettings(event) {
  event.preventDefault();
  let cfg;
  try { cfg = readSettings(); } catch (error) { toast(error.message, "error"); return; }
  await runAction("settings", $("#btn-save-settings"), async () => {
    const sup = await api("/api/supervisor/settings", {
      method: "POST",
      json: { periodic_restart_hours: cfg.restart, worker_start_delay_seconds: cfg.delay, proxy_workers_per_proxy: cfg.proxy },
    });
    await api("/api/schedule", {
      method: "POST",
      json: {
        enabled: $("#cfg-schedule-enabled").checked,
        auto_ta_thu: $("#cfg-auto-tathu").checked,
        start_time: $("#cfg-start-time").value,
        repeat_hours: cfg.repeat,
        worker_count: cfg.workerCount,
        worker_start_delay_seconds: cfg.delay,
        proxy_workers_per_proxy: cfg.proxy,
      },
    });
    markSettingsDirty(false);
    return sup;
  }, (sup) => sup.requires_restart
    ? "Đã lưu. NVHN đang chạy: Stop rồi Start để áp dụng cấu hình worker."
    : "Đã lưu cấu hình.");
}

// ====================================================================
// Workers table (cập nhật theo dòng, không vẽ lại cả bảng mỗi 5s)
// ====================================================================
const COLUMNS = {
  nvhn: ["", "Worker", "Trạng thái", "Nhân vật", "Tiến độ", "Tài nguyên", "Sự kiện cuối", ""],
  ta_thu: ["", "Worker", "Trạng thái", "Nhân vật", "Boss", "HP / MP", "Kết quả", "Tài nguyên", "Sự kiện cuối", ""],
};
const STATE_LABEL = {
  RUNNING: "Đang chạy", STOPPED: "Dừng", PAUSED: "Tạm dừng", DONE: "Xong", STALE_PID: "PID cũ", ERROR: "Lỗi",
};
let renderedHeadTab = null;

function currentWorkers() {
  const d = state.data;
  if (!d) return [];
  return state.tab === "ta_thu" ? (d.ta_thu_runtime?.workers || []) : (d.workers || []);
}

function isProblem(worker) {
  const stale = (worker.last_log_age_seconds ?? 0) > 300 && worker.state === "RUNNING";
  return ["ERROR", "STALE_PID"].includes(worker.state) || stale || worker.last_event?.level === "error";
}

function workerVisible(worker) {
  const f = state.stateFilter;
  if (f === "PROBLEM" ? !isProblem(worker) : f !== "ALL" && worker.state !== f) return false;
  if (!state.search) return true;
  const hay = `${worker.name} ${worker.char_name || ""}`.toLowerCase();
  return hay.includes(state.search);
}

function renderHead() {
  if (renderedHeadTab === state.tab) return;
  renderedHeadTab = state.tab;
  const cols = COLUMNS[state.tab];
  $("#workers-head").innerHTML = `<tr>${cols.map((c, i) => i === 0
    ? `<th class="sel"><input type="checkbox" id="select-all" aria-label="Chọn tất cả"></th>`
    : `<th>${esc(c)}</th>`).join("")}</tr>`;
  $("#select-all").addEventListener("change", (event) => {
    for (const worker of currentWorkers().filter(workerVisible)) {
      if (event.target.checked) state.selected.add(worker.name); else state.selected.delete(worker.name);
    }
    renderWorkers();
  });
}

function eventCell(worker) {
  const ev = worker.last_event;
  const age = worker.last_log_age_seconds;
  const stale = age != null && age > 300 && worker.state === "RUNNING";
  const level = ev?.level || "info";
  const msg = ev ? ev.msg.replace(/^AUTO /, "") : (worker.last_auto_log || "—");
  return `<div class="event lv-${level}">
      <span class="age ${stale ? "stale" : ""}" title="stdout.log cập nhật ${esc(formatTimestamp(worker.last_log_at))}">${esc(formatAge(age))}${ev?.ts ? ` · ${esc(ev.ts)}` : ""}</span>
      <span class="msg" title="${esc(msg)}">${esc(msg)}</span>
    </div>`;
}

function resourceCell(worker) {
  if (worker.pid == null) return `<span class="muted">—</span>`;
  return `<div class="res"><span>${worker.cpu_percent ?? "—"}% CPU</span><span>${worker.rss_mb ?? "—"} MB</span><span class="muted">${esc(worker.elapsed || "")}</span></div>`;
}

function progressCell(worker) {
  const st = worker.nvhn_status;
  if (worker.state === "DONE") {
    return `<div class="prog"><div class="bar"><i style="width:100%"></i></div><span class="ok">✔ Hoàn tất toàn bộ account</span></div>`;
  }
  if (!st) return `<span class="muted">—</span>`;
  const pct = st.nvhn_max ? Math.round((st.nvhn / st.nvhn_max) * 100) : 0;
  return `<div class="prog">
      <div class="bar"><i style="width:${pct}%"></i></div>
      <span>NV ${st.nvhn ?? "?"}/${st.nvhn_max ?? "?"} · mục tiêu ${st.progress ?? "?"}/${st.progress_max ?? "?"}</span>
      <span class="muted" title="${esc(st.state || "")}">${esc(st.map || "")}</span>
    </div>`;
}

function bossCell(worker) {
  const f = worker.fight;
  if (!f) return `<span class="muted">—</span>`;
  const lock = f.locked ? "🔒" : "🔎";
  const where = `map ${f.map ?? "?"}${f.map_target ? `→${f.map_target}` : ""} · khu ${f.zone ?? "?"}${f.zone_target != null ? `→${f.zone_target}` : ""} ${lock}`;
  if (!f.boss) return `<div class="prog"><span class="muted">Đang tìm boss</span><span class="muted">${esc(where)}</span></div>`;
  const pct = f.boss.pct ?? 0;
  return `<div class="prog">
      <div class="bar boss"><i style="width:${pct}%"></i></div>
      <span>${compact(f.boss.hp)} / ${compact(f.boss.hp_max)} (${pct}%)</span>
      <span class="muted">${esc(where)}</span>
    </div>`;
}

function hpCell(worker) {
  const f = worker.fight;
  if (!f) return `<span class="muted">—</span>`;
  const hp = f.hp_max ? Math.round((f.hp / f.hp_max) * 100) : 0;
  const mp = f.mp_max ? Math.round((f.mp / f.mp_max) * 100) : 0;
  return `<div class="prog">
      <div class="bar hp"><i style="width:${hp}%"></i></div>
      <div class="bar mp"><i style="width:${mp}%"></i></div>
      <span class="muted">${f.hp ?? "?"}/${f.hp_max ?? "?"} · ${f.mp ?? "?"}/${f.mp_max ?? "?"}</span>
    </div>`;
}

function resultCell(worker) {
  const f = worker.fight;
  const parts = [
    `<span class="ok" title="Mình kết liễu">✔ ${worker.kills ?? 0}</span>`,
    `<span class="warn" title="Người khác kết liễu">✖ ${worker.lost ?? 0}</span>`,
    `<span class="bad" title="Số lần chết">☠ ${worker.deaths ?? 0}</span>`,
  ];
  const task = f && f.task_max ? `<span class="muted">task ${f.task}/${f.task_max}${f.state && f.state !== "OK" ? ` · ${esc(f.state)}` : ""}</span>` : "";
  return `<div class="result">${parts.join("")}</div>${task}`;
}

function actionsFor(worker) {
  const actions = [];
  if (worker.state === "PAUSED" || worker.state === "STOPPED" || worker.state === "STALE_PID") actions.push(["start", "▶", "Start"]);
  if (worker.state === "RUNNING") actions.push(["stop", "■", "Stop (tạm dừng)"], ["restart", "⟳", "Restart"]);
  return actions;
}

function actionsCell(worker) {
  const buttons = actionsFor(worker).map(([action, icon, title]) =>
    `<button class="btn icon ${action === "stop" ? "danger" : ""}" data-action="${action}" title="${title}" ${state.buildActive ? "disabled" : ""}>${icon}</button>`);
  buttons.push(`<button class="btn icon ghost" data-action="log" title="Xem log">📜</button>`);
  return `<div class="row-actions">${buttons.join("")}</div>`;
}

function accountHtml(worker) {
  const acc = worker.account;
  const total = acc?.total ?? worker.accounts;
  if (!acc) return `<span class="muted small">${total ?? "?"} tài khoản</span>`;
  const done = worker.state === "DONE";
  const index = done ? acc.total : acc.index;
  return `<span class="small acct" title="Đang chạy tài khoản thứ ${acc.index}/${acc.total}">` +
    `TK <b>${index}/${acc.total}</b>${acc.username ? ` · ${esc(acc.username)}` : ""}</span>`;
}

function rowCells(worker) {
  const stateCls = `st-${(worker.state || "").toLowerCase()}`;
  const stateHtml = `<span class="state ${stateCls}">${esc(STATE_LABEL[worker.state] || worker.state)}</span>` +
    (worker.run_pass_total > 1 ? `<span class="muted small"> lượt ${worker.run_pass}/${worker.run_pass_total}</span>` : "");
  const nameHtml = `<strong>${esc(worker.name)}</strong>${accountHtml(worker)}` +
    (worker.proxy ? `<span class="muted small">proxy ${esc(worker.proxy)}</span>` : "");
  const sel = `<input type="checkbox" data-select ${state.selected.has(worker.name) ? "checked" : ""} aria-label="Chọn ${esc(worker.name)}">`;
  const charHtml = esc(worker.char_name || "—");
  if (state.tab === "ta_thu") {
    return [sel, nameHtml, stateHtml, charHtml, bossCell(worker), hpCell(worker), resultCell(worker), resourceCell(worker), eventCell(worker), actionsCell(worker)];
  }
  return [sel, nameHtml, stateHtml, charHtml, progressCell(worker), resourceCell(worker), eventCell(worker), actionsCell(worker)];
}

function renderWorkers() {
  renderHead();
  const body = $("#workers-body");
  const workers = currentWorkers();
  const visible = workers.filter(workerVisible);
  const names = new Set(workers.map((w) => w.name));
  for (const name of [...state.selected]) if (!names.has(name)) state.selected.delete(name);

  if (!visible.length) {
    state.rowCache.clear();
    const msg = !workers.length
      ? (state.tab === "ta_thu" ? "Chưa có worker Tà Thú." : "Chưa có worker. Upload account rồi Build.")
      : "Không có worker khớp bộ lọc.";
    body.innerHTML = `<tr><td class="empty" colspan="${COLUMNS[state.tab].length}">${esc(msg)}</td></tr>`;
    updateBulkBar();
    return;
  }
  // Xoá dòng "trống" nếu có.
  if (body.querySelector("td.empty")) body.innerHTML = "";

  const wanted = new Set();
  let previous = null;
  for (const worker of visible) {
    const key = `${state.tab}:${worker.name}`;
    wanted.add(key);
    let entry = state.rowCache.get(key);
    if (!entry) {
      const tr = document.createElement("tr");
      tr.dataset.worker = worker.name;
      const cells = COLUMNS[state.tab].map(() => tr.insertCell());
      entry = { tr, cells, html: [] };
      state.rowCache.set(key, entry);
    }
    const html = rowCells(worker);
    html.forEach((content, index) => {
      if (entry.html[index] !== content) {
        entry.cells[index].innerHTML = content;
        entry.html[index] = content;
      }
    });
    entry.cells[0].className = "sel";
    entry.tr.classList.toggle("selected", state.selected.has(worker.name));
    entry.tr.classList.toggle("problem", isProblem(worker));
    const expectedNext = previous ? previous.nextSibling : body.firstChild;
    if (expectedNext !== entry.tr) body.insertBefore(entry.tr, expectedNext);
    previous = entry.tr;
  }
  for (const [key, entry] of state.rowCache) {
    if (!wanted.has(key)) { entry.tr.remove(); state.rowCache.delete(key); }
  }
  const selectAll = $("#select-all");
  if (selectAll) selectAll.checked = visible.length > 0 && visible.every((w) => state.selected.has(w.name));
  updateBulkBar();
}

function updateBulkBar() {
  const count = state.selected.size;
  $("#bulk-bar").classList.toggle("hidden", count === 0);
  $("#bulk-count").textContent = `Đã chọn ${count} worker`;
}

function workerActionPath(name, action) {
  const base = state.tab === "ta_thu" ? "/api/ta-thu/workers" : "/api/workers";
  return `${base}/${encodeURIComponent(name)}/${action}`;
}

const ACTION_LABEL = { start: "Đã chạy", stop: "Đã tạm dừng", restart: "Đã restart" };

async function workerAction(name, action, button) {
  if (action === "stop") {
    const { ok } = await confirmAction({
      title: `Tạm dừng ${name}?`,
      text: "Worker sẽ được đánh dấu tạm dừng; supervisor không tự bật lại cho tới khi bạn bấm Start.",
      ok: "Tạm dừng",
    });
    if (!ok) return;
  }
  await runAction(`worker:${state.tab}:${name}`, button,
    () => api(workerActionPath(name, action), { method: "POST" }),
    `${ACTION_LABEL[action]} ${name}`);
}

async function bulkAction(action) {
  const names = [...state.selected];
  if (!names.length) return;
  const { ok } = await confirmAction({
    title: `${ACTION_LABEL[action].replace("Đã ", "")} ${names.length} worker?`,
    text: names.join(", "),
    ok: "Thực hiện",
    danger: action !== "start",
  });
  if (!ok) return;
  let done = 0;
  const failed = [];
  for (const name of names) {
    try {
      await api(workerActionPath(name, action), { method: "POST" });
      done += 1;
    } catch (error) {
      failed.push(`${name}: ${error.message}`);
    }
  }
  if (done) toast(`${ACTION_LABEL[action]} ${done}/${names.length} worker`);
  if (failed.length) toast(failed.join("\n"), "error");
  state.selected.clear();
  await refreshStatus();
}

// ====================================================================
// Main controls
// ====================================================================
async function buildWorkers(startAfter, button) {
  let cfg;
  try { cfg = readSettings(); } catch (error) { toast(error.message, "error"); return; }
  const { ok } = await confirmAction({
    title: startAfter ? "Build & Run NVHN?" : "Chỉ Build NVHN?",
    text: `Dừng NVHN đang chạy, build lại runtime và chia ${cfg.workerCount} worker từ account.csv. ` +
      "Tiến độ/trạng thái tạm dừng của worker cũ sẽ bị xoá." + (startAfter ? " Xong sẽ tự chạy supervisor." : ""),
    ok: startAfter ? "Build & Run" : "Build",
  });
  if (!ok) return;
  await runAction("build", button, async () => {
    const job = await api("/api/build", {
      method: "POST",
      json: { worker_count: cfg.workerCount, start_after_build: startAfter, proxy_workers_per_proxy: cfg.proxy },
    });
    watchBuild(job, true);
    return job;
  }, "Đã bắt đầu build");
}

async function startNvhn(button) {
  let cfg;
  try { cfg = readSettings(); } catch (error) { toast(error.message, "error"); return; }
  const ttRunning = state.data?.ta_thu?.running;
  if (ttRunning) {
    const { ok } = await confirmAction({
      title: "Start NVHN?", text: "Tà Thú đang chạy và sẽ bị dừng trước khi chạy NVHN.", ok: "Start NVHN",
    });
    if (!ok) return;
  }
  await runAction("nvhn-start", button, () => api("/api/supervisor/start", {
    method: "POST",
    json: { periodic_restart_hours: cfg.restart, worker_start_delay_seconds: cfg.delay, proxy_workers_per_proxy: cfg.proxy },
  }), "Đã chạy NVHN");
}

async function stopNvhn(button) {
  const { ok } = await confirmAction({
    title: "Stop NVHN?", text: "Dừng supervisor và toàn bộ worker NVHN. Tà Thú không bị ảnh hưởng.", ok: "Stop NVHN",
  });
  if (!ok) return;
  await runAction("nvhn-stop", button, () => api("/api/nvhn/supervisor/stop", { method: "POST" }), "Đã dừng NVHN");
}

async function startTaThu(button) {
  const existing = state.data?.ta_thu_runtime?.workers?.length || 0;
  const { ok, option } = await confirmAction({
    title: "Start Tà Thú?",
    text: existing
      ? `Chạy supervisor Tà Thú với ${existing} worker hiện có (không chờ NVHN).`
      : "Chưa có worker Tà Thú: sẽ build và chia worker từ account.csv rồi chạy.",
    ok: "Start Tà Thú",
    danger: false,
    option: existing ? { label: "Chia lại worker từ account.csv (theo Số worker trong Cấu hình)", checked: false } : null,
  });
  if (!ok) return;
  let workerCount;
  try { workerCount = readSettings().workerCount; } catch (_) { workerCount = undefined; }
  const json = option || !existing ? { rebuild: true, worker_count: workerCount } : { rebuild: false };
  await runAction("tathu-start", button, () => api("/api/ta-thu/supervisor/start", { method: "POST", json }), "Đã chạy Tà Thú");
}

async function stopTaThu(button) {
  const { ok } = await confirmAction({
    title: "Stop Tà Thú?", text: "Dừng supervisor và toàn bộ worker Tà Thú. Hệ thống sẽ không tự bật lại.", ok: "Stop Tà Thú",
  });
  if (!ok) return;
  await runAction("tathu-stop", button, () => api("/api/ta-thu/supervisor/stop", { method: "POST" }), "Đã dừng Tà Thú");
}

async function stopAll(button) {
  const { ok } = await confirmAction({
    title: "Stop tất cả?",
    text: "Dừng NVHN và Tà Thú, tắt tự khởi động (kể cả sau khi reboot). Lịch hẹn giờ vẫn giữ nguyên.",
    ok: "Stop tất cả",
  });
  if (!ok) return;
  await runAction("stop-all", button, () => api("/api/supervisor/stop", { method: "POST" }), "Đã dừng tất cả");
}

// ====================================================================
// Log drawer
// ====================================================================
const MAX_LOG_ROWS = 3000;
const log = {
  stream: null,
  runtime: "nvhn",
  worker: null,
  kind: "stdout",
  levels: new Set(["info", "success", "warn", "error"]),
  query: "",
  rows: [],         // {entry, count, el}
  pending: 0,       // dòng mới khi đang cuộn lên
  fightHistory: [], // {t, hp, bossId, zone}
  lastFight: null,
};

function openDrawer(pane = "log") {
  const drawer = $("#log-drawer");
  drawer.classList.add("open");
  drawer.setAttribute("aria-hidden", "false");
  document.body.classList.add("drawer-open");
  showPane(pane);
}

function closeDrawer() {
  const drawer = $("#log-drawer");
  drawer.classList.remove("open");
  drawer.setAttribute("aria-hidden", "true");
  document.body.classList.remove("drawer-open");
  if (log.stream) { log.stream.close(); log.stream = null; }
}

function showPane(pane) {
  for (const tab of $$(".drawer-tabs .tab")) tab.classList.toggle("active", tab.dataset.pane === pane);
  $("#pane-log").classList.toggle("hidden", pane !== "log");
  $("#pane-build").classList.toggle("hidden", pane !== "build");
  if (pane === "build") $("#build-badge").classList.add("hidden");
}

function setSeg(id, value) {
  for (const button of $$(`${id} button`)) button.classList.toggle("active", button.dataset.value === value);
}

function fillWorkerSelect() {
  const select = $("#log-worker");
  const list = log.runtime === "ta_thu" ? (state.data?.ta_thu_runtime?.workers || []) : (state.data?.workers || []);
  const names = list.map((w) => w.name);
  if (log.worker && !names.includes(log.worker)) names.unshift(log.worker);
  select.innerHTML = names.map((name) => {
    const w = list.find((x) => x.name === name);
    return `<option value="${esc(name)}">${esc(name)}${w?.char_name ? ` · ${esc(w.char_name)}` : ""}</option>`;
  }).join("") || `<option value="">(chưa có worker)</option>`;
  select.value = log.worker || "";
  select.disabled = log.kind === "supervisor";
}

function renderLogInfo() {
  const box = $("#log-info");
  const list = log.runtime === "ta_thu" ? state.data?.ta_thu_runtime?.workers : state.data?.workers;
  const worker = (list || []).find((w) => w.name === log.worker);
  if (!worker || log.kind === "supervisor") { box.classList.add("hidden"); return; }
  const acc = worker.account;
  const total = acc?.total ?? worker.accounts;
  const index = acc ? (worker.state === "DONE" ? acc.total : acc.index) : null;
  const pct = index && total ? Math.round((index / total) * 100) : 0;
  box.classList.remove("hidden");
  box.innerHTML =
    `<span><b>${esc(worker.name)}</b> · <span class="state st-${(worker.state || "").toLowerCase()}">${esc(STATE_LABEL[worker.state] || worker.state)}</span></span>` +
    `<span class="acct">Tài khoản <b>${index ?? "?"}/${total ?? "?"}</b><span class="bar"><i style="width:${pct}%"></i></span></span>` +
    (acc?.username ? `<span>TK <b>${esc(acc.username)}</b></span>` : "") +
    (worker.char_name ? `<span>Nhân vật <b>${esc(worker.char_name)}</b></span>` : "") +
    (worker.nvhn_status && log.runtime === "nvhn" ? `<span>NV <b>${worker.nvhn_status.nvhn ?? "?"}/${worker.nvhn_status.nvhn_max ?? "?"}</b></span>` : "");
}

function openLog(worker, runtime, kind = "stdout") {
  log.worker = worker;
  log.runtime = runtime;
  log.kind = kind;
  openDrawer("log");
  connectLog();
}

function connectLog() {
  if (log.stream) log.stream.close();
  setSeg("#log-runtime", log.runtime);
  setSeg("#log-kind", log.kind);
  fillWorkerSelect();
  clearLog();
  renderLogInfo();
  const params = new URLSearchParams({
    worker: log.worker || "worker-01", runtime: log.runtime, kind: log.kind, tail: "600",
  });
  $("#log-download").href = `/api/logs/download?${params}`;
  $("#log-conn").textContent = "đang kết nối…";
  log.stream = new EventSource(`/api/logs/stream?${params}`);
  log.stream.addEventListener("open", () => { $("#log-conn").textContent = "● live"; });
  log.stream.addEventListener("lines", (event) => {
    const payload = JSON.parse(event.data);
    appendEntries(payload.lines || []);
    if (payload.initial && !(payload.lines || []).length) {
      $("#log-lines").innerHTML = `<div class="log-empty">Chưa có dòng log nào${payload.path ? ` trong ${esc(payload.path)}` : ""}.</div>`;
    }
  });
  log.stream.addEventListener("reset", () => appendEntries([{ ts: null, level: "warn", kind: "session", tag: "", msg: "— file log được tạo lại —" }]));
  log.stream.onerror = () => { $("#log-conn").textContent = "mất kết nối, đang thử lại…"; };
}

function clearLog() {
  log.rows = [];
  log.pending = 0;
  log.fightHistory = [];
  log.lastFight = null;
  $("#log-lines").innerHTML = "";
  $("#log-jump").classList.add("hidden");
  $("#fight-card").classList.add("hidden");
}

function rowVisible(entry) {
  if (entry.kind === "session") return true;
  if (entry.kind === "trace") return log.levels.has("error");
  if (!log.levels.has(entry.level)) return false;
  if (log.query && !entry.msg.toLowerCase().includes(log.query)) return false;
  return true;
}

function highlight(text) {
  const safe = esc(text);
  if (!log.query) return safe;
  const q = esc(log.query).replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  return safe.replace(new RegExp(q, "gi"), (m) => `<mark>${m}</mark>`);
}

function buildRow(row) {
  const e = row.entry;
  const el = document.createElement("div");
  if (e.kind === "session") {
    el.className = "ln session";
    el.innerHTML = `<span>${esc(e.msg.replace(/=+/g, "").trim())}</span>`;
  } else {
    el.className = `ln lv-${e.level}${e.kind === "trace" ? " trace" : ""}`;
    const tag = e.tag ? e.tag.replace(/^(NVHN|TA THU)\s*/, "") || e.tag : "";
    el.innerHTML = `<span class="t${e.ts_estimated ? " est" : ""}">${esc(e.ts || "")}</span>` +
      `<span class="tag">${esc(tag)}</span>` +
      `<span class="m">${highlight(e.msg.replace(/^AUTO (NVHN|TA THU|LOGIN)[A-Z0-9 \[\]]*?:\s?/, ""))}</span>` +
      `<span class="rep${row.count > 1 ? "" : " hidden"}">×${row.count}</span>`;
    el.title = e.msg;
  }
  row.el = el;
  return el;
}

function appendEntries(entries) {
  const box = $("#log-lines");
  if (box.querySelector(".log-empty")) box.innerHTML = "";
  const atBottom = box.scrollHeight - box.scrollTop - box.clientHeight < 40;
  const follow = $("#log-follow").checked;
  const fragment = document.createDocumentFragment();
  let added = 0;
  for (const entry of entries) {
    if (entry.kind === "fight") updateFight(entry);
    if (entry.kind === "session") log.fightHistory = [];
    const last = log.rows[log.rows.length - 1];
    // Gộp dòng trùng liên tiếp (cùng nội dung & level) thành ×N.
    if (last && last.entry.kind !== "session" && last.entry.msg === entry.msg && last.entry.level === entry.level) {
      last.count += 1;
      last.entry.ts = entry.ts || last.entry.ts;
      if (last.el) {
        const rep = last.el.querySelector(".rep");
        rep.textContent = `×${last.count}`;
        rep.classList.remove("hidden");
        const t = last.el.querySelector(".t");
        if (t) t.textContent = last.entry.ts || "";
      }
      continue;
    }
    const row = { entry, count: 1, el: null };
    log.rows.push(row);
    if (rowVisible(entry)) {
      fragment.append(buildRow(row));
      added += 1;
    }
  }
  box.append(fragment);
  if (log.rows.length > MAX_LOG_ROWS) {
    const removed = log.rows.splice(0, log.rows.length - MAX_LOG_ROWS + 500);
    for (const row of removed) row.el?.remove();
  }
  if (follow && atBottom) {
    box.scrollTop = box.scrollHeight;
  } else if (added) {
    log.pending += added;
    const jump = $("#log-jump");
    jump.querySelector("span").textContent = log.pending;
    jump.classList.remove("hidden");
  }
}

function rerenderLog() {
  const box = $("#log-lines");
  box.innerHTML = "";
  const fragment = document.createDocumentFragment();
  for (const row of log.rows) {
    row.el = null;
    if (rowVisible(row.entry)) fragment.append(buildRow(row));
  }
  box.append(fragment);
  box.scrollTop = box.scrollHeight;
  log.pending = 0;
  $("#log-jump").classList.add("hidden");
}

function tsSeconds(ts) {
  if (!ts) {
    const now = new Date();
    return now.getHours() * 3600 + now.getMinutes() * 60 + now.getSeconds();
  }
  const [h, m, s] = ts.split(":").map(Number);
  return h * 3600 + m * 60 + s;
}

function updateFight(entry) {
  const f = entry.data || {};
  log.lastFight = { ...f, ts: entry.ts };
  if (f.boss && f.boss.hp != null) {
    const sample = { t: tsSeconds(entry.ts), hp: f.boss.hp, bossId: f.boss.id, zone: f.zone };
    const prev = log.fightHistory[log.fightHistory.length - 1];
    // Dòng cũ không có giờ thì không dùng để tính tốc độ.
    if (!entry.ts) { renderFight(); return; }
    if (prev && (prev.bossId !== sample.bossId || prev.zone !== sample.zone || sample.hp > prev.hp)) log.fightHistory = [];
    log.fightHistory.push(sample);
    if (log.fightHistory.length > 12) log.fightHistory.shift();
  }
  renderFight();
}

function renderFight() {
  const card = $("#fight-card");
  const f = log.lastFight;
  if (!f || log.kind !== "stdout") { card.classList.add("hidden"); return; }
  card.classList.remove("hidden");
  let rate = null;
  let eta = null;
  const hist = log.fightHistory;
  if (hist.length >= 2) {
    const first = hist[0];
    const last = hist[hist.length - 1];
    let dt = last.t - first.t;
    if (dt < 0) dt += 86400;
    if (dt > 0) {
      rate = (first.hp - last.hp) / dt;
      if (rate > 0) eta = last.hp / rate;
    }
  }
  const boss = f.boss;
  const pct = boss?.pct ?? 0;
  const hpPct = f.hp_max ? Math.round((f.hp / f.hp_max) * 100) : 0;
  const mpPct = f.mp_max ? Math.round((f.mp / f.mp_max) * 100) : 0;
  const stateBad = f.state && f.state !== "OK";
  card.innerHTML = `
    <div class="fight-head">
      <strong>${esc(f.char || "?")}</strong>
      <span class="muted">map ${f.map ?? "?"}${f.map_target ? `→${f.map_target}` : ""} · khu ${f.zone ?? "?"}${f.zone_target != null ? `→${f.zone_target}` : ""} ${f.locked ? "🔒 đã khoá" : "🔎 đang tìm"}</span>
      <span class="state ${stateBad ? "st-error" : "st-running"}">${esc(f.state || "—")}</span>
      <span class="muted small">${esc(f.ts || "")}</span>
    </div>
    ${boss ? `
    <div class="fight-boss">
      <div class="bar boss big"><i style="width:${pct}%"></i><b>${boss.hp?.toLocaleString("vi-VN") ?? "?"} / ${boss.hp_max?.toLocaleString("vi-VN") ?? "?"} (${pct}%)</b></div>
      <span class="muted small">${rate ? `−${Math.round(rate).toLocaleString("vi-VN")} máu/s · còn ~${duration(eta)}` : "đang đo tốc độ…"} · cách boss dx=${boss.dx ?? "?"}, dy=${boss.dy ?? "?"}</span>
    </div>` : `<p class="muted small">Chưa thấy boss trong khu.</p>`}
    <div class="fight-grid">
      <div><span class="muted small">HP</span><div class="bar hp"><i style="width:${hpPct}%"></i></div><span class="small">${f.hp ?? "?"}/${f.hp_max ?? "?"}</span></div>
      <div><span class="muted small">MP</span><div class="bar mp"><i style="width:${mpPct}%"></i></div><span class="small">${f.mp ?? "?"}/${f.mp_max ?? "?"}</span></div>
      <div><span class="muted small">Đòn gửi / server nhận</span><span class="small ${f.ack_ok ? "" : "bad"}">${f.atk ?? 0} / ${f.ack ?? 0}${f.ack_ok ? "" : " (lệch mục tiêu)"}</span></div>
      <div><span class="muted small">Task · skill</span><span class="small">${f.task ?? "?"}/${f.task_max ?? "?"} · ${esc(f.skill || "—")}</span></div>
    </div>`;
}

function copyVisibleLog() {
  const text = log.rows.filter((r) => rowVisible(r.entry))
    .map((r) => `${r.entry.ts ? `${r.entry.ts} ` : ""}${r.entry.msg}${r.count > 1 ? ` (×${r.count})` : ""}`).join("\n");
  navigator.clipboard.writeText(text).then(() => toast("Đã copy log đang hiển thị"), () => toast("Trình duyệt chặn copy", "error"));
}

// ====================================================================
// Build stream
// ====================================================================
let buildStream = null;

function watchBuild(job, focus) {
  if (buildStream) buildStream.close();
  state.buildActive = true;
  applyControlState();
  $("#build-title").textContent = `Build ${job.id} · ${job.worker_count ?? ""} worker`;
  $("#build-output").textContent = "";
  if (focus) openDrawer("build"); else $("#build-badge").classList.remove("hidden");
  buildStream = new EventSource(`/api/jobs/${job.id}/events`);
  buildStream.addEventListener("output", (event) => {
    const out = $("#build-output");
    const nearBottom = out.scrollHeight - out.scrollTop - out.clientHeight < 40;
    out.textContent += `${JSON.parse(event.data)}\n`;
    if (nearBottom) out.scrollTop = out.scrollHeight;
  });
  buildStream.addEventListener("status", (event) => {
    const status = JSON.parse(event.data);
    $("#build-title").textContent = `Build ${status.id} · ${status.status}`;
    if (["succeeded", "failed"].includes(status.status)) {
      buildStream.close();
      buildStream = null;
      state.buildActive = false;
      if (status.status === "succeeded") toast(status.start_after_build ? "Build xong, đã chạy NVHN" : "Build xong. Bấm Start để chạy.");
      else toast(`Build lỗi: ${status.error || "xem log build"}`, "error");
      refreshStatus();
    }
  });
}

// ====================================================================
// Wiring
// ====================================================================
function wire() {
  $("#refresh-button").addEventListener("click", refreshStatus);
  $("#btn-build-run").addEventListener("click", (e) => buildWorkers(true, e.currentTarget));
  $("#btn-build").addEventListener("click", (e) => buildWorkers(false, e.currentTarget));
  $("#btn-nvhn-start").addEventListener("click", (e) => startNvhn(e.currentTarget));
  $("#btn-nvhn-stop").addEventListener("click", (e) => stopNvhn(e.currentTarget));
  $("#btn-tathu-start").addEventListener("click", (e) => startTaThu(e.currentTarget));
  $("#btn-tathu-stop").addEventListener("click", (e) => stopTaThu(e.currentTarget));
  $("#btn-stop-all").addEventListener("click", (e) => stopAll(e.currentTarget));

  $("#account-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const file = $("#account-file").files[0];
    if (!file) return;
    await runAction("upload", event.submitter, () => api("/api/accounts/upload", {
      method: "POST", headers: { "Content-Type": "text/csv" }, body: file,
    }), (result) => `Đã lưu ${result.count} account. Bấm Build để chia lại worker.`);
    event.target.reset();
  });

  const form = $("#settings-form");
  form.addEventListener("input", () => { markSettingsDirty(true); renderProxyInfo(); });
  form.addEventListener("change", () => markSettingsDirty(true));
  form.addEventListener("submit", saveSettings);

  for (const tab of $$(".workers .tab")) {
    tab.addEventListener("click", () => {
      state.tab = tab.dataset.runtime;
      state.selected.clear();
      for (const t of $$(".workers .tab")) t.classList.toggle("active", t === tab);
      renderWorkers();
    });
  }
  for (const chip of $$("#state-filters .chip")) {
    chip.addEventListener("click", () => {
      state.stateFilter = chip.dataset.state;
      for (const c of $$("#state-filters .chip")) c.classList.toggle("active", c === chip);
      renderWorkers();
    });
  }
  $("#worker-search").addEventListener("input", (e) => { state.search = e.target.value.trim().toLowerCase(); renderWorkers(); });

  // Delegation cho bảng worker.
  $("#workers-body").addEventListener("click", (event) => {
    const tr = event.target.closest("tr[data-worker]");
    if (!tr) return;
    const name = tr.dataset.worker;
    const select = event.target.closest("[data-select]");
    if (select) {
      if (select.checked) state.selected.add(name); else state.selected.delete(name);
      tr.classList.toggle("selected", select.checked);
      updateBulkBar();
      return;
    }
    const button = event.target.closest("button[data-action]");
    if (button) {
      if (button.dataset.action === "log") openLog(name, state.tab);
      else workerAction(name, button.dataset.action, button);
      return;
    }
    if (event.target.closest(".event")) openLog(name, state.tab);
  });
  for (const button of $$("#bulk-bar [data-bulk]")) {
    button.addEventListener("click", () => {
      if (button.dataset.bulk === "clear") { state.selected.clear(); renderWorkers(); return; }
      bulkAction(button.dataset.bulk);
    });
  }

  // Drawer
  $("#drawer-close").addEventListener("click", closeDrawer);
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && $("#log-drawer").classList.contains("open") && !$("#confirm-dialog").open) closeDrawer();
  });
  for (const tab of $$(".drawer-tabs .tab")) tab.addEventListener("click", () => showPane(tab.dataset.pane));
  $("#log-worker").addEventListener("change", (e) => { log.worker = e.target.value; connectLog(); });
  for (const button of $$("#log-runtime button")) {
    button.addEventListener("click", () => {
      log.runtime = button.dataset.value;
      const list = log.runtime === "ta_thu" ? state.data?.ta_thu_runtime?.workers : state.data?.workers;
      if (list?.length && !list.some((w) => w.name === log.worker)) log.worker = list[0].name;
      connectLog();
    });
  }
  for (const button of $$("#log-kind button")) {
    button.addEventListener("click", () => { log.kind = button.dataset.value; connectLog(); });
  }
  for (const chip of $$("#log-levels .chip")) {
    chip.addEventListener("click", () => {
      const level = chip.dataset.level;
      if (log.levels.has(level)) log.levels.delete(level); else log.levels.add(level);
      chip.classList.toggle("active", log.levels.has(level));
      rerenderLog();
    });
  }
  let searchTimer = null;
  $("#log-search").addEventListener("input", (e) => {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(() => { log.query = e.target.value.trim().toLowerCase(); rerenderLog(); }, 200);
  });
  $("#log-wrap").addEventListener("change", (e) => $("#log-lines").classList.toggle("wrap", e.target.checked));
  $("#log-copy").addEventListener("click", copyVisibleLog);
  $("#log-clear").addEventListener("click", clearLog);
  const box = $("#log-lines");
  box.addEventListener("scroll", () => {
    if (box.scrollHeight - box.scrollTop - box.clientHeight < 40) {
      log.pending = 0;
      $("#log-jump").classList.add("hidden");
    }
  });
  $("#log-jump").addEventListener("click", () => {
    box.scrollTop = box.scrollHeight;
    log.pending = 0;
    $("#log-jump").classList.add("hidden");
  });
}

wire();
refreshStatus();
setInterval(() => { if (!document.hidden) refreshStatus(); }, 5000);
