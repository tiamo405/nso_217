const $ = (selector) => document.querySelector(selector);
let refreshTimer = null;
let stream = null;
let buildActive = false;
let scheduleDirty = false;
let scheduleSaving = false;
let scheduleRevision = 0;
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
  if (!value) return "Chưa có";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : date.toLocaleTimeString("vi-VN");
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
    const empty = cell("Chưa có worker nào. Hãy upload account.csv rồi bấm Build & Chạy.", "empty");
    empty.colSpan = 10;
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
    row.append(cell(worker.accounts));
    row.append(logCell(worker));

    const actions = document.createElement("td");
    const group = document.createElement("div"); group.className = "action-group";
    const workerActions = [];
    if (worker.state === "PAUSED") workerActions.push(["Start", "start"]);
    else if (worker.state !== "DONE") workerActions.push(["Stop", "stop"], ["Restart", "restart"]);
    workerActions.push(["Log", "stdout"], ["Errors", "error"]);

    for (const [label, action] of workerActions) {
      const button = document.createElement("button");
      button.className = action === "stop" ? "danger" : action === "start" ? "primary" : action === "restart" ? "secondary" : "ghost";
      button.textContent = label;
      if (["start", "stop", "restart"].includes(action)) button.disabled = buildActive;
      button.addEventListener("click", () => ["start", "stop", "restart"].includes(action)
        ? workerAction(worker.name, action, button)
        : openLog(worker.name, action));
      group.append(button);
    }
    actions.append(group);
    row.append(actions);
    body.append(row);
  }
}

async function refreshStatus() {
  const revision = scheduleRevision;
  try {
    const data = await api("/api/status");
    const supervisor = data.supervisor;
    $("#server-select").value = data.server || supervisor.server || "tk";

    if (!supervisorSettingsDirty && !supervisorSettingsSaving) {
      $("#periodic-restart-hours").value = supervisor.periodic_restart_hours ?? 3;
    }

    $("#supervisor-state").textContent = supervisor.running ? "RUNNING" : "STOPPED";
    $("#supervisor-state").style.color = supervisor.running ? "var(--accent)" : "var(--danger)";

    $("#supervisor-detail").textContent = supervisor.running
      ? `Supervisor PID ${supervisor.pid} · Tự chạy lại: ${supervisor.desired ? "Bật" : "Tắt"} · Restart định kỳ: ${supervisor.periodic_restart_hours ? `${supervisor.periodic_restart_hours}h` : "Tắt"}`
      : `Đang dừng${supervisor.stale_pid ? " (Có PID cũ)" : ""} · Tự chạy lại: ${supervisor.desired ? "Bật" : "Tắt"} · Restart định kỳ: ${supervisor.periodic_restart_hours ? `${supervisor.periodic_restart_hours}h` : "Tắt"}`;

    $("#running-count").textContent = data.totals.running;
    $("#stopped-count").textContent = data.totals.stopped;
    $("#paused-count").textContent = data.totals.paused ?? 0;
    $("#done-count").textContent = data.totals.done;
    $("#account-count").textContent = data.account.count;

    const active = $("#active-job");
    buildActive = Boolean(data.active_job);
    if (data.active_job) {
      active.textContent = `Build ${data.active_job.status}`;
      active.classList.remove("hidden");
    } else {
      active.classList.add("hidden");
    }

    $("#start-supervisor").disabled = buildActive;
    $("#stop-supervisor").disabled = buildActive;
    $("#build-button").disabled = buildActive;
    $("#run-button").disabled = buildActive;
    $("#account-file").disabled = buildActive;
    $("#account-form button").disabled = buildActive;
    $("#periodic-restart-hours").disabled = buildActive || supervisorSettingsSaving;
    $("#save-supervisor-settings").disabled = buildActive || supervisorSettingsSaving;

    if (data.active_job && !stream) watchBuild(data.active_job);

    renderWorkers(data.workers || []);
    if (data.schedule && revision === scheduleRevision && !scheduleSaving) {
      renderSchedule(data.schedule);
    }

    $("#last-refresh").textContent = `Cập nhật ${new Date().toLocaleTimeString("vi-VN")}`;
  } catch (error) {
    notify(error.message, true);
  }
}

