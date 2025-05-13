package com.pluslatte.gt6gears.network;

import com.pluslatte.gt6gears.Gt6Gears;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Gt6Gears.MODID);
    
    public static void init() {
        int id = 0;
        
        // ジェットパックのジャンプキー状態を送信するパケット
        INSTANCE.registerMessage(
            PacketJetpackJump.Handler.class,
            PacketJetpackJump.class,
            id++,
            Side.SERVER
        );
        
        // Gravity Regulatorのトグル状態を送信するパケット
        INSTANCE.registerMessage(
            PacketGravityRegulatorToggle.Handler.class,
            PacketGravityRegulatorToggle.class,
            id++,
            Side.SERVER
        );
    }
}
