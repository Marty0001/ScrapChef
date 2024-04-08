package com.example.scrapchef

data class RecipeData(
    val id: Int,
    val title: String,
    val image: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val missedIngredients: List<Ingredient>,
    val usedIngredients: List<Ingredient>,
    val unusedIngredients: List<Ingredient>,
)

data class Ingredient(
    val id: Int,
    val amount: Double,
    val unit: String,
    val aisle: String,
    val name: String,
    val original: String,
    val originalName: String,
    val extendedName: String? = null,
    val image: String
)