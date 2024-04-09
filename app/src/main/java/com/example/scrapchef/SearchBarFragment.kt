package com.example.scrapchef

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import com.example.scrapchef.databinding.FragmentSearchBarBinding
import java.io.BufferedReader
import java.io.InputStreamReader


class SearchBarFragment : Fragment() {

    private var _binding: FragmentSearchBarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBarBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.removeIngredient1.setOnClickListener {
            // Log when the button is clicked
            Log.d("SearchBarFragment", "Button clicked")
        }

        binding.ingredientSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ingredientSearchResults.visibility = View.VISIBLE
            } else {
                binding.ingredientSearchResults.visibility = View.GONE
            }
        }

        //make list of most common ingredients. csv file structured like: apple;9003
        val input = requireContext().assets.open("top-1k-ingredients.csv")
        val reader = BufferedReader(InputStreamReader(input))

        val ingredientsList : MutableList<String> = mutableListOf()

        var line: String? = reader.readLine()
        while (line != null) {
            val row: List<String> = line.split(";")
            ingredientsList.add(row[0])
            line = reader.readLine() // Read the next line
        }

        val ingredientsAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            ingredientsList.toList() // Convert MutableList to List
        )

        binding.ingredientSearchResults.adapter = ingredientsAdapter

        binding.ingredientSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.ingredientSearch.clearFocus()
                if(ingredientsList.contains(query)){

                    ingredientsAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                ingredientsAdapter.filter.filter(newText)
                return false
            }


        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}