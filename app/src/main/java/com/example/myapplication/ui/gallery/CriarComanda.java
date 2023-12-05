package com.example.myapplication.ui.gallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.database.SQDataBaseHelperConfig;
import com.example.myapplication.entity.*;
import com.example.myapplication.service.ListViewAdapterComandaService;
import com.example.myapplication.ui.tablePrices.ListViewAdapterProdutos;
import com.example.myapplication.ui.tablePrices.TablePricesViewModel;
import com.example.myapplication.util.Const;
import com.google.android.material.tabs.TabLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CriarComanda extends AppCompatActivity {

    private ListView listViewComanda;

    private ArrayList<ComandaView> visualizaComanda;
    SQLiteDatabase bancoDados;
    ArrayList<Products> produtosBase;
    ArrayList<Services> servicosBase;
    ArrayList<String> adProd;
    ArrayList<String> adService;
    ArrayList<String> adCustomer;
    ArrayList<Customer> listaCliente;
    Button botaoCrud;
    List<ServicosView> servicesViews;
    List<ProdutosView> productsViews;
    List<Worker> listWorker;
    Spinner listProduto;
    Spinner listServico;
    Spinner listCliente;
    TextView labelServico;
    TextView labelProduto;
    EditText quantidadeitem;
    Button botaoAddProduto;
    EditText editTextDataPagamento;
    Button botaoAnotacoes;
    String anotacoes = "N/D";
    String valorFuncionario = "N/D";
    Double amount;
    EditText total;
    int selectedTabPosition;
    Integer idWorker = null;
    private Integer customerId;
    private String metodoPagamento;
    public ArrayList<Integer> arrayIds;
    public ArrayList<Integer> arrayIdsProdutos;
    public ArrayList<Integer> arrayIdsServices;
    String observation;
    ArrayList<Products> arrayProdutos;
    private SQDataBaseHelperConfig db;
    //db = new SQDataBaseHelperConfig(getActivity());

    private ArrayList<String> linhas;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_comanda);
        listaCliente = new ArrayList<>();

        servicesViews = new ArrayList<>();
        productsViews = new ArrayList<>();
        botaoAnotacoes = (Button) findViewById(R.id.buttonAnnotations);
        labelProduto = (TextView) findViewById(R.id.textViewProduto);
        labelServico = (TextView) findViewById(R.id.textViewServico);
        labelProduto.setVisibility(View.INVISIBLE);
        listProduto =  (Spinner) findViewById(R.id.spinnerProduto);
        listProduto.setVisibility(View.INVISIBLE);
        listServico = (Spinner) findViewById(R.id.spinnerServico);
        editTextDataPagamento = (EditText) findViewById(R.id.editTextDateDiaAno);
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
                    editTextDataPagamento.setText(current);
                    editTextDataPagamento.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        editTextDataPagamento.addTextChangedListener(tw);
        LocalDate hoje = LocalDate.now();
        String monthValu;
        if (hoje.getMonthValue() < 10){
        monthValu = "0"+hoje.getMonthValue();
        } else {
        Integer m = hoje.getMonthValue();
        monthValu = m.toString();
        }
        String daydomes;
        if (hoje.getDayOfMonth() < 10){
            daydomes = "0" + hoje.getDayOfMonth();
        } else {
            daydomes = String.valueOf(hoje.getDayOfMonth());
        }
        editTextDataPagamento.setText(daydomes+monthValu+hoje.getYear());

        final int[] selecaopagamaneto = {0};
        botaoCrud = (Button) findViewById(R.id.buttonCrudComanda);
        botaoCrud.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder msgBox = new AlertDialog.Builder(v.getContext());
                msgBox.setTitle("Método de Pagamento");
                List<String> LISTAPAGAMENTO = Arrays.asList(Const.LIST_PAYMENT_METHOD);
                selecaopagamaneto[0] = 0;

                msgBox.setSingleChoiceItems(Const.LIST_PAYMENT_METHOD, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selecaopagamaneto[0] = which;

                    }
                });
                msgBox.setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        metodoPagamento = LISTAPAGAMENTO.get(selecaopagamaneto[0]);
                        String data = editTextDataPagamento.getText().toString();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate diaAno = LocalDate.parse(editTextDataPagamento.getText().toString(), formatter);

                        createNewOrder(servicesViews,productsViews,anotacoes ,customerId,amount,metodoPagamento,diaAno.toString());
                    }
                });
                msgBox.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                msgBox.show();

                //createNewOrder(servicesViews,productsViews,anotacoes ,customerId,amount,"DINHEIRO",LocalDate.now());
            }
        });
        //create a list of items for the spinner.
        listarProdutos();
        listarServicos ();
        listarDadosClientesSpinner();
        quantidadeitem = (EditText) findViewById(R.id.editQuantidadeProduto);
        total = (EditText) findViewById(R.id.editTextTotal);
        total.setFocusable(false);
        //String[] itemsProduto = {adProd.toArray().toString()};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterProduto = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, adProd);
        //set the spinners adapter to the previously created one.
        listProduto.setAdapter(adapterProduto);
        ArrayList<String> s = listarFuncionarios();
        total.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(current)){
                    total.removeTextChangedListener(this);
                    String partString = s.subSequence(0,10).toString();
                   // String secondString =  s.subSequence(11, count).toString();
                    String cleanString = s.toString().replaceAll("[Total R$: ]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed));
                    String finalValor = formatted.toString().replaceAll("[$]", "");
                    current = partString + finalValor;
                    total.setText(current);
                    total.setSelection(formatted.length());

                    total.addTextChangedListener(this);

                    // String tes = productvalue.getText().toString();
                    // String cleanString = tes.toString().replaceAll("[$,]", "");
                }
            }
        });

        //create a list of items for the spinner.
        //String[] itemsServico = Const.LIST_TESTE;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterServico = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, adService);
        //set the spinners adapter to the previously created one.
        listServico.setAdapter(adapterServico);

        listCliente =  (Spinner) findViewById(R.id.spinnerNomeDoCliente);
        //create a list of items for the spinner.
        //String[] itemsCliente = Const.LIST_DOCUMENTS_TYPE;
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.


        ArrayAdapter<String> adapterCliente = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, adCustomer);
        //set the spinners adapter to the previously created one.
        listCliente.setAdapter(adapterCliente);

        listViewComanda = (ListView) findViewById(R.id.lista_comanda_criar);

        botaoAddProduto = (Button) findViewById(R.id.buttonAddProduto);
        amount = 0d;
        botaoAnotacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputAnotation = new EditText(v.getContext());

                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Anotações")
                        .setView(inputAnotation)
                        .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String editTextInput = inputAnotation.getText().toString();
                                anotacoes = editTextInput;
                                Log.d("onclick","editext value is: "+ editTextInput);
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();
                dialog.show();


            }
        });
        arrayProdutos = new ArrayList<Products>();
        visualizaComanda = new ArrayList<ComandaView>();
        botaoAddProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                ComandaView produtoCadasNome = new ComandaView();




                int qnt = Integer.parseInt(quantidadeitem.getText().toString());
            //    servicesViews = new ArrayList<>();
            //    productsViews = new ArrayList<>();
                if (selectedTabPosition == 1){
                    produtoCadasNome.setNome(produtosBase.get(listProduto.getSelectedItemPosition()).getProductname());
                    produtoCadasNome.setQuantidade(quantidadeitem.getText().toString());
                    produtoCadasNome.setValor(produtosBase.get(listProduto.getSelectedItemPosition()).getProductvalue());
                    ProdutosView item = new ProdutosView();
                    item.setQuantidade(quantidadeitem.getText().toString());
                    item.setIdProduto(produtosBase.get(listProduto.getSelectedItemPosition()).getProductid());
                    productsViews.add(item);
                    Float real = qnt * Float.parseFloat(produtosBase.get(listProduto.getSelectedItemPosition()).getProductvalue());
                    amount += real;

                    total.setText("Total : R$ " + amount);
                    visualizaComanda.add(produtoCadasNome);
                    listarDadosTeste(visualizaComanda);
                } else {
                    ArrayAdapter<String> adp = new ArrayAdapter<String>(v.getContext(),
                            android.R.layout.simple_spinner_item, s);

                    final Spinner sp = new Spinner(v.getContext());
                    sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                    sp.setAdapter(adp);
                    EditText inputAnotation = new EditText(v.getContext());
                    inputAnotation.setInputType(InputType.TYPE_CLASS_NUMBER);
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    AlertDialog.Builder msgBox = new AlertDialog.Builder(v.getContext());
                    builder.setView(sp);
                    builder.setTitle("Adicionar Comissão ?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int posicao = sp.getSelectedItemPosition();
                            Integer idfuncionario = listWorker.get(posicao).getWorkerid();
                            msgBox.setView(inputAnotation);
                            msgBox.setTitle("Porcentagem da Comissão :");
                            msgBox.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (inputAnotation.getText().toString().equals("")||inputAnotation.getText().toString()==null
                                           ){
                                        android.app.AlertDialog.Builder msgBox = new AlertDialog.Builder(v.getContext());
                                        msgBox.setTitle("Os seguintes campos são obrigatórios !");
                                        msgBox.setItems( Const.MGS_BOX_FUNCIONARIO_ALERTA_CAMPOS, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {}
                                        });
                                        AlertDialog alert = msgBox.create();
                                        alert.show();

                                    }else {
                                        idWorker = idfuncionario;

                                        String editTextInput = inputAnotation.getText().toString();
                                        double porcentagem = Double.parseDouble(editTextInput) / 100;
                                        String valorSerPosi = servicosBase.get(listServico.getSelectedItemPosition()).getServicevalue();
                                        double resPor = porcentagem * Double.parseDouble(servicosBase.get(listServico.getSelectedItemPosition()).getServicevalue());
                                        BigDecimal bd = BigDecimal.valueOf(resPor);
                                        bd = bd.setScale(2, RoundingMode.HALF_DOWN);
                                        valorFuncionario = Double.toString(bd.doubleValue());
                                        produtoCadasNome.setNome(servicosBase.get(listServico.getSelectedItemPosition()).getServicename());
                                        produtoCadasNome.setQuantidade(quantidadeitem.getText().toString());
                                        produtoCadasNome.setValor(servicosBase.get(listServico.getSelectedItemPosition()).getServicevalue());
                                        ServicosView item = new ServicosView();
                                        if (idWorker != null) {
                                            item.setIdFuncioanario(idWorker);
                                            item.setValorFuncionario(valorFuncionario);
                                        }
                                        item.setQuantidade(quantidadeitem.getText().toString());
                                        item.setIdServico(servicosBase.get(listServico.getSelectedItemPosition()).getServiceid());
                                        servicesViews.add(item);
                                        float real = qnt * Float.parseFloat(servicosBase.get(listServico.getSelectedItemPosition()).getServicevalue());
                                        amount += real;
                                        total.setText("Total : R$ " + amount);
                                        visualizaComanda.add(produtoCadasNome);
                                        listarDadosTeste(visualizaComanda);
                                    }
                                }
                            });
                            msgBox.show();

                        }
                    })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    produtoCadasNome.setNome(servicosBase.get(listServico.getSelectedItemPosition()).getServicename());
                                    produtoCadasNome.setQuantidade(quantidadeitem.getText().toString());
                                    produtoCadasNome.setValor(servicosBase.get(listServico.getSelectedItemPosition()).getServicevalue());
                                    ServicosView item = new ServicosView();
                                    item.setQuantidade(quantidadeitem.getText().toString());
                                    item.setIdServico(servicosBase.get(listServico.getSelectedItemPosition()).getServiceid());
                                    servicesViews.add(item);
                                    double real = qnt * Double.parseDouble(servicosBase.get(listServico.getSelectedItemPosition()).getServicevalue());
                                    amount += real;
                                    total.setText("Total : R$ " + amount);
                                    visualizaComanda.add(produtoCadasNome);
                                    listarDadosTeste(visualizaComanda);

                                }
                            });
                    builder.create().show();



                }


            }
        });

        listCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                customerId = listaCliente.get(position).getCustomerId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TabLayout tabLayout = ( TabLayout ) findViewById (R.id.menu_comanda); // obtém a referência de TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tabLayout . getSelectedTabPosition (); // obtém a posição para a guia atual selecionada



                if (selectedTabPosition == 0){
                    listProduto.setVisibility(View.INVISIBLE);
                    labelProduto.setVisibility(View.INVISIBLE);
                    listServico.setVisibility(View.VISIBLE);
                    labelServico.setVisibility(View.VISIBLE);

                     } else if (selectedTabPosition ==1){
                        listServico.setVisibility(View.INVISIBLE);
                        labelServico.setVisibility(View.INVISIBLE);
                        listProduto.setVisibility(View.VISIBLE);
                        labelProduto.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private ArrayList<String> listarFuncionarios() {
        ArrayList<String> list = new ArrayList<>();
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbworker ORDER BY workerName", null);
            listWorker = new ArrayList<>();
            Worker item = null;
            meuCursor.moveToFirst();
            while(meuCursor!=null){
               item = new Worker();
               item.setWorkerid(meuCursor.getInt(0));
               item.setWorkername(meuCursor.getString(1));
               item.setWorkerfunction(meuCursor.getString(13));
               list.add(meuCursor.getString(1));
               listWorker.add(item);
               meuCursor.moveToNext();
            }
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void listarDados (){
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbcustomer order by customername", null);
            linhas = new ArrayList<String>();
            arrayIds = new ArrayList<Integer>();
            ArrayAdapter meuAdapter = new ArrayAdapter<String>(
                   this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listViewComanda.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                linhas.add(meuCursor.getString(1));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }
            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void listarDadosTeste (ArrayList<ComandaView> produtos){
        try {

//            linhas = new ArrayList<String>();
//            arrayIds = new ArrayList<Integer>();
//            ArrayAdapter meuAdapter = new ArrayAdapter<String>(
//                    this,
//                    android.R.layout.simple_list_item_1,
//                    android.R.id.text1,
//                    linhas
//            );
//            listViewComanda.setAdapter(meuAdapter);



            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            ListViewAdapterComandaService meuAdapter = new ListViewAdapterComandaService(this, produtos );
            listViewComanda.setAdapter(meuAdapter);
            for (ComandaView p : produtos){
                HashMap<String,String> t1 = new HashMap<String, String>();
                t1.put("A", p.getNome());
                t1.put("B", p.getQuantidade());
                list.add(t1);
            }


        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void listarProdutos (){
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbproducts ORDER BY productname", null);
            linhas = new ArrayList<String>();
            arrayIdsProdutos = new ArrayList<Integer>();
            produtosBase = new ArrayList<Products>();
            Products item = null;
            meuCursor.moveToFirst();
            adProd = new ArrayList<>();
            while(meuCursor!=null){
                item = new Products();
                linhas.add(meuCursor.getString(1));
                arrayIdsProdutos.add(meuCursor.getInt(0));
                item.setProductid(meuCursor.getInt(0));
                item.setProductname(meuCursor.getString(1));
                item.setProductdescription(meuCursor.getString(2));
                item.setProducttype(meuCursor.getString(3));
                item.setProductvalue(meuCursor.getString(4));
                item.setProductmanufacturer(meuCursor.getString(5));
                item.setCreatedate(meuCursor.getString(6));
                item.setUpdatedate(meuCursor.getString(7));
                item.setCanceldate(meuCursor.getString(8));
                item.setActive(meuCursor.getString(9));
                adProd.add(item.getProductname()+"    "+"R$ "+item.getProductvalue());
                produtosBase.add(item);
                meuCursor.moveToNext();
            }

            bancoDados.close();



        } catch (Exception e){
            e.printStackTrace();

        }


    }

    public void listarServicos (){
        try {
            SQLiteDatabase bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbservices ORDER BY serviceName", null);
            linhas = new ArrayList<String>();
            arrayIdsServices = new ArrayList<Integer>();
            servicosBase = new ArrayList<Services>();
            Services item = null;
            meuCursor.moveToFirst();
            adService = new ArrayList<>();
            while(meuCursor!=null){
                item = new Services();
              //  linhas.add(meuCursor.getString(1));
                arrayIdsServices.add(meuCursor.getInt(0));
                item.setServiceid(meuCursor.getInt(0));
                item.setServicename(meuCursor.getString(1));
                item.setServicedescription(meuCursor.getString(2));
                item.setServicetype(meuCursor.getString(3));
                item.setServicevalue(meuCursor.getString(4));
                item.setCreatedate(meuCursor.getString(5));
                item.setUpdatedate(meuCursor.getString(6));
                item.setCanceldate(meuCursor.getString(7));
                item.setActive(meuCursor.getString(8));
                adService.add(item.getServicename()+"    "+"R$ "+item.getServicevalue());
                servicosBase.add(item);
                meuCursor.moveToNext();
            }

            bancoDados.close();



        } catch (Exception e){
            e.printStackTrace();

        }


    }

    public void createNewOrder (List<ServicosView> servicesViews, List<ProdutosView> productsViews,
                                String observations, Integer customerId, Double total, String paymentMethod, String diaAno){

        try {

                if (paymentMethod.equals("CARTÃO CRÉDITO")) {
                    Float porcentagem = 2.39f / 100f;
                    Float resPor = porcentagem * total.floatValue();
                    total -= resPor;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    String totFormat = decimalFormat.format(total);
                    totFormat.replaceAll(",", ".");
                    //total = Double.parseDouble(totFormat);

                } else if (paymentMethod.equals("CARTÃO DÉBITO")) {
                    Float porcentagem = 4.99f / 100f;
                    Float resPor = porcentagem * total.floatValue();
                    total -= resPor;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    String totFormat = decimalFormat.format(total);
                    totFormat.replaceAll(",", ".");
                    // total = Double.parseDouble(totFormat);
                }


                String finishPayment = "S";
                String valorPago = total.toString();
                String statusPagamento = "Pago";

                if (paymentMethod.equals("PENDENTE")) {
                    finishPayment = "N";
                    valorPago = "0";
                    statusPagamento = "Pendente";
                }

                /*
                 */


                if (total > 0 && total != null) {
                    bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
                    String sql = "INSERT INTO tbpurchaseorder (customerId, status, totalvalue, paidvalue,observation, paymentmethod, finishpayment, createdate,  active) " +
                            "VALUES  (?,?,?,?,?,?,?,?,?)";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindLong(1, customerId.longValue());
                    stmt.bindString(2, statusPagamento);
                    stmt.bindString(3, total.toString());
                    stmt.bindString(4, valorPago);
                    stmt.bindString(5, observations);
                    stmt.bindString(6, paymentMethod);
                    stmt.bindString(7, finishPayment);
                    stmt.bindString(8, diaAno.toString());
                    stmt.bindString(9, "S");
                    Long purchaseOrderId = stmt.executeInsert();


                    for (ProdutosView item : productsViews) {

                        String insertSql = "INSERT INTO tborderproduct (purchaseorderid,productid,quantity,createdate,active) " +
                                "VALUES  (?,?,?,?,?)";
                        SQLiteStatement stmtInsert = bancoDados.compileStatement(insertSql);
                        stmtInsert.bindLong(1, purchaseOrderId);
                        stmtInsert.bindLong(2, item.getIdProduto().longValue());
                        stmtInsert.bindString(3, item.getQuantidade());
                        stmtInsert.bindString(4, diaAno.toString());
                        stmtInsert.bindString(5, "S");
                        Long orderProductId = stmtInsert.executeInsert();


                    }

                    for (ServicosView item : servicesViews) {
                        String insertSql = "INSERT INTO tborderservice (purchaseorderid,serviceid,quantity,createdate,active) " +
                                "VALUES  (?,?,?,?,?)";
                        SQLiteStatement stmtInsert = bancoDados.compileStatement(insertSql);
                        stmtInsert.bindLong(1, purchaseOrderId);
                        stmtInsert.bindLong(2, item.getIdServico().longValue());
                        stmtInsert.bindString(3, item.getQuantidade());
                        stmtInsert.bindString(4, diaAno.toString());
                        stmtInsert.bindString(5, "S");
                        Long orderserviceId = stmtInsert.executeInsert();
                        if (item.getIdFuncioanario() != null) {
                            String insert = "INSERT INTO tbworkerearning (orderserviceid, workerId, earningamount, servicedate, paid, createdate, active) " +
                                    "VALUES  (?,?,?,?,?,?,?)";
                            SQLiteStatement inSmtmt = bancoDados.compileStatement(insert);
                            inSmtmt.bindLong(1, orderserviceId);
                            inSmtmt.bindLong(2, item.getIdFuncioanario());

                            inSmtmt.bindString(3, item.getValorFuncionario());
                            inSmtmt.bindString(4, diaAno.toString());
                            inSmtmt.bindString(5, "N");
                            inSmtmt.bindString(6, diaAno.toString());
                            inSmtmt.bindString(7, "S");
                            Long id = inSmtmt.executeInsert();
                        }
                    }


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

    public void listarDadosClientesSpinner (){
        try {
            bancoDados = openOrCreateDatabase(Const.NAME_DATABASE, MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT * FROM tbcustomer order by customername asc", null);
            linhas = new ArrayList<String>();
            arrayIds = new ArrayList<Integer>();
            Customer item = null;
            adCustomer = new ArrayList<>();
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                item = new Customer();
                //  linhas.add(meuCursor.getString(1));
                arrayIdsServices.add(meuCursor.getInt(0));
                item.setCustomerId(meuCursor.getInt(0));
                item.setCustomerName(meuCursor.getString(1));
                adCustomer.add(item.getCustomerName());
                listaCliente.add(item);
                meuCursor.moveToNext();
            }

            bancoDados.close();

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private void closefragment() {
        finish();

    }



}