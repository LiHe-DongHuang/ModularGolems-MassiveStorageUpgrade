package org.lihe.modulargolemsmassivestorageupgrade.client;

import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.tabs.GolemTabManager;
import dev.xkmc.modulargolems.content.menu.tabs.ITabScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lihe.modulargolemsmassivestorageupgrade.ModularGolemsMassiveStorageUpgrade;
import org.lihe.modulargolemsmassivestorageupgrade.menu.GolemStorageMenu;

public class GolemStorageScreen extends AbstractContainerScreen<GolemStorageMenu> implements ITabScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public GolemStorageScreen(GolemStorageMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        new GolemTabManager<>(this, new EquipmentGroup(menu.golem))
                .init(this::addRenderableWidget, ModularGolemsMassiveStorageUpgrade.STORAGE_TAB);
    }

    @Override
    public int screenWidth() {
        return imageWidth;
    }

    @Override
    public int screenHeight() {
        return imageHeight;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        gui.blit(TEXTURE, x, y, 0, 0, imageWidth, 71);
        gui.blit(TEXTURE, x, y + 71, 0, 126, imageWidth, 95);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        renderTooltip(gui, mouseX, mouseY);
    }
}