package com.blue.mmccchunkloader.storage;

import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerLoadedChunks
{
    private UUID ownerId;
    private int dimension;
    public int maximumChunks;
    public boolean persistence;
    public boolean shouldBeLoaded;
    public int persistenceTimeout;
    public ArrayList<ChunkPos> loadedChunks;

    public PlayerLoadedChunks(UUID ownerId, int dimension, int maximumChunks, boolean persistence, int persistenceTimeout)
    {
        this.ownerId = ownerId;
        this.dimension = dimension;
        this.maximumChunks = maximumChunks;
        this.persistence = persistence;
        this.persistenceTimeout = persistenceTimeout;
        loadedChunks = new ArrayList<>();
        shouldBeLoaded = true;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public int getDimension() {
        return dimension;
    }
}
