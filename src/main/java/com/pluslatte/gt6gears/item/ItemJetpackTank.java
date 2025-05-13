package com.pluslatte.gt6gears.item;

import com.pluslatte.gt6gears.Gt6Gears;
import gregapi.data.MT;
import gregapi.item.ItemArmorBase;
import gregapi.data.FL;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import java.util.List;

import static gregapi.data.OP.*;

public class ItemJetpackTank extends ItemArmorBase implements IFluidContainerItem {
    
    // タンク容量（mB単位）
    private static final int TANK_CAPACITY = 16000; // 16バケツ分
    
    public ItemJetpackTank() {
        super(
                Gt6Gears.MODID,
                "gt6gears.jetpacktank",
                "Liquid Fuel Jetpack",
                "Jetpack with a liquid fuel storage.",
                "jetpack_tank",
                1, // 胴体装備（chestplate）
                new int[] {3, 6, 5, 2}, // 防御値（頭、胴、脚、足）
                392, // 耐久値
                10, // エンチャント性
                15, // 防具強度
                false,
                false,
                "P P",
                "TBT",
                "PTP",
                'P', plateCurved.dat(MT.Al),
                'T', pipeMedium.dat(MT.Ti),
                'B', Items.iron_chestplate
            );
        setCreativeTab(Gt6Gears.CREATIVE_TAB);
        setMaxStackSize(1);
    }
    
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        // ジェットパック機能は後で実装予定
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        
        FluidStack fluid = getFluid(stack);
        if (fluid != null && fluid.amount > 0) {
            list.add(EnumChatFormatting.AQUA + "Contains: " + fluid.getLocalizedName());
            list.add(EnumChatFormatting.WHITE + "" + fluid.amount + " / " + getCapacity(stack) + " mB");
        } else {
            list.add(EnumChatFormatting.RED + "Empty");
            list.add(EnumChatFormatting.WHITE + "0 / " + getCapacity(stack) + " mB");
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
        
        stack.amount = Math.min(stack.amount, maxDrain);
        
        if (doDrain) {
            if (maxDrain >= stack.amount) {
                container.stackTagCompound.removeTag("Fluid");
                
                if (container.stackTagCompound.hasNoTags()) {
                    container.stackTagCompound = null;
                }
                
                return stack;
            }
            
            NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
            fluidTag.setInteger("Amount", fluidTag.getInteger("Amount") - stack.amount);
            container.stackTagCompound.setTag("Fluid", fluidTag);
        }
        
        return stack;
    }
}
