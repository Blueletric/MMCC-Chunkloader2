package com.blue.mmccchunkloader.commands;

import com.blue.mmccchunkloader.MMCCChunkloader;
import com.blue.mmccchunkloader.storage.ModConfiguration;
import com.blue.mmccchunkloader.storage.PlayerLoadedChunks;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.UUID;

public class LoadChunkCommand extends CommandBase {
    @Override
    public String getName() {
        return "loadchunk";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            DimensionType world = sender.getEntityWorld().provider.getDimensionType();
            String dimId = (world.getName() + world.getSuffix());
            if (!ModConfiguration.dimensionWhitelist.get(dimId)) {
                TextComponentString textComponent = new TextComponentString(MMCCChunkloader.chatPrefix + "Chunk Loading is disabled for this dimension!");
                sender.sendMessage(textComponent);
                return;
            }

            PlayerLoadedChunks loadedChunks = MMCCChunkloader.playerChunkLoads.get(((EntityPlayer) sender).getUniqueID());
            boolean creatingNew = false;

            if (loadedChunks != null) {
                if (!(loadedChunks.loadedChunks.size() < loadedChunks.maximumChunks)) {
                    TextComponentString textComponent = new TextComponentString(MMCCChunkloader.chatPrefix + "You have reached your maximum number of loaded chunks!");
                    sender.sendMessage(textComponent);
                    return;
                }
            }else{
                loadedChunks = new PlayerLoadedChunks(((EntityPlayer) sender).getUniqueID(), ((EntityPlayer) sender).dimension, ModConfiguration.defaultMax, ModConfiguration.defaultPersistance, ModConfiguration.persistanceTimeout);
                creatingNew = true;
            }

            ChunkPos pos = sender.getEntityWorld().getChunkFromBlockCoords(sender.getPosition()).getPos();

            UUID uuid = MMCCChunkloader.loadedChunks.get(pos);
            if (uuid != null) {
                String playername = server.getPlayerProfileCache().getProfileByUUID(uuid).getName();
                TextComponentString alreadyLoaded = new TextComponentString(MMCCChunkloader.chatPrefix + "Chunk is already loaded by " + playername);
                sender.sendMessage(alreadyLoaded);
                return;
            }

            UUID toSave = ((EntityPlayer) sender).getUniqueID();
            loadedChunks.loadedChunks.add(pos);
            MMCCChunkloader.loadedChunks.put(pos, toSave);

            if (creatingNew) {
                MMCCChunkloader.playerChunkLoads.put(toSave, loadedChunks);
            }

            TextComponentString loaded = new TextComponentString(MMCCChunkloader.chatPrefix + "Current chunk will now be loaded.");
            sender.sendMessage(loaded);
        }else{
            TextComponentString textComponents = new TextComponentString(MMCCChunkloader.chatPrefix + "You must have an in-game presence to load chunks!");
            sender.sendMessage(textComponents);
        }
    }
}
