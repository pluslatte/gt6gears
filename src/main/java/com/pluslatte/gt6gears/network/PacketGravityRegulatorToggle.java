package com.pluslatte.gt6gears.network;

import com.pluslatte.gt6gears.item.ItemGravityRegulator;
import com.pluslatte.gt6gears.item.ability.GravityRegulatorAbilityHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PacketGravityRegulatorToggle implements IMessage {
    
    public PacketGravityRegulatorToggle() {}
    
    @Override
    public void fromBytes(ByteBuf buf) {
        // このパケットではデータの送信は必要ない
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        // このパケットではデータの送信は必要ない
    }
    
    public static class Handler implements IMessageHandler<PacketGravityRegulatorToggle, IMessage> {
        @Override
        public IMessage onMessage(PacketGravityRegulatorToggle message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            
            // Minecraft 1.7.10では直接処理する
            // AbilityHandlerを使用してトグル処理
            GravityRegulatorAbilityHandler.toggleFlight(player);
            
            return null;
        }
    }
}
