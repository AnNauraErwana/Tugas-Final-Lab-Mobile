package com.example.jumpforcoins2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GamePlayActivity extends AppCompatActivity {
    private boolean isRunning = false;
    private boolean isPlayer1 = false;
    private boolean isGameInProgress = false;
    private boolean isPlayingWithBot = false;
    private int randomImage;
    private long startTime = 0;
    private int score = 0;
    private int player1Score = 0;
    private int player2Score = 0;
    private int secondsLeft;
    private TextView player1ScoreTextView, player2ScoreTextView, timerPlayer1, timerPlayer2, player1AddScore, player2AddScore;
    private Runnable runnable, startNewRoundIfIdle;
    private Button btnPlayer1, btnPlayer2;
    private ImageButton btnPause;
    private Handler mHandler, botHandler;
    private MediaPlayer coinSFX, bombSFX, jumpSFX, randomSFX;
    private ImageView player1ImageView, player2ImageView, imageView;
    private final int[] randomImages = {R.drawable.coin_1, R.drawable.coin_2, R.drawable.coin_3, R.drawable.coin_5, R.drawable.bomb_1, R.drawable.bomb_3, R.drawable.bomb_5,};
    private final int[] player1IdleFrames = {R.drawable.boy_idle_1, R.drawable.boy_idle_2, R.drawable.boy_idle_3, R.drawable.boy_idle_4, R.drawable.boy_idle_5,
            R.drawable.boy_idle_6, R.drawable.boy_idle_7, R.drawable.boy_idle_8, R.drawable.boy_idle_9, R.drawable.boy_idle_10,
            R.drawable.boy_idle_11, R.drawable.boy_idle_12, R.drawable.boy_idle_13, R.drawable.boy_idle_14, R.drawable.boy_idle_15};
    private final int[] player2IdleFrames = {R.drawable.girl_idle_1, R.drawable.girl_idle_2, R.drawable.girl_idle_3, R.drawable.girl_idle_4, R.drawable.girl_idle_5,
            R.drawable.girl_idle_6, R.drawable.girl_idle_7, R.drawable.girl_idle_8, R.drawable.girl_idle_9, R.drawable.girl_idle_10,
            R.drawable.girl_idle_11, R.drawable.girl_idle_12, R.drawable.girl_idle_13, R.drawable.girl_idle_14, R.drawable.girl_idle_15, R.drawable.girl_idle_16};
    private final int[] player1JumpFrames = {R.drawable.boy_jump_1, R.drawable.boy_jump_2, R.drawable.boy_jump_3, R.drawable.boy_jump_4, R.drawable.boy_jump_5,
            R.drawable.boy_jump_6, R.drawable.boy_jump_7, R.drawable.boy_jump_8, R.drawable.boy_jump_9, R.drawable.boy_jump_10,
            R.drawable.boy_jump_11, R.drawable.boy_jump_12, R.drawable.boy_jump_13, R.drawable.boy_jump_14, R.drawable.boy_jump_15, R.drawable.boy_jump_1};
    private final int[] player2JumpFrames = {R.drawable.girl_jump_1, R.drawable.girl_jump_2, R.drawable.girl_jump_3, R.drawable.girl_jump_4, R.drawable.girl_jump_5,
            R.drawable.girl_jump_6, R.drawable.girl_jump_7, R.drawable.girl_jump_8, R.drawable.girl_jump_9, R.drawable.girl_jump_10,
            R.drawable.girl_jump_11, R.drawable.girl_jump_12, R.drawable.girl_jump_13, R.drawable.girl_jump_14, R.drawable.girl_jump_15,
            R.drawable.girl_jump_16, R.drawable.girl_jump_17, R.drawable.girl_jump_18, R.drawable.girl_jump_19, R.drawable.girl_jump_20,
            R.drawable.girl_jump_21, R.drawable.girl_jump_22, R.drawable.girl_jump_23, R.drawable.girl_jump_24, R.drawable.girl_jump_25,
            R.drawable.girl_jump_26, R.drawable.girl_jump_27, R.drawable.girl_jump_28, R.drawable.girl_jump_29, R.drawable.girl_jump_30};

    private final String KEY_IS_WITH_BOT = "KEY_IS_WITH_BOT";
    private final String KEY_PLAYER_1_SCORE = "KEY_PLAYER_1_SCORE";
    private final String KEY_PLAYER_2_SCORE = "KEY_PLAYER_2_SCORE";

    // Membuat objek Random
    private Random random;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        // Mengambil nilai boolean dari Intent yang dikirimkan dari activity sebelumnya
        isPlayingWithBot = getIntent().getBooleanExtra(KEY_IS_WITH_BOT, false);

        // Membuat MediaPlayer untuk setiap efek suara
        coinSFX = MediaPlayer.create(this, R.raw.coin);
        bombSFX = MediaPlayer.create(this, R.raw.bomb);
        jumpSFX = MediaPlayer.create(this, R.raw.jump);
        randomSFX = MediaPlayer.create(this, R.raw.random);

        // Mengambil referensi button
        btnPlayer1 = findViewById(R.id.btn_player_1);
        btnPlayer2 = findViewById(R.id.btn_player_2);
        btnPause = findViewById(R.id.btn_pause);

        // Mengambil referensi TextView
        player2ScoreTextView = findViewById(R.id.player_2_score);
        player1ScoreTextView = findViewById(R.id.player_1_score);
        timerPlayer1 = findViewById(R.id.timer_player_1);
        timerPlayer2 = findViewById(R.id.timer_player_2);
        player1AddScore = findViewById(R.id.player_1_add_score);
        player2AddScore = findViewById(R.id.player_2_add_score);

        // Mengambil referensi ImageView
        player1ImageView = findViewById(R.id.player_1_char);
        player2ImageView = findViewById(R.id.player_2_char);
        imageView = findViewById(R.id.random_image_view);

        // Membuat objek Random
        random = new Random();

        // Memulai game
        startGame();

        // Menambahkan event listener untuk button pause
        btnPause.setOnClickListener(view -> pauseGame());
    }

    // Method untuk memulai permainan
    public void startGame() {
        // Mengaktifkan tombol pemain 1 dan pemain 2
        btnPlayer1.setEnabled(true);
        btnPlayer2.setEnabled(true);

        // Mengatur skor pemain 1 dan pemain 2 menjadi 0
        player1Score = 0;
        player2Score = 0;

        // Memulai animasi idle untuk karakter pemain 1 dan pemain 2
        startIdleAnimation(player1ImageView, player1IdleFrames);
        startIdleAnimation(player2ImageView, player2IdleFrames);

        // Membuat Runnable untuk mengubah gambar acak setiap 50 milidetik
        runnable = createRandomImageRunnable();

        startGameProgressChecker();

        // Memulai tampilan kata acak pada imageView
        startTime = System.currentTimeMillis();
        imageView.postDelayed(runnable, 0);

        // Memulai timer mundur dengan waktu 36 detik
        startTimer(36000);

        // Menambahkan listener untuk btnPlayer1
        btnPlayer1.setOnClickListener(view -> playerClickedButton(view));

        // Menambahkan listener untuk btnPlayer2
        btnPlayer2.setOnClickListener(view -> playerClickedButton(view));

        // Memulai klik acak pada btnPlayer2 setiap 2-7 detik jika sedang bermain dengan bot
        if (isPlayingWithBot) {
            btnPlayer2.setVisibility(View.GONE);
            startButtonRandomClick(2000, 7000);
        } else {
            btnPlayer2.setVisibility(View.VISIBLE);
        }
    }

    // Method untuk membuat Runnable yang akan mengubah gambar acak setiap 50 milidetik
    private Runnable createRandomImageRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                // Menghitung waktu random untuk mengubah gambar
                int randomMillis = random.nextInt(1501) + 3000;

                // Menghentikan Runnable ketika mencapai waktu random
                if (System.currentTimeMillis() - startTime >= randomMillis) {
                    randomSFX.pause();
                    imageView.removeCallbacks(this);
                    isRunning = false;
                    startTime = 0;
                } else {
                    // Mengubah gambar acak dan melanjutkan pemutaran gambar
                    int randomNumbers = random.nextInt(randomImages.length);
                    randomImage = randomImages[randomNumbers];
                    imageView.setImageResource(randomImage);
                    randomSFX.start();
                    imageView.postDelayed(this, 50);
                }
            }
        };
    }

    private void startGameProgressChecker() {
        mHandler = new Handler();

        // Menjalankan putaran baru dalam 8 detik ketika tidak ada aksi
        startNewRoundIfIdle = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    //menghindari penambahan score dari putaran sebelumnya setelah putaran baru
                    isGameInProgress = false;
                    isRunning = true;

                    //memulai putaran baru
                    startTime = System.currentTimeMillis();
                    imageView.postDelayed(runnable, 0);
                }
                mHandler.postDelayed(this, 8000);
            }
        };

        // Menjalankan handler untuk mengecek apakah permainan sedang berlangsung
        mHandler.post(startNewRoundIfIdle);
    }


    public void setAnimation(View view) {
        if (view.getId() == R.id.btn_player_1) {
            // Mulai animasi lompat pemain 1 dengan frame lompat dan frame diam
            startJumpAnimation(player1ImageView, player1JumpFrames, player1IdleFrames);
            isPlayer1 = true;
        } else if (view.getId() == R.id.btn_player_2) {
            // Mulai animasi lompat pemain 2 dengan frame lompat dan frame diam
            startJumpAnimation(player2ImageView, player2JumpFrames, player2IdleFrames);
            isPlayer1 = false;
        }

        // Mulai efek suara lompat
        jumpSFX.start();
    }

    public void playerClickedButton(View view) {
        // Menentukan animasi yang akan digunakan berdasarkan tombol yang ditekan
        setAnimation(view);

        // Jika tombol ditekan saat putaran sedang berlangsung, maka tombol dinonaktifkan sementara
        if (isRunning || isGameInProgress) {
            view.setEnabled(false);
            btnPlayer1.setTextColor(Color.parseColor("#FFFFFF"));
            btnPlayer2.setTextColor(Color.parseColor("#FFFFFF"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, 3500);
        } else {
            // Jika putaran terhenti
            isGameInProgress = true;
            isRunning = true;

            // Memperbarui skor dan menampilkan nilai skor di layar
            updateScore(isPlayer1);

            // Memulai putaran baru dalam 2 detik
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageView.postDelayed(runnable, 0);
                    startTime = System.currentTimeMillis();
                    isGameInProgress = false;
                }
            }, 2000);
        }
    }

    public void updateScore(boolean isPlayer1) {
        // Mendefinisikan konstanta untuk skor tiap item
        final int ONE_COIN = 1;
        final int TWO_COIN = 2;
        final int THREE_COIN = 3;
        final int FIVE_COIN = 5;
        final int ONE_BOMB = -1;
        final int THREE_BOMB = -3;
        final int FIVE_BOMB = -5;

        // Memperbarui skor berdasarkan item yang dipilih
        if (randomImage == R.drawable.coin_1) {
            score = ONE_COIN;
            coinSFX.start();
        } else if (randomImage == R.drawable.coin_2) {
            score = TWO_COIN;
            coinSFX.start();
        } else if (randomImage == R.drawable.coin_3) {
            score = THREE_COIN;
            coinSFX.start();
        } else if (randomImage == R.drawable.coin_5) {
            score = FIVE_COIN;
            coinSFX.start();
        } else if (randomImage == R.drawable.bomb_1) {
            score = ONE_BOMB;
            bombSFX.start();
        } else if (randomImage == R.drawable.bomb_3) {
            score = THREE_BOMB;
            bombSFX.start();
        } else if (randomImage == R.drawable.bomb_5) {
            score = FIVE_BOMB;
            bombSFX.start();
        }


        // Menambahkan skor ke pemain yang sesuai dan memperbarui tampilan skor
        if (isPlayer1) {
            player1Score += score;
            player1ScoreTextView.setText("Player 1: " + String.valueOf(player1Score));
            setAnimationForTextView(player1AddScore);
        } else {
            player2Score += score;
            player2ScoreTextView.setText("Player 2: " + String.valueOf(player2Score));
            setAnimationForTextView(player2AddScore);
        }
    }

    // Method untuk mengklik tombol secara acak dengan delay antara minDelay dan maxDelay (dalam milidetik)
    private void startButtonRandomClick(final int minDelay, final int maxDelay) {
        // Menggunakan Handler dan Runnable untuk menjalankan klik tombol secara berulang-ulang secara acak dengan delay acak
        botHandler = new Handler();
        botHandler.postDelayed(() -> {
            btnPlayer2.performClick();
            // Pemanggilan fungsi diri sendiri (recursion) digunakan untuk menjalankan kembali klik tombol secara berulang-ulang.
            startButtonRandomClick(minDelay, maxDelay);
        }, new Random().nextInt(maxDelay - minDelay + 1) + minDelay);
    }

    public void startTimer(int millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture, 1000) { // Waktu total 30 detik, hitung mundur setiap 1 detik
            public void onTick(long millisUntilFinished) {
                // Update tampilan hitung mundur pada setiap tick (1 detik)
                secondsLeft = (int) millisUntilFinished / 1000;
                timerPlayer1.setText(String.valueOf(secondsLeft));
                timerPlayer2.setText(String.valueOf(secondsLeft));
            }

            public void onFinish() {
                // Ketika hitung mundur selesai (waktu habis)
                // Mengatur timer pemain 1 dan 2 ke nilai 0
                timerPlayer1.setText(String.valueOf(0));
                timerPlayer2.setText(String.valueOf(0));

                // Menghentikan permainan
                stopGame();

                // Membuat delay selama 1,5 detik sebelum memulai aktivitas GameOver
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Membuat Intent untuk memulai aktivitas GameOver dan mengirimkan data skor dan informasi lainnya
                        Intent intent = new Intent(GamePlayActivity.this, GameOverActivity.class);
                        intent.putExtra(KEY_PLAYER_1_SCORE, player1Score);
                        intent.putExtra(KEY_PLAYER_2_SCORE, player2Score);
                        intent.putExtra(KEY_IS_WITH_BOT, isPlayingWithBot);
                        finish();
                        startActivity(intent);
                    }
                }, 1500);
            }
        }.start();
    }

    // Membuat method untuk memulai kembali permainan
    public void restartGame() {
        // Menghentikan permainan
        stopGame();

        // Memulai permainan kembali dari awal
        startGame();

        player1ScoreTextView.setText("Player 1: " + String.valueOf(player1Score));
        player2ScoreTextView.setText("Player 2: " + String.valueOf(player2Score));
    }

    // Method untuk menghentikan sementara permainan
    private void pauseGame() {
        imageView.removeCallbacks(runnable);
        randomSFX.pause();

        // Menghentikan countdown timer dan runnable handler
        countDownTimer.cancel();
        mHandler.removeCallbacksAndMessages(null);

        // Menghentikan bot handler jika sedang bermain dengan bot
        if (isPlayingWithBot) {
            botHandler.removeCallbacksAndMessages(null);
        }

        // Menampilkan dialog pause game.
        setDialog();
    }

    // Method untuk melanjutkan permainan setelah di-pause
    public void resumeGame() {
        // Melanjutkan timer dan mengecek status game apakah sedang berjalan atau tidak
        startTimer(secondsLeft*1000);
        mHandler.post(startNewRoundIfIdle);
        startTime = System.currentTimeMillis();
        // Memulai menampilkan gambar acak dengan jeda 0 milidetik
        mHandler.postDelayed(runnable, 0);

        // Mmulai kembali handler button bot jika bermain bersama bot
        if (isPlayingWithBot) {
            startButtonRandomClick(2000,7000);
        }
    }

    // Method untuk menghentikan permainan dan membersihkan state-game serta mempersiapkan untuk putaran baru
    private void stopGame() {
        // Menghentikan efek suara random ketika permainan dihentikan
        randomSFX.pause();

        // Menonaktifkan tombol pemain agar tidak dapat menekan saat permainan dihentikan
        btnPlayer1.setEnabled(false);
        btnPlayer2.setEnabled(false);

        // Mengubah warna teks tombol pemain menjadi putih
        btnPlayer1.setTextColor(Color.parseColor("#FFFFFF"));
        btnPlayer2.setTextColor(Color.parseColor("#FFFFFF"));

        // Membersihkan handler bot jika permainan sedang dimainkan dengan bot
        if (botHandler != null) {
            botHandler.removeCallbacksAndMessages(null);
        }

        // Membersihkan timer countdown
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Membersihkan handler putaran game
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        // Menyetel status game dan putaran menjadi false dan memulai dari awal (time = 0)
        isRunning = false;
        isGameInProgress = false;
        startTime = 0;
    }

    public void setDialog() {
        // Membuat objek AlertDialog dan memasang layout dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.pause_dialog_layout, null);
        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Mengambil tombol replay pada tampilan dialog dan menambahkan aksi ketika diklik
        ImageButton btnReplay = view.findViewById(R.id.btn_replay);
        btnReplay.setOnClickListener(v -> {
            // Keluar dari dialog lalu memulai permainan baru
            dialog.dismiss();
            restartGame();
        });

        ImageButton btnResume = view.findViewById(R.id.btn_resume);
        btnResume.setOnClickListener(v -> {
            // Keluar dari dialog lalu melanjutkan permainan
            dialog.dismiss();
            resumeGame();
        });

        ImageButton btnHome = view.findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            // Menghentikan game dan membuat intent untuk berpindah ke HomeActivity
            stopGame();
            Intent intent = new Intent(GamePlayActivity.this, HomeActivity.class);
            // semua aktivitas sebelumnya dihapus dan activity baru dijalankan dalam task yang baru
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private AnimationDrawable createAnimation(int[] frames) {
        // Membuat objek AnimationDrawable baru dengan menambahkan
        // setiap frame yang diambil dari array "frames" ke dalam objek animasi
        AnimationDrawable animation = new AnimationDrawable();
        for (int frame : frames) {
            animation.addFrame(getResources().getDrawable(frame), 100);
        }

        // Mengembalikan objek AnimationDrawable yang sudah terisi dengan frame dan durasi yang sesuai.
        return animation;
    }

    private void startIdleAnimation(ImageView playerImageView, int[] idleFrames) {
        // Membuat objek AnimationDrawable untuk animasi diam
        AnimationDrawable idleAnimation = createAnimation(idleFrames);
        playerImageView.setImageDrawable(idleAnimation);
        // Menjalankan animasi diam
        idleAnimation.start();
    }

    private void startJumpAnimation(ImageView playerImageView, int[] jumpFrames, int[] idleFrames) {
        // Membuat objek AnimationDrawable untuk animasi lompat
        AnimationDrawable jumpAnimation = createAnimation(jumpFrames);
        playerImageView.setImageDrawable(jumpAnimation);
        jumpAnimation.start();

        // Menjalankan animasi diam setelah animasi lompat selesai
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startIdleAnimation(playerImageView, idleFrames);
            }
        }, jumpAnimation.getDuration(0) * jumpAnimation.getNumberOfFrames());
    }

    private void setAnimationForTextView (TextView textView) {
        textView.setVisibility(View.VISIBLE); // Menampilkan TextView

        // Mengatur warna dan isi teks pada TextView
        if (score > 0) {
            textView.setTextColor(Color.parseColor("#32CD32"));
            textView.setText("+" + String.valueOf(score));
        } else {
            textView.setTextColor(Color.parseColor("#ED4337"));
            textView.setText(String.valueOf(score));
        }

        // Membuat ObjectAnimator
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "translationY", 0f, -100f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(textView, "translationY", 0f, 100f);
        if (isPlayer1) {
            animator.setDuration(1500); // Mengatur durasi animasi selama 1,5 detik

            // Listener animasi untuk menghapus TextView setelah animasi selesai
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textView.setVisibility(View.GONE);
                }
            });

            // Memulai animasi
            animator.start();
        } else {
            animator2.setDuration(1500); // Mengatur durasi animasi selama 1,5 detik

            // Listener animasi untuk menghapus TextView setelah animasi selesai
            animator2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textView.setVisibility(View.GONE);
                }
            });
            // Memulai animasi
            animator2.start();
        }
    }

    // Menjeda permainan saat pemain menekan tombol back
    @Override
    public void onBackPressed() {
        pauseGame();
    }
}