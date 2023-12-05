package com.example.myapplication.ui.despezas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.util.Const;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CriarDespesa extends AppCompatActivity {

    EditText editTextNomeDespesa;
    EditText editTextValorDespesa;
    EditText editTextDataPagamentoDespesa;
    EditText editTextDescricaoDespesa;
    RadioButton radioButtonUnica;
    RadioButton radioButtonMensal;
    Button buttonCrudNewWorker;

    SQLiteDatabase bancoDados;

    boolean typeInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_despesa);
        editTextNomeDespesa = (EditText) findViewById(R.id.editTextNomeDespesa);
        editTextValorDespesa = (EditText) findViewById(R.id.editTextValorDespesa);
        editTextDataPagamentoDespesa = (EditText) findViewById(R.id.editTextDataPagamentoDespesa);
        editTextDescricaoDespesa = (EditText) findViewById(R.id.editTextDescricaoDespesa);
        radioButtonUnica = (RadioButton) findViewById(R.id.radioButtonUnica);
        radioButtonMensal = (RadioButton) findViewById(R.id.radioButtonMensal);
        buttonCrudNewWorker = (Button) findViewById(R.id.buttonCrudNewWorker);
        editTextDataPagamentoDespesa.setVisibility(View.INVISIBLE);

        TextWatcher wacherText =  new TextWatcher() {
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
                    Float parsed = Float.parseFloat(cleanString);
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
        };
        editTextValorDespesa.addTextChangedListener(wacherText);
        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    editTextDataPagamentoDespesa.setText(current);
                    editTextDataPagamentoDespesa.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        editTextDataPagamentoDespesa.addTextChangedListener(tw);

        radioButtonMensal.toggle();
        radioButtonUnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonMensal.setChecked(false);
                typeInput = false;
                editTextDataPagamentoDespesa.setVisibility(View.VISIBLE);
            }
        });
        radioButtonMensal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonUnica.setChecked(false);
                typeInput = true;
                editTextDataPagamentoDespesa.setVisibility(View.INVISIBLE);
            }
        });
        buttonCrudNewWorker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                cadastrarNovaDespesa();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cadastrarNovaDespesa (){
        try {



            String data =editTextDataPagamentoDespesa.getText().toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");



            bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            if (typeInput){
                String s = editTextNomeDespesa.getText().toString();
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
                    String sql = "INSERT INTO tbmonthlyexpenses (description, paymentdate, amount, createdate, active) " +
                            "VALUES  (?,?,?,?,?)";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, editTextDescricaoDespesa.getText().toString());
                    stmt.bindString(2, "MENSAL");
                    String tes = editTextValorDespesa.getText().toString();
                    String cleanString = tes.toString().replaceAll("[R$,.\\s]", "");
                    Float floatValor = Float.parseFloat(cleanString) / 100;
                    stmt.bindString(3, floatValor.toString());
                    stmt.bindString(4, LocalDate.now().toString());
                    stmt.bindString(5, editTextNomeDespesa.getText().toString());
                    Long purchaseOrderId = stmt.executeInsert();
                    bancoDados.close();
                    android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                    msgBox.setTitle("Despeza cadastrada com Sucesso !");
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
            } else {
                if (editTextNomeDespesa.getText().toString().equals("")||editTextNomeDespesa.getText().toString()==null
                        ||editTextValorDespesa.getText().toString().equals("")
                        ||editTextDataPagamentoDespesa.getText().toString().equals("")){
                    android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                    msgBox.setTitle("Os seguintes campos são obrigatórios !");

                    msgBox.setItems( new CharSequence[] {"- NOME DA DESPESA", "- VALOR DA DESPESA"
                            , "- DATA"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    AlertDialog alert = msgBox.create();
                    alert.show();

                }else {
                    LocalDate date = LocalDate.parse(editTextDataPagamentoDespesa.getText().toString(), formatter);
                    String sql = "INSERT INTO tbanotherexpenses (description, paymentdate, amount, createdate, active) " +
                            "VALUES  (?,?,?,?,?)";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, editTextDescricaoDespesa.getText().toString());
                    stmt.bindString(2, date.toString());
                    String tes = editTextValorDespesa.getText().toString();
                    String cleanString = tes.toString().replaceAll("[R$,.\\s]", "");
                    Float floatValor = Float.parseFloat(cleanString) / 100;
                    stmt.bindString(3, floatValor.toString());
                    //stmt.bindString(3, editTextValorDespesa.getText().toString());
                    stmt.bindString(4, LocalDate.now().toString());
                    stmt.bindString(5, editTextNomeDespesa.getText().toString());
                    Long purchaseOrderId = stmt.executeInsert();
                    bancoDados.close();
                    android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                    msgBox.setTitle("Despeza cadastrada com Sucesso !");
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

            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closefragment() {
        finish();

    }
}