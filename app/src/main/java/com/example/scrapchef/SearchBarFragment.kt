package com.example.scrapchef

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.scrapchef.databinding.FragmentSearchBarBinding
import com.google.android.material.shape.MaterialShapeDrawable
import java.io.BufferedReader
import java.io.InputStreamReader

class SearchBarFragment : Fragment() {

    private var _binding: FragmentSearchBarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val ingredientsList: MutableList<String> = mutableListOf()

        var line: String? = reader.readLine()
        while (line != null) {
            val row: List<String> = line.split(";")
            ingredientsList.add(row[0])
            line = reader.readLine() // Read the next line
        }

        val ingredientsAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            ingredientsList.toList()
        )

        binding.ingredientSearchResults.adapter = ingredientsAdapter

        binding.ingredientSearch.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.ingredientSearch.clearFocus()
                if (ingredientsList.contains(query)) {

                    ingredientsAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                ingredientsAdapter.filter.filter(newText)
                return false
            }
        })

        //listener for when an ingredient is selected from listView
        binding.ingredientSearchResults.setOnItemClickListener { parent, view, position, id ->
            val selectedIngredient = parent.getItemAtPosition(position) as String

            Log.d("SearchBarFragment", "Selected Ingredient: $selectedIngredient")
            addIngredientBubble(selectedIngredient)
        }
    }

    private fun addIngredientBubble(selectedIngredient: String) {

        val cardView = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = resources.getDimensionPixelSize(R.dimen.ing_card_margin)
                setMargins(margin, margin, margin, margin)
            }

            val shapeDrawable = MaterialShapeDrawable().apply {
                setCornerSize(resources.getDimensionPixelSize(R.dimen.ing_card_corner_radius).toFloat())
                fillColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.ingredient_bubble))
            }

            // Set the shape drawable as background
            background = shapeDrawable

            val linearLayout = LinearLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL

                val layoutPadding = resources.getDimensionPixelSize(R.dimen.ing_bubble_layout_pad)
                setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding)
            }

            addView(linearLayout)

            //create text view for ingredient name
            val textView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    resources.getDimensionPixelSize(R.dimen.ing_name_height)
                )

                val padding = resources.getDimensionPixelSize(R.dimen.ing_name_padding)
                setPadding(padding, padding, padding, padding)

                gravity = Gravity.CENTER_VERTICAL

                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.ing_name_text_size).toFloat())
                setTextColor(ContextCompat.getColor(context, R.color.black))

                text = selectedIngredient
            }

            linearLayout.addView(textView)

            //create button to remove ingredient
            val button = Button(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.remove_ing_width),
                    resources.getDimensionPixelSize(R.dimen.remove_ing_height)
                )
                val padding = resources.getDimensionPixelSize(R.dimen.remove_ing_padding)
                setPadding(padding, padding, padding, padding)

                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.remove_ing_text_size).toFloat())

                setTextColor(ContextCompat.getColor(context, R.color.black))

                setBackgroundColor(Color.TRANSPARENT)

                text = "X"

                setOnClickListener {
                    // Handle button click
                }
            }
            linearLayout.addView(button)
        }

        binding.ingredientBubbleLayout.addView(cardView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}