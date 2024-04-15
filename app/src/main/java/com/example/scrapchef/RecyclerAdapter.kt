package com.example.scrapchef

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.scrapchef.databinding.CardLayoutBinding

class RecyclerAdapter(private val recipes: Map<String, RecipeData>, private val ingredients : List<String>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    interface OnRecipeItemClickListener {
        fun onItemClick(recipe: RecipeData)
    }

    private var itemClickListener: OnRecipeItemClickListener? = null

    fun setOnRecipeItemClickListener(listener: OnRecipeItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipeEntry = recipes.entries.elementAt(position)
        holder.bind(recipeEntry)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    inner class ViewHolder(private val binding: CardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            //click listener for whole card view
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val recipe = recipes.entries.elementAt(position).value
                    itemClickListener?.onItemClick(recipe)
                }
            }
        }
        @SuppressLint("SetTextI18n")
        fun bind(recipeEntry: Map.Entry<String, RecipeData>) {
            val recipe = recipeEntry.value

            binding.recipeTitle.text = recipe.title

            //list of all used ingredients in recipe, even ones not selected by user
            val usedIngredientsList = recipe.usedIngredients.map { it.name }

            val usedIngredientsCommaList = StringBuilder()
            val unusedIngredientsCommaList = StringBuilder()

            //for every selected ingredient, if the recipe contains it, add it to used list
            for (ingredient in ingredients) {

                //if a user selected ingredient is used in the recipe, add it to the used list
                if (usedIngredientsList.any { it.contains(ingredient, ignoreCase = true) }) {
                    if (usedIngredientsCommaList.isNotEmpty())
                        usedIngredientsCommaList.append(", ")
                    usedIngredientsCommaList.append(ingredient)
                }
                else {
                    if (unusedIngredientsCommaList.isNotEmpty())
                        unusedIngredientsCommaList.append(", ")
                    unusedIngredientsCommaList.append(ingredient)
                }
            }

            //only show the list if its not empty
            if(usedIngredientsCommaList.isNotEmpty())
                binding.usedIngredients.text = "$usedIngredientsCommaList"
            else
                binding.usedIngredients.visibility = View.GONE

            if(unusedIngredientsCommaList.isNotEmpty())
                binding.unusedIngredients.text = "$unusedIngredientsCommaList"
            else
                binding.unusedIngredients.visibility = View.GONE

            //download image
            Glide.with(binding.root.context)
                .load(recipe.image)
                //.placeholder(R.drawable.scrapchef_logo)
                .into(binding.recipeImage)
        }
    }
}
