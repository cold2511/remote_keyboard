package com.example.wifikeyboard.ws;

import com.example.wifikeyboard.service.LockService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KeyboardWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final LockService lockService;
    private final SessionRegistry registry;
    private final ConcurrentHashMap<String, String> sessionToClient = new ConcurrentHashMap<>();
    private static Robot robot;

    static {
        try {
            robot = new Robot();
            System.out.println("✅ Robot initialized successfully!");
        } catch (AWTException e) {
            System.out.println("❌ Failed to initialize Robot: " + e.getMessage());
            e.printStackTrace();
            robot = null;
        }
    }

    public KeyboardWebSocketHandler(LockService lockService, SessionRegistry registry) {
        this.lockService = lockService;
        this.registry = registry;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("✅ Received message: " + message.getPayload());

        JsonNode node = mapper.readTree(message.getPayload());
        String type = node.get("type").asText();

        switch (type) {
            case "JOIN" -> {
                String room = node.get("roomId").asText();
                String client = node.get("clientId").asText();
                sessionToClient.put(session.getId(), client);
                registry.register(room, session);
                send(session, obj("JOINED").put("roomId", room));
                broadcastLock(room);
            }

            case "REQUEST_LOCK" -> {
                String room = registry.getRoomForSession(session);
                lockService.requestLock(room, sessionToClient.get(session.getId()));
                broadcastLock(room);
            }

            case "TYPING" -> {
                System.out.println("🟢 Raw TYPING message payload: " );
                System.out.println("📦 Parsed JSON node: " + node.toPrettyString());

                //System.out.println("🟢 Raw TYPING message payload: " + node.get("payload"));

                String room = registry.getRoomForSession(session);
                String client = sessionToClient.get(session.getId());
                if (lockService.isHolder(room, client)) {
                    lockService.refreshLease(room, client);
                    JsonNode payloadNode = node.get("payload");
                    // String text = "";

                    // if (node.has("payload")) {
                    //     JsonNode payloadNode = node.get("payload");
                    //     text = payloadNode.isTextual() ? payloadNode.textValue() : payloadNode.toString();
                    // } else {
                    //     System.out.println("⚠️ JSON has no 'payload' field: " + node.toPrettyString());
                    // }

                    //String text = payloadNode == null || payloadNode.isNull() ? "" : payloadNode.asText();
                    String text = node.path("payload").asText().trim();

                    System.out.println("✅ Extracted text for Robot: [" + text + "]");
                    forwardToRobot(text);
                } else {
                    send(session, obj("ERROR").put("message", "Not your turn"));
                }
            }

            case "RELEASE_LOCK" -> {
                String room = registry.getRoomForSession(session);
                lockService.releaseLock(room, sessionToClient.get(session.getId()));
                broadcastLock(room);
            }

            case "KEEPALIVE" -> {
                String room = registry.getRoomForSession(session);
                lockService.refreshLease(room, sessionToClient.get(session.getId()));
            }

            default -> send(session, obj("ERROR").put("message", "Unknown type"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String room = registry.getRoomForSession(session);
        String client = sessionToClient.remove(session.getId());
        registry.unregister(session);
        if (room != null && client != null) {
            lockService.clientDisconnected(room, client);
            broadcastLock(room);
        }
    }

    private void broadcastLock(String room) {
        String holder = null;
        for (var sess : registry.getSessions(room)) {
            String cid = sessionToClient.get(sess.getId());
            if (cid != null && lockService.isHolder(room, cid)) {
                holder = cid;
                break;
            }
        }

        ObjectNode msg = obj("LOCK_STATUS");
        if (holder != null) msg.put("holder", holder);
        else msg.putNull("holder");

        for (var s : registry.getSessions(room)) {
            try {
                s.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
            } catch (IOException ignored) {}
        }
    }

    private void send(WebSocketSession session, ObjectNode node) throws IOException {
        session.sendMessage(new TextMessage(node.toString()));
    }

    private ObjectNode obj(String type) {
        return mapper.createObjectNode().put("type", type);
    }

    // private void forwardToRobot(String text) {
    //     //if (text == null || text.isEmpty() || robot == null) return;
    //     System.out.println("Robot typing: " ); 
    //     System.out.println("Robot typing: " + text);
    //     // if (text == null || text.isEmpty() || robot == null) return;
    //     // System.out.println("Robot typing: " + text); 
    //     // for (char c : text.toCharArray()) {
    //     //     typeChar(c);
    //     // }

    // }
    private void forwardToRobot(String text) {
        if (robot == null) {
            System.out.println("❌ Robot not initialized!");
            return;
        }

        if (text == null) {
            System.out.println("⚠️ Received null text, skipping...");
            return;
        }

        text = text.trim(); // remove invisible chars

        if (text.isEmpty()) {
            System.out.println("⚠️ Received empty or whitespace text, skipping...");
            return;
        }

        System.out.println("🤖 Robot typing: [" + text + "]");
        for (char c : text.toCharArray()) {
            typeChar(c);
            robot.delay(50);
        }
    }


    private void typeChar(char c) {
        boolean shift = false;
        int code = -1;

        if (Character.isUpperCase(c)) {
            shift = true;
            c = Character.toLowerCase(c);
        }

        switch (c) {
            case 'a' -> code = KeyEvent.VK_A;
            case 'b' -> code = KeyEvent.VK_B;
            case 'c' -> code = KeyEvent.VK_C;
            case 'd' -> code = KeyEvent.VK_D;
            case 'e' -> code = KeyEvent.VK_E;
            case 'f' -> code = KeyEvent.VK_F;
            case 'g' -> code = KeyEvent.VK_G;
            case 'h' -> code = KeyEvent.VK_H;
            case 'i' -> code = KeyEvent.VK_I;
            case 'j' -> code = KeyEvent.VK_J;
            case 'k' -> code = KeyEvent.VK_K;
            case 'l' -> code = KeyEvent.VK_L;
            case 'm' -> code = KeyEvent.VK_M;
            case 'n' -> code = KeyEvent.VK_N;
            case 'o' -> code = KeyEvent.VK_O;
            case 'p' -> code = KeyEvent.VK_P;
            case 'q' -> code = KeyEvent.VK_Q;
            case 'r' -> code = KeyEvent.VK_R;
            case 's' -> code = KeyEvent.VK_S;
            case 't' -> code = KeyEvent.VK_T;
            case 'u' -> code = KeyEvent.VK_U;
            case 'v' -> code = KeyEvent.VK_V;
            case 'w' -> code = KeyEvent.VK_W;
            case 'x' -> code = KeyEvent.VK_X;
            case 'y' -> code = KeyEvent.VK_Y;
            case 'z' -> code = KeyEvent.VK_Z;
            case ' ' -> code = KeyEvent.VK_SPACE;
            case '\n' -> code = KeyEvent.VK_ENTER;
            case '\t' -> code = KeyEvent.VK_TAB;
            case '.' -> code = KeyEvent.VK_PERIOD;
            case ',' -> code = KeyEvent.VK_COMMA;
            case '0' -> code = KeyEvent.VK_0;
            case '1' -> code = KeyEvent.VK_1;
            case '2' -> code = KeyEvent.VK_2;
            case '3' -> code = KeyEvent.VK_3;
            case '4' -> code = KeyEvent.VK_4;
            case '5' -> code = KeyEvent.VK_5;
            case '6' -> code = KeyEvent.VK_6;
            case '7' -> code = KeyEvent.VK_7;
            case '8' -> code = KeyEvent.VK_8;
            case '9' -> code = KeyEvent.VK_9;
            case '-' -> code = KeyEvent.VK_MINUS;
            case '_' -> { shift = true; code = KeyEvent.VK_MINUS; }
            case '=' -> code = KeyEvent.VK_EQUALS;
            case '+' -> { shift = true; code = KeyEvent.VK_EQUALS; }
            case ';' -> code = KeyEvent.VK_SEMICOLON;
            case ':' -> { shift = true; code = KeyEvent.VK_SEMICOLON; }
            case '!' -> { shift = true; code = KeyEvent.VK_1; }
            case '@' -> { shift = true; code = KeyEvent.VK_2; }
            case '#' -> { shift = true; code = KeyEvent.VK_3; }
            case '$' -> { shift = true; code = KeyEvent.VK_4; }
            case '%' -> { shift = true; code = KeyEvent.VK_5; }
            case '^' -> { shift = true; code = KeyEvent.VK_6; }
            case '&' -> { shift = true; code = KeyEvent.VK_7; }
            case '*' -> { shift = true; code = KeyEvent.VK_8; }
            case '(' -> { shift = true; code = KeyEvent.VK_9; }
            case ')' -> { shift = true; code = KeyEvent.VK_0; }
            default -> {
                System.out.println("Skipping unsupported char: " + c);
                return;
            }
        }

        if (code == -1) return;

        if (shift) robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(code);
        robot.keyRelease(code);
        if (shift) robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.delay(10);
    }
}
