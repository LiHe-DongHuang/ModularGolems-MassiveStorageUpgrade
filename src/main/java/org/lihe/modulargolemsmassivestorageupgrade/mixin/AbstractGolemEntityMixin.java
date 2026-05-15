package org.lihe.modulargolemsmassivestorageupgrade.mixin;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.lihe.modulargolemsmassivestorageupgrade.Config;
import org.lihe.modulargolemsmassivestorageupgrade.api.ModCapabilities;
import org.lihe.modulargolemsmassivestorageupgrade.data.GolemStorageData;
import org.lihe.modulargolemsmassivestorageupgrade.data.StorageDiskManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class AbstractGolemEntityMixin {

    @Inject(method = "dropCustomDeathLoot", at = @At("HEAD"))
    private void modulargolems_storage$onDropCustomDeathLoot(DamageSource source, int looting, boolean hitByPlayer, CallbackInfo ci) {
        if (!((Object) this instanceof AbstractGolemEntity<?, ?> golem)) return;

        Level level = golem.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        golem.getCapability(ModCapabilities.GOLEM_STORAGE).ifPresent(storage -> {
            IItemHandler handler = storage.getInventory();
            List<ItemStack> extraStacks = new ArrayList<>();

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    extraStacks.add(stack.copy());
                    if (handler instanceof IItemHandlerModifiable modifiable) {
                        modifiable.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            }

            if (Config.SPAWN_DEATH_CHEST.get() && !extraStacks.isEmpty()) {
                BlockPos pos = modulargolems_storage$findSafePos(level, golem.blockPosition());
                level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChestBlockEntity chest) {
                    int slot = 0;
                    for (ItemStack stack : extraStacks) {
                        if (slot < chest.getContainerSize()) chest.setItem(slot++, stack);
                        else golem.spawnAtLocation(stack);
                    }
                } else {
                    for (ItemStack stack : extraStacks) golem.spawnAtLocation(stack);
                }
            } else {
                for (ItemStack stack : extraStacks) golem.spawnAtLocation(stack);
            }

            GolemStorageData.get(serverLevel).invalidate(golem.getUUID());
            StorageDiskManager.deleteAsync(serverLevel, golem.getUUID());
        });
    }

    @Unique
    private BlockPos modulargolems_storage$findSafePos(Level level, BlockPos startPos) {
        if (level.getBlockState(startPos).isAir() || level.getBlockState(startPos).canBeReplaced()) return startPos;
        for (int i = 1; i <= 2; i++) {
            BlockPos up = startPos.above(i);
            if (level.getBlockState(up).isAir() || level.getBlockState(up).canBeReplaced()) return up;
        }
        return startPos;
    }
}