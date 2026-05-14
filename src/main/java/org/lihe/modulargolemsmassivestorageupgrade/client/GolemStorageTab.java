package org.lihe.modulargolemsmassivestorageupgrade.client;

import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabBase;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lihe.modulargolemsmassivestorageupgrade.network.ModNetwork;
import org.lihe.modulargolemsmassivestorageupgrade.network.OpenStoragePacket;

public class GolemStorageTab extends GolemTabBase<EquipmentGroup, GolemStorageTab> {

    public GolemStorageTab(int index, GolemTabToken<EquipmentGroup, GolemStorageTab> token, GolemTabManager<EquipmentGroup> manager, ItemStack stack, Component title) {
        super(index, token, manager, new ItemStack(Items.CHEST), title);
    }

    @Override
    public void onTabClicked() {
        ModNetwork.CHANNEL.sendToServer(new OpenStoragePacket(manager.token.golem.getUUID()));
    }
}