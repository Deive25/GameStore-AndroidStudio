package com.example.travalhofinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GameFormActivity extends AppCompatActivity {

    private EditText titleEditText, priceEditText, quantityEditText, genresEditText, publisherEditText, descriptionEditText;
    private Spinner platformSpinner, statusSpinner;
    private Button selectImageButton, saveButton;
    private ImageView gameImageView;
    private ImageButton backButton, refreshButton;
    private Jogo jogo = null;
    private byte[] imageBytes;
    private static final String TAG = "JogoDAO";

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_form);

        initializeViews();
        setupSpinners();
        setupListeners();

        Intent intent = getIntent();
        if (intent.hasExtra("jogo")) {
            jogo = (Jogo) intent.getSerializableExtra("jogo");
            populateFormWithGameData();
        }
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.titleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        genresEditText = findViewById(R.id.genresEditText);
        publisherEditText = findViewById(R.id.publisherEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        platformSpinner = findViewById(R.id.platformSpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveButton = findViewById(R.id.saveButton);
        gameImageView = findViewById(R.id.gameImageView);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
    }

    private void populateFormWithGameData() {
        if (jogo != null) {
            titleEditText.setText(jogo.getTitulo());
            priceEditText.setText(String.valueOf(jogo.getPreco()));
            quantityEditText.setText(String.valueOf(jogo.getQuantidade()));
            genresEditText.setText(jogo.getGeneros());
            publisherEditText.setText(jogo.getPublicadora());
            descriptionEditText.setText(jogo.getDescricao());

            // Configurar o spinner de plataforma
            ArrayAdapter<CharSequence> platformAdapter = (ArrayAdapter<CharSequence>) platformSpinner.getAdapter();
            int platformPosition = platformAdapter.getPosition(jogo.getPlataforma());
            platformSpinner.setSelection(platformPosition);

            // Configurar o spinner de status
            ArrayAdapter<CharSequence> statusAdapter = (ArrayAdapter<CharSequence>) statusSpinner.getAdapter();
            int statusPosition = statusAdapter.getPosition(jogo.getStatus());
            statusSpinner.setSelection(statusPosition);

            // Configurar a imagem
            byte[] imageBytes = jogo.getFotoBytes();
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                gameImageView.setImageBitmap(bitmap);
                this.imageBytes = imageBytes;
            }
        }
    }

    private void setupSpinners() {
        // Setup platform spinner
        ArrayAdapter<CharSequence> platformAdapter = ArrayAdapter.createFromResource(this,
                R.array.platforms_array, android.R.layout.simple_spinner_item);
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(platformAdapter);

        // Setup status spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
    }

    private void setupListeners() {
        selectImageButton.setOnClickListener(v -> openImageChooser());
        saveButton.setOnClickListener(v -> saveGame());
        backButton.setOnClickListener(v -> finish());
        refreshButton.setOnClickListener(v -> resetForm());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

    private void saveGame() {
        BitmapDrawable drawable = (BitmapDrawable) gameImageView.getDrawable();
        if (jogo == null) {
            jogo = new Jogo();
            jogo.setTitulo(titleEditText.getText().toString());
            jogo.setPreco(Double.parseDouble(priceEditText.getText().toString()));
            jogo.setQuantidade(Integer.parseInt(quantityEditText.getText().toString()));
            jogo.setGeneros(genresEditText.getText().toString());
            jogo.setPublicadora(publisherEditText.getText().toString());
            jogo.setDescricao(descriptionEditText.getText().toString());
            String plataformaSelecionada = platformSpinner.getSelectedItem().toString();
            Log.d(TAG, "Plataforma selecionada: " + plataformaSelecionada);
            jogo.setPlataforma(plataformaSelecionada);
            jogo.setStatus(statusSpinner.getSelectedItem().toString());

            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fotoBytes = compressImageForDatabase(bitmap);
                jogo.setFotoBytes(fotoBytes);
            }

            JogoDAO dao = new JogoDAO(this); // Assumindo que você tem uma classe JogoDAO
            long id = dao.inserir(jogo);
            if (id > 0) {
                Toast.makeText(this, "Jogo inserido com sucesso! ID: " + id, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao inserir o jogo", Toast.LENGTH_SHORT).show();
            }
        } else {
            jogo.setTitulo(titleEditText.getText().toString());
            jogo.setPreco(Double.parseDouble(priceEditText.getText().toString()));
            jogo.setQuantidade(Integer.parseInt(quantityEditText.getText().toString()));
            jogo.setGeneros(genresEditText.getText().toString());
            jogo.setPublicadora(publisherEditText.getText().toString());
            jogo.setDescricao(descriptionEditText.getText().toString());
            String plataformaSelecionada = platformSpinner.getSelectedItem().toString();
            Log.d(TAG, "Plataforma selecionada: " + plataformaSelecionada);
            jogo.setPlataforma(plataformaSelecionada);
            jogo.setStatus(statusSpinner.getSelectedItem().toString());

            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fotoBytes = compressImageForDatabase(bitmap);
                jogo.setFotoBytes(fotoBytes);
            }

            JogoDAO dao = new JogoDAO(this);
            boolean success = dao.atualizar(jogo);
            if (success) {
                Toast.makeText(this, "Jogo atualizado com sucesso! ID: " + jogo.getId(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao atualizar o jogo", Toast.LENGTH_SHORT).show();
            }
        }
        Intent it = new Intent(this, MainActivity.class);;
        startActivity(it);
        finish();
    }

    private byte[] compressImageForDatabase(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Comprime a imagem para 50% da qualidade original
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return baos.toByteArray(); // Retorna imagem comprimida em bytes
    }

    private void resetForm() {
        titleEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        genresEditText.setText("");
        publisherEditText.setText("");
        descriptionEditText.setText("");
        platformSpinner.setSelection(0);
        statusSpinner.setSelection(0);
        gameImageView.setImageResource(android.R.color.transparent);
        imageBytes = null;
    }
}