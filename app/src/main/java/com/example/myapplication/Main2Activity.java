package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.myapplication.util.Const;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public SQLiteDatabase database;
    public ListView listViewDados;
    public ArrayList<Integer> arrayIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        criaOuAbreBancoDados();

        //listViewDados = (ListView) findViewById(R.id.list_view_clientes);
       // listarDados();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_tablePrices, R.id.nav_despesas
        ,R.id.nav_relatorios, R.id.nav_funcionarios)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void criaOuAbreBancoDados (){
        try {
            database = openOrCreateDatabase(Const.NAME_DATABASE ,MODE_PRIVATE, null);
            //Processo de criação das tabelas
            database.execSQL("CREATE TABLE IF NOT EXISTS tbcustomer(" +
                    "  customerId INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  customerName VARCHAR NOT NULL,\n" +
                    "  customerPhone VARCHAR ,\n" +
                    "  customerCellphone VARCHAR(45) ,\n" +
                    "  customerTypeDocument VARCHAR(45) ,\n" +
                    "  customerNumberDocument VARCHAR(45) ,\n" +
                    "  customerAdress VARCHAR(100) ,\n" +
                    "  customerHomeNumber VARCHAR(45) ,\n" +
                    "  customerPostalCode VARCHAR(45) ,\n" +
                    "  customerNeiborhood VARCHAR(45) ,\n" +
                    "  customerCityName VARCHAR(45) ,\n" +
                    "  customerState VARCHAR(45) ,\n" +
                    "  createDate DATETIME2(0) NOT NULL,\n" +
                    "  updateDate DATETIME2(0) ,\n" +
                    "  cancelDate DATETIME2(0) ,\n" +
                    "  active VARCHAR(45) NOT NULL)");

            database.execSQL("CREATE TABLE IF NOT EXISTS tbpurchaseorder (" +
                    "purchaseorderid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "customerId INTEGER NOT NULL,\n" +
                    "status INTEGER NOT NULL,\n" +
                    "totalvalue TEXT NOT NULL,\n" +
                    "paidvalue TEXT,\n" +
                    "observation TEXT,\n" +
                    "paymentmethod TEXT,\n" +
                    "finishpayment TEXT NOT NULL,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "cancedatel DATETIME2(0),\n" +
                    "active TEXT NOT NULL)");

            database.execSQL("CREATE TABLE IF NOT EXISTS tbworker (" +
                    "workerid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "workername TEXT NOT NULL,\n" +
                    "workercellphone TEXT,\n" +
                    "workerphone TEXT,\n" +
                    "workertypedocument TEXT,\n" +
                    "workerdocumentnumber TEXT,\n" +
                    "workerpostalcode TEXT,\n" +
                    "workerhomeadress TEXT,\n" +
                    "workerhomenumber TEXT,\n" +
                    "workerneiborhood TEXT,\n" +
                    "workerhomecityname TEXT,\n" +
                    "workerstate TEXT,\n" +
                    "workerpaymentday TEXT NOT NULL,\n" +
                    "workerfunction TEXT,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");


            database.execSQL("CREATE TABLE IF NOT EXISTS tbproducts (" +
                    "productid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "productname TEXT,\n" +
                    "productdescription TEXT,\n" +
                    "producttype TEXT,\n" +
                    "productvalue TEXT,\n" +
                    "productmanufacturer TEXT,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");

            database.execSQL("CREATE TABLE IF NOT EXISTS tbservices (" +
                    "serviceid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "servicename TEXT,\n" +
                    "servicedescription TEXT,\n" +
                    "servicetype TEXT,\n" +
                    "servicevalue TEXT NOT NULL,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");


            database.execSQL("CREATE TABLE IF NOT EXISTS tborderproduct (" +
                    "orderproductid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "purchaseorderid INTEGER NOT NULL,\n" +
                    "productid INTEGER NOT NULL,\n" +
                    "quantity INTEGER,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");

            database.execSQL("CREATE TABLE IF NOT EXISTS tborderservice (" +
                    "orderserviceid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "purchaseorderid INTEGER NOT NULL,\n" +
                    "serviceid INTEGER NOT NULL,\n" +
                    "quantity INTEGER,\n" +
                    "createdate DATETIME2(0),\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");


            database.execSQL("CREATE TABLE IF NOT EXISTS tbworkerearning (\n" +
                    "workerearningId INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "orderserviceid INTEGER NOT NULL,\n" +
                    "workerId INTEGER NOT NULL,\n" +
                    "earningamount TEXT NOT NULL,\n" +
                    "servicedate TEXT,\n" +
                    "paid TEXT,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");


            database.execSQL("CREATE TABLE IF NOT EXISTS tbmonthlyexpenses (" +
                    "monthlyexpensesid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "description TEXT,\n" +
                    "paymentdate INTEGER,\n" +
                    "amount INTEGER,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");

            database.execSQL("CREATE TABLE IF NOT EXISTS tbanotherexpenses (" +
                    "anotherexpensesid  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "description TEXT,\n" +
                    "paymentdate INTEGER,\n" +
                    "amount TEXT,\n" +
                    "createdate DATETIME2(0) NOT NULL,\n" +
                    "updatedate DATETIME2(0),\n" +
                    "canceldate DATETIME2(0),\n" +
                    "active TEXT NOT NULL\n" +
                    ")");

            database.execSQL("CREATE TABLE IF NOT EXISTS tbrevenues (" +
                    "revenueId INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "purchaseorderid INTEGER,\n" +
                    "month INTEGER,\n" +
                    "year INTEGER,\n" +
                    "createdate DATETIME2(0) NOT NULL\n" +
                    ")");
            database.close();
         //   listarDados ();
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    public void listarDados (){
        try {
        database = openOrCreateDatabase(Const.NAME_DATABASE ,MODE_PRIVATE, null);
            Cursor meuCursor = database.rawQuery("SELECT * FROM tbcustomer", null);
            ArrayList<String> linhas = new ArrayList<String>();
            ArrayAdapter meuAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listViewDados.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                linhas.add(meuCursor.getString(1));
                meuCursor.moveToNext();
            }
           // database.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }


}