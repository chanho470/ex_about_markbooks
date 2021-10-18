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

    private LinearLayout rootLayout;
    private TextView textViewTitle;
    private TextView textViewBody;
    private ImageView imageView;

    private Palette.Swatch vibrantSwatch;

    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;

    private int swatchNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.root_layout);
        textViewTitle =findViewById(R.id.text_view_title);
        textViewBody = findViewById(R.id.text_view_body);
        imageView = findViewById(R.id.image_view);

        Bitmap bitmap =((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                     vibrantSwatch = palette.getLightVibrantSwatch();
                     lightVibrantSwatch =palette.getLightVibrantSwatch();
                     darkVibrantSwatch =palette.getDarkVibrantSwatch();
                     mutedSwatch=palette.getMutedSwatch();
                     lightMutedSwatch=palette.getLightMutedSwatch();
                     darkMutedSwatch=palette.getDarkMutedSwatch();
            }
        });
    }
    public void nextSwatch(View v){
        Palette.Swatch currnetSwatch = null;

        switch (swatchNumber){
            case 0:
                currnetSwatch = vibrantSwatch;
                textViewTitle.setText("Vibrant");
            case 1:
                currnetSwatch = lightVibrantSwatch;
                textViewTitle.setText("Light Vibrant");
                break;
            case 2:
                currnetSwatch = darkVibrantSwatch;
                textViewTitle.setText("Dark Vibrant");
                break;
            case 3:
                currnetSwatch = mutedSwatch;
                textViewTitle.setText("Muted");
                break;
            case 4:
                currnetSwatch = lightMutedSwatch;
                textViewTitle.setText("Light Muted");
                break;
            case 5:
                currnetSwatch = darkMutedSwatch;
                textViewTitle.setText("Dark Muted");
                break;
        }

        if(currnetSwatch !=null){
            rootLayout.setBackgroundColor(currnetSwatch.getRgb());
            textViewTitle.setTextColor(currnetSwatch.getTitleTextColor());
            textViewBody.setTextColor(currnetSwatch.getBodyTextColor());
        }else{
            rootLayout.setBackgroundColor(Color.WHITE);
            textViewTitle.setTextColor(Color.RED);
            textViewBody.setTextColor(Color.RED);
        }
        if (swatchNumber < 5){
            swatchNumber++;
        }

    }

}