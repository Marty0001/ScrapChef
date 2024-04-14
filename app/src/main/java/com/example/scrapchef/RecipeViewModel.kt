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

    private val _recipeDirections: MutableLiveData<MutableMap<String, RecipeDirectionsData>> = MutableLiveData()
    val recipeDirections: LiveData<MutableMap<String, RecipeDirectionsData>> = _recipeDirections

    fun fetchRecipeDirections(context: Context, recipeId : Int){

        val url = "https://api.spoonacular.com/recipes/$recipeId/analyzedInstructions?stepBreakdown" +
                "&apiKey=42c0d09e6c564072a4cbbd1a204a5c64"

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->

                val recipeArray = JSONArray(response)

                val tempRecipeDirections: MutableMap<String, RecipeDirectionsData> = mutableMapOf()

                for (i in 0 until recipeArray.length()) {
                    val recipeObject: JSONObject = recipeArray.getJSONObject(i)
                    val recipeDirectionsData = addRecipeDirectionsData(recipeObject)
                    tempRecipeDirections[recipeDirectionsData.name] = recipeDirectionsData
                }

                _recipeDirections.value = tempRecipeDirections // Update the value here after fetch is done

                Log.i("GetRecipeDirections", "fetch request successful")
            },
            {
                Log.i("GetRecipeDirections", "fetch request failed")
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

    fun addRecipeDirectionsData(recipeObject: JSONObject): RecipeDirectionsData {
        val name = recipeObject.getString("name")
        val stepsArray = recipeObject.getJSONArray("steps")

        val stepsList: MutableList<Step> = mutableListOf()

        for (j in 0 until stepsArray.length()) {
            val stepObject = stepsArray.getJSONObject(j)
            val number = stepObject.getInt("number")
            val step = stepObject.getString("step")

            val ingredientsArray = stepObject.getJSONArray("ingredients")
            val ingredientsList: MutableList<Ingredient> = mutableListOf()
            for (k in 0 until ingredientsArray.length()) {
                val ingredientObject = ingredientsArray.getJSONObject(k)
                val ingredientId = ingredientObject.optInt("id")
                val ingredientName = ingredientObject.optString("name")
                val ingredientAmount = ingredientObject.optDouble("amount")
                val ingredientUnit = ingredientObject.optString("unit")
                val ingredientAisle = ingredientObject.optString("aisle")
                val ingredientOriginal = ingredientObject.optString("original")
                val ingredientOriginalName = ingredientObject.optString("originalName")
                val ingredientExtendedName = ingredientObject.optString("extendedName", null)
                val ingredientImage = ingredientObject.optString("image")

                val ingredient = Ingredient(
                    ingredientId,
                    ingredientAmount,
                    ingredientUnit,
                    ingredientAisle,
                    ingredientName,
                    ingredientOriginal,
                    ingredientOriginalName,
                    ingredientExtendedName,
                    ingredientImage
                )
                ingredientsList.add(ingredient)
            }

            val stepData = Step(number, step, ingredientsList)
            stepsList.add(stepData)
        }

        return RecipeDirectionsData(name, stepsList)
    }




}