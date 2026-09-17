const $ = (selector) => document.querySelector(selector);
let refreshTimer = null;
let stream = null;
let buildActive = false;
let currentTab = "nvhn"; // "nvhn" hoặc "ta_thu"
let supervisorSettingsDirty = false;
let supervisorSettingsSaving = false;

async function api(path, options = {}) {
  const headers = new Headers(options.headers || {});
  if (options.json !== undefined) {
    headers.set("Content-Type", "application/json");
    options.body = JSON.stringify(options.json);
  }
  const response = await fetch(path, { ...options, headers });
  let body = {};
  try { body = await response.json(); } catch (_) { body = {}; }
  if (!response.ok) {
    const error = new Error(body.detail || `HTTP ${response.status}`);
    error.status = response.status;
    throw error;
  }
  return body;
}

function notify(message, isError = false) {
  const box = $("#notice");
  box.textContent = message;
  box.classList.remove("hidden", "error-notice");
  if (isError) box.classList.add("error-notice");
  window.setTimeout(() => box.classList.add("hidden"), 6000);
}

function showDashboard() {
  refreshStatus();
  if (refreshTimer) clearInterval(refreshTimer);
  refreshTimer = setInterval(refreshStatus, 5000);
}

function stateBadge(state) {
  const span = document.createElement("span");
  span.className = `state state-${state.toLowerCase()}`;
  span.textContent = state;
  return span;
}

function cell(value, className = "") {
  const td = document.createElement("td");
  if (className) td.className = className;
  td.textContent = value ?? "—";
  return td;
}

function formatTimestamp(value) {
  if (!value) return "Chưa có thời gian";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString("vi-VN");
}

function logCell(worker) {
  const td = document.createElement("td");
  td.className = "log-cell";
  const timestamp = document.createElement("span");
  timestamp.className = "log-time";
  timestamp.textContent = formatTimestamp(worker.last_log_at);
  const content = document.createElement("span");
  content.textContent = worker.last_auto_log || "—";
  td.append(timestamp, content);
  return td;
}

function renderWorkers(workers, runtime = "nvhn") {
  const body = $("#workers-body");
  body.replaceChildren();
  if (!workers.length) {
    const row = document.createElement("tr");
    const emptyMsg = runtime === "ta_thu" ? "Chưa có worker Tà Thú." : "Chưa có worker. Upload account rồi build.";
    const empty = cell(emptyMsg, "empty");
    empty.colSpan = 11;
    row.append(empty);
    body.append(row);
    return;
  }
  for (const worker of workers) {
    const row = document.createElement("tr");
    row.append(cell(worker.name));
    const state = document.createElement("td"); state.append(stateBadge(worker.state)); row.append(state);
    row.append(cell(worker.char_name || "—"));
    row.append(cell(`${worker.run_pass || 1}/${worker.run_pass_total || 2}`));
    row.append(cell(worker.pid));
    row.append(cell(worker.cpu_percent == null ? "—" : `${worker.cpu_percent}%`));
    row.append(cell(worker.rss_mb == null ? "—" : `${worker.rss_mb} MB`));
    row.append(cell(worker.elapsed));
    row.append(cell(worker.accounts));
    row.append(logCell(worker));
    const actions = document.createElement("td");
    const group = document.createElement("div"); group.className = "action-group";
    const workerActions = [];
    if (runtime === "nvhn") {
      if (worker.state === "PAUSED") workerActions.push(["Start", "start"]);
      else if (worker.state !== "DONE") workerActions.push(["Stop", "stop"], ["Restart", "restart"]);
    }
    workerActions.push(["Log", "stdout"], ["Errors", "error"]);
    for (const [label, action] of workerActions) {
      const button = document.createElement("button");
      button.className = action === "stop" ? "danger" : action === "start" ? "primary" : action === "restart" ? "secondary" : "ghost";
      button.textContent = label;
      if (["start", "stop", "restart"].includes(action)) button.disabled = buildActive;
      button.addEventListener("click", () => ["start", "stop", "restart"].includes(action)
        ? workerAction(worker.name, action, button)
        : openLog(worker.name, action, runtime));
      group.append(button);
    }
    actions.append(group); row.append(actions); body.append(row);
  }
}