function renderSchedule(schedule) {
  const badge = $("#schedule-status-badge");
  badge.textContent = schedule.enabled ? "Đang bật" : "Tắt";
  badge.className = `badge ${schedule.enabled ? "badge-success" : "badge-muted"}`;

  // Polling cập nhật trạng thái, nhưng giữ nguyên các giá trị chưa lưu.
  if (!scheduleDirty && !scheduleSaving) {
    $("#schedule-enabled").checked = schedule.enabled;
    $("#schedule-mode").value = schedule.mode;
    $("#schedule-daily-time").value = schedule.daily_time || "01:00";
    $("#schedule-interval-hours").value = schedule.interval_hours || 6;
  $("#schedule-worker-count").value = schedule.worker_count || 10;
  $("#schedule-server").value = schedule.server || $("#server-select").value || "tk";

    if (schedule.mode === "daily") {
      $("#group-daily-time").classList.remove("hidden");
      $("#group-interval-hours").classList.add("hidden");
    } else {
      $("#group-daily-time").classList.add("hidden");
      $("#group-interval-hours").classList.remove("hidden");
    }
  }

  if (schedule.enabled && schedule.next_run_at) {
    const nextDate = new Date(schedule.next_run_at);
    $("#schedule-next-run").textContent = `Lần chạy tới: ${nextDate.toLocaleString("vi-VN")}`;
  } else {
    $("#schedule-next-run").textContent = schedule.enabled ? "Đang tính toán..." : "Chưa bật lịch hẹn";
  }
}

async function workerAction(workerName, action, button) {
  button.disabled = true;
  try {
    const result = await api(`/api/workers/${workerName}/${action}`, { method: "POST" });
    notify(result.output || `Đã thực hiện ${action} cho ${workerName}`);
    await refreshStatus();
  } catch (error) {
    notify(error.message, true);
  } finally {
    button.disabled = false;
  }
}

async function openLog(workerName, kind) {
  const title = kind === "stdout" ? `Log ${workerName}` : `Lỗi ${workerName}`;
  $("#modal-title").textContent = title;
  const terminal = $("#modal-terminal");
  terminal.textContent = "Đang tải log...";
  $("#log-modal").classList.remove("hidden");

  try {
    const data = await api(`/api/workers/${workerName}/logs?kind=${kind}&lines=300`);
    terminal.textContent = data.content || "Chưa có dữ liệu log.";
    terminal.scrollTop = terminal.scrollHeight;
  } catch (error) {
    terminal.textContent = `Lỗi tải log: ${error.message}`;
  }
}

$("#modal-close").addEventListener("click", () => {
  $("#log-modal").classList.add("hidden");
});

async function supervisorAction(action, button) {
  button.disabled = true;
  try {
    const options = { method: "POST" };
    if (action === "start") {
      const periodicHours = readPeriodicRestartHours();
      if (periodicHours === null) return;
      options.json = {
        server: $("#server-select").value,
        periodic_restart_hours: periodicHours,
      };
    }
    await api(`/api/supervisor/${action}`, options);
    notify(`Đã gửi lệnh ${action} supervisor`);
    if (action === "start") supervisorSettingsDirty = false;
    await refreshStatus();
  } catch (error) {
    notify(error.message, true);
  } finally {
    button.disabled = false;
  }
}

function readPeriodicRestartHours() {
  const value = Number.parseInt($("#periodic-restart-hours").value, 10);
  if (!Number.isInteger(value) || value < 0 || value > 168) {
    notify("Số giờ restart phải từ 0 đến 168", true);
    return null;
  }
  return value;
}

async function saveSupervisorSettings() {
  if (supervisorSettingsSaving) return;
  const periodicHours = readPeriodicRestartHours();
  if (periodicHours === null) return;

  supervisorSettingsSaving = true;
  $("#save-supervisor-settings").disabled = true;
  try {
    const result = await api("/api/supervisor/settings", {
      method: "POST",
      json: { periodic_restart_hours: periodicHours },
    });
    supervisorSettingsDirty = false;
    if (result.requires_restart) {
      notify("Đã lưu. Supervisor đang chạy; hãy Stop rồi Start Supervisor để áp dụng cấu hình mới.");
    } else {
      notify("Đã lưu cấu hình restart định kỳ.");
    }
    await refreshStatus();
  } catch (error) {
    notify(error.message, true);
  } finally {
    supervisorSettingsSaving = false;
    $("#save-supervisor-settings").disabled = false;
  }
}

