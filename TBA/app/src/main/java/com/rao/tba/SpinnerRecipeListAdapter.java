package com.rao.tba;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * Created by ajmagat on 2/22/16.
 */
public class SpinnerRecipeListAdapter extends ArrayAdapter<int[]> {

    public SpinnerRecipeListAdapter(Context context, ArrayList<int[]> spinners) {
        super(context, 0, spinners);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        int[] spinnerType = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        System.out.println("Spinner type is " + spinnerType[0]);
        System.out.println("Spinner second type is " + spinnerType[1]);
        if (spinnerType[0] == 0) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_if_spinner, parent, false);
            Spinner temp = (Spinner) convertView.findViewById(R.id.if_recipe_spinner);
            temp.setSelection(spinnerType[1]);

        } else if (spinnerType[0] == 1) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_then_spinner, parent, false);
            Spinner temp = (Spinner) convertView.findViewById(R.id.then_recipe_spinner);
            temp.setSelection(spinnerType[1]);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_do_spinner, parent, false);
            Spinner temp = (Spinner) convertView.findViewById(R.id.do_recipe_spinner);
            temp.setSelection(spinnerType[1]);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}


