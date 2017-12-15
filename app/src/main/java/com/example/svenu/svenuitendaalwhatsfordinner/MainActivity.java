package com.example.svenu.svenuitendaalwhatsfordinner;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean visibility = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFragment(new LogInFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        MenuItem[] menuItems = new MenuItem[] {
                menu.findItem(R.id.whats_actually_for_dinner_menu),
                menu.findItem(R.id.my_favourites_menu),
                menu.findItem(R.id.all_favourites_menu),
                menu.findItem(R.id.add_recipe_menu),
                menu.findItem(R.id.search_recipe_menu),
                menu.findItem(R.id.user_menu),
                menu.findItem(R.id.log_out_menu)
        };
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].setVisible(visibility);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle args;
        switch (item.getItemId()) {
            case R.id.whats_actually_for_dinner_menu:
                startFragment(new WhatsActuallyForDinnerFragment());
                setMenuVisibility(true);
                break;
            case R.id.my_favourites_menu:
                startFragment(new MyFavouritesFragment());
                setMenuVisibility(true);
                break;
            case R.id.all_favourites_menu:
                startFragment(new AllFavouritesFragment());
                setMenuVisibility(true);
                break;
            case R.id.add_recipe_menu:
                startFragment(new AddRecipeFragment());
                setMenuVisibility(true);
                break;
            case R.id.search_recipe_menu:
                startFragment(new SearchFragment());
                setMenuVisibility(true);
                break;
            case R.id.user_menu:
                startFragment(new UserPreferenceFragment());
                setMenuVisibility(true);
                break;
            case R.id.log_out_menu:
                FirebaseAuth.getInstance().signOut();
                FragmentManager fm = getSupportFragmentManager();
                if (fm != null) {
                    fm.popBackStack();
                }
                startFragment(new LogInFragment());
                setMenuVisibility(false);
                break;
        }
        return true;
    }

    private void startFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
//        if (fm != null) {
//            fm.popBackStack();
//        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void setMenuVisibility(boolean aVisibility) {
        visibility = aVisibility;
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        List fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for(Object o : fragmentList) {
            if(o instanceof LogInFragment) {
                handled = ((LogInFragment)o).onBackPressed();
                if(handled) {
                    Log.d("activity: ", "close");
                    break;
                }
            }
        }

        if(!handled) {
            super.onBackPressed();
        }
    }
}
