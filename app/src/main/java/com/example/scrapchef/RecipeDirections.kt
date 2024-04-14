package com.example.scrapchef

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.scrapchef.databinding.FragmentRecipeDirectionsBinding
import com.example.scrapchef.databinding.FragmentRecipeSearchResultsBinding

class RecipeDirections : Fragment() {

    private lateinit var binding: FragmentRecipeDirectionsBinding

    override fun onStart() {
        super.onStart()

        //underline back button text
        binding.backButton.paint?.isUnderlineText = true

        arguments?.let { it ->
            val args = RecipeDirectionsArgs.fromBundle(it)

            val recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

            recipeViewModel.fetchRecipeDirections(requireContext(), args.recipe.id)

            //wait to fetch recipes, then fill the recycler view
            recipeViewModel.recipeDirections.observe(viewLifecycleOwner) { recipeDirections ->

                Log.d("RecipeDirections", "Recipe name: ${recipeDirections.values.firstOrNull()?.name}")

                // Log the number of steps in the first recipe directions data
                val firstRecipeSteps = recipeDirections.values.firstOrNull()?.steps
                Log.d("RecipeDirections", "Number of steps: ${firstRecipeSteps?.size}")

                // Log the description of the first step in the first recipe directions data
                val firstStepDescription = firstRecipeSteps?.firstOrNull()?.step
                Log.d("RecipeDirections", "First step description: $firstStepDescription")

                // Log the name of the first ingredient in the first step of the first recipe directions data
                val firstIngredientName = firstRecipeSteps?.firstOrNull()?.ingredients?.firstOrNull()?.name
                Log.d("RecipeDirections", "First ingredient name: $firstIngredientName")

            }


            binding.backButton.setOnClickListener {
                val action: RecipeDirectionsDirections.BackToResults =
                    RecipeDirectionsDirections.backToResults(args.selectedIngredients)

                action.setSelectedIngredients(args.selectedIngredients)

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
        binding = FragmentRecipeDirectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

}