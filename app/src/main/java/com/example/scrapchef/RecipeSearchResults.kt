package com.example.scrapchef

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.scrapchef.databinding.FragmentRecipeSearchResultsBinding


class RecipeSearchResults : Fragment() {

    private lateinit var binding : FragmentRecipeSearchResultsBinding

    override fun onStart() {
        super.onStart()

        arguments?.let{ it ->
            val args = RecipeSearchResultsArgs.fromBundle(it)
            Log.i("RecipreSRFragment","${args.selectedIngredients}")

            binding.temp.text=args.selectedIngredients.toString()

            binding.temp.setOnClickListener{
                Navigation.findNavController(it).navigate(R.id.back_to_search)

            }
        }
    }
    override  fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
    }

}