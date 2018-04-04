package com.blue.mmccchunkloader.storage;

import com.blue.mmccchunkloader.Reference;
import com.google.gson.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

public class SavedData extends WorldSavedData {
    private static final String DATA_NAME = Reference.MOD_ID + "_Chunkloaders";

    private ChunkLoaders chunkLoaders = new ChunkLoaders();

    private static SavedData instance = null;
    public static SavedData get(World world)
    {
        if (instance == null)
        {
            MapStorage storage = world.getMapStorage();
            if (storage == null)
            {
                FMLLog.log(Level.ERROR, "Map storage is null for world");
                return null;
            }
            instance = (SavedData)storage.getOrLoadData(SavedData.class, DATA_NAME);

            if (instance == null)
            {
                instance = new SavedData();
                storage.setData(DATA_NAME, instance);
                instance.markDirty();

            }
        }
        return instance;
    }

    private SavedData()
    {
        super(DATA_NAME);
    }

    public SavedData(String name) {
        super(name);
    }

    @Nonnull
    public ChunkLoaders getChunkloaders()
    {
        return chunkLoaders;
    }

    public void setChunkLoaders(ChunkLoaders chunkLoaders) {
        this.chunkLoaders = chunkLoaders;
        markDirty();
    }

    @Override
    public void readFromNBT(@Nonnull  NBTTagCompound nbt) {
        String json = nbt.getString(ChunkLoaders.class.getSimpleName());
        if (!json.isEmpty())
        {
            chunkLoaders = new Gson().fromJson(json, ChunkLoaders.class);
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setString(ChunkLoaders.class.getSimpleName(), new Gson().toJson(chunkLoaders, ChunkLoaders.class));
        return compound;
    }
}
