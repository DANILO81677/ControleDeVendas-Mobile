package com.example.myapplication.service;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.entity.ComandaList;

import java.util.ArrayList;

public class ListViewAdapterListarComandasService extends ArrayAdapter {
    public static final String FIRST_COLUMN = "A";
    public static final String SECOND_COLUMN = "B";
    public static final String LAST_COLUMN = "C";

    private final ArrayList<ComandaList> list;
    private final Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txttree;
    TextView txtLast;
    String valor;


    public ListViewAdapterListarComandasService(Activity activity, ArrayList<ComandaList> list) {
        super(activity, R.layout.colum_row_listar_comandas_tela, list);
        // TODO Auto-generated constructor stub

        this.activity=activity;
        this.list=list;
        this.valor=valor;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=activity.getLayoutInflater();

        View view = null;
        convertView = null;
        if (convertView == null) {


            convertView=inflater.inflate(R.layout.colum_row_listar_comandas_tela, null);

            txtFirst=(TextView) convertView.findViewById(R.id.mensal_lista_mes);
            txtSecond=(TextView) convertView.findViewById(R.id.mensal_lista_ano);
            txttree = (TextView) convertView.findViewById(R.id.mensal_lista_despesa);
            txtLast =(TextView) convertView.findViewById(R.id.mensal_lista_receita);

        }




        ComandaList map =list.get(position);

        if (map.getStatus().equals("Pendente")) {
            txttree.setTextColor(Color.RED);
        } else if (map.getStatus().equals("Variavel")||map.getStatus().equals("Mensal")||map.getStatus().equals(null)||map.getStatus().isEmpty()) {
            txttree.setTextColor(Color.BLACK);
        } else {
            txttree.setTextColor(Color.GREEN);
        }

        txtFirst.setText(map.getNome());
        txtSecond.setText("R$ "+map.getValor());
        txtLast.setText(map.getData());
        txttree.setText(map.getStatus());




        return convertView;
    }

}