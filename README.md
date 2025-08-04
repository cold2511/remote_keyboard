# Remote Keyboard

A simple web-based remote keyboard that lets you send key presses from your phone to your PC using WebSockets.

## 🚀 Features

- Send key presses from your phone or other devices
- Modifier keys: Shift, Ctrl, Alt
- Directional controls: Up, Down, Left, Right
- Special keys: Apostrophe, Question Mark, Semicolon, Square Brackets, Plus, Hyphen, etc.
- Designed for maximum usability on mobile devices

## 🛠️ Tech Stack

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Node.js, WebSocket (`ws` library)
- **Server**: `http-server` or any static file server
- **Tunnel**: [Ngrok](https://ngrok.com/) for remote access

## 📦 How to Run

1. **Start the server:**

```bash
npx http-server -p 8080
Start the WebSocket server:

bash
Copy
Edit
node server.js

Start the static file server:

bash
Copy
Edit
http-server -p 8080
Start the WebSocket server:

bash
Copy
Edit
node server.js
(Optional) Expose your local server with ngrok (for mobile access):

bash
Copy
Edit
ngrok http 8080
Use the HTTPS link provided by ngrok on your phone to access the keyboard interface.

📱 Mobile Optimization
The UI is responsive and styled to occupy maximum screen area on mobile.

Large buttons for easy key tapping.

📂 Project Structure
pgsql
Copy
Edit
remote-keyboard/
├── index.html
├── style.css
├── script.js
├── server.js
└── README.md
🧠 Notes
Make sure your phone and PC are on the same Wi-Fi network (unless using ngrok).

When accessing over HTTPS (like via ngrok), make sure your WebSocket also uses wss://.

📃 License
This project is licensed under the MIT License.

yaml
Copy
Edit

---

---

Let me know if you want this added to a `README.md` file directly or published to a GitHub repo.
