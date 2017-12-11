package com.example.svenu.svenuitendaalwhatsfordinner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFragment(new LogInFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle args;
        switch (item.getItemId()) {
            case R.id.whats_actually_for_dinner_menu:
//                startFragment();
                setMenuVisibility(true);
                getSupportActionBar().setTitle(R.string.app_name);
                break;
            case R.id.my_favourites_menu:
                args = new Bundle();
                args.putBoolean("isPublic", false);
                startFragment(new MyFavouritesFragment(), args);
                setMenuVisibility(true);
                break;
            case R.id.all_favourites_menu:
                args = new Bundle();
                args.putBoolean("isPublic", true);
                startFragment(new MyFavouritesFragment(), args);
                setMenuVisibility(true);
                break;
            case R.id.add_recipe_menu:
                startFragment(new AddRecipeFragment());
                setMenuVisibility(true);
                break;
            case R.id.search_recipe_menu:
//                startFragment();
                setMenuVisibility(true);
                getSupportActionBar().setTitle(R.string.search_recipe_name);
                break;
            case R.id.user_menu:
                startFragment(new UserPreferenceFragment());
                setMenuVisibility(true);
                break;
            case R.id.log_out_menu:
                FirebaseAuth.getInstance().signOut();
                startFragment(new LogInFragment());
                setMenuVisibility(false);
                break;
        }
        return true;
    }

    private void startFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        //TODO: sluit vorige fragment
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void startFragment(Fragment fragment, Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        //TODO: sluit vorige fragment
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(args);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void setMenuVisibility(boolean visibility) {
        MenuItem[] menuItems = new MenuItem[] {
            findViewById(R.id.whats_actually_for_dinner_menu),
            findViewById(R.id.my_favourites_menu),
            findViewById(R.id.all_favourites_menu),
            findViewById(R.id.add_recipe_menu),
            findViewById(R.id.search_recipe_menu),
            findViewById(R.id.user_menu),
            findViewById(R.id.log_out_menu)
        };

        int size = menuItems.length;
//        for (int i = 0; i < size; i++) {
//            menuItems[i].setVisible(visibility);
//        }
        this.invalidateOptionsMenu();
    }
}
