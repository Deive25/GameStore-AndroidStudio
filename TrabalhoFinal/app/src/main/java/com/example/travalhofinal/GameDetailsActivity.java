package com.example.travalhofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GameDetailsActivity extends AppCompatActivity {

    private ImageView gameImageView;
    private TextView gameTitleTextView, gamePriceTextView, gameQuantityTextView,
            gamePlatformTextView, gameGenresTextView, gamePublisherTextView,
            gameStatusTextView, gameDescriptionTextView;
    private byte[] imageBytes;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        initializeViews();
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        Jogo jogo = (Jogo) getIntent().getSerializableExtra("jogo");
        if (jogo != null) {
            displayGameDetails(jogo);
        }
    }

    private void initializeViews() {
        gameImageView = findViewById(R.id.gameImageView);
        gameTitleTextView = findViewById(R.id.gameTitleTextView);
        gamePriceTextView = findViewById(R.id.gamePriceTextView);
        gameQuantityTextView = findViewById(R.id.gameQuantityTextView);
        gamePlatformTextView = findViewById(R.id.gamePlatformTextView);
        gameGenresTextView = findViewById(R.id.gameGenresTextView);
        gamePublisherTextView = findViewById(R.id.gamePublisherTextView);
        gameStatusTextView = findViewById(R.id.gameStatusTextView);
        gameDescriptionTextView = findViewById(R.id.gameDescriptionTextView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                gameImageView.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageBytes = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayGameDetails(Jogo jogo) {
        gameTitleTextView.setText(jogo.getTitulo());
        gamePriceTextView.setText(String.valueOf(jogo.getPreco()));
        gameQuantityTextView.setText(String.valueOf(jogo.getQuantidade()));
        gamePlatformTextView.setText(jogo.getPlataforma());
        gameGenresTextView.setText(jogo.getGeneros());
        gamePublisherTextView.setText(jogo.getPublicadora());
        gameStatusTextView.setText(jogo.getStatus());
        gameDescriptionTextView.setText(jogo.getDescricao());
        BitmapDrawable drawable = (BitmapDrawable) gameImageView.getDrawable();

        byte[] imageBytes = jogo.getFotoBytes();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            gameImageView.setImageBitmap(bitmap);
            this.imageBytes = imageBytes;
        }
    }
}