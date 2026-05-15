package org.lihe.modulargolemsmassivestorageupgrade.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lihe.modulargolemsmassivestorageupgrade.api.IGolemStorage;
import org.lihe.modulargolemsmassivestorageupgrade.api.ModCapabilities;
import org.lihe.modulargolemsmassivestorageupgrade.data.GolemStorageData;

public class GolemStorageProvider implements ICapabilitySerializable<CompoundTag>, IGolemStorage {
    private final Entity entity;
    private final LazyOptional<IGolemStorage> optional = LazyOptional.of(() -> this);
    private ItemStackHandler inventory;

    public GolemStorageProvider(Entity entity) {
        this.entity = entity;
    }

    private void ensureInventory() {
        if (inventory == null && entity.level() instanceof ServerLevel serverLevel) {
            this.inventory = GolemStorageData.get(serverLevel).getOrCreateInventory(entity.getUUID());
        }
    }

    @Override
    public IItemHandler getInventory() {
        ensureInventory();
        return inventory != null ? inventory : new ItemStackHandler(27);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (entity.level() instanceof ServerLevel serverLevel) {
            GolemStorageData.get(serverLevel).setDirty();
        }
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.GOLEM_STORAGE ? optional.cast() : LazyOptional.empty();
    }
}