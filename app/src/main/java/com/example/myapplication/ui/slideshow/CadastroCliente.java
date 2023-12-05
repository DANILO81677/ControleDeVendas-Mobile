package com.example.myapplication.ui.slideshow;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CadastroCliente extends AppCompatActivity {

    private Spinner dropdown;
    Button botao;
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
    String documentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);
        //get the spinner from the xml.
        //dropdown =  (Spinner) findViewById(R.id.spinnerTipoDocumento);
        //create a list of items for the spinner.
        String[] items = Const.LIST_DOCUMENTS_TYPE;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        /*dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                documentType = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        editTextNome = (EditText) findViewById(R.id.editText_Nome);
        editTextTelefone = (EditText) findViewById(R.id.editText_Telefone);
        editTextCelular = (EditText) findViewById(R.id.editText_Celular);
        editTextEndereco = (EditText) findViewById(R.id.editText_Endereco);
        editTextCidade = (EditText) findViewById(R.id.editText_Cidade);
        editTextBairro = (EditText) findViewById(R.id.editText_Bairro);
        editTextCEP = (EditText) findViewById(R.id.editText_CEP);
        editTextUF = (EditText) findViewById(R.id.editText_UF);
        editTextNumeroDocumento = (EditText) findViewById(R.id.editText_NumeroDocumento);
        editTextNumeroCasa = (EditText) findViewById(R.id.editText_NumeroCasa);

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

        botao = (Button) findViewById(R.id.buttonCadastrarNovo);

        botao.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                //tipodocumento =  (Spinner) findViewById(R.id.spinnerTipoDocumento);
                cadastrar();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cadastrar(){

            try{
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

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate aniversario = null;
                    String das = editTextNumeroDocumento.getText().toString();
                    if (editTextNumeroDocumento.getText().toString() != null || !editTextNumeroDocumento.getText().toString().isEmpty()) {
                        if (das.equals("")) {
                            aniversario = LocalDate.parse("01/01/2001", formatter);
                        } else {
                            aniversario = LocalDate.parse(editTextNumeroDocumento.getText().toString(), formatter);
                        }
                    } else {
                        aniversario = LocalDate.parse("01/01/2001", formatter);
                    }
                    bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
                    String sql = "INSERT INTO tbcustomer (customerName,customerPhone,customerCellphone," +
                            "customerTypeDocument,customerNumberDocument," +
                            "customerAdress,customerHomeNumber,customerPostalCode," +
                            "customerNeiborhood,customerCityName,customerState,createDate,active) " +
                            "VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, editTextNome.getText().toString());
                    stmt.bindString(2, editTextTelefone.getText().toString());
                    stmt.bindString(3, editTextCelular.getText().toString());
                    stmt.bindString(4, "CPF");
                    stmt.bindString(5, aniversario.toString());
                    stmt.bindString(6, editTextEndereco.getText().toString());
                    stmt.bindString(7, editTextNumeroCasa.getText().toString());
                    stmt.bindString(8, editTextCEP.getText().toString());
                    stmt.bindString(9, editTextBairro.getText().toString());
                    stmt.bindString(10, editTextCidade.getText().toString());
                    stmt.bindString(11, editTextUF.getText().toString());
                    stmt.bindString(12, LocalDate.now().toString());
                    stmt.bindString(13, "S");
                    stmt.executeInsert();
                    bancoDados.close();
                    android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
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
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    private void closefragment() {
        finish();

    }

}