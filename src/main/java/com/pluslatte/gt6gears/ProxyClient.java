package com.pluslatte.gt6gears;

import com.pluslatte.gt6gears.event.KeyInputHandler;
import com.pluslatte.gt6gears.network.PacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregapi.api.Abstract_Mod;
import gregapi.api.Abstract_Proxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

public class ProxyClient extends Abstract_Proxy {
    
    @Override
    public void onProxyAfterPostInit(Abstract_Mod aMod, FMLPostInitializationEvent aEvent) {
        System.out.println("####################EVENT REG (CLIENT)####################");
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        
        // パケットハンドラーの初期化
        PacketHandler.init();
        
        // クライアント側のイベントハンドラーを登録
        KeyInputHandler keyHandler = new KeyInputHandler();
        FMLCommonHandler.instance().bus().register(keyHandler);
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if (
            (
                player.getEquipmentInSlot(3) == null ||
                player.getEquipmentInSlot(3).getItem() != Gt6Gears.itemGravityRegulator
            )
            && !player.capabilities.isCreativeMode
            && player.capabilities.allowFlying
        ) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
        }

        if (player.getEquipmentInSlot(1) == null) {
            player.stepHeight = 0.5F;
            return;
        }
        if (player.getEquipmentInSlot(1).getItem() == Gt6Gears.itemMechanicalBoots) {
            player.stepHeight = 1.0F;
        } else {
            player.stepHeight = 0.5F;
        }
    }

    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entity;

        if (player.getEquipmentInSlot(1) == null) {
            return;
        }
        if (player.getEquipmentInSlot(1).getItem() == Gt6Gears.itemMechanicalBoots) {
            player.motionY += 0.333F;
        }
    }
    
    public String getKeyBinding() {
        return "G";
    }
}
