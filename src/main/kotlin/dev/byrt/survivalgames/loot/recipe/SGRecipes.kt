package dev.byrt.survivalgames.loot.recipe

import dev.byrt.survivalgames.loot.SGLoot
import dev.byrt.survivalgames.loot.items.SGItems
import dev.byrt.survivalgames.plugin
import dev.byrt.survivalgames.util.Keys
import org.bukkit.inventory.ShapedRecipe

object SGRecipes {
    val recipes = listOf(
        ShapedRecipe(Keys.DIAMOND_SWORD_RECIPE, SGLoot.getItem(SGItems.DIAMOND_SWORD)).apply {
            shape(
                "D",
                "D",
                "S"
            )
            setIngredient('D', SGLoot.getItem(SGItems.DIAMOND))
            setIngredient('S', SGLoot.getItem(SGItems.STICK))
        },
        ShapedRecipe(Keys.IRON_SWORD_RECIPE, SGLoot.getItem(SGItems.IRON_SWORD)).apply {
            shape(
                "I",
                "I",
                "S"
            )
            setIngredient('I', SGLoot.getItem(SGItems.IRON_INGOT))
            setIngredient('S', SGLoot.getItem(SGItems.STICK))
        }
    )

    fun registerRecipes() {
        plugin.server.clearRecipes()
        recipes.forEach { recipe -> plugin.server.addRecipe(recipe) }
    }
}