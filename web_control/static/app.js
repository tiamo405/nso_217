const $ = (selector) => document.querySelector(selector);
let refreshTimer = null;
let stream = null;
let buildActive = false;

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

function renderWorkers(workers) {
  const body = $("#workers-body");
  body.replaceChildren();
  if (!workers.length) {
    const row = document.createElement("tr");
    const empty = cell("Chưa có worker. Upload account rồi build.", "empty");
    empty.colSpan = 10;
    row.append(empty);
    body.append(row);
    return;
  }
  for (const worker of workers) {
    const row = document.createElement("tr");
    row.append(cell(worker.name));
    const state = document.createElement("td"); state.append(stateBadge(worker.state)); row.append(state);
    row.append(cell(`${worker.run_pass || 1}/${worker.run_pass_total || 2}`));
    row.append(cell(worker.pid));
    row.append(cell(worker.cpu_percent == null ? "—" : `${worker.cpu_percent}%`));
    row.append(cell(worker.rss_mb == null ? "—" : `${worker.rss_mb} MB`));
    row.append(cell(worker.elapsed));
    row.append(cell(worker.accounts));
    row.append(logCell(worker));
    const actions = document.createElement("td");
    const group = document.createElement("div"); group.className = "action-group";
    for (const [label, action] of [["Restart", "restart"], ["Log", "stdout"], ["Errors", "error"]]) {
      const button = document.createElement("button");
      button.className = label === "Restart" ? "secondary" : "ghost";
      button.textContent = label;
      if (action === "restart") button.disabled = buildActive;
      button.addEventListener("click", () => action === "restart" ? restartWorker(worker.name, button) : openLog(worker.name, action));
      group.append(button);
    }
    actions.append(group); row.append(actions); body.append(row);
  }
}

async function refreshStatus() {
  try {
    const data = await api("/api/status");
    const supervisor = data.supervisor;
    $("#supervisor-state").textContent = supervisor.running ? "RUNNING" : "STOPPED";
    $("#supervisor-state").style.color = supervisor.running ? "var(--accent)" : "var(--warning)";
    $("#supervisor-detail").textContent = supervisor.running
      ? `PID ${supervisor.pid} · tự khởi động: ${supervisor.desired ? "bật" : "tắt"}`
      : `Đang dừng${supervisor.stale_pid ? " · có PID cũ" : ""} · tự khởi động: ${supervisor.desired ? "bật" : "tắt"}`;
    $("#running-count").textContent = data.totals.running;
    $("#stopped-count").textContent = data.totals.stopped;
    $("#done-count").textContent = data.totals.done;
    $("#account-count").textContent = data.account.count;
    const active = $("#active-job");
    buildActive = Boolean(data.active_job);
    if (data.active_job) { active.textContent = `Build ${data.active_job.status}`; active.classList.remove("hidden"); }
    else active.classList.add("hidden");
    $("#start-supervisor").disabled = buildActive;
    $("#stop-supervisor").disabled = buildActive;
    $("#build-button").disabled = buildActive;
    $("#account-file").disabled = buildActive;
    $("#account-form button").disabled = buildActive;
    if (data.active_job && !stream) watchBuild(data.active_job);
    renderWorkers(data.workers);
    $("#last-refresh").textContent = `Cập nhật ${new Date().toLocaleTimeString("vi-VN")}`;
  } catch (error) {
    notify(error.message, true);
  }
}

async function supervisorAction(action, button) {
  button.disabled = true;
  try {
    await api(`/api/supervisor/${action}`, { method: "POST" });
    notify(action === "start" ? "Đã chạy supervisor" : "Đã dừng supervisor và worker");
    await refreshStatus();
  } catch (error) { notify(error.message, true); }
  finally { button.disabled = false; }
}

async function restartWorker(name, button) {
  button.disabled = true;
  try {
    await api(`/api/workers/${encodeURIComponent(name)}/restart`, { method: "POST" });
    notify(`Đã restart ${name}`); await refreshStatus();
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

function openLog(worker, kind) {
  if (stream) stream.close();
  showConsole(`${worker} · ${kind === "error" ? "java-errors.log" : "stdout.log"}`);
  stream = new EventSource(`/api/workers/${encodeURIComponent(worker)}/logs/stream?kind=${kind}`);
  stream.addEventListener("log", (event) => {
    const payload = JSON.parse(event.data);
    const label = payload.initial ? "log gần nhất" : "log mới";
    appendConsole(`[${formatTimestamp(payload.timestamp)} · ${label}]\n${payload.text}`);
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
      notify(status.status === "succeeded" ? "Build thành công" : `Build lỗi: ${status.error}`, status.status === "failed");
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

$("#build-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const button = $("#build-button"); button.disabled = true;
  try {
    const job = await api("/api/build", { method: "POST", json: {
      worker_count: Number($("#worker-count").value),
      start_after_build: $("#start-after-build").checked,
    }});
    watchBuild(job);
  } catch (error) { button.disabled = false; notify(error.message, true); }
});

showDashboard();
