package com.pluslatte.gt6gears.recipe;

import gregapi.data.IL;
import gregapi.data.MT;
import gregapi.data.OD;
import gregapi.data.OP;
import static gregapi.data.CS.*;

/**
 * GregTech の Mud のレシピを追加するクラス
 */
public class RecipesMud {
    public static void registerRecipes() {
        // 粘土と砂の粉から Mud を 2個作成する非定型レシピ
        // Clay Ball + Sand Dust -> 2x Mud Ball
        gregapi.util.CR.shapeless(
                IL.Mud_Ball.get(2),
                gregapi.util.CR.DEF_NCC,
                new Object[] {
                        OD.itemClay,
                        OP.dust.dat(MT.Sand)
                });
    }
}