$("#start-supervisor").addEventListener("click", (event) => supervisorAction("start", event.currentTarget));
$("#stop-supervisor").addEventListener("click", (event) => supervisorAction("stop", event.currentTarget));
$("#save-supervisor-settings").addEventListener("click", saveSupervisorSettings);
$("#periodic-restart-hours").addEventListener("input", () => {
  supervisorSettingsDirty = true;
});
$("#refresh-button").addEventListener("click", refreshStatus);

$("#account-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const fileInput = $("#account-file");
  const file = fileInput.files[0];
  if (!file) return;

  const button = event.target.querySelector("button");
  button.disabled = true;
  try {
    const res = await fetch("/api/account", {
      method: "POST",
      headers: { "Content-Type": "text/csv; charset=utf-8" },
      body: await file.arrayBuffer(),
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.detail || "Upload thất bại");
    notify(`Đã cập nhật ${data.count} tài khoản vào account.csv`);
    fileInput.value = "";
    await refreshStatus();
  } catch (error) {
    notify(error.message, true);
  } finally {
    button.disabled = false;
  }
});

async function triggerBuild(startAfterBuild) {
  const count = parseInt($("#worker-count").value, 10);
  if (!count || count < 1) {
    notify("Số lượng worker không hợp lệ", true);
    return;
  }
  try {
    const data = await api("/api/build", {
      method: "POST",
      json: {
        worker_count: count,
        start_after_build: startAfterBuild,
        server: $("#server-select").value,
      },
    });
    notify(`Bắt đầu build với ${count} workers...`);
    watchBuild(data.job);
  } catch (error) {
    notify(error.message, true);
  }
}

function watchBuild(job) {
  if (stream) stream.close();
  $("#modal-title").textContent = `Build Job ${job.id}`;
  const terminal = $("#modal-terminal");
  terminal.textContent = "Đang khởi tạo tiến trình build...\n";
  $("#log-modal").classList.remove("hidden");

  stream = new EventSource(`/api/build/${job.id}/events`);

  stream.addEventListener("log", (event) => {
    const data = JSON.parse(event.data);
    terminal.textContent += `${data.line}\n`;
    terminal.scrollTop = terminal.scrollHeight;
  });

  stream.addEventListener("status", (event) => {
    const payload = JSON.parse(event.data);
    if (payload.status === "succeeded" || payload.status === "failed") {
      stream.close();
      stream = null;
      refreshStatus();
      if (payload.status === "succeeded") {
        notify("Build và khởi tạo workers thành công!");
      } else {
        notify(`Build thất bại: ${payload.error || ""}`, true);
      }
    }
  });

  stream.onerror = () => {
    if (stream) { stream.close(); stream = null; }
  };
}

$("#build-button").addEventListener("click", () => triggerBuild(false));
$("#run-button").addEventListener("click", () => triggerBuild(true));

for (const eventName of ["input", "change"]) {
  $("#schedule-form").addEventListener(eventName, () => {
    scheduleDirty = true;
    scheduleRevision += 1;
  });
}

$("#schedule-mode").addEventListener("change", (event) => {
  if (event.target.value === "daily") {
    $("#group-daily-time").classList.remove("hidden");
    $("#group-interval-hours").classList.add("hidden");
  } else {
    $("#group-daily-time").classList.add("hidden");
    $("#group-interval-hours").classList.remove("hidden");
  }
});

$("#schedule-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  if (scheduleSaving) return;
  scheduleSaving = true;
  scheduleDirty = true;
  const revision = ++scheduleRevision;
  const button = $("#schedule-form button[type='submit']");
  button.disabled = true;
  const enabled = $("#schedule-enabled").checked;
  const mode = $("#schedule-mode").value;
  const dailyTime = $("#schedule-daily-time").value;
  const intervalHours = parseInt($("#schedule-interval-hours").value, 10);
  const workerCount = parseInt($("#schedule-worker-count").value, 10);

  try {
    const updated = await api("/api/schedule", {
      method: "POST",
      json: {
        enabled,
        mode,
        daily_time: dailyTime,
        interval_hours: intervalHours,
      worker_count: workerCount,
      server: $("#schedule-server").value,
      },
    });
    scheduleSaving = false;
    if (revision === scheduleRevision) scheduleDirty = false;
    scheduleRevision += 1;
    notify("Đã lưu cấu hình hẹn giờ!");
    renderSchedule(updated);
  } catch (error) {
    notify(error.message, true);
  } finally {
    scheduleSaving = false;
    button.disabled = false;
  }
});

// Khởi chạy
refreshStatus();
refreshTimer = setInterval(refreshStatus, 4000);
