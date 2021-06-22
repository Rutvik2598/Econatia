package com.company.econatia;

import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PostSelectActivity extends AppCompatActivity {

    private Button image_select, video_select, text_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_select);

        image_select = findViewById(R.id.image_select);
        video_select = findViewById(R.id.video_select);
        text_select = findViewById(R.id.text_select);

        image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostSelectActivity.this, PostActivity.class);
                ContextCompat.startForegroundService(PostSelectActivity.this, intent);
                startActivity(intent);
            }
        });

        video_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostSelectActivity.this, PostVideoActivity.class);
                ContextCompat.startForegroundService(PostSelectActivity.this, intent);
                startActivity(intent);
            }
        });

        text_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostSelectActivity.this, PostTextActivity.class);
                ContextCompat.startForegroundService(PostSelectActivity.this, intent);
                startActivity(intent);
            }
        });

    }
}
