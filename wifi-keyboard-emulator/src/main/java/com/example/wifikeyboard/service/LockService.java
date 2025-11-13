// package com.example.wifikeyboard.service;

// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;
// import java.time.Instant;
// import java.util.*;
// import java.util.concurrent.*;

// @Service
// public class LockService {

//     private static class LockInfo {
//         final String holderClientId;
//         Instant leaseExpiry;
//         LockInfo(String holderClientId, Instant leaseExpiry) {
//             this.holderClientId = holderClientId;
//             this.leaseExpiry = leaseExpiry;
//         }
//     }

//     private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> waitQueues = new ConcurrentHashMap<>();
//     private final ConcurrentHashMap<String, LockInfo> locks = new ConcurrentHashMap<>();
//     private final long leaseSeconds = 5;

//     public void requestLock(String roomId, String clientId) {
//         waitQueues.computeIfAbsent(roomId, r -> new ConcurrentLinkedQueue<>()).add(clientId);
//         tryGrantLock(roomId);
//     }

//     private void tryGrantLock(String roomId) {
//         locks.compute(roomId, (r, current) -> {
//             if (current != null && Instant.now().isBefore(current.leaseExpiry))
//                 return current;
//             Queue<String> q = waitQueues.get(roomId);
//             if (q == null) return null;
//             String next = q.poll();
//             if (next == null) return null;
//             return new LockInfo(next, Instant.now().plusSeconds(leaseSeconds));
//         });
//     }

//     public boolean isHolder(String roomId, String clientId) {
//         LockInfo li = locks.get(roomId);
//         return li != null && li.holderClientId.equals(clientId) && Instant.now().isBefore(li.leaseExpiry);
//     }

//     public void refreshLease(String roomId, String clientId) {
//         locks.computeIfPresent(roomId, (r, current) -> {
//             if (current.holderClientId.equals(clientId))
//                 current.leaseExpiry = Instant.now().plusSeconds(leaseSeconds);
//             return current;
//         });
//     }

//     public void releaseLock(String roomId, String clientId) {
//         locks.computeIfPresent(roomId, (r, current) -> current.holderClientId.equals(clientId) ? null : current);
//         ConcurrentLinkedQueue<String> q = waitQueues.get(roomId);
//         if (q != null) q.remove(clientId);
//         tryGrantLock(roomId);
//     }

//     public void clientDisconnected(String roomId, String clientId) {
//         releaseLock(roomId, clientId);
//     }

//     @Scheduled(fixedDelay = 1000)
//     public void sweep() {
//         Instant now = Instant.now();
//         for (var e : locks.entrySet()) {
//             if (e.getValue() != null && now.isAfter(e.getValue().leaseExpiry))
//                 releaseLock(e.getKey(), e.getValue().holderClientId);
//         }
//     }
// }


package com.example.wifikeyboard.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LockService {

    // Store: room → holderClientId
    private final Map<String, String> lockHolder = new ConcurrentHashMap<>();
    // Store: room → lastActivityTimestamp
    private final Map<String, Long> lastActive = new ConcurrentHashMap<>();

    // lease timeout in ms (adjust as needed)
    private static final long LEASE_TIMEOUT_MS = 500;

    public synchronized void requestLock(String room, String clientId) {
        if (!lockHolder.containsKey(room)) {
            lockHolder.put(room, clientId);
            lastActive.put(room, System.currentTimeMillis());
            System.out.println("🔒 Lock granted to " + clientId + " in room " + room);
        }
    }

    public synchronized void releaseLock(String room, String clientId) {
        if (clientId.equals(lockHolder.get(room))) {
            lockHolder.remove(room);
            lastActive.remove(room);
            System.out.println("🔓 Lock released for room " + room + " by " + clientId);
        }
    }

    public synchronized boolean isHolder(String room, String clientId) {
        return clientId.equals(lockHolder.get(room));
    }

    public synchronized void refreshLease(String room, String clientId) {
        if (clientId.equals(lockHolder.get(room))) {
            lastActive.put(room, System.currentTimeMillis());
        }
    }

    public synchronized void clientDisconnected(String room, String clientId) {
        if (clientId.equals(lockHolder.get(room))) {
            releaseLock(room, clientId);
        }
    }

    // ✅ Run every second to check for expired locks
    @Scheduled(fixedRate = 1000)
    public synchronized void expireInactiveLocks() {
        long now = System.currentTimeMillis();
        for (String room : lockHolder.keySet()) {
            Long last = lastActive.get(room);
            if (last != null && (now - last) > LEASE_TIMEOUT_MS) {
                String client = lockHolder.get(room);
                System.out.println("⏰ Lock expired for room " + room + " (held by " + client + ")");
                releaseLock(room, client);
            }
        }
    }
}

