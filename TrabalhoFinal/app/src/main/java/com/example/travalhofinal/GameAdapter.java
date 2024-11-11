package com.example.travalhofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class GameAdapter extends ArrayAdapter<Jogo> {
    private Context context;
    private List<Jogo> jogos;

    public GameAdapter(Context context, List<Jogo> jogos) {
        super(context, R.layout.item_game, jogos);
        this.context = context;
        this.jogos = jogos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_game, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView detailsTextView = convertView.findViewById(R.id.detailsTextView);

        Jogo jogo = jogos.get(position);
        titleTextView.setText(jogo.getTitulo());

        String details = String.format("Publicadora: %s, Plataforma: %s, Quantidade: %d, Status: %s",
                jogo.getPublicadora(),
                jogo.getPlataforma(),
                jogo.getQuantidade(),
                jogo.getStatus());

        detailsTextView.setText(details);

        return convertView;
    }

    public void updateList(List<Jogo> newList) {
        clear();
        addAll(newList);
        notifyDataSetChanged();
    }
}
