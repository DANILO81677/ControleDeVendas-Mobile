package com.example.myapplication.ui.funcionarios;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.service.ListViewAdapterListarComandasService;
import com.example.myapplication.util.Const;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RendimentosFuncionario extends AppCompatActivity {

    TextView totalGanho;
    TextView totalPendente;
    ListView listaService;
    ArrayList<Integer> arrayIds;
    SQLiteDatabase bancoDados;
    int pendente = 0;
    int totalJaGanho = 0;
    Integer idBanco;
    Integer idSelecionado;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendimentos_funcionario);
        listaService = (ListView) findViewById(R.id.listRendimentosmes);
        Intent intent = getIntent();
        idBanco = intent.getIntExtra("id",0);
        listaServicos(idBanco);
        totalGanho = (TextView) findViewById(R.id.textViewTotalGanhoMes);
        totalGanho.setTextColor(Color.GREEN);
        totalGanho.setText("Total de Rendimentos nesse mes : R$ "+totalJaGanho);
        totalGanho.setTextSize(20);
        totalPendente = (TextView) findViewById(R.id.textViewTotalPendenteMes);
        totalPendente.setTextColor(Color.RED);
        totalPendente.setText("Total a Receber nesse mes : R$ "+pendente);
        totalPendente.setTextSize(20);
        listaService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // String name = linhas.get(i);
                idSelecionado = arrayIds.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(view.getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Pagar Funcionario", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pagarRendimento(view, idSelecionado);

                    }
                });
                msgBox.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                msgBox.show();
            }});

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pagarRendimento(View view, Integer idSelecionado) {
        try{
            bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "UPDATE  tbworkerearning SET paid = ?, updatedate = ? " +
                    "WHERE workerearningId = ? ";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1,"S");
            stmt.bindString(2, LocalDate.now().toString());
            stmt.bindLong(3, idSelecionado.longValue());
            stmt.executeUpdateDelete();
            Toast.makeText(this, "Pago com Sucesso", Toast.LENGTH_SHORT).show();
            bancoDados.close();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void listaServicos (Integer id){
        LocalDate hoje = LocalDate.now();
        try {
            bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbworkerearning  where workerId = "+id+" order by serviceDate desc", null);
            ComandaList item = null;
            arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaList> list = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaService.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                item = new ComandaList();
                arrayIds.add(meuCursor.getInt(0));
                item.setNome(meuCursor.getString(0));
                item.setValor(meuCursor.getString(3));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String  dataConsulta  = meuCursor.getString(4);
                LocalDate dateStart = LocalDate.parse(dataConsulta, formatter);


                String status = "PAGO";
                if(hoje.getMonthValue() == dateStart.getMonthValue()) {
                    double total = Double.parseDouble(meuCursor.getString(3));
                    totalJaGanho += total;

                    if (meuCursor.getString(5).equals("N")) {
                        status = "Pendente";
                        Float value = Float.parseFloat(meuCursor.getString(3));
                        pendente += value;
                    }
                }
                item.setStatus(status);
                item.setData(meuCursor.getString(4));
                list.add(item);
                meuCursor.moveToNext();
              /*  item.setNome();
                linhas.add(meuCursor.getString(1));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();*/
            }
            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }


    }


}