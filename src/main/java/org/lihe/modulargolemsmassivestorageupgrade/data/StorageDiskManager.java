package org.lihe.modulargolemsmassivestorageupgrade.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.items.ItemStackHandler;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageDiskManager {
    private static final String FOLDER_NAME = "golem_storage";
    private static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();

    private static File getStorageDir(ServerLevel level) {
        File dir = new File(level.getServer().getWorldPath(LevelResource.ROOT).toFile(), FOLDER_NAME);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static void deleteAsync(ServerLevel level, UUID uuid) {
        File file = new File(getStorageDir(level), uuid.toString() + ".dat");
        IO_EXECUTOR.submit(() -> {
            if (file.exists()) file.delete();
        });
    }

    public static void saveAsync(ServerLevel level, UUID uuid, CompoundTag inventoryTag) {
        CompoundTag snapshot = inventoryTag.copy();
        File file = new File(getStorageDir(level), uuid.toString() + ".dat");
        IO_EXECUTOR.submit(() -> {
            try {
                CompoundTag nbt = new CompoundTag();
                nbt.put("Inventory", snapshot);
                NbtIo.writeCompressed(nbt, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void load(ServerLevel level, UUID uuid, ItemStackHandler handler) {
        File file = new File(getStorageDir(level), uuid.toString() + ".dat");
        if (file.exists()) {
            try {
                CompoundTag nbt = NbtIo.readCompressed(file);
                if (nbt != null) {
                    handler.deserializeNBT(nbt.getCompound("Inventory"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}