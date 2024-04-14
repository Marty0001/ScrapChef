package com.example.scrapchef

import java.io.Serializable //serializable so RecipeData object can be passed as an argument between fragments

data class RecipeData(
    val id: Int,
    val title: String,
    val image: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val missedIngredients: List<Ingredient>,
    val usedIngredients: List<Ingredient>,
    val unusedIngredients: List<Ingredient>,
) : Serializable

data class RecipeDirectionsData(
    val name: String,
    val steps: List<Step>
)

data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<Ingredient>,
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