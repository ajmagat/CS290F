package com.rao.tba;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.List;

/**
 * A fragment representing the list of existing Recipes
 */
public class RecipeFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String MAP_NAME = "RecipeMap";

    // List to hold recipes
    private List<Recipe> mRecipes;
    private OnListFragmentInteractionListener mListener;
    private RecipeListAdapter mListAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeFragment() {
    }

    /**
     * @brief Static method to create RecipeFragment
     * @param columnCount
     * @param recipeList
     * @return a RecipeFragment
     */
    public static RecipeFragment newInstance(int columnCount, List<Recipe> recipeList) {
        RecipeFragment fragment = new RecipeFragment();
        fragment.setRecipeList(recipeList);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @brief Stter method for mRecipes
     * @param recipeList
     */
    private void setRecipeList(List<Recipe> recipeList) {
        this.mRecipes = recipeList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new RecipeListAdapter(mRecipes, mListener, getContext()));
            mListAdapter = (RecipeListAdapter) recyclerView.getAdapter();
        }
        return view;
    }

    /**
     * @brief Accessor method to get mListAdapter
     * @return mListAdapter
     */
    public RecipeListAdapter getRecipeListAdapter() {
        return mListAdapter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Recipe item, int pos, RecipeListAdapter adapter, List<Recipe> values);
    }

    /**
     * @brief Method to force update of recipe list in shared preferences
     */
    public void updateRecipeList() {
        try {
            SharedPreferences prefs = getActivity().getApplication().getApplicationContext().getSharedPreferences("RAOStore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            JSONObject jsonObject = new JSONObject();

            for ( Recipe r : mRecipes ) {
                jsonObject.put(r.getName(), r.toString());
            }

            //write the updated map to prefs
            editor.putString(MAP_NAME, jsonObject.toString()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
