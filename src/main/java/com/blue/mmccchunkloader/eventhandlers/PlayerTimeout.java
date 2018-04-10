package com.blue.mmccchunkloader.eventhandlers;

import java.util.UUID;

public class PlayerTimeout {
    private UUID playerUUID = null;
    private long currentTimeout = -1;
    private long timeoutValue = -1;

    public PlayerTimeout(UUID playerUUID, long timeoutValue) {
        this.playerUUID = playerUUID;
        currentTimeout = 0;
        this.timeoutValue = timeoutValue;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void addTime(long millis) {
        this.currentTimeout += millis;
    }

    public boolean timeoutCompleted() {
        return (currentTimeout > timeoutValue);
    }
}
