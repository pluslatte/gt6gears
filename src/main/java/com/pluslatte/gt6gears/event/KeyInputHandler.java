package com.pluslatte.gt6gears.event;

import com.pluslatte.gt6gears.ProxyClient;
import com.pluslatte.gt6gears.item.ItemGravityRegulator;
import com.pluslatte.gt6gears.item.ItemJetpackTank;
import com.pluslatte.gt6gears.network.PacketHandler;
import com.pluslatte.gt6gears.network.PacketJetpackJump;
import com.pluslatte.gt6gears.network.PacketGravityRegulatorToggle;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyInputHandler {
    
    private boolean wasJumping = false;
    private boolean wasToggleKeyPressed = false;
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        
        EntityPlayer player = mc.thePlayer;
        
        // 胴体装備をチェック
        ItemStack chestplate = player.inventory.armorItemInSlot(2);
        
        // Gravity Regulatorのトグル処理
        if (chestplate != null && chestplate.getItem() instanceof ItemGravityRegulator) {
            handleGravityRegulator(player);
        }
        
        // ジェットパックの処理
        if (chestplate != null && chestplate.getItem() instanceof ItemJetpackTank) {
            handleJetpack(player);
        } else {
            // ジェットパックを装備していない場合、ジャンプ状態をリセット
            if (player.getEntityData().getBoolean("JetpackJumping")) {
                player.getEntityData().setBoolean("JetpackJumping", false);
                PacketHandler.INSTANCE.sendToServer(new PacketJetpackJump(false));
            }
        }
    }
    
    private void handleJetpack(EntityPlayer player) {
        // ジャンプキーの状態を取得
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
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
    
    private void handleGravityRegulator(EntityPlayer player) {
        // 登録されたキーバインディングの状態をチェック
        boolean isToggleKeyPressed = ProxyClient.keyGravityToggle != null && 
                                     ProxyClient.keyGravityToggle.getIsKeyPressed();
        
        // キーが新しく押された瞬間のみトグル
        if (isToggleKeyPressed && !wasToggleKeyPressed) {
            // サーバーにトグルリクエストを送信
            PacketHandler.INSTANCE.sendToServer(new PacketGravityRegulatorToggle());
        }
        
        wasToggleKeyPressed = isToggleKeyPressed;
    }
}
