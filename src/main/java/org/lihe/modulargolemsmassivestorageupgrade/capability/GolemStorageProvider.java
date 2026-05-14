package org.lihe.modulargolemsmassivestorageupgrade.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lihe.modulargolemsmassivestorageupgrade.api.IGolemStorage;
import org.lihe.modulargolemsmassivestorageupgrade.api.ModCapabilities;

public class GolemStorageProvider implements ICapabilitySerializable<CompoundTag>, IGolemStorage {

    private final ItemStackHandler inventory = new ItemStackHandler(27);
    private final LazyOptional<IGolemStorage> optional = LazyOptional.of(() -> this);

    @Override
    public IItemHandler getInventory() {
        return inventory;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.GOLEM_STORAGE) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt);
    }
}