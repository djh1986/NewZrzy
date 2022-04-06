package com.zrzyyzt.runtimeviewer.BMOD.PhotoModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zrzyyzt.runtimeviewer.R;


public class PhotoActivity extends AppCompatActivity {

    public static final String IMAGE_NAME = "image_name";

    public static final String IMAGE_PATH = "image_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        String fruitName = intent.getStringExtra(IMAGE_NAME);
        String imagePath = intent.getStringExtra(IMAGE_PATH);

        ImageView fruitImageView = (ImageView) findViewById(R.id.fruit_image_view);


        Glide.with(this).load(imagePath).into(fruitImageView);
        String fruitContent = generateFruitContent(fruitName);

    }

    private String generateFruitContent(String fruitName) {
        StringBuilder fruitContent = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            fruitContent.append(fruitName);
        }
        return fruitContent.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
