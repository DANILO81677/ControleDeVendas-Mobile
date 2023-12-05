package com.example.myapplication.ui.gallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.entity.ComandaView;
import com.example.myapplication.service.ListViewAdapterListarComandasService;
import com.example.myapplication.util.Const;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class VisualizarComanda extends AppCompatActivity {

    TextView total;
    TextView totalPendente;
    TextView nomeCliente;
    EditText descricao;
    EditText dataServico;
    ListView listaServico;
    ListView listaProduto;
    Button botaoAlterar;
    Button botaoPagar;
    Integer idPrincipal;
    Integer idSelecionado;
    ArrayList<Integer> arrayIdsServico;
    ArrayList<Integer> arrayIdsProduto;
    String quantidadeServico;
    String getQuantidadeProduto;
    ArrayList<ComandaList> listProduto;
    ArrayList<ComandaList> listServico;
    String textTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_comanda);

        totalPendente = (TextView) findViewById(R.id.textViewPendente23);
        total = (TextView) findViewById(R.id.textViewTotal23);
        nomeCliente = (TextView) findViewById(R.id.textViewNomeCliente23);
        descricao = (EditText) findViewById(R.id.editTextTextDescricao23);
        dataServico = (EditText) findViewById(R.id.editTextDate23);
        botaoAlterar = (Button) findViewById(R.id.buttonAlterar23);
        botaoPagar = (Button) findViewById(R.id.buttonPagar23);
        Intent intent = getIntent();
        idPrincipal = intent.getIntExtra("id",0);
        getComanda(idPrincipal);


        listaServico = (ListView) findViewById(R.id.listaservico23);
        listarServicoGet(idPrincipal);
        listaProduto = (ListView) findViewById(R.id.listaproduto23);
        listarProdutoGet(idPrincipal);
        botaoAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterarComandaS(v);
            }
        });

        botaoPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputAnotation = new EditText(v.getContext());
                AlertDialog.Builder msgBox = new AlertDialog.Builder(v.getContext());
                msgBox.setTitle("Quanto está sendo Pago ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setView(inputAnotation);
                msgBox.setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String valor = inputAnotation.getText().toString();
                        pagarUmValor(v, valor);

                    }
                });
                msgBox.setPositiveButton("Pagar Tudo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pagarTudo(v);
                    }
                });

                msgBox.show();
            }
        });



        listaServico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idSelecionado = arrayIdsServico.get(position);
                EditText inputAnotation = new EditText(view.getContext());
                AlertDialog.Builder msgBox = new AlertDialog.Builder(view.getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setView(inputAnotation);
//                msgBox.setNegativeButton("Alterar Quantidade", new DialogInterface.OnClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.O)
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        quantidadeServico = inputAnotation.getText().toString();
//                        alterarQuantidadeServico(view, idSelecionado);
//
//                    }
//                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarServico(idSelecionado);
                    }
                });

                msgBox.show();
            }
        });

        listaProduto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idSelecionado = arrayIdsProduto.get(position);
                EditText inputAnotation = new EditText(view.getContext());
                AlertDialog.Builder msgBox = new AlertDialog.Builder(view.getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setView(inputAnotation);
//                msgBox.setNegativeButton("Alterar Quantidade", new DialogInterface.OnClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.O)
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        getQuantidadeProduto = inputAnotation.getText().toString();
//                        alterarQuantidadeProduto(view, idSelecionado);
//
//                    }
//                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarProduto(idSelecionado);
                    }
                });

                msgBox.show();
            }
        });

    }

    private void alterarComandaS(View v) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "UPDATE tbpurchaseorder SET observation = ?,createdate = ?" +
                    "WHERE purchaseorderid = ?";

            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1,descricao.getText().toString());
            stmt.bindString(2,dataServico.getText().toString());
            stmt.bindLong(3,idPrincipal.longValue());
            stmt.executeUpdateDelete();

            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
            msgBox.setTitle("Alterado com Sucesso !");
            msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    // Faça aqui o que pretende quando o dialog é cancelado
                    closefragment();
                }
            });
            msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closefragment();
                }
            });
            msgBox.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void pagarTudo(View v) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "UPDATE tbpurchaseorder SET paidvalue = ?,status = ?,finishpayment = ?" +
                    "WHERE purchaseorderid = ?";

            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1,textTotal);
            stmt.bindString(2,"PAGO");
            stmt.bindString(3,"S");
            stmt.bindLong(4,idPrincipal.longValue());
            stmt.executeUpdateDelete();

            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
            msgBox.setTitle("Comanda paga com Sucesso !");
            msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    // Faça aqui o que pretende quando o dialog é cancelado
                    closefragment();
                }
            });
            msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closefragment();
                }
            });
            msgBox.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void pagarUmValor(View v, String valor) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "UPDATE tbpurchaseorder SET paidvalue = ?" +
                    "WHERE purchaseorderid = ?";

            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1,valor);
            stmt.bindLong(2,idPrincipal.longValue());
            stmt.executeUpdateDelete();

            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
            msgBox.setTitle("Pago com Sucesso !");
            msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    // Faça aqui o que pretende quando o dialog é cancelado
                    closefragment();
                }
            });
            msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closefragment();
                }
            });
            msgBox.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deletarProduto(Integer idSelecionado) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "DELETE FROM tborderproduct WHERE orderproductid = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado.longValue());
            stmt.executeUpdateDelete();
            Toast.makeText(this, "Deletado com Sucesso", Toast.LENGTH_SHORT).show();
            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
            msgBox.setTitle("Deletado com Sucesso !");
            msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    // Faça aqui o que pretende quando o dialog é cancelado
                    closefragment();
                }
            });
            msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closefragment();
                }
            });
            msgBox.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void alterarQuantidadeProduto(View view, Integer idSelecionado) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "UPDATE tborderproduct SET quantity = ?,updatedate = ?" +
                    "WHERE orderproductid = ?";

            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1,getQuantidadeProduto);
            stmt.bindString(2, LocalDate.now().toString());
            stmt.bindLong(3,idSelecionado.longValue());
            stmt.executeUpdateDelete();


            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
            msgBox.setTitle("Alterado com Sucesso !");
            msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    // Faça aqui o que pretende quando o dialog é cancelado
                    closefragment();
                }
            });
            msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closefragment();
                }
            });
            msgBox.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deletarServico(Integer idSelecionado) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "DELETE FROM tborderservice WHERE orderserviceid = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado.longValue());
            stmt.executeUpdateDelete();
            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
            msgBox.setTitle("Deletado com Sucesso !");
            msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    // Faça aqui o que pretende quando o dialog é cancelado
                    closefragment();
                }
            });
            msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closefragment();
                }
            });
            msgBox.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closefragment() {
        finish();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void alterarQuantidadeServico(View view, Integer idSelecionado) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql = "UPDATE tborderservice SET quantity = ?,updatedate = ?" +
                        "WHERE orderserviceid = ?";

            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1,quantidadeServico);
            stmt.bindString(2, LocalDate.now().toString());
            stmt.bindLong(3,idSelecionado.longValue());
            stmt.executeUpdateDelete();
             bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
            msgBox.setTitle("Alteradp com Sucesso !");
            msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    // Faça aqui o que pretende quando o dialog é cancelado
                    closefragment();
                }
            });
            msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closefragment();
                }
            });
            msgBox.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void listarProdutoGet(Integer idPrincipal) {
        arrayIdsProduto = new ArrayList<>();
        try {

            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT a.orderproductid, b.productname, a.quantity, b.productvalue, a.createdate  from tborderproduct a, tbproducts b where b.productid = a.productid and a.purchaseorderid = " + idPrincipal.toString(), null);
            ArrayList<ComandaList> list = new ArrayList<>();
            listProduto = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaProduto.setAdapter(meuAdapter);
            cursor.moveToFirst();
            while(cursor!=null){
                ComandaList item = new ComandaList();
                arrayIdsProduto.add(cursor.getInt(0));
                item.setNome(cursor.getString(1));
                item.setValor(cursor.getString(3));
                item.setStatus(cursor.getString(2));
                item.setData(cursor.getString(4));
                list.add(item);
                listProduto.add(item);
                cursor.moveToNext();
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

    private void listarServicoGet(Integer idPrincipal) {
        arrayIdsServico = new ArrayList<>();
        try {

            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT a.orderserviceid, b.servicename, a.quantity, b.servicevalue, a.createdate  from tborderservice a, tbservices b where b.serviceid = a.serviceid and a.purchaseorderid = " + idPrincipal.toString(), null);
            ArrayList<ComandaList> list = new ArrayList<>();
            listServico = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(this, list);
            listaServico.setAdapter(meuAdapter);
            cursor.moveToFirst();
            while(cursor!=null){
                ComandaList item = new ComandaList();
                arrayIdsServico.add(cursor.getInt(0));
                item.setNome(cursor.getString(1));
                item.setValor(cursor.getString(3));
                item.setStatus(cursor.getString(2));
                item.setData(cursor.getString(4));
                list.add(item);
                listServico.add(item);
                cursor.moveToNext();
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

    private void getComanda(Integer idPrincipal) {


        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT a.purchaseorderid,a.customerId,a.totalvalue, a.paidvalue, a.status,a.observation, a.finishpayment, a.createdate from tbpurchaseorder a where a.purchaseorderid = " + idPrincipal.toString(), null);
            cursor.moveToFirst();
            Cursor cursorCliente = bancoDados.rawQuery("SELECT * FROM tbcustomer WHERE customerId = " + cursor.getString(1), null);
            cursorCliente.moveToFirst();
            nomeCliente.setText(cursorCliente.getString(1));
            textTotal = cursor.getString(2);
            String totalStrin = "Total da Comanda : R$ "+cursor.getString(2);;
            if (cursor.getString(6).equals("N")){
                double pagoV = Double.parseDouble(cursor.getString(3));
                double tot = Double.parseDouble(cursor.getString(2));
                double result = tot - pagoV;
                totalPendente.setText("Pendente pagamento:  "+ NumberFormat.getCurrencyInstance().format((result)));
                totalPendente.setTextColor(Color.RED);
            }else{
                double pagoV = Double.parseDouble(cursor.getString(3));
                double tot = Double.parseDouble(cursor.getString(2));
             //   double result = tot - pagoV;
                double result = tot;
                totalPendente.setText("Comanda Paga: "+ NumberFormat.getCurrencyInstance().format((result)));
                totalPendente.setTextColor(Color.GREEN);
            }
            total.setText(totalStrin);
            descricao.setText(cursor.getString(5));
            dataServico.setText(cursor.getString(7));
            bancoDados.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}