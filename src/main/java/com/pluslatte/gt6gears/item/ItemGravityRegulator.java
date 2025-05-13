package com.pluslatte.gt6gears.item;

import com.pluslatte.gt6gears.Gt6Gears;
import com.pluslatte.gt6gears.ProxyClient;
import com.pluslatte.gt6gears.ProxyCommon;
import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.item.IItemEnergy;
import gregapi.item.ItemArmorBase;
import gregapi.oredict.OreDictItemData;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;

public class ItemGravityRegulator extends ItemArmorBase implements IItemEnergy {
    
    // エネルギー関連の定数
    private static final long ENERGY_CAPACITY = 1000000L; // 最大容量: 1,000,000 EU
    private static final long ENERGY_SIZE_IN = 128L; // 入力サイズ: 128 EU/t
    private static final long ENERGY_SIZE_OUT = 128L; // 出力サイズ: 128 EU/t
    private static final long ACTIVATION_COST = 10000L; // 起動コスト: 10,000 EU
    private static final long ENERGY_PER_TICK = 50L; // 維持コスト: 50 EU/tick
    
    public ItemGravityRegulator() {
        super(
                Gt6Gears.MODID,
                "gt6gears.gravityregulator",
                "Gravity Regulator",
                "Allows you to fly. Powered by EU.",
                "gravity_regulator",
                1, // 胴体装備（chestplate）
                new int[] {3, 6, 5, 2}, // 防御値（頭、胴、脚、足）
                392, // 耐久値
                15, // エンチャント性
                20, // 防具強度
                false,
                false,
                "CPC",
                "FBF",
                "CUC",
                'C', plateGemTiny.dat(MT.NetherStar), // ネザースターの小プレート
                'P', plateCurved.dat(MT.Os), // Osmiumプレート
                'B', circuit.dat(MT.Ti), // Tiの回路（高級回路の代替）
                'F', ring.dat(MT.TungstenSteel), // タングステン鋼のリング
                'U', plateGem.dat(MT.Diamond) // ダイヤモンドプレート
            );
        setCreativeTab(Gt6Gears.CREATIVE_TAB);
        setMaxStackSize(1);
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        
        // エネルギー情報を表示
        long energy = getEnergyStored(TD.Energy.EU, stack);
        list.add(LH.Chat.WHITE + "Energy: " + LH.Chat.GREEN + energy + " / " + ENERGY_CAPACITY + " EU");
        
        // 飛行状態を表示
        NBTTagCompound nbt = stack.getTagCompound();
        boolean isEnabled = nbt != null && nbt.getBoolean("FlightEnabled");
        list.add(LH.Chat.WHITE + "Flight: " + (isEnabled ? LH.Chat.GREEN + "Enabled" : LH.Chat.RED + "Disabled"));
        
        // 使用方法
        String keyBind = "G"; // デフォルト値
        if (Gt6Gears.PROXY instanceof ProxyClient) {
            keyBind = ((ProxyClient)Gt6Gears.PROXY).getKeyBinding();
        } else if (Gt6Gears.PROXY instanceof ProxyCommon) {
            keyBind = ((ProxyCommon)Gt6Gears.PROXY).getKeyBinding();
        }
        list.add(LH.Chat.CYAN + "Press " + LH.Chat.YELLOW + keyBind +
                 LH.Chat.CYAN + " to toggle flight mode");
    }
    
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        
        if (world.isRemote) return;
        
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        
        boolean isEnabled = nbt.getBoolean("FlightEnabled");
        
