const WebSocket = require('ws');
const robot = require('robotjs');

const wss = new WebSocket.Server({ port: 3000 });

wss.on('connection', ws => {
  console.log("Client connected!");

  ws.on('message', msg => {
    const data = JSON.parse(msg);

    const { key, shift, ctrl, alt } = data;

    // Press down modifier keys
    if (shift) robot.keyToggle('shift', 'down');
    if (ctrl) robot.keyToggle('control', 'down');
    if (alt) robot.keyToggle('alt', 'down');

    // Special handling
    if (key === 'space') robot.keyTap('space');
    else if (key === 'enter') robot.keyTap('enter');
    else if (key === 'backspace') robot.keyTap('backspace');
    else robot.keyTap(key);

    // Release modifiers
    if (shift) robot.keyToggle('shift', 'up');
    if (ctrl) robot.keyToggle('control', 'up');
    if (alt) robot.keyToggle('alt', 'up');
  });
});