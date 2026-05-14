package org.lihe.modulargolemsmassivestorageupgrade.network;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.lihe.modulargolemsmassivestorageupgrade.menu.GolemStorageMenu;

import java.util.UUID;
import java.util.function.Supplier;

public class OpenStoragePacket {
    private final UUID uuid;

    public OpenStoragePacket(UUID uuid) { this.uuid = uuid; }
    public OpenStoragePacket(FriendlyByteBuf buf) { this.uuid = buf.readUUID(); }
    public void toBytes(FriendlyByteBuf buf) { buf.writeUUID(uuid); }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            var entity = player.serverLevel().getEntity(uuid);
            if (entity instanceof AbstractGolemEntity<?, ?> golem) {
                NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (id, inv, p) -> new GolemStorageMenu(id, inv, golem),
                        golem.getDisplayName()
                ), buf -> buf.writeInt(golem.getId()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}