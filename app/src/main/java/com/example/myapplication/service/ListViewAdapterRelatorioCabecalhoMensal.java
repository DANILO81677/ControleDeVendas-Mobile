package com.example.myapplication.service;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.myapplication.R;
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.entity.RelatorioMensalListaCabecalho;

import java.util.ArrayList;

public class ListViewAdapterRelatorioCabecalhoMensal extends BaseAdapter {
    public static final String FIRST_COLUMN = "A";
    public static final String SECOND_COLUMN = "B";
    public static final String LAST_COLUMN = "C";

    public ArrayList<RelatorioMensalListaCabecalho> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txttree;
    TextView txtLast;
    TextView txtSix;
    String valor;


    public ListViewAdapterRelatorioCabecalhoMensal(Activity activity,
                                                   ArrayList<RelatorioMensalListaCabecalho> list){
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

            convertView=inflater.inflate(R.layout.colum_row_listar_comandas_relatorios_cabecalho_mensal, null);

            txtFirst=(TextView) convertView.findViewById(R.id.mensal_header_lista_mes);
            txtSecond=(TextView) convertView.findViewById(R.id.mensal_header_lista_ano);
            txttree = (TextView) convertView.findViewById(R.id.mensal_header_lista_despesa);
            txtLast =(TextView) convertView.findViewById(R.id.mensal_header_lista_receita);
            txtSix = (TextView) convertView.findViewById(R.id.mensal_header_lista_faturado);

        }




        RelatorioMensalListaCabecalho map =list.get(position);

        /*if (map.getStatus().equals("Pendente")) {
            txttree.setTextColor(Color.RED);
        }else {
            txttree.setTextColor(Color.GREEN);
        }*/

        txtFirst.setText(map.getMes());
        txtSecond.setText(map.getAno());
        txttree.setText(map.getDespeza());
        txtLast.setText(map.getReceita());
        txtSix.setText(map.getFaturado());



        return convertView;
    }

}