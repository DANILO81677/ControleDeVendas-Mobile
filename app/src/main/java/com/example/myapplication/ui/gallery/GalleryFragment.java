package com.example.myapplication.ui.gallery;

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
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.service.ListViewAdapterComandaService;
import com.example.myapplication.service.ListViewAdapterListarComandasService;
import com.example.myapplication.ui.despezas.AlterarDespesa;
import com.example.myapplication.ui.slideshow.CadastroCliente;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    Button botaoNovaComanda;
    ListView listaComandas;
    public ArrayList<Integer> arrayIds;
    Integer idSelecionado;

    private SQDataBaseHelperConfig db;

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        db =  new SQDataBaseHelperConfig(getActivity());
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        listaComandas = (ListView) root.findViewById(R.id.lista_comandas_ativas);
        listarComandas();
        botaoNovaComanda = (Button) root.findViewById(R.id.buttonNovaComanda);
        botaoNovaComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastro(v);
            }
        });

       // final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ;
            }
        });

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

        return root;



    }

    private void excluirComandaAtiva(Integer idSelecionado) {
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            String sqlproduct = "DELETE FROM tborderproduct WHERE purchaseorderid = ?";
            SQLiteStatement stmtOrderProduct = bancoDados.compileStatement(sqlproduct);
            stmtOrderProduct.bindLong(1,idSelecionado);
            stmtOrderProduct.executeUpdateDelete();

            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tborderservice WHERE purchaseorderid = "+idSelecionado, null);
            ArrayList<Integer> listaServicesId = new ArrayList<>();
            meuCursor.moveToFirst();
            int tamanho = meuCursor.getCount()-1;
            int inicioPassagem = 0;
            while( tamanho > inicioPassagem) {
                inicioPassagem++;
                listaServicesId.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }

            for (Integer serviceId : listaServicesId){
                String sqlOrderService = "DELETE FROM tbworkerearning WHERE orderserviceid = ?";
                SQLiteStatement stmtOrderService = bancoDados.compileStatement(sqlOrderService);
                stmtOrderService.bindLong(1,serviceId);
                stmtOrderService.executeUpdateDelete();
            }

            String sqlOrderService = "DELETE FROM tborderservice WHERE purchaseorderid = ?";
            SQLiteStatement stmtOrderService = bancoDados.compileStatement(sqlOrderService);
            stmtOrderService.bindLong(1,idSelecionado);
            stmtOrderService.executeUpdateDelete();



            String sql = "DELETE FROM tbpurchaseorder WHERE purchaseorderid = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1,idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(getContext());
            msgBox.setTitle("Cadastrado com Sucesso !");
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

    private void abrirComanda(View view, Integer idSelecionado) {
        Intent intent = new Intent(view.getContext(), VisualizarComanda.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    public void abrirTelaCadastro(View v){
        Intent intent = new Intent(v.getContext(), CriarComanda.class);
        startActivity(intent);
    }

    public void listarComandas (){
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();



            Cursor meuCursor = bancoDados.rawQuery("SELECT a.purchaseorderid,'b.customerName',a.totalvalue, a.status, a.createdate,* from tbpurchaseorder a  order by a.createdate desc", null);
            ComandaList item = null;

            arrayIds = new ArrayList<Integer>();
            ArrayList<ComandaList> list = new ArrayList<>();
            ListViewAdapterListarComandasService meuAdapter = new ListViewAdapterListarComandasService(getActivity(), list);
            listaComandas.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            int tamanho = meuCursor.getCount()-1;
            int inicioPassagem = 0;
            while( tamanho > inicioPassagem) {
                inicioPassagem++;
                item = new ComandaList();
                arrayIds.add(meuCursor.getInt(0));
               Cursor cursorCliente = bancoDados.rawQuery("SELECT * FROM tbcustomer WHERE customerId = " + meuCursor.getString(6), null);
               cursorCliente.moveToFirst();
               if (cursorCliente.getCount() <= 0) {
                   item.setNome("Cliente Excluido");
               } else {
                   item.setNome(cursorCliente.getString(1));
               }

                double parsed = Double.parseDouble(meuCursor.getString(2));

                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                String vfN = decimalFormat.format(parsed);
                item.setValor(vfN);
                item.setStatus(meuCursor.getString(3));
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

    void apagarDepois(){
        //apacar
        SQLiteDatabase bancoDados = db.getReadableDatabase();
        Cursor meuCursor = bancoDados.rawQuery("SELECT * from tborderservice WHERE purchaseorderid = "+idSelecionado, null);
        ArrayList<Integer> listaServicesId = new ArrayList<>();
        meuCursor.moveToFirst();
        int tamanho = meuCursor.getCount()-1;
        int inicioPassagem = 0;
        while( tamanho > inicioPassagem) {
            listaServicesId.add(meuCursor.getInt(0));
            meuCursor.moveToNext();
        }

        for (Integer serviceId : listaServicesId){
            String sqlOrderService = "DELETE FROM tbworkerearning WHERE orderserviceid = ?";
            SQLiteStatement stmtOrderService = bancoDados.compileStatement(sqlOrderService);
            stmtOrderService.bindLong(1,serviceId);
            stmtOrderService.executeUpdateDelete();
        }
        //apagar
    }

    private void closefragment() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();

    }

}