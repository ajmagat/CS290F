package com.rao.tba;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @brief Fragment used for the editing of recipes
 */
public class EditRecipesFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private List<Recipe> mRecipeList;
    private Spinner spinner1, spinner2, spinner3;
    private Button btnSubmit;
    private EditText recipeName;

    /**
     * @brief Default constructor
     */
    public EditRecipesFragment() {
    }

    /**
     * @brief Static method to create EditRecipesFragment
     * @param sectionNumber
     * @param recipeList
     * @return an EditRecipesFragment
     */
    public static EditRecipesFragment newInstance(int sectionNumber, List<Recipe> recipeList) {
        EditRecipesFragment fragment = new EditRecipesFragment();
        fragment.setRecipeList(recipeList);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @brief Setter method
     * @param recipeList
     */
    private void setRecipeList(List<Recipe> recipeList) {
        this.mRecipeList = recipeList;
    }

    public void hello() {
        System.out.println("bye");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
        addListenerOnButton(rootView);
        return rootView;
    }

    /**
     * @brief Create buttons and spinners for edit recipes fragment
     * @param rootView
     */
    public void addListenerOnButton(View rootView) {
        spinner1 = (Spinner) rootView.findViewById(R.id.spinner1);
        spinner2 = (Spinner) rootView.findViewById(R.id.spinner2);
        spinner3 = (Spinner) rootView.findViewById(R.id.spinner3);
        recipeName = (EditText) rootView.findViewById(R.id.recipeName);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        final EditRecipesFragment instance = this;
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                //Read in all existing recipes to a map
                Map<String, String> recipeMap = new HashMap<String, String>();
                SharedPreferences prefs = getActivity().getApplication().getApplicationContext().getSharedPreferences("RecipeStore", Context.MODE_PRIVATE);

                try {
                    if (prefs != null) {
                        System.out.println("RecipeStore is not null");
                        String jsonString = prefs.getString("RecipeMap", (new JSONObject()).toString());
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Iterator<String> keysItr = jsonObject.keys();
                        while (keysItr.hasNext()) {
                            String key = keysItr.next();
                            String value = (String) jsonObject.get(key);
                            recipeMap.put(key, value);
                        }

                        System.out.println("RECIPE MAP BEFORE ADDITION: ");
                        System.out.println(recipeMap);

                        String recipe_name = recipeName.getText().toString();
                        String recipe_if = String.valueOf(spinner1.getSelectedItem());
                        String recipe_then_1 = String.valueOf(spinner2.getSelectedItem());
                        String recipe_then_2 = String.valueOf(spinner3.getSelectedItem());

                        Recipe newRecipe = new Recipe(recipe_if, recipe_then_1, recipe_then_2, recipe_name);
                        String newRecipeString = newRecipe.toString();
                        recipeMap.put(recipe_name, newRecipeString);


                        //write the updated map to prefs
                        JSONObject jsonObjectToWrite = new JSONObject(recipeMap);
                        String jsonStringToWrite = jsonObjectToWrite.toString();

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("RecipeMap").commit();
                        editor.putString("RecipeMap", jsonStringToWrite);
                        editor.commit();

                        mRecipeList.add(newRecipe);
                        ((RecipeFragment) getFragmentManager().getFragments().get(1)).getRecipeListAdapter().notifyDataSetChanged();

                        recipeName.setText("");
                        spinner1.setSelection(1);
                        spinner1.setSelection(2);
                    } else {
                        System.out.println("RecipeStore is null. First time access?");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("OnClickListener : " +
                        "\nRecipe Name : " + recipeName.getText().toString() +
                        "\nSpinner 1 : " + String.valueOf(spinner1.getSelectedItem()) +
                        "\nSpinner 2 : " + String.valueOf(spinner2.getSelectedItem()) +
                        "\nSpinner 3 : " + String.valueOf(spinner3.getSelectedItem()));
            }
        });
    }
}

