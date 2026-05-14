package org.lihe.modulargolemsmassivestorageupgrade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import org.lihe.modulargolemsmassivestorageupgrade.api.ModCapabilities;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("checkgolemstorage")
                .requires(source -> source.hasPermission(2))
                .executes(ModCommands::executeCheck));

        dispatcher.register(Commands.literal("transfertogolem")
                .requires(source -> source.hasPermission(2))
                .executes(ModCommands::executeTransfer));
    }

    private static int executeCheck(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            ServerPlayer player = source.getPlayerOrException();
            Entity lookedAt = getLookedAtEntity(player, 5.0D);

            if (lookedAt instanceof AbstractGolemEntity<?, ?> golem) {
                return golem.getCapability(ModCapabilities.GOLEM_STORAGE).map(storage -> {
                    IItemHandler handler = storage.getInventory();
                    int count = 0;
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (!handler.getStackInSlot(i).isEmpty()) count++;
                    }
                    final int finalCount = count;
                    source.sendSuccess(() -> Component.translatable("commands.modulargolemsmassivestorageupgrade.check.success", finalCount), false);
                    return count;
                }).orElseGet(() -> {
                    source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.common.no_data"));
                    return 0;
                });
            }
            source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.common.no_target"));
            return 0;
        } catch (Exception e) {
            source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.common.error", e.getMessage()));
            return 0;
        }
    }

    private static int executeTransfer(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            ServerPlayer player = source.getPlayerOrException();
            ItemStack stackInHand = player.getMainHandItem();

            if (stackInHand.isEmpty()) {
                source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.transfer.empty_hand"));
                return 0;
            }

            Entity lookedAt = getLookedAtEntity(player, 5.0D);
            if (lookedAt instanceof AbstractGolemEntity<?, ?> golem) {
                return golem.getCapability(ModCapabilities.GOLEM_STORAGE).map(storage -> {
                    ItemStack remainder = storage.insertItem(stackInHand.copy(), false);
                    int transferredCount = stackInHand.getCount() - remainder.getCount();

                    if (transferredCount > 0) {
                        player.setItemInHand(player.getUsedItemHand(), remainder);
                        source.sendSuccess(() -> Component.translatable("commands.modulargolemsmassivestorageupgrade.transfer.success", transferredCount), false);
                        return transferredCount;
                    } else {
                        source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.transfer.full"));
                        return 0;
                    }
                }).orElseGet(() -> {
                    source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.common.no_data"));
                    return 0;
                });
            }
            source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.common.no_target"));
            return 0;
        } catch (Exception e) {
            source.sendFailure(Component.translatable("commands.modulargolemsmassivestorageupgrade.common.error", e.getMessage()));
            return 0;
        }
    }

    private static Entity getLookedAtEntity(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 endVec = eyePos.add(viewVec.scale(range));
        AABB box = player.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0D);
        List<Entity> entities = player.level().getEntities(player, box, e -> (e instanceof AbstractGolemEntity) && e.isPickable());
        Entity closest = null;
        double minDir = range;
        for (Entity entity : entities) {
            float collisionSize = entity.getPickRadius();
            AABB entityBox = entity.getBoundingBox().inflate(collisionSize);
            Optional<Vec3> hit = entityBox.clip(eyePos, endVec);
            if (hit.isPresent()) {
                double dist = eyePos.distanceTo(hit.get());
                if (dist < minDir) {
                    closest = entity;
                    minDir = dist;
                }
            }
        }
        return closest;
    }
}