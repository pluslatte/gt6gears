package com.pluslatte.gt6gears;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import gregapi.api.Abstract_Mod;
import gregapi.api.Abstract_Proxy;
import net.minecraftforge.common.MinecraftForge;

public final class ProxyCommon extends Abstract_Proxy {
    // Insert your Serverside-only implementation of Stuff here
    @Override
    public void onProxyAfterPostInit(Abstract_Mod aMod, FMLPostInitializationEvent aEvent) {
        // サーバーサイドの初期化（現在は特に処理なし）
    }
}