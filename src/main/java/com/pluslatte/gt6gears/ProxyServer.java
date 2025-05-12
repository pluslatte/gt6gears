package com.pluslatte.gt6gears;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregapi.api.Abstract_Proxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;


public final class ProxyServer extends Abstract_Proxy {
    // Insert your Serverside-only implementation of Stuff here
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if (player.getEquipmentInSlot(4) == null) {
            return;
        }
        if (player.getEquipmentInSlot(4).getItem() == Gt6Gears.itemSpeedBoots) {
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

        if (player.getEquipmentInSlot(4) == null) {
            return;
        }
        if (player.getEquipmentInSlot(4).getItem() == Gt6Gears.itemSpeedBoots) {
            player.motionY += 0.333F;
        }
    }
}