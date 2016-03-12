package com.rao.tba;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @brief Fragment used for the editing of recipes
 */
public class EditRecipesFragment extends Fragment {
    // TODO(afresh): Make RecipeMap a member variable so we don't have to go into shared preferences all the time
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String MAP_NAME = "RecipeMap";

    public static List<Recipe> mRecipeList;

    private Button btnSubmit;
    private EditText recipeName;
    private Button mBtnIfAdd;
    private Button mBtnThenAdd;
    private Button mBtnDoAdd;
    private Button mBtnClear;
    private SpinnerRecipeListAdapter mIfSpinnerAdapter;
    private SpinnerRecipeListAdapter mThenSpinnerAdapter;
    private SpinnerRecipeListAdapter mDoSpinnerAdapter;

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

    /**
     * @brief Setter method
     * @param ifSpinnerList
     */
    private void setIfSpinnerAdapter(SpinnerRecipeListAdapter ifSpinnerList) {
        this.mIfSpinnerAdapter = ifSpinnerList;
    }

    /**
     * @brief Setter method
     * @param thenSpinnerList
     */
    private void setThenSpinnerAdapter(SpinnerRecipeListAdapter thenSpinnerList) {
        this.mThenSpinnerAdapter = thenSpinnerList;
    }


    /**
     * @brief Setter method
     * @param doSpinnerList
     */
    private void setDoSpinnerAdapter(SpinnerRecipeListAdapter doSpinnerList) {
        this.mDoSpinnerAdapter = doSpinnerList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_recipe, container, false);

        // Make spinner lists
        ArrayList<int[]> ifSpinnerList = new ArrayList<>();
        ArrayList<int[]> thenSpinnerList = new ArrayList<>();
        ArrayList<int[]> doSpinnerList = new ArrayList<>();

        // Create spinner adapters
        SpinnerRecipeListAdapter ifSpinnerAdapter = new SpinnerRecipeListAdapter(getContext(), ifSpinnerList);
        setIfSpinnerAdapter(ifSpinnerAdapter);
        SpinnerRecipeListAdapter thenSpinnerAdapter = new SpinnerRecipeListAdapter(getContext(), thenSpinnerList);
        setThenSpinnerAdapter(thenSpinnerAdapter);
        SpinnerRecipeListAdapter doSpinnerAdapter = new SpinnerRecipeListAdapter(getContext(), doSpinnerList);
        setDoSpinnerAdapter(doSpinnerAdapter);

        // Set adapters
        ListView listIfView = (ListView) rootView.findViewById(R.id.ifList);
        listIfView.setAdapter(ifSpinnerAdapter);
        ListView listThenView = (ListView) rootView.findViewById(R.id.thenList);
        listThenView.setAdapter(thenSpinnerAdapter);
        ListView listDoView = (ListView) rootView.findViewById(R.id.doList);
        listDoView.setAdapter(doSpinnerAdapter);

