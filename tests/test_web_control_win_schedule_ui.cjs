// Run: node --test tests/test_web_control_win_schedule_ui.cjs
const assert = require('node:assert/strict');
const { test } = require('node:test');
const { readFileSync } = require('node:fs');
const { join } = require('node:path');
const vm = require('node:vm');

const saved = {
  enabled: false,
  schedule_type: 'start_then_repeat',
  start_time: '01:00',
  repeat_hours: 6,
  worker_count: 10,
};

function setup() {
  const elements = new Map();
  function element(selector) {
    if (!elements.has(selector)) elements.set(selector, {
      value: '', checked: false, disabled: false, style: {}, listeners: {},
      classList: { add() {}, remove() {} },
      addEventListener(name, callback) { this.listeners[name] = callback; },
      replaceChildren() {}, append() {},
    });
    return elements.get(selector);
  }
  const requests = [];
  const context = vm.createContext({
    document: { querySelector: element, createElement: () => element(Symbol()) },
    Headers, setInterval() {}, window: { setTimeout() {} },
    fetch(path, options) {
      return new Promise(resolve => requests.push({
        path, options,
        respond(body, ok = true) { resolve({ ok, status: ok ? 200 : 500, json: async () => body }); },
      }));
    },
  });
  vm.runInContext(readFileSync(join(__dirname, '../web_control_win/static/app.js'), 'utf8'), context);
  context.renderSchedule(saved);
  function edit(count) {
    element('#schedule-worker-count').value = String(count);
    element('#schedule-enabled').checked = true;
    element('#schedule-start-time').value = '12:30';
    element('#schedule-repeat-hours').value = '3';
    element('#schedule-form').listeners.input();
  }
  return {
    context, element, requests, edit,
    submit: () => element('#schedule-form').listeners.submit({ preventDefault() {} }),
  };
}

test('polling keeps unsaved checkbox and worker count', () => {
  const ui = setup();
  ui.edit(100);
  ui.context.renderSchedule(saved);
  assert.equal(ui.element('#schedule-worker-count').value, '100');
  assert.equal(ui.element('#schedule-enabled').checked, true);
  assert.equal(ui.element('#schedule-start-time').value, '12:30');
  assert.equal(ui.element('#schedule-repeat-hours').value, '3');
});

test('save sends edited values and ignores an older status response', async () => {
  const ui = setup();
  const pendingStatus = ui.context.refreshStatus();
  const statusRequest = ui.requests.at(-1);
  ui.edit(100);
  const saving = ui.submit();
  const request = ui.requests.at(-1);
  const payload = JSON.parse(request.options.body);
  assert.equal(payload.worker_count, 100);
  assert.equal(payload.enabled, true);
  assert.equal(payload.start_time, '12:30');
  assert.equal(payload.repeat_hours, 3);
  assert.equal(ui.element("#schedule-form button[type='submit']").disabled, true);
  request.respond(payload);
  await saving;
  statusRequest.respond({ schedule: saved, supervisor: {}, totals: {}, account: {}, workers: [] });
  await pendingStatus;
  assert.equal(Number(ui.element('#schedule-worker-count').value), 100);
  assert.equal(ui.element('#schedule-enabled').checked, true);
  assert.equal(ui.element("#schedule-form button[type='submit']").disabled, false);
});

test('failed save keeps the draft through later polling', async () => {
  const ui = setup();
  ui.edit(80);
  const saving = ui.submit();
  ui.requests.at(-1).respond({ detail: 'Cannot save' }, false);
  await saving;
  ui.context.renderSchedule(saved);
  assert.equal(ui.element('#schedule-worker-count').value, '80');
  assert.equal(ui.element('#notice').textContent, 'Cannot save');
  assert.equal(ui.element("#schedule-form button[type='submit']").disabled, false);
});

test('edits made during save survive the save response', async () => {
  const ui = setup();
  ui.edit(100);
  const saving = ui.submit();
  const request = ui.requests.at(-1);
  ui.edit(120);
  request.respond(JSON.parse(request.options.body));
  await saving;
  ui.context.renderSchedule({ ...saved, worker_count: 100 });
  assert.equal(ui.element('#schedule-worker-count').value, '120');
});
