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

public class LoadChunkCommand extends CommandBase {
    @Override
    public String getName() {
        return "loadchunk";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Loads the chunk that the user is standing in.";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {

            //Performs a whitelist check to make sure loading chunks in this dimension is enabled
            DimensionType world = sender.getEntityWorld().provider.getDimensionType();
            String dimId = (world.getName() + world.getSuffix());
            if (!ModConfiguration.dimensionWhitelist.get(dimId)) {
                TextComponentString textComponent = new TextComponentString(MMCCChunkloader.chatPrefix + "Chunk Loading is disabled for this dimension!");
                sender.sendMessage(textComponent);
                return;
            }

            //Attempts to get the chunkload profile for the player in this dimension
            PlayerLoadedChunks loadedChunks = null;
            for (PlayerLoadedChunks playerLoadedChunks : MMCCChunkloader.playerChunkLoads) {
                if ((playerLoadedChunks.getOwnerId().equals(((EntityPlayer) sender).getUniqueID())) && (playerLoadedChunks.getDimension() == ((EntityPlayer) sender).dimension)) {
                    loadedChunks = playerLoadedChunks;
                    break;
                }
            }

            //If the attempt succeeds, a maximum chunkload check is performed.  If not, a new chunkload profile is created
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

            //Retrieves the chunk position of the player, and checks if it is already loaded by someone else
            ChunkPos pos = sender.getEntityWorld().getChunkFromBlockCoords(sender.getPosition()).getPos();
            int currentDim = ((EntityPlayer) sender).dimension;
            for (PlayerLoadedChunks loadCheck : MMCCChunkloader.playerChunkLoads) {
                if (currentDim == loadCheck.getDimension()) {
                    if (loadCheck.loadedChunks.contains(pos)) {
                        String playername = server.getPlayerProfileCache().getProfileByUUID(loadCheck.getOwnerId()).getName();
                        TextComponentString alreadyLoaded = new TextComponentString(MMCCChunkloader.chatPrefix + "Chunk is already loaded by " + playername);
                        sender.sendMessage(alreadyLoaded);
                        return;
                    }
                }
            }

            //Adds the chunk position to the chunkload profile, and adds the profile to the main list if it was just created
            loadedChunks.loadedChunks.add(pos);
            if (creatingNew) {
                MMCCChunkloader.playerChunkLoads.add(loadedChunks);
            }

            TextComponentString loaded = new TextComponentString(MMCCChunkloader.chatPrefix + "Current chunk will now be loaded.");
            sender.sendMessage(loaded);
        }else{
            TextComponentString textComponents = new TextComponentString(MMCCChunkloader.chatPrefix + "You must have an in-game presence to load chunks!");
            sender.sendMessage(textComponents);
        }
    }
}
