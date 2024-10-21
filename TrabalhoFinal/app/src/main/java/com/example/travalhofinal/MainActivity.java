package com.example.travalhofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridView listView;
    private JogoDAO dao;
    private List<Jogo> jogos;
    private List<Jogo> jogosFiltrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listaJogos);
        dao = new JogoDAO(this);
        jogos = dao.listar();
        jogosFiltrados = new ArrayList<>();
        jogosFiltrados.addAll(jogos);
        JogoAdapter adaptador = new JogoAdapter(this, jogos);

        listView.setAdapter(adaptador);
        registerForContextMenu(listView);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto, menu);
    }

    public void excluir(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        final Jogo jogoExcluir = jogosFiltrados.get(menuInfo.position);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Realmente deseja excluir o jogo?")
                .setNegativeButton("NÃO",null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jogosFiltrados.remove(jogoExcluir);
                        jogos.remove(jogoExcluir);
                        dao.excluir(jogoExcluir);
                        listView.invalidateViews();
                    }
                } ).create();
        dialog.show();
    }

    public void atualizar(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Jogo jogoAtualizar = jogosFiltrados.get(menuInfo.position);
        Intent it = new Intent(this, GameFormActivity.class);
        it.putExtra("jogo", jogoAtualizar);
        startActivity(it);
    }

    public void detalhes(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Jogo jogo = jogosFiltrados.get(menuInfo.position);
        Intent it = new Intent(this, GameDetailsActivity.class);
        it.putExtra("jogo", jogo);
        startActivity(it);
    }

    public void irParaInserir(View view) {
        Intent intent = new Intent(this, GameFormActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e){
            Log.d("NumberGenerated", e.toString());
        }
    }
}