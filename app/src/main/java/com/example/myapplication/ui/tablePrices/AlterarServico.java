package com.example.myapplication.ui.tablePrices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.entity.Customer;
import com.example.myapplication.entity.Services;
import com.example.myapplication.util.Const;

import java.text.NumberFormat;
import java.time.LocalDate;

public class AlterarServico extends AppCompatActivity {

    Button botao;
    SQLiteDatabase bancoDados;
    EditText servicename;
    EditText servicedescription;
    EditText servicetype;
    EditText servicevalue;
    EditText createdate;
    EditText active;
    Integer idBanco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_novo_servico);
        servicename = (EditText) findViewById(R.id.editTextNomeServico);
        servicedescription = (EditText) findViewById(R.id.editTextDescricaoServico);
        servicetype = (EditText) findViewById(R.id.editTextTipoServico);
        servicevalue = (EditText) findViewById(R.id.editTextValorServico);
        botao = (Button) findViewById(R.id.buttonCadastraServico);
        Intent intent = getIntent();
        idBanco = intent.getIntExtra("id",0);
        Services servico = getServico(idBanco);

        servicevalue.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    servicevalue.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");
                    cleanString.replaceAll("\\s","");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    servicevalue.setText(formatted);
                    servicevalue.setSelection(formatted.length());

                    servicevalue.addTextChangedListener(this);

                    // String tes = productvalue.getText().toString();
                    // String cleanString = tes.toString().replaceAll("[$,]", "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        servicename.setText(servico.getServicename());
        servicedescription.setText(servico.getServicedescription());
        servicetype.setText(servico.getServicetype());
        servicevalue.setText(servico.getServicevalue());

        botao.setText("Alterar Serviço");

        botao.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                alterar();
            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alterar(){

        try{
            if (servicename.getText().toString().equals("")||servicename.getText().toString()==null
                    ||servicevalue.getText().toString().equals("")){
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Os seguintes campos são obrigatórios !");
                msgBox.setItems( Const.MGS_BOX_SERVICO_ALERTA_CAMPOS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog alert = msgBox.create();
                alert.show();

            }else {
                bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
                String sql = "UPDATE tbservices  SET servicename = ?,servicedescription = ?,servicetype = ?,servicevalue = ?,updateDate = ? " +
                        "WHERE  serviceid = ?";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1, servicename.getText().toString());
                stmt.bindString(2, servicedescription.getText().toString());
                stmt.bindString(3, servicetype.getText().toString());
                String tes = servicevalue.getText().toString();
                String cleanString = tes.toString().replaceAll("[R$,.\\s]", "");
                Float floatValor = Float.parseFloat(cleanString) / 100;
                stmt.bindString(4, floatValor.toString());
                stmt.bindString(5, LocalDate.now().toString());
                stmt.bindLong(6, idBanco.longValue());
                stmt.executeUpdateDelete();
                bancoDados.close();
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Serviço Alterado com Sucesso !");
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Services getServico (Integer id ){
        Services res = new Services();
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tbservices WHERE serviceid = " + id.toString(), null);
            cursor.moveToFirst();
            res.setServicename(cursor.getString(1));
            res.setServicedescription(cursor.getString(2));
            res.setServicetype(cursor.getString(3));
            res.setServicevalue(cursor.getString(4));
            bancoDados.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    private void closefragment() {
        finish();

    }
}