async function refreshStatus() {
  try {
    const data = await api("/api/status");
    const supervisor = data.supervisor;
    $("#server-select").value = data.server || supervisor.server || "tk";
    if (!supervisorSettingsDirty && !supervisorSettingsSaving) {
      $("#periodic-restart-hours").value = supervisor.periodic_restart_hours ?? 3;
      $("#worker-start-delay-seconds").value = supervisor.worker_start_delay_seconds ?? 30;
    }
    const taThuSupervisor = data.ta_thu || { running: false };
    $("#supervisor-state").textContent = supervisor.running ? "RUNNING" : (taThuSupervisor.running ? "TÀ THÚ" : "STOPPED");
    $("#supervisor-state").style.color = supervisor.running ? "var(--accent)" : (taThuSupervisor.running ? "var(--warning)" : "var(--danger)");
    let supDetail = supervisor.running
      ? `NVHN PID ${supervisor.pid} · tự khởi động: ${supervisor.desired ? "bật" : "tắt"}`
      : `NVHN: dừng${supervisor.stale_pid ? " (PID cũ)" : ""} · tự khởi động: ${supervisor.desired ? "bật" : "tắt"}`;
    supDetail += ` · restart định kỳ: ${supervisor.periodic_restart_hours ? `${supervisor.periodic_restart_hours}h` : "tắt"}`;
    supDetail += ` · giãn cách worker: ${supervisor.worker_start_delay_seconds ?? 30}s`;
    if (taThuSupervisor.running) {
      supDetail += ` | Tà Thú: PID ${taThuSupervisor.pid}`;
    } else {
      supDetail += " | Tà Thú: dừng";
    }
    $("#supervisor-detail").textContent = supDetail;
    $("#running-count").textContent = data.totals.running;
    $("#stopped-count").textContent = data.totals.stopped;
    $("#paused-count").textContent = data.totals.paused ?? 0;
    $("#done-count").textContent = data.totals.done;
    $("#account-count").textContent = data.account.count;
    const active = $("#active-job");
    buildActive = Boolean(data.active_job);
    if (data.active_job) { active.textContent = `Build ${data.active_job.status}`; active.classList.remove("hidden"); }
    else active.classList.add("hidden");
    $("#start-supervisor").disabled = buildActive;
    $("#stop-supervisor").disabled = buildActive;
    if ($("#stop-ta-thu-supervisor")) $("#stop-ta-thu-supervisor").disabled = buildActive;
    $("#build-button").disabled = buildActive;
    $("#run-button").disabled = buildActive;
    $("#account-file").disabled = buildActive;
    $("#account-form button").disabled = buildActive;
    $("#periodic-restart-hours").disabled = buildActive || supervisorSettingsSaving;
    $("#worker-start-delay-seconds").disabled = buildActive || supervisorSettingsSaving;
    $("#save-supervisor-settings").disabled = buildActive || supervisorSettingsSaving;
    if (data.active_job && !stream) watchBuild(data.active_job);
    if (currentTab === "ta_thu") {
      try {
        const taThuData = await api("/api/ta-thu/status");
        renderWorkers(taThuData.workers || [], "ta_thu");
      } catch (_) {
        renderWorkers([], "ta_thu");
      }
    } else {
      renderWorkers(data.workers || [], "nvhn");
    }
    if (data.schedule) renderSchedule(data.schedule);
    $("#last-refresh").textContent = `Cập nhật ${new Date().toLocaleTimeString("vi-VN")}`;
  } catch (error) {
    notify(error.message, true);
  }
}

function renderSchedule(schedule) {
  const badge = $("#schedule-status-badge");
  badge.textContent = schedule.enabled ? "Đang bật" : "Tắt";
  badge.className = `badge ${schedule.enabled ? "badge-success" : "badge-muted"}`;

  $("#schedule-enabled").checked = schedule.enabled;
  $("#schedule-auto-ta-thu").checked = schedule.auto_ta_thu !== false;
  $("#schedule-mode").value = schedule.mode;
  $("#schedule-daily-time").value = schedule.daily_time || "01:00";
  $("#schedule-interval-hours").value = schedule.interval_hours || 6;
  $("#schedule-worker-count").value = schedule.worker_count || 10;
  $("#schedule-server").value = schedule.server || $("#server-select").value || "tk";

  const phaseLabel = schedule.current_phase === "ta_thu" ? "Đang chạy Tà Thú 👹" : "Nhiệm vụ hàng ngày ⚔️";
  $("#schedule-current-phase").textContent = phaseLabel;
  $("#schedule-current-phase").style.color = schedule.current_phase === "ta_thu" ? "var(--warning)" : "var(--accent)";

  if (schedule.mode === "daily") {
    $("#group-daily-time").classList.remove("hidden");
    $("#group-interval-hours").classList.add("hidden");
  } else {
    $("#group-daily-time").classList.add("hidden");
    $("#group-interval-hours").classList.remove("hidden");
  }

  $("#schedule-next-run").textContent = schedule.enabled && schedule.next_run_at
    ? formatTimestamp(schedule.next_run_at)
    : "Chưa lên lịch";
  $("#schedule-last-run").textContent = schedule.last_run_at
    ? formatTimestamp(schedule.last_run_at)
    : "Chưa chạy lần nào";
}

