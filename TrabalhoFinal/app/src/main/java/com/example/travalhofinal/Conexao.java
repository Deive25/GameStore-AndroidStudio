package com.example.travalhofinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Conexao extends SQLiteOpenHelper{
    private static final String name = "banco.db";
    private static final int version = 1;

    public Conexao(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela Jogo
        db.execSQL("CREATE TABLE jogo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "preco REAL, " +
                "quantidade INTEGER, " +
                "plataforma TEXT, " +
                "generos TEXT, " +
                "publicadora TEXT, " +
                "status TEXT, " +
                "descricao TEXT, " +
                "foto_bytes BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Método para gerenciar atualizações do banco de dados
    }
}
