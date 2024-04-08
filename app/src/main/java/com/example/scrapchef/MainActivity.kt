package com.example.scrapchef

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //make list of most common ingredients. csv file structured like: apple;9003

        val input = InputStreamReader(assets.open("top-1k-ingredients.csv"))
        val reader = BufferedReader(input)

        val ingredientsList : MutableMap<String, Int> = mutableMapOf()

        Log.i("MAIN", "begin reading list")
        var line: String? = reader.readLine()
        while (line != null) {
            val row: List<String> = line.split(";")
            ingredientsList[row[0]] = row[1].toInt()
            line = reader.readLine() // Read the next line
        }

        Log.i("MAIN", "got list")
        val recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        val random = Random.Default // Create an instance of Random
        val shuffledIngredients = ingredientsList.keys.shuffled(random) // Shuffle the keys of the ingredientsList
        val randomList = shuffledIngredients.take(1)

        Log.i("MAIN", "testing random ingredients: $randomList")

        recipeViewModel.fetchRecipes(this, randomList)

        recipeViewModel.recipeList.observe(this) { recipeMap ->

            Log.i("MAIN", "Recipe count: ${recipeMap.size}")
        }

    }

}