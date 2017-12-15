package com.example.svenu.svenuitendaalwhatsfordinner;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by svenu on 8-12-2017.
 */

public class RecipeAdapter extends ArrayAdapter {
    private Context theContext;
    private ArrayList<Recipe> recipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> arrayList) {
        super(context, R.layout.row_recipe, arrayList);
        theContext = context;
        recipes = arrayList;
        Log.d("inside", "constructing adapter");
        Log.d("list", arrayList.toString());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("inside", "inside adapter");
        LayoutInflater layoutInflater = (LayoutInflater) theContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.row_recipe, parent, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView recipeTitle = view.findViewById(R.id.recipe_title);
        TextView recipeSource = view.findViewById(R.id.recipe_source);

        Recipe recipe = recipes.get(position);

        recipeTitle.setText(recipe.name);

        String source = recipe.source;
        if (source == null) {
            source = recipe.userName;
        }
        recipeSource.setText(source);
        String image = recipe.image;
        if (image != null) {
            imageRequestFunction(image, imageView);
        }

        view.setTag(recipe);

        return view;
    }

    private void imageRequestFunction(String imageUrl, final ImageView imageView) {
        RequestQueue queue = Volley.newRequestQueue(theContext);
        // bron: https://www.programcreek.com/javi-api-examples/index.php?api=com.android.volley.toolbox.ImageRequest
        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        }, 0, 0, null, Bitmap.Config.ALPHA_8,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        queue.add(imageRequest);
    }
}
