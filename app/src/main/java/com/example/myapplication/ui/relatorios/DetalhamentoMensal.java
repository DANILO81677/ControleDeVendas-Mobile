package com.example.myapplication.ui.relatorios;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.service.ListViewAdapterListarComandasService;
import com.example.myapplication.util.Const;
import com.google.android.material.tabs.TabLayout;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class DetalhamentoMensal extends AppCompatActivity {

    ArrayList<Integer> arrayIds;
    ListView listaReceita;
    ListView listaPendente;
    ListView listaDespesa;
    ListView listaSalario;

    TextView labelReceita;
    TextView labelPendente;
    TextView labelDespesa;
    TextView labelSalario;

    LocalDate dataMes;

    double amountReceita = 0;
    double amountPendente = 0;
    double amountDespesa = 0;
    double amountSalario = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhamento_mensal);
        //lista_relatorio_detalhamento_mensal_receita

        Intent intent = getIntent();
        String dataStr = intent.getStringExtra("data");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dataMes = LocalDate.parse(dataStr, formatter);

        listaReceita = (ListView) findViewById(R.id.lista_relatorio_detalhamento_mensal_receita);
        listaPendente = (ListView) findViewById(R.id.lista_relatorio_detalhamento_mensal_pendente);
        listaDespesa = (ListView) findViewById(R.id.lista_relatorio_detalhamento_mensal_despesa);
        listaSalario = (ListView) findViewById(R.id.lista_relatorio_detalhamento_mensal_salario);

        labelReceita = (TextView) findViewById(R.id.textViewTotalDetalhamentoReceitas);
        labelPendente = (TextView) findViewById(R.id.textViewTotalDetalhamentoPendente);
        labelDespesa = (TextView) findViewById(R.id.textViewTotalDetalhamentoDespesa);
        labelSalario = (TextView) findViewById(R.id.textViewTotalDetalhamentoSalario);


        listaReceita.setVisibility(View.VISIBLE);
        listaPendente.setVisibility(View.INVISIBLE);
        listaDespesa.setVisibility(View.INVISIBLE);
        listaSalario.setVisibility(View.INVISIBLE);

        labelReceita.setVisibility(View.VISIBLE);
        labelPendente.setVisibility(View.INVISIBLE);
        labelDespesa.setVisibility(View.INVISIBLE);
        labelSalario.setVisibility(View.INVISIBLE);

        listaTodasReceitas();
        listaTodasPendentes();
        listaTodasDespesas();
        listaTodasSalario();

        labelReceita.setText("Total: "+NumberFormat.getCurrencyInstance().format(amountReceita));
        labelPendente.setText("Total: "+NumberFormat.getCurrencyInstance().format(amountPendente));
        labelDespesa.setText("Total: "+NumberFormat.getCurrencyInstance().format(amountDespesa));
        labelSalario.setText("Total: "+NumberFormat.getCurrencyInstance().format(amountSalario));

        TabLayout tabLayout = ( TabLayout ) findViewById (R.id.tabLayoutDetalhamentoMensal); // obtém a referência de TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tabLayout . getSelectedTabPosition (); // obtém a posição para a guia atual selecionada



                if (selectedTabPosition == 0){
                    //OVERVIEW
                    listaReceita.setVisibility(View.VISIBLE);
                    listaPendente.setVisibility(View.INVISIBLE);
                    listaDespesa.setVisibility(View.INVISIBLE);
                    listaSalario.setVisibility(View.INVISIBLE);

                    labelReceita.setVisibility(View.VISIBLE);
                    labelPendente.setVisibility(View.INVISIBLE);
                    labelDespesa.setVisibility(View.INVISIBLE);
                    labelSalario.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getContext(), "Selecionado Produtos"+selectedTabPosition, Toast.LENGTH_SHORT).show();
                } else if (selectedTabPosition ==1){
                    //MENSAL
                    listaReceita.setVisibility(View.INVISIBLE);
                    listaPendente.setVisibility(View.VISIBLE);
                    listaDespesa.setVisibility(View.INVISIBLE);
                    listaSalario.setVisibility(View.INVISIBLE);

                    labelReceita.setVisibility(View.INVISIBLE);
                    labelPendente.setVisibility(View.VISIBLE);
                    labelDespesa.setVisibility(View.INVISIBLE);
                    labelSalario.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getContext(), "Selecionado Serviços"+selectedTabPosition, Toast.LENGTH_SHORT).show();
                } else if (selectedTabPosition == 2){
                    //ANUAL
                    listaReceita.setVisibility(View.INVISIBLE);
                    listaPendente.setVisibility(View.INVISIBLE);
                    listaDespesa.setVisibility(View.VISIBLE);
                    listaSalario.setVisibility(View.INVISIBLE);

                    labelReceita.setVisibility(View.INVISIBLE);
                    labelPendente.setVisibility(View.INVISIBLE);
                    labelDespesa.setVisibility(View.VISIBLE);
                    labelSalario.setVisibility(View.INVISIBLE);
                } else if (selectedTabPosition == 3){
                    //ANUAL
                    listaReceita.setVisibility(View.INVISIBLE);
                    listaPendente.setVisibility(View.INVISIBLE);
                    listaDespesa.setVisibility(View.INVISIBLE);
                    listaSalario.setVisibility(View.VISIBLE);

                    labelReceita.setVisibility(View.INVISIBLE);
                    labelPendente.setVisibility(View.INVISIBLE);
                    labelDespesa.setVisibility(View.INVISIBLE);
                    labelSalario.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listaTodasSalario() {
        String dataInicioMes = dataMes.with(TemporalAdjusters.firstDayOfMonth()).toString();
        String dataFimMes = dataMes.with(TemporalAdjusters.lastDayOfMonth()).toString();

        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbworkerearning  where servicedate >= '"+dataInicioMes+"' and servicedate <= '"+dataFimMes+"' order by servicedate desc", null);
            ComandaList item = null;
            //arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaList> list = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaSalario.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            int tamanho = meuCursor.getCount();
            int inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem) {
                inicioPassagem++;
                if (meuCursor.getString(3) != null) {
                    Cursor meuCursorFuncionario = bancoDados.rawQuery("SELECT * from tbworker where workerid = "+meuCursor.getString(2), null);
                    meuCursorFuncionario.moveToFirst();
                    item = new ComandaList();
                    //arrayIds.add(meuCursor.getInt(0));
                    if (meuCursorFuncionario.getCount()>0 && meuCursorFuncionario.getString(1) != null) {
                        item.setNome(meuCursorFuncionario.getString(1));
                    } else {
                        item.setNome("N/D");
                    }
                    meuCursorFuncionario = null;
                    double parsed = Double.parseDouble(meuCursor.getString(3));
                    amountSalario += parsed;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    String vfN = decimalFormat.format(parsed);
                    item.setValor(vfN);
                    item.setStatus("");
                    item.setData(meuCursor.getString(4));
                    list.add(item);

              /*  item.setNome();
                linhas.add(meuCursor.getString(1));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();*/
                }
                meuCursor.moveToNext();
            }


            bancoDados.close();


        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listaTodasDespesas() {
        String dataInicioMes = dataMes.with(TemporalAdjusters.firstDayOfMonth()).toString();
        String dataFimMes = dataMes.with(TemporalAdjusters.lastDayOfMonth()).toString();
        String dataInicioAno = dataMes.with(TemporalAdjusters.firstDayOfYear()).toString();

        try {

            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbanotherexpenses where paymentdate >= '" + dataInicioMes + "' and paymentdate <= '" + dataFimMes+ "' order by paymentdate DESC", null);
            ComandaList item = null;
            Cursor meuCursorMensal = bancoDados.rawQuery("SELECT  * from tbmonthlyexpenses where createdate >='" + dataInicioAno + "' order by createdate DESC ", null);
            meuCursorMensal.moveToFirst();


           //arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaList> list = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaDespesa.setAdapter(meuAdapter);
            int tamanho = meuCursorMensal.getCount();
            int inicioPassagem = 0;
            while(meuCursorMensal!=null && tamanho > inicioPassagem) {
                inicioPassagem++;
                if (meuCursorMensal.getString(3)!= null) {
                    item = new ComandaList();
                    item.setStatus("Mensal");
                    item.setNome(meuCursorMensal.getString(7));
                    item.setData(dataFimMes);
                    double parsed = Double.parseDouble(meuCursorMensal.getString(3));
                    amountDespesa += parsed;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    String vfN = decimalFormat.format(parsed);
                    item.setValor(vfN);
                    list.add(item);
                }
                meuCursorMensal.moveToNext();
            }
            meuCursor.moveToFirst();
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem) {
                inicioPassagem++;
                if (meuCursor.getString(3)!= null) {
                    item = new ComandaList();
                    //arrayIds.add(meuCursor.getInt(0));
                    item.setNome(meuCursor.getString(1));

                    double parsed = Double.parseDouble(meuCursor.getString(3));
                    amountDespesa += parsed;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    String vfN = decimalFormat.format(parsed);
                    item.setValor(vfN);
                    item.setStatus("Variavel");
                    item.setData(meuCursor.getString(2));
                    list.add(item);

                }
                meuCursor.moveToNext();
              /*  item.setNome();
                linhas.add(meuCursor.getString(1));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();*/
            }


            bancoDados.close();


        }  catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listaTodasPendentes() {
        String dataInicioMes = dataMes.with(TemporalAdjusters.firstDayOfMonth()).toString();
        String dataFimMes = dataMes.with(TemporalAdjusters.lastDayOfMonth()).toString();

        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT a.purchaseorderid,b.customerName,a.totalvalue, a.status, a.createdate from tbpurchaseorder a, tbcustomer b where b.customerId = a.customerId and a.finishpayment = 'N' and a.createdate >= '"+dataInicioMes+"' and a.createdate <= '"+dataFimMes+"' order by a.createdate desc", null);
            ComandaList item = null;
            arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaList> list = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaPendente.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            if (meuCursor.getCount() <= 0){
                meuCursor = bancoDados.rawQuery("SELECT a.purchaseorderid,'Cliente',a.totalvalue, a.status, a.createdate from tbpurchaseorder a where a.finishpayment = 'N+" +
                        "' and a.createdate >= '"+dataInicioMes+"' and a.createdate <= '"+dataFimMes+"' order by a.createdate desc", null);
                meuCursor.moveToFirst();
            }
            int tamanho = meuCursor.getCount();
            int inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem) {
                inicioPassagem++;
                if (meuCursor.getString(3) != null) {
                    item = new ComandaList();
                    //arrayIds.add(meuCursor.getInt(0));
                    item.setNome(meuCursor.getString(1));

                    double parsed = Double.parseDouble(meuCursor.getString(2));
                    amountPendente += parsed;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    String vfN = decimalFormat.format(parsed);
                    item.setValor(vfN);
                    item.setStatus(meuCursor.getString(3));
                    item.setData(meuCursor.getString(4));
                    list.add(item);

              /*  item.setNome();
                linhas.add(meuCursor.getString(1));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();*/
                }
                meuCursor.moveToNext();
            }


            bancoDados.close();


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listaTodasReceitas() {

        String dataInicioMes = dataMes.with(TemporalAdjusters.firstDayOfMonth()).toString();
        String dataFimMes = dataMes.with(TemporalAdjusters.lastDayOfMonth()).toString();

        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT a.purchaseorderid,b.customerName,a.totalvalue, a.status, a.createdate from tbpurchaseorder a, tbcustomer b where b.customerId = a.customerId and a.finishpayment = 'S' and a.createdate >= '"+dataInicioMes+"' and a.createdate <= '"+dataFimMes+"' order by a.createdate desc", null);
            ComandaList item = null;
            arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaList> list = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaReceita.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            if (meuCursor.getCount() <= 0){
                meuCursor = bancoDados.rawQuery("SELECT a.purchaseorderid,'Cliente',a.totalvalue, a.status, a.createdate from tbpurchaseorder a where a.finishpayment = 'S' and a.createdate >= '"+dataInicioMes+"' and a.createdate <= '"+dataFimMes+"' order by a.createdate desc", null);
                meuCursor.moveToFirst();
            }
            int tamanho = meuCursor.getCount();
            int inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem) {
                inicioPassagem++;
                if (meuCursor.getString(3) != null) {
                    item = new ComandaList();
                    //arrayIds.add(meuCursor.getInt(0));
                    item.setNome(meuCursor.getString(1));

                    double parsed = Double.parseDouble(meuCursor.getString(2));
                    amountReceita += parsed;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    String vfN = decimalFormat.format(parsed);
                    item.setValor(vfN);
                    item.setStatus(meuCursor.getString(3));
                    item.setData(meuCursor.getString(4));
                    list.add(item);
                }
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