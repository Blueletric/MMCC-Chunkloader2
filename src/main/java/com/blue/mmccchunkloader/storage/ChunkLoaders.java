package com.blue.mmccchunkloader.storage;

import java.util.ArrayList;
import java.util.List;


public class ChunkLoaders
{
    private final Object lock = new Object();
    private List<ChunkLoaderPos> loaders = new ArrayList<ChunkLoaderPos>();

    public List<ChunkLoaderPos> getLoaders()
    {
         return new ArrayList<ChunkLoaderPos>(loaders);
    }

    public void addLoader(ChunkLoaderPos pos)
    {
        if (!loaders.contains(pos))
        {
            loaders.add(pos);
        }
    }

    public void removeLoader(ChunkLoaderPos pos)
    {
        if (pos != null)
        {
            loaders.remove(pos);
        }
    }

    public void updateLoginTimeStamp(String ownerId, long loginTimeStamp)
    {
        for (ChunkLoaderPos loaderPosItr : loaders)
        {
            if (loaderPosItr.ownerId.equals(ownerId))
            {
                loaderPosItr.loginTimeStamp = loginTimeStamp;
            }
        }
    }
}
