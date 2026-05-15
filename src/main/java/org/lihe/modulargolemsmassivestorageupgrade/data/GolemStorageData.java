package org.lihe.modulargolemsmassivestorageupgrade.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.items.ItemStackHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GolemStorageData extends SavedData {
    private static final String NAME = "modular_golems_storage";
    private final ServerLevel level;
    private final Cache<UUID, ItemStackHandler> storageCache;

    public GolemStorageData(ServerLevel level) {
        this.level = level;
        this.storageCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .removalListener(notification -> {
                    if (notification.getValue() instanceof ItemStackHandler handler && notification.getKey() instanceof UUID uuid) {
                        StorageDiskManager.saveAsync(this.level, uuid, handler.serializeNBT());
                    }
                })
                .build();
    }

    public static GolemStorageData get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new RuntimeException("Client side access to SavedData");
        }
        return serverLevel.getServer().overworld().getDataStorage().computeIfAbsent(
                tag -> load(serverLevel, tag),
                () -> new GolemStorageData(serverLevel),
                NAME
        );
    }

    public ItemStackHandler getOrCreateInventory(UUID uuid) {
        ItemStackHandler cached = storageCache.getIfPresent(uuid);
        if (cached != null) return cached;

        ItemStackHandler handler = new ItemStackHandler(27) {
            @Override
            protected void onContentsChanged(int slot) {
                setDirty();
            }
        };
        StorageDiskManager.load(level, uuid, handler);
        storageCache.put(uuid, handler);
        return handler;
    }

    public void invalidate(UUID uuid) {
        storageCache.invalidate(uuid);
    }

    public static GolemStorageData load(ServerLevel level, CompoundTag nbt) {
        return new GolemStorageData(level);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        for (var entry : storageCache.asMap().entrySet()) {
            StorageDiskManager.saveAsync(level, entry.getKey(), entry.getValue().serializeNBT());
        }
        return nbt;
    }
}