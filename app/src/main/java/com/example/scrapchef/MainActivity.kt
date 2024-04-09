package com.example.scrapchef

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
//
//        val random = Random.Default // Create an instance of Random
//        val shuffledIngredients = ingredientsList.keys.shuffled(random) // Shuffle the keys of the ingredientsList
//        val randomList = shuffledIngredients.take(1)
//
//        Log.i("MAIN", "testing random ingredients: $randomList")
//
//        recipeViewModel.fetchRecipes(this, randomList)
//
//        recipeViewModel.recipeList.observe(this) { recipeMap ->
//
//            Log.i("MAIN", "Recipe count: ${recipeMap.size}")
//        }

    }

}