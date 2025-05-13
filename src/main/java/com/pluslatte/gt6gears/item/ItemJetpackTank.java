package com.pluslatte.gt6gears.item;

import com.pluslatte.gt6gears.Gt6Gears;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.item.ItemArmorBase;
import gregapi.data.FL;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import java.util.List;

import static gregapi.data.OP.*;

public class ItemJetpackTank extends ItemArmorBase implements IFluidContainerItem {
    
    // タンク容量（mB単位）
    private static final int TANK_CAPACITY = 16000; // 16バケツ分（16000mB）

    public ItemJetpackTank() {
        super(
                Gt6Gears.MODID,
                "gt6gears.jetpacktank",
                "Liquid Fuel Jetpack",
                "Allows flying.",
                "jetpack_tank",
                1, // 胴体装備（chestplate）
                new int[] {0, 6, 0, 0}, // 防御値6（ダイヤモンド胸当ての8より低く、鉄の6と同等）
                336, // 耐久値を鉄240とダイヤ528の中間に設定
                12, // エンチャント性を鉄とダイヤの中間に
                15, // 防具強度を中間的な値に
                false,
                false
                // レシピを後で登録するため、ここでは指定しない
            );
        setCreativeTab(Gt6Gears.CREATIVE_TAB);
        setMaxStackSize(1);
    }
    
    public static void registerRecipe() {
        // レシピを手動で登録
        gregapi.util.CR.shaped(
            gregapi.util.ST.make(Gt6Gears.itemJetpackTank, 1, 0),
            gregapi.util.CR.DEF_REV_NCC,
            "P P",
            "TBT",
            "PTP",
            'P', plateCurved.dat(MT.Steel),
            'T', pipeMedium.dat(MT.Steel),
            'B', Items.leather_chestplate
        );
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        list.add(LH.Chat.WHITE + "Fill or empty this by using taps or funnels with some fluid storages.");
        FluidStack fluid = getFluid(stack);
        if (fluid != null && fluid.amount > 0) {
            list.add(LH.Chat.CYAN + "Contains: " + fluid.getLocalizedName());
            list.add(LH.Chat.WHITE + fluid.amount + " / " + getCapacity(stack) + " mB");
        } else {
            list.add(LH.Chat.RED + "Empty");
            list.add(LH.Chat.WHITE + "0 / " + getCapacity(stack) + " mB");
        }
    }
    
    // IFluidContainerItem実装
    @Override
    public FluidStack getFluid(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid")) {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
    }
    
    @Override
    public int getCapacity(ItemStack container) {
        return TANK_CAPACITY;
    }
    
    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }
        
        // 燃料のみを受け入れる
        if (!isUsableFuel(resource)) {
            return 0;
        }
        
        if (!doFill) {
            if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid")) {
                return Math.min(getCapacity(container), resource.amount);
            }
            
            FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
            
            if (stack == null) {
                return Math.min(getCapacity(container), resource.amount);
            }
            
            if (!stack.isFluidEqual(resource)) {
                return 0;
            }
            
            return Math.min(getCapacity(container) - stack.amount, resource.amount);
        }
        
        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }
        
        if (!container.stackTagCompound.hasKey("Fluid")) {
            NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());
            
            if (getCapacity(container) < resource.amount) {
                fluidTag.setInteger("Amount", getCapacity(container));
                container.stackTagCompound.setTag("Fluid", fluidTag);
                return getCapacity(container);
            }
            
            container.stackTagCompound.setTag("Fluid", fluidTag);
            return resource.amount;
        }
        
        NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
        FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);
        
        // 既に別の燃料が入っている場合は拒否
        if (!stack.isFluidEqual(resource)) {
            return 0;
        }
        
        int filled = getCapacity(container) - stack.amount;
        
        if (resource.amount < filled) {
            stack.amount += resource.amount;
            filled = resource.amount;
        } else {
            stack.amount = getCapacity(container);
        }
        
        container.stackTagCompound.setTag("Fluid", stack.writeToNBT(fluidTag));
        return filled;
    }
    
    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid")) {
            return null;
        }
        
        FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
        
        if (stack == null) {
            return null;
        }
        
        // ドレインする量を計算（最大値を超えないように）
        int drainAmount = Math.min(stack.amount, maxDrain);
        
        // 実際にドレインする場合のみNBTを更新
        if (doDrain && drainAmount > 0) {
            NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
            int remainingAmount = stack.amount - drainAmount;
            
            if (remainingAmount <= 0) {
                // 完全に空になった場合
                container.stackTagCompound.removeTag("Fluid");
                
                if (container.stackTagCompound.hasNoTags()) {
                    container.stackTagCompound = null;
                }
            } else {
                // まだ液体が残っている場合
                fluidTag.setInteger("Amount", remainingAmount);
                container.stackTagCompound.setTag("Fluid", fluidTag);
            }
        }
        
        // ドレインされる液体を返す（amountは実際にドレインされる量に設定）
        stack.amount = drainAmount;
        return stack;
    }
    
    /**
     * 指定された液体が使用可能な燃料かどうかを判定する
     * @param fluid 判定する液体
     * @return 使用可能な燃料の場合true
     */
    private boolean isUsableFuel(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return false;
        }
        
        // GregTech6の燃料定義をチェック
        return FL.Fuel.is(fluid) ||
                FL.Diesel.is(fluid) ||
                FL.Kerosine.is(fluid) ||
                FL.Petrol.is(fluid);
    }
    
    // ジェットパック関連の定数
    private static final int FUEL_CONSUMPTION_PER_TICK = 1; // 1ティックあたりの燃料消費量（mB）
    private static final double JETPACK_THRUST = 0.15; // ジェットパックの推進力 (適切な値に修正)
    private static final double MAX_VERTICAL_SPEED = 1.0; // 最大上昇速度
    
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        
        // プレイヤーがジャンプキーを押しているか確認
        boolean isJumping = player.getEntityData().getBoolean("JetpackJumping");
        
        // 燃料をチェック
        FluidStack fuel = getFluid(itemStack);
        if (fuel == null || fuel.amount <= 0) {
            return;
        }
        
        if (isJumping) {
            // 燃料があり、ジャンプキーが押されている場合
            
            if (fuel.amount >= FUEL_CONSUMPTION_PER_TICK) {
                if (!world.isRemote) {
                    // サーバー側でのみ燃料を消費
                    drain(itemStack, FUEL_CONSUMPTION_PER_TICK, true);
                }
                
                // 上方向への推進力を適用（クライアントとサーバー両方で実行）
                if (player.motionY < MAX_VERTICAL_SPEED) {
                    player.motionY += JETPACK_THRUST;
                    if (player.motionY > MAX_VERTICAL_SPEED) {
                        player.motionY = MAX_VERTICAL_SPEED;
                    }
                }
                
                // 重力の影響を軽減
                if (player.motionY < 0) {
                    player.motionY *= 0.9;
                }
                
                // 落下ダメージをリセット
                player.fallDistance = 0;
                
                if (!world.isRemote) {
                    // パーティクル効果を表示（サーバー側）
                    for (int i = 0; i < 3; i++) {
                        double offsetX = player.posX + (player.getRNG().nextDouble() - 0.5) * 0.5;
                        double offsetZ = player.posZ + (player.getRNG().nextDouble() - 0.5) * 0.5;
                        world.spawnParticle("smoke", offsetX, player.posY - 0.5, offsetZ, 0, -0.1, 0);
                        world.spawnParticle("flame", offsetX, player.posY - 0.5, offsetZ, 0, -0.05, 0);
                    }
                }
            }
        }
    }
}
