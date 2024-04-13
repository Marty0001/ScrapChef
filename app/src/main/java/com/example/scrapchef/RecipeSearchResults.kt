package com.example.scrapchef

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.scrapchef.databinding.FragmentRecipeSearchResultsBinding


class RecipeSearchResults : Fragment() {

    private lateinit var binding: FragmentRecipeSearchResultsBinding

    private val currentIngredients : MutableList<String> = mutableListOf()
    var recipeList: MutableMap<String, RecipeData> = mutableMapOf()


    override fun onStart() {
        super.onStart()

        binding.backButton.paint?.isUnderlineText = true

        //receive selected ingredients from search fragment
        arguments?.let { it ->
            val args = RecipeSearchResultsArgs.fromBundle(it)

            for(ingredient in args.selectedIngredients){
                currentIngredients.add(ingredient.toString())
            }

            val recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

            recipeViewModel.fetchRecipes( requireContext(), currentIngredients)

            recipeViewModel.recipeList.observe(this) { recipeMap ->

                recipeList = recipeMap

                Log.i("SearchResults", "Recipe count: ${recipeList.size}")
            }

            binding.backButton.setOnClickListener {
                val action: RecipeSearchResultsDirections.BackToSearch =
                    RecipeSearchResultsDirections.backToSearch()

                action.setReturnedIngredients(args.selectedIngredients)

                Navigation.findNavController(it).navigate(action)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}