        if (isEnabled) {
            // 飛行が有効な場合
            long energy = getEnergyStored(TD.Energy.EU, stack);
            
            if (energy >= ENERGY_PER_TICK) {
                // エネルギーがある場合
                if (!player.capabilities.allowFlying) {
                    // 飛行を有効化（起動コストを消費）
                    if (energy >= ACTIVATION_COST) {
                        setEnergyStored(TD.Energy.EU, stack, energy - ACTIVATION_COST);
                        player.capabilities.allowFlying = true;
                        player.sendPlayerAbilities();
                    } else {
                        // 起動コストが足りない場合は無効化
                        nbt.setBoolean("FlightEnabled", false);
                    }
                } else {
                    // 維持コストを消費
                    setEnergyStored(TD.Energy.EU, stack, energy - ENERGY_PER_TICK);
                }
            } else {
                // エネルギーがない場合は飛行を無効化
                if (player.capabilities.allowFlying && !player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                    nbt.setBoolean("FlightEnabled", false);
                }
            }
        } else {
            // 飛行が無効な場合
            if (player.capabilities.allowFlying && !player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                player.sendPlayerAbilities();
            }
        }
    }
    
    // 装備を外したときの処理（ItemArmorBaseには存在しない可能性があるのでコメントアウト）
    /*
    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && !player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }
    */
    
    // アイテムが削除されたりドロップされたときにも飛行能力をリセット
    @Override
    public boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem entityItem) {
        // ドロップされたアイテムの所有者を特定するのは難しいので、別の方法で処理する
        return super.onEntityItemUpdate(entityItem);
    }
    
    // IItemEnergyインターフェース実装
    @Override
    public boolean isEnergyType(TagData aEnergyType, ItemStack aStack, boolean aEmitting) {
        return aEnergyType == TD.Energy.EU;
    }
    
    @Override
    public Collection<TagData> getEnergyTypes(ItemStack aStack) {
        return Collections.singleton(TD.Energy.EU);
    }
    
    @Override
    public long doEnergyInjection(TagData aEnergyType, ItemStack aStack, long aSize, long aAmount, IInventory aInventory, World aWorld, int aX, int aY, int aZ, boolean aDoInject) {
        if (aEnergyType != TD.Energy.EU || aAmount < 1) return 0;
        long stored = getEnergyStored(aEnergyType, aStack);
        long toStore = Math.min(aAmount * aSize, ENERGY_CAPACITY - stored);
        if (aDoInject && toStore > 0) {
            setEnergyStored(aEnergyType, aStack, stored + toStore);
        }
        return toStore / aSize;
    }
    
    @Override
    public boolean canEnergyInjection(TagData aEnergyType, ItemStack aStack, long aSize) {
        return aEnergyType == TD.Energy.EU && aSize <= ENERGY_SIZE_IN;
    }
    
    @Override
    public long doEnergyExtraction(TagData aEnergyType, ItemStack aStack, long aSize, long aAmount, IInventory aInventory, World aWorld, int aX, int aY, int aZ, boolean aDoExtract) {
        return 0; // 電力の出力はしない
    }
    
    @Override
    public boolean canEnergyExtraction(TagData aEnergyType, ItemStack aStack, long aSize) {
        return false; // 電力の出力はしない
    }
    
    @Override
    public boolean useEnergy(TagData aEnergyType, ItemStack aStack, long aEnergyAmount, EntityLivingBase aPlayer, IInventory aInventory, World aWorld, int aX, int aY, int aZ, boolean aDoUse) {
        if (aEnergyType != TD.Energy.EU) return F;
        long stored = getEnergyStored(aEnergyType, aStack);
        if (stored >= aEnergyAmount) {
            if (aDoUse) {
                setEnergyStored(aEnergyType, aStack, stored - aEnergyAmount);
            }
            return T;
        }
        return F;
    }
    
    @Override
    public ItemStack setEnergyStored(TagData aEnergyType, ItemStack aStack, long aAmount) {
        if (aEnergyType != TD.Energy.EU) return aStack;
        NBTTagCompound tNBT = aStack.getTagCompound();
        if (tNBT == null) tNBT = UT.NBT.make();
        UT.NBT.setNumber(tNBT, NBT_ENERGY, aAmount);
        UT.NBT.set(aStack, tNBT);
        return aStack;
    }
    
    @Override
    public long getEnergyStored(TagData aEnergyType, ItemStack aStack) {
        if (aEnergyType != TD.Energy.EU) return 0;
        NBTTagCompound tNBT = aStack.getTagCompound();
        return tNBT == null ? 0 : tNBT.getLong(NBT_ENERGY);
    }
    
    @Override
    public long getEnergyCapacity(TagData aEnergyType, ItemStack aStack) {
        return aEnergyType == TD.Energy.EU ? ENERGY_CAPACITY : 0;
    }
    
    @Override
    public long getEnergySizeInputMin(TagData aEnergyType, ItemStack aStack) {
        return aEnergyType == TD.Energy.EU ? 1 : 0;
    }
    
    @Override
    public long getEnergySizeOutputMin(TagData aEnergyType, ItemStack aStack) {
        return 0; // 出力しない
    }
    
    @Override
    public long getEnergySizeInputRecommended(TagData aEnergyType, ItemStack aStack) {
        return aEnergyType == TD.Energy.EU ? ENERGY_SIZE_IN : 0;
    }
    
    @Override
    public long getEnergySizeOutputRecommended(TagData aEnergyType, ItemStack aStack) {
        return 0; // 出力しない
    }
    
    @Override
    public long getEnergySizeInputMax(TagData aEnergyType, ItemStack aStack) {
        return aEnergyType == TD.Energy.EU ? ENERGY_SIZE_IN * 2 : 0;
    }
    
    @Override
    public long getEnergySizeOutputMax(TagData aEnergyType, ItemStack aStack) {
        return 0; // 出力しない
    }
    
    @Override
    public int getItemStackLimit(ItemStack aStack) {
        return 1;
    }
}
