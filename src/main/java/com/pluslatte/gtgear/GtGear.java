package com.pluslatte.gtgear;

import gregapi.api.Abstract_Mod;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = GtGear.MODID, version = GtGear.VERSION)
public final class GtGear extends Abstract_Mod
{
    public static final String MODID = "gtgear";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }
}
