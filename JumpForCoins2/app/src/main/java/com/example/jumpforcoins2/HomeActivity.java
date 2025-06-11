package com.example.jumpforcoins2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button btnPlay, btnPlayWithBot, btnHowToPlay, btnAbout;
    private final String KEY_IS_WITH_BOT = "KEY_IS_WITH_BOT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi komponen UI
        btnPlay = findViewById(R.id.play);
        btnPlayWithBot = findViewById(R.id.play_bot);
        btnHowToPlay = findViewById(R.id.btn_how_to_play);
        btnAbout = findViewById(R.id.btn_about);

        // Set click listener pada masing masing button
        btnPlay.setOnClickListener(view -> {
            Intent intent = new Intent(this, GamePlayActivity.class);
            intent.putExtra(KEY_IS_WITH_BOT, false);
            startActivity(intent);
        });

        btnPlayWithBot.setOnClickListener(view -> {
            Intent intent = new Intent(this, GamePlayActivity.class);
            intent.putExtra(KEY_IS_WITH_BOT, true);
            startActivity(intent);
        });

        btnHowToPlay.setOnClickListener(view -> {
            Intent intent = new Intent(this, HowToPlayActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(view -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });
    }
}