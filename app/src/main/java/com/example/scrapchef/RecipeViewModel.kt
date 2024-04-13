package com.example.scrapchef

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class RecipeViewModel : ViewModel() {

    private val _recipeList: MutableLiveData<MutableMap<String, RecipeData>> = MutableLiveData()
    val recipeList: LiveData<MutableMap<String, RecipeData>> = _recipeList

    fun fetchRecipeDirections(context: Context, ingredients : List<String>){

        //make ingredients query. structured like: ing1,+ing2,+ing3...
        val ingredientsQuery = StringBuilder()
        ingredientsQuery.append(ingredients[0])

        for (item in ingredients.subList(1, ingredients.size)) {
            ingredientsQuery.append(",+$item")
        }

        val url = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=$ingredientsQuery&sort=max-used-ingredients&number=5" +
                "&apiKey=42c0d09e6c564072a4cbbd1a204a5c64"

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->

                val recipeArray = JSONArray(response)

                val tempRecipeList: MutableMap<String, RecipeData> = mutableMapOf()

                for (i in 0 until recipeArray.length()) {
                    val recipe: JSONObject = recipeArray.getJSONObject(i)
                    tempRecipeList[recipe.getString("title")] = addRecipe(recipe)
                }

                _recipeList.value = tempRecipeList // Update the value here after fetch is done

                Log.i("GetIngredients", "fetch request successful")
            },
            {
                Log.i("GetIngredients", "fetch request failed")
            })

        queue.add(stringRequest)

    }

    fun fetchRecipes(context: Context, ingredients : List<String>){

        //make ingredients query. structured like: ing1,+ing2,+ing3...
        val ingredientsQuery = StringBuilder()
        ingredientsQuery.append(ingredients[0])

        for (item in ingredients.subList(1, ingredients.size)) {
            ingredientsQuery.append(",+$item")
        }

        val url = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=$ingredientsQuery&sort=max-used-ingredients&number=5" +
                "&apiKey=42c0d09e6c564072a4cbbd1a204a5c64"

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->

                val recipeArray = JSONArray(response)

                val tempRecipeList: MutableMap<String, RecipeData> = mutableMapOf()

                for (i in 0 until recipeArray.length()) {
                    val recipe: JSONObject = recipeArray.getJSONObject(i)
                    tempRecipeList[recipe.getString("title")] = addRecipe(recipe)
                }

                _recipeList.value = tempRecipeList // Update the value here after fetch is done

                Log.i("GetIngredients", "fetch request successful")
            },
            {
                Log.i("GetIngredients", "fetch request failed")
            })

        queue.add(stringRequest)

    }

    private fun addRecipe(recipe: JSONObject): RecipeData {
        val id = recipe.getInt("id")
        val title = recipe.getString("title")
        val image = recipe.getString("image")
        val usedIngredientCount = recipe.getInt("usedIngredientCount")
        val missedIngredientCount = recipe.getInt("missedIngredientCount")

        val missedIngredients = parseIngredients(recipe.getJSONArray("missedIngredients"))
        val usedIngredients = parseIngredients(recipe.getJSONArray("usedIngredients"))
        val unusedIngredients = parseIngredients(recipe.getJSONArray("unusedIngredients"))

        return RecipeData(
            id,
            title,
            image,
            usedIngredientCount,
            missedIngredientCount,
            missedIngredients,
            usedIngredients,
            unusedIngredients
        )
    }

    private fun parseIngredients(ingredientsArray: JSONArray): List<Ingredient> {
        val ingredients = mutableListOf<Ingredient>()
        for (i in 0 until ingredientsArray.length()) {
            val ingredientObject = ingredientsArray.getJSONObject(i)
            val id = ingredientObject.getInt("id")
            val amount = ingredientObject.optDouble("amount", 0.0)
            val unit = ingredientObject.optString("unit","")
            val aisle = ingredientObject.optString("aisle","")
            val name = ingredientObject.getString("name")
            val original = ingredientObject.getString("original")
            val originalName = ingredientObject.optString("originalName", "")
            val extendedName = ingredientObject.optString("extendedName", "")
            val image = ingredientObject.getString("image")

            ingredients.add(
                Ingredient(
                    id,
                    amount,
                    unit,
                    aisle,
                    name,
                    original,
                    originalName,
                    extendedName,
                    image
                )
            )
        }
        return ingredients
    }

}