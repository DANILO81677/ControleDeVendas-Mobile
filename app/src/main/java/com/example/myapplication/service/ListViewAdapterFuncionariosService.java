package com.example.myapplication.service;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.entity.ComandaView;

import java.util.ArrayList;

public class ListViewAdapterFuncionariosService extends BaseAdapter {
    public static final String FIRST_COLUMN = "A";
    public static final String SECOND_COLUMN = "B";
    public static final String LAST_COLUMN = "C";

    public ArrayList<ComandaView> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtLast;
    String valor;


    public ListViewAdapterFuncionariosService(Activity activity, ArrayList<ComandaView> list){
        super();
        this.activity=activity;
        this.list=list;
        this.valor = valor;
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

        if(convertView == null){

            convertView=inflater.inflate(R.layout.column_row_layout_comanda_service, null);

            txtFirst=(TextView) convertView.findViewById(R.id.produto_nome_linear_comanda_service);
            txtSecond=(TextView) convertView.findViewById(R.id.produto_valor_quantidade_comanda_service);
            txtLast =(TextView) convertView.findViewById(R.id.produto_valor_linear_comanda_service);

        }

        ComandaView map =list.get(position);



        txtFirst.setText(map.getNome());
        txtSecond.setText(map.getQuantidade());
        txtLast.setText(map.getValor());




        return convertView;
    }

}