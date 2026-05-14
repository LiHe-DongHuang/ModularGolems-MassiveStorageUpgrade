package org.lihe.modulargolemsmassivestorageupgrade.mixin;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.lihe.modulargolemsmassivestorageupgrade.Config;
import org.lihe.modulargolemsmassivestorageupgrade.api.ModCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class AbstractGolemEntityMixin {

    @Inject(method = "dropCustomDeathLoot", at = @At("HEAD"), cancellable = true)
    private void modulargolems_storage$onDropLoot(DamageSource source, int looting, boolean hitByPlayer, CallbackInfo ci) {
        if (Config.SPAWN_DEATH_CHEST == null || !Config.SPAWN_DEATH_CHEST.get()) {
            return;
        }
        if (!((Object) this instanceof AbstractGolemEntity<?, ?> golem)) {
            return;
        }

        Level level = golem.level();
        if (!level.isClientSide) {
            List<ItemStack> allDrops = new ArrayList<>();
            golem.addItemsToList(allDrops);

            golem.getCapability(ModCapabilities.GOLEM_STORAGE).ifPresent(storage -> {
                IItemHandler inventory = storage.getInventory();
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        allDrops.add(stack.copy());
                    }
                }
            });

            if (!allDrops.isEmpty()) {
                BlockPos pos = modulargolems_storage$findSafePos(level, golem.blockPosition());

                level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
                BlockEntity be = level.getBlockEntity(pos);

                if (be instanceof ChestBlockEntity chest) {
                    int slot = 0;
                    for (ItemStack stack : allDrops) {
                        if (slot < chest.getContainerSize()) {
                            chest.setItem(slot++, stack);
                        } else {
                            golem.spawnAtLocation(stack);
                        }
                    }
                } else {
                    for (ItemStack stack : allDrops) {
                        golem.spawnAtLocation(stack);
                    }
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private BlockPos modulargolems_storage$findSafePos(Level level, BlockPos startPos) {
        if (level.getBlockState(startPos).isAir() || level.getBlockState(startPos).canBeReplaced()) {
            return startPos;
        }
        for (int i = 1; i <= 2; i++) {
            if (level.getBlockState(startPos.above(i)).isAir()) return startPos.above(i);
        }
        return startPos;
    }
}