        addListenerOnButton(rootView);
        return rootView;
    }

    /**
     * @brief Create buttons and spinners for edit recipes fragment
     * @param rootView
     */
    public void addListenerOnButton(View rootView) {
        recipeName = (EditText) rootView.findViewById(R.id.recipeName);

        recipeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( ! hasFocus ) {
                    hideKeyboard(v);
                }
            }
        });

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        mBtnClear = (Button) rootView.findViewById(R.id.btnClear);

        mBtnIfAdd = (Button) rootView.findViewById(R.id.ifAddButton);
        mBtnThenAdd = (Button) rootView.findViewById(R.id.thenAddButton);
        mBtnDoAdd = (Button) rootView.findViewById(R.id.doAddButton);

        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeName.setText("");
                mIfSpinnerAdapter.clear();
                mThenSpinnerAdapter.clear();
                mDoSpinnerAdapter.clear();
            }
        });


        mBtnIfAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] tempInt = {0, 0};
                mIfSpinnerAdapter.add(tempInt);
            }
        });

        mBtnThenAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] tempInt = {1, 0};
                mThenSpinnerAdapter.add(tempInt);
            }
        });

        mBtnDoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] tempInt = {2, 0};
                mDoSpinnerAdapter.add(tempInt);
            }
        });
        final EditRecipesFragment instance = this;
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView listIfView = (ListView) getView().findViewById(R.id.ifList);
                ListView listThenView = (ListView) getView().findViewById(R.id.thenList);
                ListView listDoView = (ListView) getView().findViewById(R.id.doList);

                if (recipeName.getText().toString().replaceAll("\\s+", "").length() < 1 || listIfView.getChildCount() == 0 || listDoView.getChildCount() == 0) {
                    Toast.makeText(getContext(), "Please Enter: Recipe Name and at least 1 IF and DO", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences prefs = getActivity().getApplication().getApplicationContext().getSharedPreferences("RAOStore", Context.MODE_PRIVATE);

                try {
                    if (prefs != null) {
                        String jsonString = prefs.getString(MAP_NAME, (new JSONObject()).toString());
                        JSONObject jsonObject = new JSONObject(jsonString);

                        String recipe_name = recipeName.getText().toString();
                        ArrayList<String> ifArray = new ArrayList<>();
                        ArrayList<String> thenArray = new ArrayList<>();
                        ArrayList<String> doArray = new ArrayList<>();

                        View v2;
                        String recipePart = "";
                        Spinner tempSpinner;

                        for (int i = 0; i < listIfView.getChildCount(); i++) {
                            v2 = listIfView.getChildAt(i);
                            tempSpinner = (Spinner) v2.findViewById(R.id.if_recipe_spinner);
                            recipePart = String.valueOf(tempSpinner.getSelectedItem());

                            ifArray.add(recipePart);
                        }

                        for (int i = 0; i < listThenView.getChildCount(); i++) {
                            v2 = listThenView.getChildAt(i);
                            tempSpinner = (Spinner) v2.findViewById(R.id.then_recipe_spinner);
                            recipePart = String.valueOf(tempSpinner.getSelectedItem());

                            thenArray.add(recipePart);
                        }

                        for (int i = 0; i < listDoView.getChildCount(); i++) {
                            v2 = listDoView.getChildAt(i);
                            tempSpinner = (Spinner) v2.findViewById(R.id.do_recipe_spinner);
                            recipePart = String.valueOf(tempSpinner.getSelectedItem());

                            doArray.add(recipePart);
                        }

                        Recipe newRecipe = new Recipe(ifArray, thenArray, doArray, recipe_name);
                        String newRecipeString = newRecipe.toString();
                        jsonObject.put(recipe_name, newRecipeString);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(MAP_NAME, jsonObject.toString());
                        editor.commit();

                        mRecipeList.add(newRecipe);
                        ((RecipeFragment) getFragmentManager().getFragments().get(1)).getRecipeListAdapter().notifyDataSetChanged();

                        recipeName.setText("");
                        mIfSpinnerAdapter.clear();
                        mThenSpinnerAdapter.clear();
                        mDoSpinnerAdapter.clear();
                    } else {
                        System.out.println("RecipeStore is null. First time access?");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @brief Method to hide keyboard
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * @brief Method to fill fragment with information of given recipe
     * @param fillRecipe
     */
    public void fillWithRecipe(Recipe fillRecipe) {
        mIfSpinnerAdapter.clear();
        mThenSpinnerAdapter.clear();
        mDoSpinnerAdapter.clear();

        ArrayList<String> tempIf = fillRecipe.getIfList();
        ArrayList<String> tempThen = fillRecipe.getThenList();
        ArrayList<String> tempDo = fillRecipe.getDoList();

        ArrayList<String> ifArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.if_array)));
        ArrayList<String> thenArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.then_array)));
        ArrayList<String> doArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.do_array)));


        for (String s : tempIf) {
            int[] tempInt = new int[2];
            tempInt[0] = 0;
            tempInt[1] = ifArray.indexOf(s);
            mIfSpinnerAdapter.add(tempInt);
        }

        for (String s : tempThen) {
            int[] tempInt = new int[2];
            tempInt[0] = 1;
            tempInt[1] = thenArray.indexOf(s);
            mThenSpinnerAdapter.add(tempInt);
        }

        for (String s : tempDo) {
            int[] tempInt = new int[2];
            tempInt[0] = 2;
            tempInt[1] = doArray.indexOf(s);
            mDoSpinnerAdapter.add(tempInt);
        }

        recipeName = (EditText) getView().findViewById(R.id.recipeName);
        recipeName.setText(fillRecipe.getName());
    }
}

