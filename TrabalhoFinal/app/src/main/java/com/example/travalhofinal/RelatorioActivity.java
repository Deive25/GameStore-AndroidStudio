package com.example.travalhofinal;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.os.Environment;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

public class RelatorioActivity extends AppCompatActivity {

    private GridView gamesGridView;
    private GameAdapter gameAdapter;
    private List<Jogo> jogos;
    private List<Jogo> jogosFiltrados;
    private ImageButton backButton;
    private SearchView searchView;
    private JogoDAO jogoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        backButton = findViewById(R.id.backButton);
        jogoDAO = new JogoDAO(this);
        gamesGridView = findViewById(R.id.gamesGridView);
        searchView = findViewById(R.id.searchView);
        ImageButton generateReportButton = findViewById(R.id.generateReportButton);

        carregarJogos();

        gameAdapter = new GameAdapter(this, jogosFiltrados);
        gamesGridView.setAdapter(gameAdapter);

        setupSearchView();

        generateReportButton.setOnClickListener(v -> generateReport());
        backButton.setOnClickListener(v -> finish());
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterGames(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterGames(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            filterGames("");
            return false;
        });
    }

    private void carregarJogos() {
        try {
            jogos = jogoDAO.listar();
            jogosFiltrados = new ArrayList<>(jogos);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar jogos: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            jogos = new ArrayList<>();
            jogosFiltrados = new ArrayList<>();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarJogos();
        if (gameAdapter != null) {
            gameAdapter.updateList(jogosFiltrados);
        }
    }

    private void filterGames(String query) {
        jogosFiltrados.clear();
        if (query.isEmpty()) {
            jogosFiltrados.addAll(jogos);
        } else {
            for (Jogo jogo : jogos) {
                if (jogo.getTitulo().toLowerCase().contains(query.toLowerCase()) ||
                        jogo.getPublicadora().toLowerCase().contains(query.toLowerCase()) ||
                        jogo.getPlataforma().toLowerCase().contains(query.toLowerCase())) {
                    jogosFiltrados.add(jogo);
                }
            }
        }
        gameAdapter.updateList(jogosFiltrados);
    }

    private void generateReport() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String fileName = "inventory_report_" + timeStamp + ".txt";

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File reportFile = new File(downloadsDir, fileName);

            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(reportFile);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "UTF-8");

            writer.append("RELATÓRIO DE INVENTARIO\n");
            writer.append("=======================\n\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            writer.append("Gerado em: " + dateFormat.format(new Date()) + "\n\n");

            writer.append("RESUMO POR PLATAFORMA\n");
            writer.append("====================\n");
            java.util.Map<String, Integer> jogoPorPlataforma = new java.util.HashMap<>();
            for (Jogo jogo : jogos) {
                jogoPorPlataforma.merge(jogo.getPlataforma(), 1, Integer::sum);
            }
            for (java.util.Map.Entry<String, Integer> entry : jogoPorPlataforma.entrySet()) {
                writer.append(String.format("%s: %d jogos\n", entry.getKey(), entry.getValue()));
            }
            writer.append("\n");

            writer.append("LISTA DETALHADA DE JOGOS\n");
            writer.append("=======================\n\n");
            for (Jogo jogo : jogos) {
                writer.append(String.format("Titulo: %s\n", jogo.getTitulo()));
                writer.append(String.format("Publicadora: %s\n", jogo.getPublicadora()));
                writer.append(String.format("Plataforma: %s\n", jogo.getPlataforma()));
                writer.append(String.format("Generos: %s\n", jogo.getGeneros()));
                writer.append(String.format("Preco: R$ %.2f\n", jogo.getPreco()));
                writer.append(String.format("Quantidade: %d\n", jogo.getQuantidade()));
                writer.append(String.format("Status: %s\n", jogo.getStatus()));
                writer.append("------------------------\n");
            }

            writer.append("\nRESUMO FINANCEIRO\n");
            writer.append("================\n");
            writer.append(String.format("Total de Jogos: %d\n", jogos.size()));

            int totalUnidades = jogos.stream()
                    .mapToInt(Jogo::getQuantidade)
                    .sum();

            double valorTotal = jogos.stream()
                    .mapToDouble(j -> j.getPreco() * j.getQuantidade())
                    .sum();

            writer.append(String.format("Total de Unidades: %d\n", totalUnidades));
            writer.append(String.format("Valor Total do Inventario: R$ %.2f\n", valorTotal));

            writer.close();

            Toast.makeText(this,
                    "Relatório salvo em Downloads:\n" + fileName,
                    Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this,
                    "Erro ao gerar relatório: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}