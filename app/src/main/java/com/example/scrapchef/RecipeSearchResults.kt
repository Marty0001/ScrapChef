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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scrapchef.databinding.FragmentRecipeSearchResultsBinding


class RecipeSearchResults : Fragment(), RecyclerAdapter.OnRecipeItemClickListener {

    private lateinit var binding: FragmentRecipeSearchResultsBinding
    private lateinit var adapter: RecyclerAdapter

    private val currentIngredients : MutableList<String> = mutableListOf()
    //var recipeList: MutableMap<String, RecipeData> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onItemClick(recipe: RecipeData) {
        Log.i("Results", recipe.title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //underline back button text
        binding.backButton.paint?.isUnderlineText = true

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //show loading icon while the recycler view is empty
        binding.progressBar.visibility = View.VISIBLE

        //receive selected ingredients from search fragment
        arguments?.let { it ->
            val args = RecipeSearchResultsArgs.fromBundle(it)

            for (ingredient in args.selectedIngredients) {
                currentIngredients.add(ingredient.toString())
            }

            val recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

            recipeViewModel.fetchRecipes(requireContext(), currentIngredients)

            //wait to fetch recipes, then fill the recycler view
            recipeViewModel.recipeList.observe(viewLifecycleOwner) { recipeMap ->

                adapter = RecyclerAdapter(recipeMap, currentIngredients)
                adapter.setOnRecipeItemClickListener(this)
                binding.recyclerView.adapter = adapter

                binding.progressBar.visibility = View.GONE
            }

            //navigate back to search fragment and send back the selected ingredients to remake the ingredient bubbles
            binding.backButton.setOnClickListener {
                val action: RecipeSearchResultsDirections.BackToSearch =
                    RecipeSearchResultsDirections.backToSearch()

                action.setReturnedIngredients(args.selectedIngredients)

                Navigation.findNavController(it).navigate(action)

            }
        }
    }

}