package com.example.svenu.svenuitendaalwhatsfordinner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecipeFragment extends Fragment {

    View rootView;

    public AddRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.add_recipe_name);

        //TODO

        // Inflate the layout for this fragment
        return rootView;
    }

}
