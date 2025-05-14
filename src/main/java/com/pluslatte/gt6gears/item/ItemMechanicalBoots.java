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
                3, // 足装備
                new int[] {0, 0, 0, 3}, // 足装備の防御値を3に（鉄のブーツ2より少し高い）
                234, // 耐久値を鉄のブーツ195の1.2倍（コスト分高く）
                12, // エンチャント性を鉄より少し高く
                10, // 防具強度
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
            gregapi.util.ST.make(Gt6Gears.itemMechanicalBoots, 1, 0),
            gregapi.util.CR.DEF_REV_NCC,
            "G G",
            "C C",
            "SBS",
            'G', gearGtSmall.dat(MT.Fe),
            'C', plateCurved.dat(MT.Fe),
            'S', spring.dat(MT.Fe),
            'B', Items.iron_boots
        );
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
