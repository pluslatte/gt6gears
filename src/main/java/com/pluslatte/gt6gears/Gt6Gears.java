package com.pluslatte.gt6gears;

import com.pluslatte.gt6gears.item.ItemSpeedBoots;
import cpw.mods.fml.common.event.*;
import gregapi.api.Abstract_Mod;
import gregapi.api.Abstract_Proxy;
import gregapi.code.ModData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid = Gt6Gears.MODID, version = Gt6Gears.VERSION)
public final class Gt6Gears extends Abstract_Mod
{
    public static final String MODID = "gt6gears";
    public static final String MODNAME = "gt6 gears";
    public static final String VERSION = "1.0";
    public static ModData MOD_DATA = new ModData(MODID, MODNAME);

    public static CreativeTabs CREATIVE_TAB;

    public static ItemSpeedBoots itemSpeedBoots;

    @cpw.mods.fml.common.SidedProxy(modId = MODID, clientSide = "com.pluslatte.gt6gears.ProxyClient", serverSide = "com.pluslatte.gt6gears.ProxyServer")
    public static ProxyCommon PROXY;

    @Override
    public String getModID() {
        return MODID;
    }

    @Override
    public String getModName() {
        return MODNAME;
    }

    @Override
    public String getModNameForLog() {
        return MODNAME;
    }

    @Override
    public Abstract_Proxy getProxy() {
        return PROXY;
    }

    // Do not change these 7 Functions. Just keep them this way.
    @cpw.mods.fml.common.Mod.EventHandler public final void onPreLoad           (cpw.mods.fml.common.event.FMLPreInitializationEvent    aEvent) {onModPreInit(aEvent);}
    @cpw.mods.fml.common.Mod.EventHandler public final void onLoad              (cpw.mods.fml.common.event.FMLInitializationEvent       aEvent) {onModInit(aEvent);}
    @cpw.mods.fml.common.Mod.EventHandler public final void onPostLoad          (cpw.mods.fml.common.event.FMLPostInitializationEvent   aEvent) {onModPostInit(aEvent);}
    @cpw.mods.fml.common.Mod.EventHandler public final void onServerStarting    (cpw.mods.fml.common.event.FMLServerStartingEvent       aEvent) {onModServerStarting(aEvent);}
    @cpw.mods.fml.common.Mod.EventHandler public final void onServerStarted     (cpw.mods.fml.common.event.FMLServerStartedEvent        aEvent) {onModServerStarted(aEvent);}
    @cpw.mods.fml.common.Mod.EventHandler public final void onServerStopping    (cpw.mods.fml.common.event.FMLServerStoppingEvent       aEvent) {onModServerStopping(aEvent);}
    @cpw.mods.fml.common.Mod.EventHandler public final void onServerStopped     (cpw.mods.fml.common.event.FMLServerStoppedEvent        aEvent) {onModServerStopped(aEvent);}

    @Override
    public void onModPreInit2(FMLPreInitializationEvent aEvent) {
        CREATIVE_TAB = new CreativeTabs(MODID) {
            @Override
            public ItemStack getIconItemStack() {
                return new ItemStack(itemSpeedBoots, 1, 0);
            }

            @Override
            public Item getTabIconItem() {
                return itemSpeedBoots;
            }
        };

        itemSpeedBoots = new ItemSpeedBoots();
    }

    @Override
    public void onModInit2(FMLInitializationEvent aEvent) {
        // some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
    }

    @Override
    public void onModPostInit2(FMLPostInitializationEvent aEvent) {

    }

    @Override
    public void onModServerStarting2(FMLServerStartingEvent aEvent) {

    }

    @Override
    public void onModServerStarted2(FMLServerStartedEvent aEvent) {

    }

    @Override
    public void onModServerStopping2(FMLServerStoppingEvent aEvent) {

    }

    @Override
    public void onModServerStopped2(FMLServerStoppedEvent aEvent) {

    }
}
