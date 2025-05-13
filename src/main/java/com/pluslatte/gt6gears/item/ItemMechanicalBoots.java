package com.pluslatte.gt6gears.item;

import com.pluslatte.gt6gears.Gt6Gears;
import gregapi.data.MT;
import gregapi.item.ItemArmorBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static gregapi.data.OP.*;

public class ItemMechanicalBoots extends ItemArmorBase {
    public ItemMechanicalBoots() {
        super(
                Gt6Gears.MODID,
                "gt6gears.mechanicalboots",
                "Mechanical Boots",
                "Assists walking.",
                "mechanical_boots",
                3,
                new int[] {2, 2, 2, 2},
                392,
                8,
                12,
                false,
                false,
                "G G",
                "C C",
                "SBS",
                'G', gearGtSmall.dat(MT.Fe),
                'C', plateCurved.dat(MT.Fe),
                'S', spring.dat(MT.Fe),
                'B', Items.iron_boots
            );
        setCreativeTab(Gt6Gears.CREATIVE_TAB);
        setMaxStackSize(1);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);

        if (player.onGround) {
                // 移動速度向上
                player.motionX *= 1.2;
                player.motionZ *= 1.2;
        }
    }
}
