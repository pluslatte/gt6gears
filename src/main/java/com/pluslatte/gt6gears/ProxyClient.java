package com.pluslatte.gt6gears;

import com.pluslatte.gt6gears.event.KeyInputHandler;
import com.pluslatte.gt6gears.network.PacketHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregapi.api.Abstract_Mod;
import gregapi.api.Abstract_Proxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

public class ProxyClient extends Abstract_Proxy {
    
    // キーバインディングの定義
    public static KeyBinding keyGravityToggle;
    
    @Override
    public void onProxyAfterInit(Abstract_Mod aMod, FMLInitializationEvent aEvent) {
        // キーバインディングの初期化
        keyGravityToggle = new KeyBinding(
            "key.gt6gears.gravity_toggle.desc",
            Keyboard.KEY_G,
            "key.gt6gears.category"
        );
        
        // キーバインディングを登録
        ClientRegistry.registerKeyBinding(keyGravityToggle);
    }
    
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

        // Gravity Regulatorの飛行能力チェック（クライアントサイド）
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

        // Mechanical Bootsの処理
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
            player.motionY += 0.222F;
        }
    }

    public String getKeyBinding() {
        return keyGravityToggle != null ? 
            net.minecraft.client.settings.GameSettings.getKeyDisplayString(keyGravityToggle.getKeyCode()) : 
            "G";
    }
}
