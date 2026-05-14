package org.lihe.modulargolemsmassivestorageupgrade.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {
    public static final Capability<IGolemStorage> GOLEM_STORAGE =
            CapabilityManager.get(new CapabilityToken<>() {});
}