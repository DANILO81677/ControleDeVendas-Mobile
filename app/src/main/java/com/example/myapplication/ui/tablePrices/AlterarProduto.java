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
import com.example.myapplication.entity.Products;
import com.example.myapplication.entity.Services;
import com.example.myapplication.util.Const;

import java.text.NumberFormat;
import java.time.LocalDate;

public class AlterarProduto extends AppCompatActivity {

    Button botao;
    SQLiteDatabase bancoDados;
    EditText productname;
    EditText productdescription;
    EditText producttype;
    EditText productvalue;
    EditText productmanufacturer;
    EditText createdate;
    EditText updatedate;
    EditText canceldate;
    EditText active;
    Integer idBanco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_novo_item);
        botao = (Button) findViewById(R.id.buttonCadastrarNovoProduct);
        productname = (EditText) findViewById(R.id.editTextNomeProduto);
        productdescription = (EditText) findViewById(R.id.editTextDescricaoProduto);
        producttype = (EditText) findViewById(R.id.editTextTipoProduto);
        productvalue = (EditText) findViewById(R.id.editTextValorProduto);
        productmanufacturer = (EditText) findViewById(R.id.editTextFabricanteProduto);

        Intent intent = getIntent();
        idBanco = intent.getIntExtra("id",0);

        productvalue.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    productvalue.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");
                    cleanString.replaceAll("\\s","");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    productvalue.setText(formatted);
                    productvalue.setSelection(formatted.length());

                    productvalue.addTextChangedListener(this);

                    // String tes = productvalue.getText().toString();
                    // String cleanString = tes.toString().replaceAll("[$,]", "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Products produto = getProduto(idBanco);
        productname.setText(produto.getProductname());
        productdescription.setText(produto.getProductdescription());
        producttype.setText(produto.getProducttype());
        productvalue.setText(produto.getProductvalue());
        productmanufacturer.setText(produto.getProductmanufacturer());

        botao.setText("Alterar Produto");
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
            if (productname.getText().toString().equals("")||productname.getText().toString()==null
                    ||productvalue.getText().toString().equals("")
                    ||producttype.getText().toString().equals("")){
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Os seguintes campos são obrigatórios !");

                msgBox.setItems( Const.MGS_BOX_PRODUTO_ALERTA_CAMPOS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog alert = msgBox.create();
                alert.show();

            }else {
                bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
                String sql = "UPDATE tbproducts SET productname = ?,productdescription = ?,producttype = ?,productvalue = ?,productmanufacturer = ?,updateDate = ? " +
                        "WHERE productid = ?";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1, productname.getText().toString());
                stmt.bindString(2, productdescription.getText().toString());
                stmt.bindString(3, producttype.getText().toString());
                String tes = productvalue.getText().toString();

                String cleanString = tes.toString().replaceAll("[R$,.\\s]", "");
                Float floatValor = Float.parseFloat(cleanString) / 100;
                stmt.bindString(4, floatValor.toString());
                stmt.bindString(5, productmanufacturer.getText().toString());
                stmt.bindString(6, LocalDate.now().toString());
                stmt.bindLong(7, idBanco.longValue());
                stmt.executeUpdateDelete();
                bancoDados.close();
                android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
                msgBox.setTitle("Produto Alterado com Sucesso  !");
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

    public Products getProduto (Integer id ){
        Products res = new Products();
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tbproducts WHERE productid = " + id.toString(), null);
            cursor.moveToFirst();
            res.setProductname(cursor.getString(1));
            res.setProductdescription(cursor.getString(2));
            res.setProducttype(cursor.getString(3));
            res.setProductvalue(cursor.getString(4));
            res.setProductmanufacturer(cursor.getString(5));
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