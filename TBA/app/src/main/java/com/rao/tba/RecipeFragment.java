package com.rao.tba;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A fragment representing the list of existing Recipes
 */
public class RecipeFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";

    // List to hold recipes
    private List<Recipe> mRecipes;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecipeListAdapter mListAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeFragment() {
    }


    public void printStuff() {
        System.out.println(mRecipes.toString());
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
        System.out.println("HELLO THERE FROM RECIPEFRAG");
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new RecipeListAdapter(mRecipes, mListener));
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(Recipe item);
    }
}

/* Reference
    public static class ViewRecipesFragment extends ListFragment {
        private List<RecipeFragment> mRecipes;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ViewRecipesFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mRecipes = new ArrayList<RecipeFragment>();
        }

        public static ViewRecipesFragment newInstance(int sectionNumber) {
            ViewRecipesFragment fragment = new ViewRecipesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Map<String, String> recipeMap = new HashMap<String, String>();
            SharedPreferences prefs = getActivity().getApplication().getApplicationContext().getSharedPreferences("RecipeStore", MODE_PRIVATE);

            //Create layout
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(lp);


            //Create button for each recipe
            try{
                if (prefs != null){
                    String jsonString = prefs.getString("RecipeMap", (new JSONObject()).toString());
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while(keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = (String) jsonObject.get(key);

                        Button myButton = new Button(getActivity());
                        myButton.setText("Edit \"" + key + "\" Recipe");
                        LinearLayout.LayoutParams button_lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                        myButton.setLayoutParams(button_lp);
                        layout.addView(myButton);
                    }

                } else {
                    System.out.println("RecipeStore is null.");
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            ViewGroup viewGroup = (ViewGroup) rootView;
            viewGroup.addView(layout);
            return rootView;
        }
    }
 */