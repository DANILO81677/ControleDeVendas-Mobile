package com.example.myapplication.ui.despezas;

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
import com.example.myapplication.R;
import com.example.myapplication.database.SQDataBaseHelperConfig;
import com.example.myapplication.entity.ComandaView;
import com.example.myapplication.service.ListViewAdapterFuncionariosService;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DespezasFragment extends Fragment {

    private DespezasModel despezasViewModel;

    public ArrayList<Integer> arrayIdsvariavel;
    public ArrayList<Integer> arrayIdsMensal;

    private SQDataBaseHelperConfig db;

    Float amount = 0F;

    Button cadastro;

    ListView listaMensal;

    ListView listaVariavel;
    ListView listaVariavelGeral;

    TextView totalDespesas;
    TextView labelUnico;
    TextView labelUnicoGeral;

    Integer idSelecionado;



    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db =  new SQDataBaseHelperConfig(getActivity());
        despezasViewModel =
                new ViewModelProvider(this).get(DespezasModel.class);
        View root = inflater.inflate(R.layout.fragment_despezas, container, false);
      //  final TextView textView = root.findViewById(R.id.textViewdespezas);
        despezasViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
             //   textView.setText(s);
            }
        });
        cadastro = (Button) root.findViewById(R.id.buttonCreateNewExpense);
        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastro(v);
            }
        });

        listaMensal = (ListView) root.findViewById(R.id.listaMensalDespesa);
        listarDespesaMensal();
        listaVariavel = (ListView) root.findViewById(R.id.listaUnicaDespesa);
        listaVariavelGeral = (ListView) root.findViewById(R.id.listaUnicaDespesaGeral);
        listarDespesaVariavelGeral ();
        listaVariavelGeral.setVisibility(View.INVISIBLE);
        listarDespesaVariavel();
        totalDespesas = (TextView) root.findViewById(R.id.textViewTotalDespesas);
        labelUnico = (TextView) root.findViewById(R.id.textView6);
        labelUnicoGeral = (TextView) root.findViewById(R.id.textView9);
        labelUnicoGeral.setVisibility(View.INVISIBLE);

        totalDespesas.setText("Total de Despesas  : "+NumberFormat.getCurrencyInstance().format((amount)));
        totalDespesas.setTextSize(20);


        listaMensal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // String name = linhas.get(i);
                idSelecionado = arrayIdsMensal.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Alterar Despesa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alterarMensal(view, "mensal");

                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarmensal(idSelecionado);
                    }
                });

                msgBox.show();
            }});

        listaVariavelGeral.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // String name = linhas.get(i);
                idSelecionado = arrayIdsvariavel.get(i);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Alterar Despesa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alterarVariavel(view, "variavel");

                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarVariavel(idSelecionado);
                    }
                });

                msgBox.show();
            }});

        listaVariavel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idSelecionado = arrayIdsvariavel.get(position);
                AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
                msgBox.setTitle("O que deseja fazer ?");
                msgBox.setIcon(android.R.drawable.ic_dialog_info);
                msgBox.setNegativeButton("Alterar Despesa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alterarVariavel(view, "variavel");

                    }
                });
                msgBox.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletarVariavel(idSelecionado);
                    }
                });

                msgBox.show();
            }
        });

        TabLayout tabLayout = ( TabLayout ) root.findViewById (R.id.menu_despeza_unico_list); // obtém a referência de TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tabLayout . getSelectedTabPosition (); // obtém a posição para a guia atual selecionada



                if (selectedTabPosition == 0){

                    listaVariavel.setVisibility(View.VISIBLE);
                    labelUnico.setVisibility(View.VISIBLE);
                    labelUnicoGeral.setVisibility(View.INVISIBLE);
                    listaVariavelGeral.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getContext(), "Selecionado Produtos"+selectedTabPosition, Toast.LENGTH_SHORT).show();
                } else if (selectedTabPosition ==1){
                    listaVariavel.setVisibility(View.INVISIBLE);
                    labelUnico.setVisibility(View.INVISIBLE);
                    labelUnicoGeral.setVisibility(View.VISIBLE);
                    listaVariavelGeral.setVisibility(View.VISIBLE);
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

    private void deletarVariavel(Integer idSelecionado) {
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            String sql = "DELETE FROM tbanotherexpenses WHERE anotherexpensesid = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            Toast.makeText(getContext(), "Despesa Deletada !", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void alterarVariavel(View view, String variavel) {
        Intent intent = new Intent(view.getContext(), AlterarDespesa.class);
        intent.putExtra("id",idSelecionado);
        intent.putExtra("type", variavel);
        startActivity(intent);
    }

    private void deletarmensal(Integer idSelecionado) {

        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            String sql = "DELETE FROM tbmonthlyexpenses WHERE monthlyexpensesid = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            Toast.makeText(getContext(), "Despesa Deletada !", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void alterarMensal(View view, String mensal) {
        Intent intent = new Intent(view.getContext(), AlterarDespesa.class);
        intent.putExtra("id",idSelecionado);
        intent.putExtra("type", mensal);
        startActivity(intent);
    }

    public void abrirTelaCadastro(View v){
        Intent intent = new Intent(v.getContext(), CriarDespesa.class);
        startActivity(intent);
    }

    public void listarDespesaMensal (){
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbmonthlyexpenses ", null);
            ComandaView item = null;
            arrayIdsMensal = new ArrayList<Integer>();
            ArrayList<ComandaView> list = new ArrayList<>();
            ListViewAdapterFuncionariosService meuAdapter = new ListViewAdapterFuncionariosService(getActivity(), list);
            listaMensal.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            int tamanho = meuCursor.getCount();
            int inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                item = new ComandaView();
                arrayIdsMensal.add(meuCursor.getInt(0));
                item.setNome(meuCursor.getString(7));
                String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s","");

                Float parsed = Float.parseFloat(valorDespesaBanco);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed));

                item.setValor(formatted);
                String dia = meuCursor.getString(2);
                String diaMes = dia.substring(0,2);
                item.setQuantidade(diaMes);
                list.add(item);
                Float rvSom = parsed;
                Float soma = rvSom;
                amount += soma;
                meuCursor.moveToNext();
            }
            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void listarDespesaVariavel (){
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbanotherexpenses order by paymentdate desc ", null);
            ComandaView item = null;
            arrayIdsvariavel = new ArrayList<Integer>();
            ArrayList<ComandaView> list = new ArrayList<>();
            ListViewAdapterFuncionariosService meuAdapter = new ListViewAdapterFuncionariosService(getActivity(), list);
            listaVariavel.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            int tamanho = meuCursor.getCount();
            int inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                item = new ComandaView();
                arrayIdsvariavel.add(meuCursor.getInt(0));
                item.setNome(meuCursor.getString(7));
                String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s","");

                Float parsed = Float.parseFloat(valorDespesaBanco);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed));

                item.setValor(formatted);

                item.setQuantidade(meuCursor.getString(2));

                String data =meuCursor.getString(2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate datePag = LocalDate.parse(data, formatter);
                LocalDate hoje = LocalDate.now();
                if (datePag.getMonthValue() == hoje.getMonthValue()) {
                    list.add(item);

                    Integer soma = Integer.parseInt(meuCursor.getString(3));
                    amount += soma;
                }

                meuCursor.moveToNext();
            }
            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void listarDespesaVariavelGeral (){
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbanotherexpenses  order by paymentdate desc", null);
            ComandaView item = null;
            arrayIdsvariavel = new ArrayList<Integer>();
            ArrayList<ComandaView> list = new ArrayList<>();
            ListViewAdapterFuncionariosService meuAdapter = new ListViewAdapterFuncionariosService(getActivity(), list);
            listaVariavelGeral.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            int tamanho = meuCursor.getCount();
            int inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                item = new ComandaView();
                arrayIdsvariavel.add(meuCursor.getInt(0));
                item.setNome(meuCursor.getString(7));
                String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s","");

                Float parsed = Float.parseFloat(valorDespesaBanco);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed));

                item.setValor(formatted);
                item.setQuantidade(meuCursor.getString(2));

                String data =meuCursor.getString(2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate datePag = LocalDate.parse(data, formatter);
                list.add(item);

                meuCursor.moveToNext();
            }
            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }



    }

}