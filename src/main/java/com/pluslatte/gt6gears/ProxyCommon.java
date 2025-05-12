package com.pluslatte.gt6gears;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregapi.api.Abstract_Proxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;

public class ProxyCommon extends Abstract_Proxy {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if (player.getEquipmentInSlot(1) == null) {
            return;
        }
        if (player.getEquipmentInSlot(1).getItem() == Gt6Gears.itemSpeedBoots) {
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
        if (player.getEquipmentInSlot(1).getItem() == Gt6Gears.itemSpeedBoots) {
            player.motionY += 0.333F;
        }
    }
}
