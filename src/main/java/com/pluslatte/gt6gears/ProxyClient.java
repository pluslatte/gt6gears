package com.pluslatte.gt6gears;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import gregapi.api.Abstract_Mod;
import gregapi.api.Abstract_Proxy;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends Abstract_Proxy {

    @Override
    public void onProxyAfterPostInit(Abstract_Mod aMod, FMLPostInitializationEvent aEvent) {
        // クライアントサイドの初期化（現在は特に処理なし）
    }
}
