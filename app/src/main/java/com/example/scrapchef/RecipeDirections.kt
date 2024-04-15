package com.example.scrapchef

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.scrapchef.databinding.FragmentRecipeDirectionsBinding

class RecipeDirections : Fragment() {

    private lateinit var binding: FragmentRecipeDirectionsBinding
    private val alreadyListedIngredient = HashSet<String>()

    override fun onStart() {
        super.onStart()

        //underline back button text
        binding.backButton.paint?.isUnderlineText = true

        arguments?.let { it ->
            val args = RecipeDirectionsArgs.fromBundle(it)

            val recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

            recipeViewModel.fetchRecipeDirections(requireContext(), args.recipe.id)

            recipeViewModel.recipeDirections.observe(viewLifecycleOwner) { recipeDirections ->

                Glide.with(binding.root.context)
                    .load(args.recipe.image)
                    .into(binding.recipeImage)

                binding.recipeTitle.text = args.recipe.title

                // Iterate through each recipe directions data
                recipeDirections.values.forEach { recipeDirection ->

                    recipeDirection.steps.forEach { step ->

                        //remove 'no recipe directions found' message if recipe step has text
                        if(step.step.isNotEmpty())
                            binding.noDirections.visibility = View.GONE

                        showStep(step)

                        step.ingredients.forEach { ingredient ->

                            showIngredient(args.recipe, ingredient)
                        }
                    }
                }
            }

            binding.backButton.setOnClickListener {
                val action: RecipeDirectionsDirections.BackToResults =
                    RecipeDirectionsDirections.backToResults(args.selectedIngredients)

                action.setSelectedIngredients(args.selectedIngredients)

                Navigation.findNavController(it).navigate(action)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDirectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showIngredient(recipe : RecipeData, ingredient: Ingredient){

        if (!alreadyListedIngredient.contains(ingredient.name)) {
            alreadyListedIngredient.add(ingredient.name)

            val ingredientText = buildString {
                append(ingredient.name)

                //get the ingredient amount and unit from the first recipe because the individual recipe fetch doesn't contain the amounts
                val ingredientAmount = recipe.usedIngredients.firstOrNull { it.name == ingredient.name }

                if (ingredientAmount != null) {
                    if (ingredientAmount.amount > 0)
                        if (ingredientAmount.amount % 1 == 0.0)
                            append(" ${ingredientAmount.amount.toInt()}")
                        else
                            append(" ${ingredientAmount.amount}")

                    if (ingredientAmount.unit != "")
                        append(" ${ingredientAmount.unit}")
                }
            }

            val ingredientTextView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT                )

                val padding = resources.getDimensionPixelSize(R.dimen.ing_name_padding)
                setPadding(padding, padding, padding * 2, padding)

                typeface = ResourcesCompat.getFont(context, R.font.lora)

                text = "•${ingredientText}"
            }

            ingredientTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.direction_text_size))
            binding.recipeIngredientsLayout.addView(ingredientTextView)
        }
    }

    private fun showStep(step : Step){

        val linearLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = resources.getDimensionPixelSize(R.dimen.step_row_margin)
                setMargins(margin, margin*3, margin, margin*3)
            }
            orientation = LinearLayout.HORIZONTAL
        }

        val stepTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val padding = resources.getDimensionPixelSize(R.dimen.ing_name_padding)
            setPadding(padding, padding, padding, padding)

            val stepText = SpannableStringBuilder().apply {
                append("Step ${step.number}:\n", StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(step.step)
            }

            typeface = ResourcesCompat.getFont(context, R.font.lora)

            text = stepText
        }

        stepTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.direction_text_size))

        linearLayout.addView(stepTextView)
        binding.stepsTable.addView(linearLayout)
    }

}