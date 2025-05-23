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
    public ItemMechanicalBoots(boolean isTungstenSteel) {
        super(
                Gt6Gears.MODID,
                isTungstenSteel ? "gt6gears.mechanicalbootstungsteel" : "gt6gears.mechanicalboots",
                isTungstenSteel ? "Tungstensteel Reinforced Mechanical Boots" : "Mechanical Boots",
                "Assists walking.",
                isTungstenSteel ? "mechanical_boots_ts" : "mechanical_boots",
                3, // 足装備
                new int[] {0, 0, 0, 3}, // 足装備の防御値を3に（鉄のブーツ2より少し高い）
                isTungstenSteel ? 2340 : 234, // 耐久値を鉄のブーツ195の1.2倍（コスト分高く）タングステンスチール製の場合はその 10 倍に
                isTungstenSteel ? 16 : 12, // エンチャント性を鉄より少し高く
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

    public static void registerRecipeTungstenSteel() {
        // レシピを手動で登録
        gregapi.util.CR.shaped(
                gregapi.util.ST.make(Gt6Gears.itemMechanicalBootsTs, 1, 0),
                gregapi.util.CR.DEF_REV_NCC,
                "G G",
                "C C",
                "SBS",
                'G', gearGtSmall.dat(MT.TungstenSteel),
                'C', plateCurved.dat(MT.TungstenSteel),
                'S', spring.dat(MT.TungstenSteel),
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
