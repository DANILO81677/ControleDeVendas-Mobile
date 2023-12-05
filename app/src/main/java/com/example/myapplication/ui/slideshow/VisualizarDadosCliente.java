package com.example.myapplication.ui.slideshow;

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
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myapplication.R;
import com.example.myapplication.entity.Customer;
import com.example.myapplication.util.Const;

import java.time.LocalDate;
import java.util.Calendar;

public class VisualizarDadosCliente extends AppCompatActivity {

    private Spinner dropdown;
    Button botaoSalvar;
    SQLiteDatabase bancoDados;
    EditText editTextNome;
    EditText editTextTelefone;
    EditText editTextCelular;
    EditText editTextEndereco;
    EditText editTextCidade;
    EditText editTextBairro;
    EditText editTextCEP;
    EditText editTextUF;
    EditText editTextNumeroDocumento;
    EditText editTextNumeroCasa;
    Spinner tipodocumento;
    Integer idBanco;
    String documentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_dados_cliente);

        Intent intent = getIntent();
        idBanco = intent.getIntExtra("id",0);
        Customer dados = getCliente(idBanco);

        //tipodocumento =  (Spinner) findViewById(R.id.spinnerTipoDocumento2);
        //create a list of items for the spinner.
        String[] items = Const.LIST_DOCUMENTS_TYPE;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        /*tipodocumento.setAdapter(adapter);
        tipodocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                documentType = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        editTextNome = (EditText) findViewById(R.id.editText_Nome2);
        editTextNome.setText(dados.getCustomerName());
        editTextTelefone = (EditText) findViewById(R.id.editText_Telefone2);
        editTextTelefone.setText(dados.getCustomerPhone());
        editTextCelular = (EditText) findViewById(R.id.editText_Celular2);
        editTextCelular.setText(dados.getCustomerCellphone());
        editTextEndereco = (EditText) findViewById(R.id.editText_Endereco2);
        editTextEndereco.setText(dados.getCustomerAdress());
        editTextCidade = (EditText) findViewById(R.id.editText_Cidade2);
        editTextCidade.setText(dados.getCustomerCityName());
        editTextBairro = (EditText) findViewById(R.id.editText_Bairro2);
        editTextBairro.setText(dados.getCustomerNeiborhood());
        editTextCEP = (EditText) findViewById(R.id.editText_CEP2);
        editTextCEP.setText(dados.getCustomerPostalCode());
        editTextUF = (EditText) findViewById(R.id.editText_UF2);
        editTextUF.setText(dados.getCustomerState());
        editTextNumeroDocumento = (EditText) findViewById(R.id.editText_NumeroDocumento2);
        editTextNumeroDocumento.setText(dados.getCustomerNumberDocument());
        editTextNumeroCasa = (EditText) findViewById(R.id.editText_NumeroCasa2);
        editTextNumeroCasa.setText(dados.getCustomerHomeNumber());

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

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    editTextNumeroDocumento.setText(current);
                    editTextNumeroDocumento.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        editTextNumeroDocumento.addTextChangedListener(tw);

        //Toast.makeText(this, idBanco.toString(), Toast.LENGTH_SHORT).show();

        botaoSalvar = (Button) findViewById(R.id.buttonSalvarAlteracoesCliente);
        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                salvarAlteracao ();
            }
        });
    }

    public Customer getCliente (Integer id ){
        Customer cliente = new Customer();
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tbcustomer WHERE customerId = " + id.toString(), null);
            cursor.moveToFirst();
            cliente.setCustomerName(cursor.getString(1));
            cliente.setCustomerPhone(cursor.getString(2));
            cliente.setCustomerCellphone(cursor.getString(3));
           // cliente.setCustomerTypeDocument(cursor.getString(4));
            String dataLimpa = cursor.getString(5).replaceAll("-","");
            cliente.setCustomerNumberDocument(dataLimpa);
            cliente.setCustomerAdress(cursor.getString(6));
            cliente.setCustomerHomeNumber(cursor.getString(7));
            cliente.setCustomerPostalCode(cursor.getString(8));
            cliente.setCustomerNeiborhood(cursor.getString(9));
            cliente.setCustomerCityName(cursor.getString(10));
            cliente.setCustomerState(cursor.getString(11));
            cliente.setCreateDate(cursor.getString(12));
            cliente.setUpdateDate(cursor.getString(13));
        } catch (Exception e){
            e.printStackTrace();
        }
        return cliente;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void salvarAlteracao (){
        try {
            if (editTextNumeroDocumento.getText().toString().equals("")||editTextNumeroDocumento.getText().toString()==null
                    ||editTextNome.getText().toString().equals("")){
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Os seguintes campos são obrigatórios !");
                msgBox.setItems( Const.MGS_BOX_CLIENTE_ALERTA_CAMPOS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog alert = msgBox.create();
                alert.show();

            }else {
                SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
                String sql = "UPDATE tbcustomer SET customerName = ?,customerPhone = ?,customerCellphone = ?," +
                        "customerTypeDocument = ?,customerNumberDocument = ?," +
                        "customerAdress = ?,customerHomeNumber = ?,customerPostalCode = ?," +
                        "customerNeiborhood = ?,customerCityName = ?,customerState = ?,updateDate = ? " +
                        "WHERE customerId = ?";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1, editTextNome.getText().toString());
                stmt.bindString(2, editTextTelefone.getText().toString());
                stmt.bindString(3, editTextCelular.getText().toString());
                // stmt.bindString(4,documentType);
                stmt.bindString(6, editTextEndereco.getText().toString());
                stmt.bindString(10, editTextCidade.getText().toString());
                stmt.bindString(9, editTextBairro.getText().toString());
                stmt.bindString(8, editTextCEP.getText().toString());
                stmt.bindString(11, editTextUF.getText().toString());
                stmt.bindString(12, LocalDate.now().toString());
                stmt.bindString(5, editTextNumeroDocumento.getText().toString());
                stmt.bindString(7, editTextNumeroCasa.getText().toString());

                stmt.bindLong(13, idBanco.longValue());
                stmt.executeUpdateDelete();
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Alterado com Sucesso !");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void closefragment() {
        finish();

    }
}