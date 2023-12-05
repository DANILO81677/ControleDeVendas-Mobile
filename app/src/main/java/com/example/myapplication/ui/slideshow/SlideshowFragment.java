package com.example.myapplication.ui.slideshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.R;
import com.example.myapplication.database.SQDataBaseHelperConfig;
import com.example.myapplication.service.CustomerService;
import com.example.myapplication.ui.funcionarios.RendimentosFuncionario;
import com.example.myapplication.util.Const;

import java.time.LocalDate;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openDatabase;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.database.sqlite.SQLiteDatabase;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    public Button botaoCadastro;

    private int idSelecionado;

    private String nomeselecionado;

    public ListView listViewDados;
    public ArrayList<Integer> arrayIds;

    private SQDataBaseHelperConfig db;
    //db = new SQDataBaseHelperConfig(getActivity());

    private ArrayList<String> linhas;

    private CustomerService service;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db =  new SQDataBaseHelperConfig(getActivity());
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        botaoCadastro = (Button) root.findViewById(R.id.buttonCadastrar) ;
        listViewDados = root.findViewById(R.id.list_view_clientes);

        listarDados();

        listViewDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = linhas.get(i);
                nomeselecionado = name;
                idSelecionado = arrayIds.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("Cliente : " + name);
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setMessage(Const.MGS_BOX_CLIENTES);
                msgBox.setNegativeButton("Visualizar Dados", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // excluir();
                       // listarDados();
                        abrirTelaAlterar(view);
                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarCliente(idSelecionado);
                    }
                });

                msgBox.show();

            }
        });

        listViewDados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                idSelecionado = arrayIds.get(position);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Visualizar Comandas", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        abrirRendimentos(view, idSelecionado);

                    }
                });
                msgBox.show();
                return true;
            }
        });





        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            abrirTelaCadastro(v);
            }
        });

       // final TextView textView = root.findViewById(R.id.textView3);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
              //  textView.setText(s);
            }
        });


        return root;
    }
    private void abrirRendimentos(View view, Integer idSelecionado) {
        Intent intent = new Intent(view.getContext(), ListarTodasComandasCliente.class);
        intent.putExtra("customerId",idSelecionado);
        startActivity(intent);
    }

    private void deletarCliente(int idSelecionado) {
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            String sql = "DELETE FROM tbcustomer WHERE customerId = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(getActivity());
            msgBox.setTitle("Excluido com Sucesso !");
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

    public void abrirTelaCadastro(View v){
        Intent intent = new Intent(v.getContext(), CadastroCliente.class);
        startActivity(intent);
    }

    public void listarDados (){
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbcustomer order by customerName asc", null);
            linhas = new ArrayList<String>();
            arrayIds = new ArrayList<Integer>();
            ArrayAdapter meuAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listViewDados.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                linhas.add(meuCursor.getString(1));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }
            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void abrirTelaAlterar(View v){
        Intent intent = new Intent(v.getContext(), VisualizarDadosCliente.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    private void closefragment() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();

    }


}