package org.lihe.modulargolemsmassivestorageupgrade.api;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public interface IGolemStorage {
    IItemHandler getInventory();

    default ItemStack insertItem(ItemStack stack, boolean simulate) {
        return ItemHandlerHelper.insertItemStacked(getInventory(), stack, simulate);
    }
}