async function supervisorAction(action, button) {
  button.disabled = true;
  try {
    const options = { method: "POST" };
    if (action === "start") {
      const settings = readSupervisorSettings();
      if (settings === null) return;
      options.json = {
        server: $("#server-select").value,
        periodic_restart_hours: settings.periodicHours,
        worker_start_delay_seconds: settings.startDelaySeconds,
      };
    }
    await api(`/api/supervisor/${action}`, options);
    notify(action === "start" ? "Đã chạy supervisor" : "Đã dừng supervisor và worker");
    if (action === "start") supervisorSettingsDirty = false;
    await refreshStatus();
  } catch (error) { notify(error.message, true); }
  finally { button.disabled = false; }
}

function readSupervisorSettings() {
  const periodicHours = Number.parseInt($("#periodic-restart-hours").value, 10);
  const startDelaySeconds = Number.parseInt($("#worker-start-delay-seconds").value, 10);
  if (!Number.isInteger(periodicHours) || periodicHours < 0 || periodicHours > 168) {
    notify("Số giờ restart phải từ 0 đến 168", true);
    return null;
  }
  if (!Number.isInteger(startDelaySeconds) || startDelaySeconds < 0 || startDelaySeconds > 3600) {
    notify("Giãn cách khởi động phải từ 0 đến 3600 giây", true);
    return null;
  }
  return { periodicHours, startDelaySeconds };
}

async function saveSupervisorSettings() {
  if (supervisorSettingsSaving) return;
  const settings = readSupervisorSettings();
  if (settings === null) return;

  supervisorSettingsSaving = true;
  $("#save-supervisor-settings").disabled = true;
  try {
    const result = await api("/api/supervisor/settings", {
      method: "POST",
      json: {
        periodic_restart_hours: settings.periodicHours,
        worker_start_delay_seconds: settings.startDelaySeconds,
      },
    });
    supervisorSettingsDirty = false;
    if (result.requires_restart) {
      notify("Đã lưu. Supervisor đang chạy; hãy Stop rồi Start Supervisor để áp dụng.");
    } else {
      notify("Đã lưu cấu hình Supervisor.");
    }
    await refreshStatus();
  } catch (error) {
    notify(error.message, true);
  } finally {
    supervisorSettingsSaving = false;
    $("#save-supervisor-settings").disabled = false;
  }
}

async function workerAction(name, action, button) {
  button.disabled = true;
  try {
    await api(`/api/workers/${encodeURIComponent(name)}/${action}`, { method: "POST" });
    const labels = { start: "Đã chạy", stop: "Đã tạm dừng", restart: "Đã restart" };
    notify(`${labels[action]} ${name}`); await refreshStatus();
  } catch (error) { notify(error.message, true); }
  finally { button.disabled = false; }
}

function showConsole(title) {
  $("#console-title").textContent = title;
  $("#console-output").textContent = "";
  $("#console-panel").classList.remove("hidden");
}

function appendConsole(text) {
  const output = $("#console-output");
  output.textContent += text.endsWith("\n") ? text : `${text}\n`;
  output.scrollTop = output.scrollHeight;
}

function openLog(worker, kind, runtime = "nvhn") {
  if (stream) stream.close();
  const label = runtime === "ta_thu" ? "Tà Thú" : "NVHN";
  showConsole(`[${label}] ${worker} · ${kind === "error" ? "java-errors.log" : "stdout.log"}`);
  stream = new EventSource(`/api/workers/${encodeURIComponent(worker)}/logs/stream?kind=${kind}&runtime=${runtime}`);
  stream.addEventListener("log", (event) => {
    const payload = JSON.parse(event.data);
    const logLabel = payload.initial ? "log gần nhất" : "log mới";
    appendConsole(`[${formatTimestamp(payload.timestamp)} · ${logLabel}]\n${payload.text}`);
  });
  stream.onerror = () => appendConsole("[Mất kết nối log, trình duyệt đang thử lại…]");
}

