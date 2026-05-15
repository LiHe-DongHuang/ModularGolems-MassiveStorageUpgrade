package org.lihe.modulargolemsmassivestorageupgrade;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabToken;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lihe.modulargolemsmassivestorageupgrade.capability.GolemStorageProvider;
import org.lihe.modulargolemsmassivestorageupgrade.client.GolemStorageScreen;
import org.lihe.modulargolemsmassivestorageupgrade.client.GolemStorageTab;
import org.lihe.modulargolemsmassivestorageupgrade.menu.GolemStorageMenu;
import org.lihe.modulargolemsmassivestorageupgrade.network.ModNetwork;

@Mod(ModularGolemsMassiveStorageUpgrade.MODID)
public class ModularGolemsMassiveStorageUpgrade {
    public static final String MODID = "modulargolemsmassivestorageupgrade";

    public static GolemTabToken<EquipmentGroup, GolemStorageTab> STORAGE_TAB;

    public ModularGolemsMassiveStorageUpgrade() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        GolemStorageMenu.MENUS.register(modBus);
        ModNetwork.register();

        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::onAttachCapabilities);

        modBus.addListener(this::clientSetup);
    }

    private void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof AbstractGolemEntity<?, ?> golem) {
            event.addCapability(new ResourceLocation(MODID, "storage"), new GolemStorageProvider(golem));
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(GolemStorageMenu.STORAGE_MENU.get(), GolemStorageScreen::new);
            STORAGE_TAB = new GolemTabToken<>(
                    GolemStorageTab::new,
                    () -> Items.CHEST,
                    Component.translatable("gui.modulargolemsmassivestorageupgrade.storage_tab")
            );
            GolemTabRegistry.LIST_EQUIPMENT.add(STORAGE_TAB);
        });
    }
}