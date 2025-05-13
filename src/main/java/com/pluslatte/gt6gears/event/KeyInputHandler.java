package com.pluslatte.gt6gears.event;

import com.pluslatte.gt6gears.item.ItemJetpackTank;
import com.pluslatte.gt6gears.network.PacketHandler;
import com.pluslatte.gt6gears.network.PacketJetpackJump;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class KeyInputHandler {
    
    private boolean wasJumping = false;
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        
        EntityPlayer player = mc.thePlayer;
        
        // 胴体装備をチェック
        ItemStack chestplate = player.inventory.armorItemInSlot(2);
        if (chestplate == null || !(chestplate.getItem() instanceof ItemJetpackTank)) {
            // ジェットパックを装備していない場合、ジャンプ状態をリセット
            if (player.getEntityData().getBoolean("JetpackJumping")) {
                player.getEntityData().setBoolean("JetpackJumping", false);
                PacketHandler.INSTANCE.sendToServer(new PacketJetpackJump(false));
            }
            return;
        }
        
        // ジャンプキーの状態を取得
        GameSettings settings = mc.gameSettings;
        boolean isJumping = settings.keyBindJump.getIsKeyPressed();
        
        // 状態が変化した場合のみパケットを送信
        if (isJumping != wasJumping) {
            wasJumping = isJumping;
            // クライアント側でもデータを設定
            player.getEntityData().setBoolean("JetpackJumping", isJumping);
            // サーバーに通知
            PacketHandler.INSTANCE.sendToServer(new PacketJetpackJump(isJumping));
        }
    }
}
