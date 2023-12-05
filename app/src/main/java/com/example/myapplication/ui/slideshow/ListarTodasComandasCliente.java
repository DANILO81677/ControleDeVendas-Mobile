package com.example.myapplication.ui.slideshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.database.SQDataBaseHelperConfig;
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.service.ListViewAdapterListarComandasService;
import com.example.myapplication.ui.gallery.VisualizarComanda;
import com.example.myapplication.util.Const;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ListarTodasComandasCliente extends AppCompatActivity {
    double amountCustomer = 0;
    ListView listaComandas;
    public ArrayList<Integer> arrayIds;
    Integer idSelecionado;
    Integer customerId;
    TextView valorTotal;
    private SQDataBaseHelperConfig db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_todas_comandas_cliente);
        valorTotal = (TextView) findViewById(R.id.labelTotalClienteJafeito);
        listaComandas = (ListView) findViewById(R.id.lista_comandas_por_cliente);
        Intent intent = getIntent();
        customerId = intent.getIntExtra("customerId",0);
        listarComandas();
        valorTotal.setText("Valor total ja produzido : "+ NumberFormat.getCurrencyInstance().format((amountCustomer)));

        listaComandas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // String name = linhas.get(i);
                idSelecionado = arrayIds.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(view.getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Visualizar Comanda", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        abrirComanda(view, idSelecionado);

                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        excluirComandaAtiva(idSelecionado);
                    }
                });

                msgBox.show();
            }});
    }

    public void listarComandas (){

        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT *  from tbpurchaseorder where customerId = "+customerId+"  order by createdate desc", null);
            ComandaList item = null;
            Cursor cursorCliente = bancoDados.rawQuery("SELECT * FROM tbcustomer WHERE customerId = " + customerId.toString(), null);
            cursorCliente.moveToFirst();
            arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaList> list = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaComandas.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                item = new ComandaList();
                arrayIds.add(meuCursor.getInt(0));
                item.setNome(cursorCliente.getString(1));

                double parsed = Double.parseDouble(meuCursor.getString(3));
                amountCustomer += parsed;


                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                String vfN = decimalFormat.format(parsed);
                item.setValor(vfN);
                item.setStatus(meuCursor.getString(2));
                item.setData(meuCursor.getString(8));
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

    private void excluirComandaAtiva(Integer idSelecionado) {
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sqlproduct = "DELETE FROM tborderproduct WHERE purchaseorderid = ?";
            SQLiteStatement stmtOrderProduct = bancoDados.compileStatement(sqlproduct);
            stmtOrderProduct.bindLong(1,idSelecionado);
            stmtOrderProduct.executeUpdateDelete();

            String sqlOrderService = "DELETE FROM tborderservice WHERE purchaseorderid = ?";
            SQLiteStatement stmtOrderService = bancoDados.compileStatement(sqlOrderService);
            stmtOrderService.bindLong(1,idSelecionado);
            stmtOrderService.executeUpdateDelete();

            String sql = "DELETE FROM tbpurchaseorder WHERE purchaseorderid = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            Toast.makeText(this, "Comanda Deletada !", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void abrirComanda(View view, Integer idSelecionado) {
        Intent intent = new Intent(view.getContext(), VisualizarComanda.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }
}