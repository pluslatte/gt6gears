package com.pluslatte.gt6gears.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketJetpackJump implements IMessage {
    
    private boolean isJumping;
    
    public PacketJetpackJump() {}
    
    public PacketJetpackJump(boolean isJumping) {
        this.isJumping = isJumping;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.isJumping = buf.readBoolean();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isJumping);
    }
    
    public static class Handler implements IMessageHandler<PacketJetpackJump, IMessage> {
        
        @Override
        public IMessage onMessage(PacketJetpackJump message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            
            // プレイヤーのNBTデータにジャンプ状態を保存
            player.getEntityData().setBoolean("JetpackJumping", message.isJumping);
            
            return null;
        }
    }
}
