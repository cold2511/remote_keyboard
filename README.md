# WiFi Keyboard Emulator

A Spring Boot–based application that allows users to send keystrokes over WiFi from any client device (mobile/PC) to a server machine. It works like a **remote wireless keyboard**, enabling real‑time key transmission through WebSockets.

---

## 🚀 Features

* **Real-time keystroke transmission** using WebSockets
* **Custom locking mechanism** to prevent concurrent key presses
* **Automatic inactivity handling** (lock release on inactivity)
* **Lightweight Spring Boot backend**
* **Simple REST endpoint for testing**
* **Cross-device support** (works from browser/mobile UI)

---

## 🛠️ Tech Stack

* **Java 17+**
* **Spring Boot**

  * WebSocket
  * REST
  * Scheduler
* **Maven**

---

## 📁 Project Structure

```
wifi-keyboard-emulator
│── src/main/java/com/example
│   ├── controller
│   ├── websocket
│   ├── service
│   ├── config
│── src/main/resources
│   ├── application.properties
│── pom.xml
```

---

## ⚙️ How It Works

1. The client connects via **WebSocket**.
2. Sends keystrokes to the backend.
3. Backend processes the key and forwards it to the OS keyboard emulator.
4. A **temporary lock** prevents multiple clients from sending input at the same time.
5. A scheduled task releases the lock automatically after a few seconds of inactivity.

---

## ▶️ Running the Project

### **1. Clone the repository**

```bash
git clone https://github.com/yourusername/wifi-keyboard-emulator.git
cd wifi-keyboard-emulator
```

### **2. Build & Run**

```bash
mvn spring-boot:run
```

### **3. WebSocket URL**

```
ws://localhost:8080/keyboard
```

---

## 📡 API Endpoints

### **Test REST API**

```
GET /ping
Response: "Server is running"
```

---

## 🔐 Concurrency & Multi‑User Handling

* **Multiple users can connect** to the WebSocket simultaneously.
* However, **only one user can send keystrokes at a time**.
* A **lock mechanism** ensures a single active controller:

  * When a user sends a keystroke, the system locks.
  * Other connected users cannot send input until the lock is released.
  * A **scheduled inactivity timer** automatically releases the lock after a few seconds.
* This prevents conflicting keystrokes from different users.

---

## 🧪 Testing

You can test via:

* Browser WebSocket clients
* Postman (for REST)
* Custom frontend (HTML/JS)

---

## 📝 Future Enhancements

* UI for sending keys
* Authentication layer
* Multi-client priority queue
* Custom key mappings

---

## 🤝 Contributing

Feel free to open issues or submit PRs.

---

## 📄 License

MIT License (You can change this)
