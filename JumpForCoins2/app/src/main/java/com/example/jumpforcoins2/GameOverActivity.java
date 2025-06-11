package com.example.jumpforcoins2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {
    private TextView resultTextView;
    private Button btnPlayAgain, btnHome;
    private final String KEY_PLAYER_1_SCORE = "KEY_PLAYER_1_SCORE";
    private final String KEY_PLAYER_2_SCORE = "KEY_PLAYER_2_SCORE";
    private final String KEY_IS_WITH_BOT = "KEY_IS_WITH_BOT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Inisialisasi komponen UI
        resultTextView = findViewById(R.id.result);
        btnPlayAgain = findViewById(R.id.btn_play_again);
        btnHome = findViewById(R.id.btn_home);

        // Mengambil data game dari intent extras
        int player1Score = getIntent().getIntExtra(KEY_PLAYER_1_SCORE, 0);
        int player2Score = getIntent().getIntExtra(KEY_PLAYER_2_SCORE, 0);
        boolean isWithBot = getIntent().getBooleanExtra(KEY_IS_WITH_BOT, false);

        // Menentukan pemenang atau hasil seri dan mengatur teks hasil permainan
        if (player1Score > player2Score) {
            resultTextView.setText("Player 1 Win!");
        } else if (player1Score < player2Score) {
            resultTextView.setText("Player 2 Win!");
        } else {
            resultTextView.setText("Draw!");
        }

        btnPlayAgain.setEnabled(true);

        btnPlayAgain.setOnClickListener(v -> {
            btnPlayAgain.setEnabled(false);
            Intent intent = new Intent(this, GamePlayActivity.class);
            intent.putExtra(KEY_IS_WITH_BOT, isWithBot);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            backToHome();
        });
    }

    // Method untuk kembali ke activity home
    public void backToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // onBackPressed untuk kembali ke activity home
    @Override
    public void onBackPressed() {
        backToHome();
    }
}