package com.example.myapplication.ui.funcionarios;

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
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.entity.ComandaView;
import com.example.myapplication.service.ListViewAdapterComandaService;
import com.example.myapplication.service.ListViewAdapterFuncionariosService;
import com.example.myapplication.service.ListViewAdapterListarComandasService;
import com.example.myapplication.ui.despezas.AlterarDespesa;
import com.example.myapplication.ui.despezas.DespezasModel;
import com.example.myapplication.ui.gallery.CriarComanda;

import java.util.ArrayList;
import java.util.HashMap;

public class FuncionariosFragment extends Fragment {

    private FuncionariosModel funcionariosViewModel;

    public ArrayList<Integer> arrayIds;

    private SQDataBaseHelperConfig db;

    Button botaoAbreTelaNovoFuncionario;

    ListView listaFuncionarios;

    Integer idSelecionado;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db =  new SQDataBaseHelperConfig(getActivity());
        funcionariosViewModel =
                new ViewModelProvider(this).get(FuncionariosModel.class);
        View root = inflater.inflate(R.layout.fragment_funcionarios, container, false);

       /* funcionariosViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        botaoAbreTelaNovoFuncionario = (Button) root.findViewById(R.id.buttonCadastraNovoFuncioanrio);
        botaoAbreTelaNovoFuncionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastro(v);
            }
        });

        listaFuncionarios = (ListView) root.findViewById(R.id.listViewlistarfuncionarios);
        listarTodosFuncionarios();
        listaFuncionarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // String name = linhas.get(i);
                idSelecionado = arrayIds.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Alterar Funcionario", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alterarFuncionario(view);

                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarFuncionario(idSelecionado);
                    }
                });

                msgBox.show();
            }});
        listaFuncionarios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                idSelecionado = arrayIds.get(position);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Visualizar Rendimentos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        abrirRendimentos(view, idSelecionado);

                    }
                });
                msgBox.show();
                return true;
            }
        });


        return root;
    }

    private void abrirRendimentos(View view, Integer idSelecionado) {
        Intent intent = new Intent(view.getContext(), RendimentosFuncionario.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    private void alterarFuncionario(View view) {
        Intent intent = new Intent(view.getContext(), AlterarFuncionario.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    public void abrirTelaCadastro(View v){
        Intent intent = new Intent(v.getContext(), activity_cadastro_funcionario.class);
        startActivity(intent);
    }


    public void listarTodosFuncionarios (){
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbworker ", null);
            ComandaView item = null;
            arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaView> list = new ArrayList<>();
            ListViewAdapterFuncionariosService meuAdapter = new ListViewAdapterFuncionariosService(getActivity(), list);
            listaFuncionarios.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                item = new ComandaView();
                arrayIds.add(meuCursor.getInt(0));
                item.setNome(meuCursor.getString(1));
                item.setValor(meuCursor.getString(13));
                item.setQuantidade(meuCursor.getString(12));
                list.add(item);
                meuCursor.moveToNext();
            }
            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private void deletarFuncionario(Integer idSelecionado) {
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            String sql = "DELETE FROM tbworker WHERE workerid = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            Toast.makeText(getContext(), "Funcionario Deletado !", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}