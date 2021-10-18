package com.example.pal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private LinearLayout rootLayout;
    //private Palette.Swatch vibrantSwatch;
    //private Palette.Swatch lightVibrantSwatch;
    //private Palette.Swatch darkVibrantSwatch;
    //private Palette.Swatch mutedSwatch;
    //private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;

    private int swatchNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        rootLayout = findViewById(R.id.root_layout);

        Bitmap bitmap =((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                     //vibrantSwatch = palette.getLightVibrantSwatch();
                     //lightVibrantSwatch =palette.getLightVibrantSwatch();
                     //darkVibrantSwatch =palette.getDarkVibrantSwatch();
                     //mutedSwatch=palette.getMutedSwatch();
                     //lightMutedSwatch=palette.getLightMutedSwatch();
                     darkMutedSwatch=palette.getDarkMutedSwatch();
            }
        });

        Palette.Swatch currnetSwatch =darkMutedSwatch;
        if(currnetSwatch == darkMutedSwatch){
            rootLayout.setBackgroundColor(currnetSwatch.getRgb());

        }else{
            rootLayout.setBackgroundColor(Color.BLUE);

        }
    }


}