package com.example.loanbot.bot;

import java.util.HashMap;
import java.util.Map;

public class UserSessionStorage {

    private final Map<Long, UserSession> sessions = new HashMap<>();

    public UserSession getSession(long userId) {
        return sessions.computeIfAbsent(userId, id -> new UserSession());
    }

    public void clearSession(long userId) {
        sessions.remove(userId);
    }
}