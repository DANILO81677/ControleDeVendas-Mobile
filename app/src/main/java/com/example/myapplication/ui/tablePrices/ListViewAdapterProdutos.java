package com.example.myapplication.ui.tablePrices;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapterProdutos extends BaseAdapter {
    public static final String FIRST_COLUMN = "A";
    public static final String SECOND_COLUMN = "B";

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;


    public ListViewAdapterProdutos(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
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

            convertView=inflater.inflate(R.layout.column_row_layout_products, null);

            txtFirst=(TextView) convertView.findViewById(R.id.produto_nome_linear);
            txtSecond=(TextView) convertView.findViewById(R.id.produto_valor_linear);

        }

        HashMap<String, String> map=list.get(position);

            txtFirst.setText(map.get(FIRST_COLUMN));
            txtSecond.setText("R$ "+map.get(SECOND_COLUMN));




        return convertView;
    }

}
