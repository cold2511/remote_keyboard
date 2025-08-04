🔡 Remote Keyboard Emulator

A mobile-responsive web app that turns your phone into a wireless keyboard for your PC using WebSockets and robotjs.

This can be useful for:
- Typing from a distance.
- Turning any mobile device into a keyboard.
- Fun DIY projects and automation!

----------------------------------------
🚀 How to Run

1. Start the Static File Server (for frontend)
This serves your keyboard UI:

    npx http-server -p 8080

This serves your index.html at: http://localhost:8080

2. Start the WebSocket Server (for backend)
This handles key events and simulates real keystrokes on your PC:

    node server.js

----------------------------------------
📶 Using on Same Wi-Fi (Without ngrok)

If your phone and PC are on the same Wi-Fi, follow these steps:

1. Run the WebSocket server on your PC:

    node server.js

2. Find your system's local IP address:
   - Run this in Command Prompt:
   
        ipconfig

   - Look for your IPv4 address (e.g., 192.168.1.5)

3. On your phone’s browser, go to:

    http://<your-ip-address>:3000

    Example:
    http://192.168.1.5:3000

4. Enter the password (check server.js for the correct one):

    const PASSWORD = "mySecret123";  // example line in server.js
   (NOTE:You can also change the password on server.js initially it is in line no.11 const PASSWORD="mySecret123";  to anything u want) 

6. GOLAAA! 🎉 You’ll now see the keyboard UI.
   Tap keys on your phone — the keystrokes will appear on your PC instantly!

----------------------------------------
🌐 (Optional) Expose to Mobile via ngrok

If your phone is not on the same Wi-Fi or you want HTTPS access:

    ngrok http 8080

Use the HTTPS URL provided by ngrok on your mobile browser.

Note: Make sure to use wss:// in the frontend if accessing over HTTPS.

----------------------------------------
📱 Mobile Optimization

- Large buttons for easy tapping
- Responsive layout for all screen sizes
- Supports:
  - Modifier keys (Shift, Ctrl, Alt)
  - Navigation keys (Enter, Backspace, Arrows)
  - Extended ASCII input

----------------------------------------
📁 Project Structure

remote-keyboard/
├── index.html      - Frontend interface
├── style.css       - UI styling
├── script.js       - Handles key presses + WebSocket
├── server.js       - Node.js WebSocket + robotjs backend
└── README.txt      - This file

----------------------------------------
⚙️ Technologies Used

- Node.js + WebSocket (backend)
- robotjs for simulating keystrokes
- http-server for static file hosting
- HTML, CSS, JavaScript (frontend)
- ngrok (optional) for exposing localhost

----------------------------------------
🧠 Notes

- Both devices must be on the same network (if not using ngrok).
- WebSocket must match the protocol (use wss:// for HTTPS).
- Avoid exposing this app publicly without authentication.

----------------------------------------
📃 License

This project is licensed under the MIT License.
Feel free to use and modify it as you like.

----------------------------------------
🙌 Credits

Built with ❤️ by cold2511
GitHub: https://github.com/cold2511
