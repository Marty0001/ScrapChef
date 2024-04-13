package com.example.scrapchef

import android.os.Bundle
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