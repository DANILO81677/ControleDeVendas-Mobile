package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.example.myapplication.util.Const;

public class SQDataBaseHelperConfig extends SQLiteOpenHelper {

    public SQDataBaseHelperConfig(Context context) {
        super(context, Const.NAME_DATABASE, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
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
                "paymentmethod INTEGER,\n" +
                "finishpayment TEXT NOT NULL,\n" +
                "createdate TEXT NOT NULL,\n" +
                "cancedatel TEXT,\n" +
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
                "workerfunction INTEGER,\n" +
                "createdate INTEGER NOT NULL,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active INTEGER NOT NULL\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS tbproducts (" +
                "productid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "productname TEXT,\n" +
                "productdescription TEXT,\n" +
                "producttype TEXT,\n" +
                "productvalue TEXT,\n" +
                "productmanufacturer TEXT,\n" +
                "createdate INTEGER NOT NULL,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active TEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tbservices (" +
                "serviceid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "servicename TEXT,\n" +
                "servicedescription TEXT,\n" +
                "servicetype TEXT,\n" +
                "servicevalue TEXT NOT NULL,\n" +
                "createdate INTEGER NOT NULL,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active TEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tborderproduct (\n" +
                "\torderproductid\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tpurchaseorderid\tINTEGER,\n" +
                "\tproductid\tINTEGER,\n" +
                "\tquantity\tINTEGER,\n" +
                "\tcreatedate\tINTEGER NOT NULL,\n" +
                "\tupdatedate\tINTEGER,\n" +
                "\tcanceldate\tINTEGER,\n" +
                "\tactive\tTEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tborderproduct (" +
                "orderproductid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "purchaseorderid INTEGER NOT NULL,\n" +
                "productid INTEGER NOT NULL,\n" +
                "quantity INTEGER,\n" +
                "createdate INTEGER NOT NULL,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active TEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tborderservice (" +
                "orderserviceid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "purchaseorderid INTEGER NOT NULL,\n" +
                "productid INTEGER NOT NULL,\n" +
                "quantity INTEGER,\n" +
                "createdate INTEGER,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active TEXT NOT NULL\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS tbworkerearning (\n" +
                "workerearningId INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "orderserviceid INTEGER NOT NULL,\n" +
                "workerId INTEGER NOT NULL,\n" +
                "earningamount TEXT NOT NULL,\n" +
                "servicedate TEXT,\n" +
                "paid TEXT,\n" +
                "createdate INTEGER NOT NULL,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active TEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tbmonthlyexpenses (\n" +
                "\tmonthlyexpensesid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tdescription\tTEXT,\n" +
                "\tpaymentdate\tINTEGER,\n" +
                "\tamount\tINTEGER,\n" +
                "\tcreatedate\tINTEGER NOT NULL,\n" +
                "\tupdatedate\tINTEGER,\n" +
                "\tcanceldate\tINTEGER,\n" +
                "\tactive\tTEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tbmonthlyexpenses (" +
                "monthlyexpensesid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "description TEXT,\n" +
                "paymentdate INTEGER,\n" +
                "amount INTEGER,\n" +
                "createdate INTEGER NOT NULL,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active TEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tbanotherexpenses (" +
                "anotherexpensesid  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "description TEXT,\n" +
                "paymentdate INTEGER,\n" +
                "amount TEXT,\n" +
                "createdate INTEGER NOT NULL,\n" +
                "updatedate INTEGER,\n" +
                "canceldate INTEGER,\n" +
                "active TEXT NOT NULL\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS tbrevenues (" +
                "revenueId INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "purchaseorderid INTEGER,\n" +
                "month INTEGER,\n" +
                "year INTEGER,\n" +
                "createdate INTEGER NOT NULL\n" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