function watchBuild(job) {
  buildActive = true;
  if (stream) stream.close();
  showConsole(`Build ${job.id}`);
  stream = new EventSource(`/api/jobs/${job.id}/events`);
  stream.addEventListener("output", (event) => appendConsole(JSON.parse(event.data)));
  stream.addEventListener("status", (event) => {
    const status = JSON.parse(event.data);
    $("#active-job").textContent = `Build ${status.status}`;
    $("#active-job").classList.remove("hidden");
    if (["succeeded", "failed"].includes(status.status)) {
      stream.close(); stream = null;
      buildActive = false;
      $("#build-button").disabled = false;
      $("#run-button").disabled = false;
      notify(status.status === "succeeded" ? "Build thành công! Nhấn Run để chạy." : `Build lỗi: ${status.error}`, status.status === "failed");
      refreshStatus();
    }
  });
}

$("#refresh-button").addEventListener("click", refreshStatus);
$("#start-supervisor").addEventListener("click", (event) => supervisorAction("start", event.currentTarget));
$("#stop-supervisor").addEventListener("click", (event) => supervisorAction("stop", event.currentTarget));
$("#close-console").addEventListener("click", () => {
  if (stream) stream.close(); stream = null; $("#console-panel").classList.add("hidden");
});

$("#account-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const file = $("#account-file").files[0];
  if (!file) return;
  try {
    const result = await api("/api/accounts/upload", {
      method: "POST", headers: { "Content-Type": "text/csv" }, body: file,
    });
    notify(`Đã lưu ${result.count} account`); event.target.reset(); await refreshStatus();
  } catch (error) { notify(error.message, true); }
});

$("#build-button").addEventListener("click", async () => {
  const buildBtn = $("#build-button");
  const runBtn = $("#run-button");
  buildBtn.disabled = true;
  runBtn.disabled = true;
  try {
    const job = await api("/api/build", { method: "POST", json: {
      worker_count: Number($("#worker-count").value),
      start_after_build: false,
      server: $("#server-select").value,
    }});
    watchBuild(job);
  } catch (error) {
    buildBtn.disabled = false;
    runBtn.disabled = false;
    notify(error.message, true);
  }
});

$("#run-button").addEventListener("click", async (event) => {
  const button = event.currentTarget;
  await supervisorAction("start", button);
});

$("#save-supervisor-settings").addEventListener("click", saveSupervisorSettings);
$("#periodic-restart-hours").addEventListener("input", () => {
  supervisorSettingsDirty = true;
});
$("#worker-start-delay-seconds").addEventListener("input", () => {
  supervisorSettingsDirty = true;
});

if ($("#stop-ta-thu-supervisor")) {
  $("#stop-ta-thu-supervisor").addEventListener("click", async (event) => {
    const button = event.currentTarget;
    button.disabled = true;
    try {
      await api("/api/ta-thu/supervisor/stop", { method: "POST" });
      notify("Đã dừng Tà Thú supervisor và toàn bộ worker Tà Thú!");
      await refreshStatus();
    } catch (error) {
      notify(error.message, true);
    } finally {
      button.disabled = false;
    }
  });
}

$("#schedule-mode").addEventListener("change", (event) => {
  const mode = event.target.value;
  if (mode === "daily") {
    $("#group-daily-time").classList.remove("hidden");
    $("#group-interval-hours").classList.add("hidden");
  } else {
    $("#group-daily-time").classList.add("hidden");
    $("#group-interval-hours").classList.remove("hidden");
  }
});

$("#schedule-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const saveBtn = $("#schedule-save-button");
  saveBtn.disabled = true;
  try {
    const payload = {
      enabled: $("#schedule-enabled").checked,
      auto_ta_thu: $("#schedule-auto-ta-thu").checked,
      mode: $("#schedule-mode").value,
      daily_time: $("#schedule-daily-time").value,
      interval_hours: Number($("#schedule-interval-hours").value),
      worker_count: Number($("#schedule-worker-count").value),
      server: $("#schedule-server").value,
    };
    const updated = await api("/api/schedule", { method: "POST", json: payload });
    renderSchedule(updated);
    notify("Đã lưu cấu hình hẹn giờ & Tà Thú!");
  } catch (error) {
    notify(error.message, true);
  } finally {
    saveBtn.disabled = false;
  }
});

$("#tab-nvhn").addEventListener("click", () => {
  currentTab = "nvhn";
  $("#tab-nvhn").classList.add("active");
  $("#tab-ta-thu").classList.remove("active");
  refreshStatus();
});

$("#tab-ta-thu").addEventListener("click", () => {
  currentTab = "ta_thu";
  $("#tab-ta-thu").classList.add("active");
  $("#tab-nvhn").classList.remove("active");
  refreshStatus();
});

showDashboard();
