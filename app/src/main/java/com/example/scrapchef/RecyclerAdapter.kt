package com.example.scrapchef

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.scrapchef.RecipeData
import com.example.scrapchef.databinding.CardLayoutBinding

class RecyclerAdapter(private val recipe: Map<String, RecipeData>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipeEntry = recipe.entries.elementAt(position)
        holder.bind(recipeEntry)
    }

    override fun getItemCount(): Int {
        return recipe.size
    }

    inner class ViewHolder(private val binding: CardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(recipeEntry: Map.Entry<String, RecipeData>) {
            val recipe = recipeEntry.value

            // Bind data to views
            binding.recipeTitle.text = recipe.title
            binding.recipeDetail.text = "${recipe.usedIngredientCount} used, ${recipe.missedIngredientCount} missed"
            Glide.with(binding.root.context)
                .load(recipe.image)
                .into(binding.recipeImage)
        }
    }
}
