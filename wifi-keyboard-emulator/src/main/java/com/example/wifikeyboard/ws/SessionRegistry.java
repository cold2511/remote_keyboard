package com.example.wifikeyboard.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {
    private final ConcurrentHashMap<String, Set<WebSocketSession>> sessionsPerRoom = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionToRoom = new ConcurrentHashMap<>();

    public void register(String roomId, WebSocketSession session) {
        sessionsPerRoom.computeIfAbsent(roomId, r -> ConcurrentHashMap.newKeySet()).add(session);
        sessionToRoom.put(session.getId(), roomId);
    }

    public void unregister(WebSocketSession session) {
        String roomId = sessionToRoom.remove(session.getId());
        if (roomId != null) sessionsPerRoom.getOrDefault(roomId, Set.of()).remove(session);
    }

    public Set<WebSocketSession> getSessions(String roomId) {
        return sessionsPerRoom.getOrDefault(roomId, Set.of());
    }

    public String getRoomForSession(WebSocketSession session) {
        return sessionToRoom.get(session.getId());
    }
}
