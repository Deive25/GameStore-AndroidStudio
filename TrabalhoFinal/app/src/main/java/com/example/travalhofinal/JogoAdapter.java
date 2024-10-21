package com.example.travalhofinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class JogoAdapter extends ArrayAdapter<Jogo> {
    private Context context;
    private List<Jogo> jogos;

    public JogoAdapter(Context context, List<Jogo> jogos) {
        super(context, R.layout.list_item, jogos);
        this.context = context;
        this.jogos = jogos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.text_item);
        TextView subitemText = convertView.findViewById(R.id.text_subitem);
        ImageView imageView = convertView.findViewById(R.id.image_view);

        Jogo jogo = jogos.get(position);
        itemText.setText(jogo.getTitulo());
        subitemText.setText("Preço: " + String.valueOf(jogo.getPreco()) + "R$");

        byte[] fotoBytes = jogo.getFotoBytes();
        if (fotoBytes != null && fotoBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
            imageView.setImageBitmap(bitmap);
        }

        return convertView;
    }
}


