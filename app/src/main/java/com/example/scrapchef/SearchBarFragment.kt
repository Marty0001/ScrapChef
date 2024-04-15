package com.example.scrapchef

import android.annotation.SuppressLint
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.example.scrapchef.databinding.FragmentSearchBarBinding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.InputStreamReader

class SearchBarFragment : Fragment() {

    private lateinit var binding : FragmentSearchBarBinding

    private val currentIngredients = LinkedHashSet<String>()
    private val maxIngredients = 6

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        arguments?.let{ it ->
            val args = SearchBarFragmentArgs.fromBundle(it)

            //not null means fragment was loaded from recipe results fragment and has to reload ingredient bubbles
            if(args.returnedIngredients!=null)
                for(ingredient in args.returnedIngredients!!){
                    currentIngredients.add(ingredient.toString())
                    addIngredientBubble(ingredient.toString())
                }
        }

        binding.totalIngredient.text = "${currentIngredients.count()}/$maxIngredients"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //make list of most common ingredients. csv file structured like: apple;9003
        val input = requireContext().assets.open("top-1k-ingredients.csv")
        val reader = BufferedReader(InputStreamReader(input))

        val ingredientsList: MutableList<String> = mutableListOf()

        var line: String? = reader.readLine()
        while (line != null) {
            val row: List<String> = line.split(";")
            ingredientsList.add(row[0])
            line = reader.readLine()
        }

        //add 1k ingredients to listview
        val ingredientsAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            ingredientsList.toList()
        )
        binding.ingredientSearchResults.adapter = ingredientsAdapter


        //hide listView when the searchbar doesn't have focus
        binding.ingredientSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ingredientSearchResults.visibility = View.VISIBLE
                binding.searchRecipes.visibility = View.GONE
            } else {
                binding.ingredientSearchResults.visibility = View.GONE
                binding.searchRecipes.visibility = View.VISIBLE
            }
        }

        //dynamically display relevant results in listview whenever a new character is entered in search bar
        binding.ingredientSearch.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                if (ingredientsList.contains(query)) {

                    ingredientsAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { currentText ->
                    // list to hold the filtered ingredients
                    val filteredIngredients = mutableListOf<String>()

                    // add the current text to the filtered list if it's not empty
                    if (currentText.isNotEmpty()) {
                        filteredIngredients.add(currentText)
                    }

                    // add the filtered ingredients based on the search query
                    ingredientsList.filterTo(filteredIngredients) { ingredient ->
                        ingredient.contains(currentText, ignoreCase = true)
                    }

                    // update adapter
                    ingredientsAdapter.clear()
                    ingredientsAdapter.addAll(filteredIngredients)
                    ingredientsAdapter.notifyDataSetChanged()
                }
                return false
            }

        })

        //force clear focus from the search bar when empty space is touched
        binding.searchFragmentLayout.setOnTouchListener { _, _ ->
            binding.ingredientSearch.clearFocus()

            // Hide the keyboard
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchFragmentLayout.windowToken, HIDE_IMPLICIT_ONLY)

            // Return false to indicate that touch event is not consumed and can be passed to other listeners
            false
        }

        //listener for when an ingredient is selected from listView
        binding.ingredientSearchResults.setOnItemClickListener { parent, view_, position, _ ->
            val selectedIngredient = parent.getItemAtPosition(position) as String

            //if ingredient not already selected
            if(!currentIngredients.contains(selectedIngredient)) {

                //max ingredients already selected
                if(currentIngredients.count() == maxIngredients){
                    Snackbar.make(view_, "Maximum ingredients selected", Snackbar.LENGTH_SHORT).show() }
                else{
                    //add ingredient to selected hashSet, update count display, and make ingredient bubble
                    currentIngredients.add(selectedIngredient)
                    binding.totalIngredient.text="${currentIngredients.count()}/$maxIngredients"
                    addIngredientBubble(selectedIngredient) }
            }
        }

        //listener for search recipes button
        binding.searchRecipes.setOnClickListener {

            if(currentIngredients.isEmpty()){
                Snackbar.make(view, "Please select at least one ingredient", Snackbar.LENGTH_SHORT).show()
            }else{

                val action : SearchBarFragmentDirections.SearchToResults =
                    SearchBarFragmentDirections.searchToResults(currentIngredients)

                action.setSelectedIngredients(currentIngredients)

                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addIngredientBubble(selectedIngredient: String) {

        //make card view
        val cardView = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = resources.getDimensionPixelSize(R.dimen.ing_card_margin)
                setMargins(margin, margin, margin, margin)
            }

            val bubbleShape = MaterialShapeDrawable().apply {
                setCornerSize(resources.getDimensionPixelSize(R.dimen.ing_card_corner_radius).toFloat())
                fillColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.ingredient_bubble))
            }

            background = bubbleShape//make cardview into bubble shape

            //add linear layout to card view for horizontal display
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

            //add text view to linear layout for ingredient name
            val textView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    resources.getDimensionPixelSize(R.dimen.ing_name_height)
                )

                val padding = resources.getDimensionPixelSize(R.dimen.ing_name_padding)
                setPadding(padding, padding, padding*2, padding)

                gravity = Gravity.CENTER_VERTICAL

                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.ing_name_text_size).toFloat())
                setTextColor(ContextCompat.getColor(context, R.color.black))

                //typeface = ResourcesCompat.getFont(context, R.font.lora)
                text = selectedIngredient
            }

            linearLayout.addView(textView)

            //add button to linear layout to remove ingredient
            val button = Button(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.remove_ing_width),
                    resources.getDimensionPixelSize(R.dimen.remove_ing_height)
                )
                val padding = resources.getDimensionPixelSize(R.dimen.remove_ing_padding)
                setPadding(padding, padding, padding, padding)

                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.remove_ing_text_size).toFloat())

                setTextColor(ContextCompat.getColor(context, R.color.x_button))

                setBackgroundColor(Color.TRANSPARENT)
                //setBackgroundResource(R.drawable.x_button_border)

                text = "x"

                setOnClickListener {
                    //delete all elements in cardView and the CardView itself
                    var viewToRemove: View? = this@apply // Start from the button
                    while (viewToRemove != null && viewToRemove != binding.ingredientBubbleLayout) {
                        val parent = viewToRemove.parent as? ViewGroup // Get the parent of the current view
                        parent?.removeView(viewToRemove) // Remove the current view from its parent
                        viewToRemove = parent // Move up to the next parent
                    }
                    currentIngredients.remove(selectedIngredient)
                    binding.totalIngredient.text="${currentIngredients.count()}/$maxIngredients"
                }
            }
            linearLayout.addView(button)
        }

        binding.ingredientBubbleLayout.addView(cardView)//add ingredient bubble to display
    }
}