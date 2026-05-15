package org.lihe.modulargolemsmassivestorageupgrade.menu;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lihe.modulargolemsmassivestorageupgrade.ModularGolemsMassiveStorageUpgrade;
import org.lihe.modulargolemsmassivestorageupgrade.api.ModCapabilities;

public class GolemStorageMenu extends AbstractContainerMenu {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModularGolemsMassiveStorageUpgrade.MODID);

    public static final RegistryObject<MenuType<GolemStorageMenu>> STORAGE_MENU = MENUS.register("storage_menu",
            () -> IForgeMenuType.create((id, inv, buf) -> {
                int entityId = buf.readInt();
                var entity = inv.player.level().getEntity(entityId);
                if (entity instanceof AbstractGolemEntity<?, ?> golem) {
                    return new GolemStorageMenu(id, inv, golem);
                }
                return null;
            }));

    public final AbstractGolemEntity<?, ?> golem;

    public GolemStorageMenu(int id, Inventory inv, AbstractGolemEntity<?, ?> golem) {
        super(STORAGE_MENU.get(), id);
        this.golem = golem;

        this.golem.getCapability(ModCapabilities.GOLEM_STORAGE).ifPresent(handler -> {
            IItemHandler inventory = handler.getInventory();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new SlotItemHandler(inventory, j + i * 9, 8 + j * 18, 18 + i * 18));
                }
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return golem.isAlive() && player.distanceToSqr(golem) < 64.0D;
    }
}