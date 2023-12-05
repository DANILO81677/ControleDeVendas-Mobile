package com.example.myapplication.ui.tablePrices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.database.SQDataBaseHelperConfig;
import com.example.myapplication.ui.slideshow.CadastroCliente;
import com.example.myapplication.util.Const;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class TablePricesFragment extends Fragment {

    private TablePricesViewModel tableViewModel;

    public ListView listViewDados1;
    public ListView listViewDados2;
    public ArrayList<String> linhas;
    private int idSelecionado;
    public Button botaoProdutos;
    public Button botaoServicos;
    public ArrayList<Integer> arrayIds;
    public ArrayList<Integer> arrayIdsProdutos;

    private SQDataBaseHelperConfig db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        db =  new SQDataBaseHelperConfig(getActivity());
        tableViewModel =
                new ViewModelProvider(this).get(TablePricesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tableprices, container, false);
        final TextView textView = root.findViewById(R.id.textViewTable);
        textView.setVisibility(View.INVISIBLE);
        botaoProdutos = root.findViewById(R.id.buttonProduct);
        botaoServicos = root.findViewById(R.id.buttonService);
        listViewDados1 = root.findViewById(R.id.list1);
        listarDados1 ();
        listViewDados2 = root.findViewById(R.id.list2);
        listarDados2();
        listViewDados2.setVisibility(View.INVISIBLE);
        listViewDados1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //String name = linhas.get(i);
                idSelecionado = arrayIdsProdutos.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja Fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Alterar Dados", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        abrirTelaAlterarProduto(view);

                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    deletarproduto(idSelecionado);
                    }
                });
                msgBox.show();
        }});
        listViewDados2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               // String name = linhas.get(i);
               idSelecionado = arrayIds.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Alterar Dados", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        abrirTelaAlterarServico(view);

                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    deletarServico(idSelecionado);
                    }
                });

                msgBox.show();
            }});

        botaoProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastroProduto(v);
            }
        });

        botaoServicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastroServico(v);
            }
        });




        tableViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });



         TabLayout tabLayout = ( TabLayout ) root.findViewById (R.id.simpleTabLayout); // obtém a referência de TabLayout
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tabLayout . getSelectedTabPosition (); // obtém a posição para a guia atual selecionada



                if (selectedTabPosition == 0){
                    listViewDados2.setVisibility(View.INVISIBLE);
                    listViewDados1.setVisibility(View.VISIBLE);
                    //Toast.makeText(getContext(), "Selecionado Produtos"+selectedTabPosition, Toast.LENGTH_SHORT).show();
                } else if (selectedTabPosition ==1){
                    listViewDados1.setVisibility(View.INVISIBLE);
                    listViewDados2.setVisibility(View.VISIBLE);
                    //Toast.makeText(getContext(), "Selecionado Serviços"+selectedTabPosition, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

    public void listarDados1 (){
        try {
            ArrayList<HashMap<String, String>> list ;
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbproducts ORDER BY productName", null);
            linhas = new ArrayList<String>();
            arrayIdsProdutos = new ArrayList<Integer>();
           /* ArrayAdapter meuAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    linhas
            );*/
            meuCursor.moveToFirst();
            list = new ArrayList<HashMap<String, String>>();
            ListViewAdapterProdutos meuAdapter = new ListViewAdapterProdutos(getActivity(), list);
            listViewDados1.setAdapter(meuAdapter);
            while(meuCursor!=null){
                //String rowproduto = meuCursor.getString(1) + "                                        " + "R$ "+meuCursor.getString(4 ).toString();
                HashMap<String,String> t1 = new HashMap<String, String>();
                t1.put("A", meuCursor.getString(1));
                t1.put("B", meuCursor.getString(4));
                list.add(t1);
                //linhas.add(rowproduto);
                arrayIdsProdutos.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }



            bancoDados.close();


        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void listarDados2 (){
        try {
            ArrayList<HashMap<String, String>> list ;
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbservices ORDER BY serviceName", null);
            linhas = new ArrayList<String>();
            arrayIds = new ArrayList<Integer>();
            meuCursor.moveToFirst();
            list = new ArrayList<HashMap<String, String>>();
            ListViewAdapterProdutos meuAdapter = new ListViewAdapterProdutos(getActivity(), list);
            listViewDados2.setAdapter(meuAdapter);
            while(meuCursor!=null){
                //String rowproduto = meuCursor.getString(1) + "                                        " + "R$ "+meuCursor.getString(4 ).toString();
                HashMap<String,String> t1 = new HashMap<String, String>();
                t1.put("A", meuCursor.getString(1));
                t1.put("B", meuCursor.getString(4));
                list.add(t1);
                //linhas.add(rowproduto);
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }
            bancoDados.close();

            /*linhas = new ArrayList<String>();
            ArrayAdapter meuAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listViewDados2.setAdapter(meuAdapter);
            for (String linha : Const.LIST_SERVICO) {
                linhas.add(linha);
            }*/

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void abrirTelaCadastroProduto (View v){
        Intent intent = new Intent(v.getContext(), CriarNovoItem.class);
        startActivity(intent);
    }

    public void abrirTelaCadastroServico (View v){
        Intent intent = new Intent(v.getContext(), CriarNovoServico.class);
        startActivity(intent);
    }

    public void abrirTelaAlterarServico (View v){
        Intent intent = new Intent(v.getContext(), AlterarServico.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    private void deletarServico(int idSelecionado) {
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            String sql = "DELETE FROM tbservices WHERE serviceid  = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void abrirTelaAlterarProduto (View v){
        Intent intent = new Intent(v.getContext(), AlterarProduto.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    private void deletarproduto(int idSelecionado) {
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            String sql = "DELETE FROM tbproducts WHERE productid  = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void closefragment() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();

    }
}
