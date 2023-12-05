package com.example.myapplication.ui.funcionarios;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.util.Const;

import java.time.LocalDate;

public class AlterarFuncionario extends AppCompatActivity {

    EditText editNome;
    EditText editCelular;
    EditText editTelefonrFixo;
    EditText editNumeroDocumento;
    EditText editEndereco;
    EditText editNumeroCasa;
    EditText editBairro;
    EditText editCidade;
    EditText editUF;
    EditText editCep;
    EditText editFuncaoTrabalho;
    EditText editDiaPagamento;
    Button btnCadastrar;
    private Spinner tipoDocumento;
    SQLiteDatabase bancoDados;
    String documentType;
    Integer idBanco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);
        editNome= (EditText) findViewById(R.id.editTextNomeFuncionario);
        editCelular= (EditText) findViewById(R.id.editTextCelularFuncionario);
        editTelefonrFixo= (EditText) findViewById(R.id.editTextTelefoneFixoFuncionario);
        editNumeroDocumento= (EditText) findViewById(R.id.editTextNumeroDocumentoFuncionario);
        editEndereco= (EditText) findViewById(R.id.editTextEnderecoFuncionario);
        editNumeroCasa= (EditText) findViewById(R.id.editTextNumeroCasaFuncionario);
        editBairro= (EditText) findViewById(R.id.editTextBairroFuncionario);
        editCidade= (EditText) findViewById(R.id.editTextCidadeFuncionario);
        editUF= (EditText) findViewById(R.id.editTextEstadoFuncionario);
        editCep= (EditText) findViewById(R.id.editTextCEPFuncionario);
        editFuncaoTrabalho= (EditText) findViewById(R.id.editTextFuncaoTrabalhoFuncionario);
        editDiaPagamento= (EditText) findViewById(R.id.editTextDiaPagamentoFuncionario);
        tipoDocumento= (Spinner) findViewById(R.id.spinnerTipoDocumentoFuncionario);
        Intent intent = getIntent();
        idBanco = intent.getIntExtra("id",0);
        getFuncionario(idBanco);

        //create a list of items for the spinner.
        String[] items = Const.LIST_DOCUMENTS_TYPE;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        tipoDocumento.setAdapter(adapter);
        tipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                documentType = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCadastrar = (Button) findViewById(R.id.buttonCadastrarWorker);
        btnCadastrar.setText("Alterar Funcionario");
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                alterar();
            }
        });
    }

    private void getFuncionario(Integer idBanco) {
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tbworker WHERE workerid = " + idBanco.toString(), null);
            cursor.moveToFirst();
            editNome.setText(cursor.getString(1));
            editCelular.setText(cursor.getString(2));
            editTelefonrFixo.setText(cursor.getString(3));
            editNumeroDocumento.setText(cursor.getString(5));
            editCep.setText(cursor.getString(6));
            editEndereco.setText(cursor.getString(7));
            editNumeroCasa.setText(cursor.getString(8));
            editBairro.setText(cursor.getString(9));
            editCidade.setText(cursor.getString(10));
            editUF.setText(cursor.getString(11));
            editDiaPagamento.setText(cursor.getString(12));
            editFuncaoTrabalho.setText(cursor.getString(13));

            bancoDados.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alterar(){

        try{
            if (editNome.getText().toString().equals("")||editNome.getText().toString()==null
                    ||editDiaPagamento.getText().toString().equals("")
                    ||editFuncaoTrabalho.getText().toString().equals("")){
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Os seguintes campos são obrigatórios !");
                msgBox.setItems( Const.MGS_BOX_FUNCIONARIO_ALERTA_CAMPOS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog alert = msgBox.create();
                alert.show();

            }else {
                bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
                String sql = "UPDATE  tbworker SET workername = ?, workercellphone = ?, workerphone = ?, workertypedocument = ?, workerdocumentnumber = ?, workerpostalcode = ?, workerhomeadress = ?, workerhomenumber = ?, workerneiborhood = ?, workerhomecityname = ?, workerstate = ?, workerpaymentday = ?, workerfunction = ?, updateDate = ? " +
                        "WHERE workerid = ? ";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1, editNome.getText().toString());
                stmt.bindString(2, editCelular.getText().toString());
                stmt.bindString(3, editTelefonrFixo.getText().toString());
                stmt.bindString(4, documentType);
                stmt.bindString(5, editNumeroDocumento.getText().toString());
                stmt.bindString(6, editCep.getText().toString());
                stmt.bindString(7, editEndereco.getText().toString());
                stmt.bindString(8, editNumeroCasa.getText().toString());
                stmt.bindString(9, editBairro.getText().toString());
                stmt.bindString(10, editCidade.getText().toString());
                stmt.bindString(11, editUF.getText().toString());
                stmt.bindString(12, editDiaPagamento.getText().toString());
                stmt.bindString(13, editFuncaoTrabalho.getText().toString());
                stmt.bindString(14, LocalDate.now().toString());
                stmt.bindLong(15, idBanco.longValue());
                stmt.executeUpdateDelete();
                Toast.makeText(this, "Alterado com Sucesso", Toast.LENGTH_SHORT).show();
                bancoDados.close();
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void closefragment() {
        finish();

    }

}