package com.example.travalhofinal;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class JogoDAO {
    private Conexao conexao;
    private SQLiteDatabase banco;

    public JogoDAO(Context context) {
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }

    public long inserir(Jogo jogo) {
        long id = -1;
        ContentValues values = new ContentValues();
        values.put("titulo", jogo.getTitulo());
        values.put("preco", jogo.getPreco());
        values.put("quantidade", jogo.getQuantidade());
        values.put("plataforma", jogo.getPlataforma());
        values.put("generos", jogo.getGeneros());
        values.put("publicadora", jogo.getPublicadora());
        values.put("status", jogo.getStatus());
        values.put("descricao", jogo.getDescricao());
        values.put("foto_bytes", jogo.getFotoBytes());

        id = banco.insert("jogo", null, values);

        return id;
    }

    @SuppressLint("Range")
    public List<Jogo> listar() {
        List<Jogo> jogos = new ArrayList<>();

        Cursor cursor = banco.query("jogo", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Jogo jogo = new Jogo();
            jogo.setId(cursor.getInt(cursor.getColumnIndex("id")));
            jogo.setTitulo(cursor.getString(cursor.getColumnIndex("titulo")));
            jogo.setPreco(cursor.getDouble(cursor.getColumnIndex("preco")));
            jogo.setQuantidade(cursor.getInt(cursor.getColumnIndex("quantidade")));
            jogo.setPlataforma(cursor.getString(cursor.getColumnIndex("plataforma")));
            jogo.setGeneros(cursor.getString(cursor.getColumnIndex("generos")));
            jogo.setPublicadora(cursor.getString(cursor.getColumnIndex("publicadora")));
            jogo.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            jogo.setDescricao(cursor.getString(cursor.getColumnIndex("descricao")));
            jogo.setFotoBytes(cursor.getBlob(cursor.getColumnIndex("foto_bytes")));

            jogos.add(jogo);
        }
        cursor.close();

        return jogos;
    }

    public void excluir(Jogo a){
        banco.delete("jogo", "id = ?", new String[]{a.getId().toString()});
    }

    @SuppressLint("Range")
    public boolean atualizar(Jogo jogo) {
        boolean success = false;

        ContentValues values = new ContentValues();
        values.put("titulo", jogo.getTitulo());
        values.put("preco", jogo.getPreco());
        values.put("quantidade", jogo.getQuantidade());
        values.put("plataforma", jogo.getPlataforma());
        values.put("generos", jogo.getGeneros());
        values.put("publicadora", jogo.getPublicadora());
        values.put("status", jogo.getStatus());
        values.put("descricao", jogo.getDescricao());
        values.put("foto_bytes", jogo.getFotoBytes());

        int rowsAffected = banco.update("jogo", values, "id = ?",
                new String[]{String.valueOf(jogo.getId())});
        success = rowsAffected > 0;
        return success;
    }
}
