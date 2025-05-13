package com.pluslatte.gt6gears.item.ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import com.pluslatte.gt6gears.Gt6Gears;
import com.pluslatte.gt6gears.network.PacketGravityRegulatorToggle;
import com.pluslatte.gt6gears.network.PacketHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashSet;
import java.util.Set;

public class GravityRegulatorAbilityHandler {
    
    // プレイヤーごとの飛行状態を管理するセット
    private static final Set<String> playersWithFlight = new HashSet<String>();
    private static final Set<String> playersWithRegulator = new HashSet<String>();
    
    // プレイヤーを識別するキー作成
    private static String playerKey(EntityPlayer player) {
        return player.getGameProfile().getName() + ":" + player.worldObj.isRemote;
    }
    
    // プレイヤーがGravity Regulatorを装備しているかチェック
    public static boolean playerHasGravityRegulator(EntityPlayer player) {
        ItemStack armor = player.getCurrentArmor(2); // 胸部装備
        return armor != null && armor.getItem() == Gt6Gears.itemGravityRegulator;
    }
    
    // Gravity RegulatorがONになっているかチェック
    public static boolean isRegulatorEnabled(ItemStack stack) {
        if (stack == null || stack.getItem() != Gt6Gears.itemGravityRegulator) {
            return false;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean("FlightEnabled");
    }
    
    @SubscribeEvent
    public void updatePlayerAbilityStatus(LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            String key = playerKey(player);
            
            boolean hasRegulator = playerHasGravityRegulator(player);
            boolean wasTrackingRegulator = playersWithRegulator.contains(key);
            boolean wasFlying = playersWithFlight.contains(key);
            
            if (hasRegulator) {
                ItemStack regulatorStack = player.getCurrentArmor(2);
                boolean isEnabled = isRegulatorEnabled(regulatorStack);
                
                if (!wasTrackingRegulator) {
                    // 新しく装備した場合
                    playersWithRegulator.add(key);
                    
                    // 最初は飛行オフで開始
                    if (player.capabilities.allowFlying && !player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                }
                
                if (isEnabled) {
                    // レギュレーターがONの場合
                    if (!wasFlying) {
                        // 飛行能力をONにする
                        player.capabilities.allowFlying = true;
                        player.sendPlayerAbilities();
                        playersWithFlight.add(key);
                    }
                } else {
                    // レギュレーターがOFFの場合
                    if (wasFlying) {
                        // 飛行能力をOFFにする
                        if (!player.capabilities.isCreativeMode) {
                            player.capabilities.allowFlying = false;
                            player.capabilities.isFlying = false;
                            player.sendPlayerAbilities();
                        }
                        playersWithFlight.remove(key);
                    }
                }
            } else {
                // レギュレーターを装備していない場合
                if (wasTrackingRegulator) {
                    // 装備を外した場合
                    if (!player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                    playersWithRegulator.remove(key);
                    playersWithFlight.remove(key);
                }
            }
        }
    }
    
    // プレイヤーが飛行能力を持っているかチェック（外部から呼び出し可能）
    public static boolean hasFlightAbility(EntityPlayer player) {
        String key = playerKey(player);
        return playersWithFlight.contains(key);
    }
    
    // 飛行状態をトグル（パケット受信時に呼び出される）
    public static void toggleFlight(EntityPlayer player) {
        ItemStack regulatorStack = player.getCurrentArmor(2);
        if (regulatorStack != null && regulatorStack.getItem() == Gt6Gears.itemGravityRegulator) {
            NBTTagCompound nbt = regulatorStack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                regulatorStack.setTagCompound(nbt);
            }
            
            boolean currentState = nbt.getBoolean("FlightEnabled");
            nbt.setBoolean("FlightEnabled", !currentState);
            
            // クライアント側の場合はサーバーに通知
            if (player.worldObj.isRemote) {
                PacketHandler.INSTANCE.sendToServer(new PacketGravityRegulatorToggle());
            }
        }
    }
    
    // エネルギー消費の処理（ItemGravityRegulatorから呼び出される）
    public static boolean handleEnergyConsumption(ItemStack stack, EntityPlayer player, long energy, 
                                                 long activationCost, long energyPerTick) {
        if (!hasFlightAbility(player)) {
            return false;
        }
        
        // エネルギーがない場合は飛行を無効化
        if (energy < energyPerTick) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null) {
                nbt.setBoolean("FlightEnabled", false);
            }
            return false;
        }
        
        return true;
    }
}
