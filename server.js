const express = require('express');
const path = require('path');
const http = require('http');
const WebSocket = require('ws');
const robot = require('robotjs');

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

const CORRECT_PASSWORD = "secret123"; // Backend-controlled password
let actualcount=0;
const ioi=[0,0];
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());
// app.use(express.static(__dirname)); // Serve index.html and others

// POST route for password checking
app.post('/check-password', (req, res) => {
  const { password } = req.body;
  if (password === CORRECT_PASSWORD) {
    res.json({ success: true });
  } else {
    res.json({ success: false });
  }
});

// WebSocket logic (your original code)
wss.on('connection', ws => {
  console.log("Client connected!");

  ws.on('message', msg => {
    const data = JSON.parse(msg);

    const { keycount,stime,key, shift, ctrl, alt } = data;

    if (shift) robot.keyToggle('shift', 'down');
    if (ctrl) robot.keyToggle('control', 'down');
    if (alt) robot.keyToggle('alt', 'down');

    if (key === 'space') robot.keyTap('space');
    else if (key === 'enter') robot.keyTap('enter');
    else if (key === 'backspace') robot.keyTap('backspace');
    else robot.keyTap(key);
    actualcount++;

    if (shift) robot.keyToggle('shift', 'up');
    if (ctrl) robot.keyToggle('control', 'up');
    if (alt) robot.keyToggle('alt', 'up');
    const curtime=Date.now();
    const latency=curtime-stime;
    const yy=ioi[1];
    const xx=ioi[0];
    const dd=(xx*yy)+latency;
    const uu=dd/(yy+1);
    ioi[0]=uu;
    ioi[1]=yy+1;
    console.log(`Avg latency: ${uu} ms`);
    console.log(`sentkeys: ${keycount} keyscaught: ${actualcount}`);
    const tput=(actualcount/keycount)*100;
    console.log(`ThroughputPercentage: ${tput}`);
  });
});

// Start both HTTP + WebSocket server
const PORT = 3000;
server.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});