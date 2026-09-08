package com.example.loanbot.bot;

import java.util.HashSet;
import java.util.Set;

public class ManagerSessionStorage {

    private final Set<Long> authorizedManagers = new HashSet<>();

    public void authorize(long userId) {
        authorizedManagers.add(userId);
    }

    public void revoke(long userId) {
        authorizedManagers.remove(userId);
    }

    public boolean isAuthorized(long userId) {
        return authorizedManagers.contains(userId);
    }
}
