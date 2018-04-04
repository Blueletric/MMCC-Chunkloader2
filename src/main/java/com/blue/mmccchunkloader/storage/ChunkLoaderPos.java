package com.blue.mmccchunkloader.storage;

import java.io.Serializable;

public class ChunkLoaderPos implements Serializable
{
    public String ownerId = "";
    public long loginTimeStamp = 0;
    public int dimension = 0;

    public ChunkLoaderPos(String ownerId, int dimension, long loginTimeStamp)
    {
        this.ownerId = ownerId;
        this.dimension = dimension;
        this.loginTimeStamp = loginTimeStamp;
    }

    public String toString() {
        return "dim: " + dimension + " ownerId: " + " loginTimeStamp: " + loginTimeStamp;
    }
}
