package com.example.myapplication.ui.despezas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.util.Const;

import java.text.NumberFormat;
import java.time.LocalDate;

public class AlterarDespesa extends AppCompatActivity {

    EditText editTextNomeDespesa;
    EditText editTextValorDespesa;
    EditText editTextDataPagamentoDespesa;
    EditText editTextDescricaoDespesa;
    Button botaoAlterarDespesa;
    Integer idBanco;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_despesa);
        editTextNomeDespesa = (EditText) findViewById(R.id.editTextAlterarDespesaNome);
        editTextValorDespesa = (EditText) findViewById(R.id.editTextAlterarDespesaValor);
        editTextDataPagamentoDespesa = (EditText) findViewById(R.id.editTextAlterarDespesaDataPagamento);
        editTextDescricaoDespesa = (EditText) findViewById(R.id.editTextAlterarDespesaDescricao);
        botaoAlterarDespesa = (Button) findViewById(R.id.buttonAlterarDespesa);
        Intent intent = getIntent();
        idBanco = intent.getIntExtra("id",0);
        type = intent.getStringExtra("type");
        editTextValorDespesa.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    editTextValorDespesa.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");
                    cleanString.replaceAll("\\s","");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    editTextValorDespesa.setText(formatted);
                    editTextValorDespesa.setSelection(formatted.length());

                    editTextValorDespesa.addTextChangedListener(this);

                    // String tes = productvalue.getText().toString();
                    // String cleanString = tes.toString().replaceAll("[$,]", "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(type.equals("mensal")){
            getMensal(idBanco);
        } else {
            getVariavel(idBanco);
        }

        botaoAlterarDespesa.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                executarUpdateDespesa(type, idBanco);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void executarUpdateDespesa(String type, Integer idBanco) {
        try{
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            String sql;
            if (type.equals("mensal")){
                sql = "UPDATE tbmonthlyexpenses SET description = ?,paymentdate = ?,amount = ?,updatedate = ?,active = ?" +
                        "WHERE monthlyexpensesid = ?";
            }else {
                sql = "UPDATE tbanotherexpenses SET description = ?,paymentdate = ?,amount = ?,updatedate = ?,active = ?" +
                        "WHERE anotherexpensesid = ?";
            }
            if (editTextNomeDespesa.getText().toString().equals("")||editTextNomeDespesa.getText().toString()==null
                    ||editTextValorDespesa.getText().toString().equals("")){
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Os seguintes campos são obrigatórios !");
                msgBox.setItems( new CharSequence[] {"- NOME DA DESPESA", "- VALOR DA DESPESA"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog alert = msgBox.create();
                alert.show();

            }else {
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1, editTextDescricaoDespesa.getText().toString());
                stmt.bindString(2, editTextDataPagamentoDespesa.getText().toString());
                String tes = editTextValorDespesa.getText().toString();
                String cleanString = tes.toString().replaceAll("[R$,.\\s]", "");
                Float floatValor = Float.parseFloat(cleanString) / 100;
                stmt.bindString(3, floatValor.toString());
                stmt.bindString(4, LocalDate.now().toString());
                stmt.bindString(5, editTextNomeDespesa.getText().toString());
                stmt.bindLong(6, idBanco.longValue());
                stmt.executeUpdateDelete();
                bancoDados.close();
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Despeza cadastrada com Sucesso !");
                msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closefragment();
                    }
                });
                msgBox.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        // Faça aqui o que pretende quando o dialog é cancelado
                        closefragment();
                    }
                });
                msgBox.show();
            }


           // finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getVariavel(Integer idBanco) {
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tbanotherexpenses WHERE anotherexpensesid = " + idBanco.toString(), null);
            cursor.moveToFirst();
            editTextDescricaoDespesa.setText(cursor.getString(1));
            editTextDataPagamentoDespesa.setText(cursor.getString(2));
            editTextValorDespesa.setText(cursor.getString(3));
            editTextNomeDespesa.setText(cursor.getString(7));
            bancoDados.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getMensal(Integer idBanco) {
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tbmonthlyexpenses WHERE monthlyexpensesid = " + idBanco.toString(), null);
            cursor.moveToFirst();
            editTextDescricaoDespesa.setText(cursor.getString(1));
            editTextDataPagamentoDespesa.setText(cursor.getString(2));
            editTextValorDespesa.setText(cursor.getString(3));
            editTextNomeDespesa.setText(cursor.getString(7));
            bancoDados.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void closefragment() {
        finish();

    }
}