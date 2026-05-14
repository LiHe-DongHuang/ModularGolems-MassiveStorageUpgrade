package org.lihe.modulargolemsmassivestorageupgrade;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ModularGolemsMassiveStorageUpgrade.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue SPAWN_DEATH_CHEST;

    static {
        BUILDER.push("General Settings");
        SPAWN_DEATH_CHEST = BUILDER
                .comment("Whether to spawn a chest with items when a golem dies")
                .translation("config.modulargolemsmassivestorageupgrade.spawn_death_chest")
                .define("spawnDeathChest", true);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}