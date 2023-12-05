package com.example.myapplication.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.util.Const;

import java.time.LocalDate;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openDatabase;


public class CustomerService extends AppCompatActivity {

    private SQLiteDatabase bancoDados;

    public SQLiteDatabase abrirBancoDados (){
        try {
            bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);

            return bancoDados;

        } catch (Exception e){
            e.printStackTrace();
            return  null;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cadastrar(){

        try{
            bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);


            bancoDados.close();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
