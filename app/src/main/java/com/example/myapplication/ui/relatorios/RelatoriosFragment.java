package com.example.myapplication.ui.relatorios;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.database.SQDataBaseHelperConfig;
import com.example.myapplication.entity.ComandaList;
import com.example.myapplication.entity.ComandaView;
import com.example.myapplication.entity.OverviewEmpresa;
import com.example.myapplication.entity.RelatorioMensalListaCabecalho;
import com.example.myapplication.service.ListViewAdapterFuncionariosService;
import com.example.myapplication.service.ListViewAdapterListarComandasService;
import com.example.myapplication.service.ListViewAdapterRelatorioCabecalhoMensal;
import com.example.myapplication.service.ListViewAdapterRelatorioMensal;
import com.example.myapplication.ui.funcionarios.FuncionariosModel;
import com.example.myapplication.ui.gallery.VisualizarComanda;
import com.google.android.material.tabs.TabLayout;

import java.math.RoundingMode;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RelatoriosFragment extends Fragment {

    private RelatoriosModel relatoriosViewModel;
    private SQDataBaseHelperConfig db;
    TextView despesaMensal;
    TextView receitaMensal;
    TextView pendenteMensal;
    TextView faturadoMensal;

    TextView despesaAnual;
    TextView receitaAnual;
    TextView faturadoAnual;

    ListView cabecalhoMensal;
    ListView relatorioMensal;
    ListView cabecalhoAnual;
    ListView relatorioAnual;


    TextView faturadoOverviewGeral;
    TextView despesasOverviewGeral;
    TextView receitasOverviewGeral;
    TextView funcionariosOverviewGeral;
    TextView balancoOverviewGeral;

    TextView faturadoOverviewDiario;
    TextView despesasOverviewDiario;
    TextView receitasOverviewDiario;
    TextView funcionariosOverviewDiario;
    TextView balancoOverviewDiario;
    String mesrelatorioselecionado;
    LinearLayout overviewRelatorioGeral;
    LinearLayout overviewRelatorioDiario;

    ArrayList<LocalDate> listaDatas;


    Float amount = 0F;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db =  new SQDataBaseHelperConfig(getActivity());
        relatoriosViewModel =
                new ViewModelProvider(this).get(RelatoriosModel.class);
        View root = inflater.inflate(R.layout.fragment_relatorios, container, false);
        overviewRelatorioGeral = (LinearLayout) root.findViewById(R.id.layout_overview);
        overviewRelatorioDiario = (LinearLayout) root.findViewById(R.id.layout_overview_diario);

        cabecalhoMensal = (ListView) root.findViewById(R.id.list_mensal_relatorio_header);
        cabecalhoMensal.setVisibility(View.INVISIBLE);
        relatorioMensal = (ListView) root.findViewById(R.id.list_mensal_relatorio11);
        relatorioMensal.setVisibility(View.INVISIBLE);


        OverviewEmpresa telaOverview = montarOverviewTela();
        faturadoOverviewGeral = (TextView) root.findViewById(R.id.label_overview_geral_faturado);
        despesasOverviewGeral = (TextView) root.findViewById(R.id.label_overview_geral_despesa);
        receitasOverviewGeral = (TextView) root.findViewById(R.id.label_overview_geral_receita);
        funcionariosOverviewGeral = (TextView) root.findViewById(R.id.label_overview_geral_funcionarios);
        balancoOverviewGeral = (TextView) root.findViewById(R.id.label_overview_geral_balanco);

        faturadoOverviewDiario = (TextView) root.findViewById(R.id.label_overview_diario_faturado);
        despesasOverviewDiario = (TextView) root.findViewById(R.id.label_overview_diario_despesa);
        receitasOverviewDiario = (TextView) root.findViewById(R.id.label_overview_diario_receita);
        funcionariosOverviewDiario = (TextView) root.findViewById(R.id.label_overview_diario_funcionarios);
        balancoOverviewDiario = (TextView) root.findViewById(R.id.label_overview_diario_balanco);

        faturadoOverviewGeral.setText("Soma total do faturado até hoje : "+ telaOverview.getFaturadoGeral());
        despesasOverviewGeral.setText("Total já Gasto com Despesas: "+telaOverview.getDespesasGeral());
        receitasOverviewGeral.setText("Total de receitas: "+telaOverview.getReceitasGeral());
        funcionariosOverviewGeral.setText("Total já gasto com funcionarios : "+telaOverview.getFuncionariosGeral());
        faturadoOverviewDiario.setText("Total faturado hoje : "+telaOverview.getFaturadoMes());
        despesasOverviewDiario.setText("Total de despesas hoje: "+telaOverview.getDespesaMes());
        receitasOverviewDiario.setText("Total de receitas hoje: "+telaOverview.getReceitaMes());
        funcionariosOverviewDiario.setText("Total gasto com funcioanrios hoje: "+telaOverview.getFuncionariosMes());
        balancoOverviewDiario.setText("Balanço Diario da Empresa");
        balancoOverviewGeral.setText("Balanço total da empresa até hoje");

        cabecalhoAnual = (ListView) root.findViewById(R.id.list_anual_relatorio_header);
        cabecalhoAnual.setVisibility(View.INVISIBLE);
        relatorioAnual = (ListView) root.findViewById(R.id.list_anual_relatorio);
        relatorioAnual.setVisibility(View.INVISIBLE);

        despesaMensal = (TextView) root.findViewById(R.id.labeltext_mensal_despezas);
        receitaMensal = (TextView) root.findViewById(R.id.labeltext_mensal_receitas);
        pendenteMensal = (TextView) root.findViewById(R.id.labeltext_mensal_pendente);
        faturadoMensal = (TextView) root.findViewById(R.id.labeltext_mensal_faturado);


        despesaAnual = (TextView) root.findViewById(R.id.labeltext_anual_despezas2);
        receitaAnual = (TextView) root.findViewById(R.id.labeltext_anual_receitas3);
        faturadoAnual = (TextView) root.findViewById(R.id.labeltext_anual_faturado2);
        despesaAnual.setVisibility(View.INVISIBLE);
        receitaAnual.setVisibility(View.INVISIBLE);
        faturadoAnual.setVisibility(View.INVISIBLE);


        despesaMensal.setVisibility(View.INVISIBLE);
        receitaMensal.setVisibility(View.INVISIBLE);
        pendenteMensal.setVisibility(View.INVISIBLE);
        faturadoMensal.setVisibility(View.INVISIBLE);

        carregaLabelsMensal();
        carregaLabelsAnual();
        configuraCabecalho();
        listaRelatorioMensal();
        configuraCabecalhoAnual();
        listaRelatorioAnual();
        TabLayout tabLayout = ( TabLayout ) root.findViewById (R.id.tab_relatorio); // obtém a referência de TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tabLayout . getSelectedTabPosition (); // obtém a posição para a guia atual selecionada



                if (selectedTabPosition == 0){
                    //OVERVIEW
                    overviewRelatorioGeral.setVisibility(View.VISIBLE);
                    overviewRelatorioDiario.setVisibility(View.VISIBLE);
                    activeInativeMensal(View.INVISIBLE);
                    activeInativeAnual(View.INVISIBLE);
                    //Toast.makeText(getContext(), "Selecionado Produtos"+selectedTabPosition, Toast.LENGTH_SHORT).show();
                } else if (selectedTabPosition ==1){
                    //MENSAL
                    overviewRelatorioGeral.setVisibility(View.INVISIBLE);
                    overviewRelatorioDiario.setVisibility(View.INVISIBLE);
                    activeInativeMensal(View.VISIBLE);
                    activeInativeAnual(View.INVISIBLE);
                    //Toast.makeText(getContext(), "Selecionado Serviços"+selectedTabPosition, Toast.LENGTH_SHORT).show();
                } else if (selectedTabPosition == 2){
                    //ANUAL
                    overviewRelatorioGeral.setVisibility(View.INVISIBLE);
                    overviewRelatorioDiario.setVisibility(View.INVISIBLE);
                    activeInativeMensal(View.INVISIBLE);
                    activeInativeAnual(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        relatorioMensal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // String name = linhas.get(i);
                mesrelatorioselecionado = listaDatas.get(i).toString();
                AlertDialog.Builder msgBox = new AlertDialog.Builder(view.getContext());
                msgBox.setTitle("Visualizar Detalhamento ?");
                msgBox.setIcon(android.R.drawable.ic_menu_agenda);
                msgBox.setNegativeButton("Abrir", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // abrirDetalhamentoMes(view, idSelecionado);
                        abrirDetalhamentoMes(view, mesrelatorioselecionado);

                    }
                });


                msgBox.show();
            }});



        return root;
    }
    private void abrirDetalhamentoMes(View view, String messelecionado) {
        Intent intent = new Intent(view.getContext(), DetalhamentoMensal.class);
        intent.putExtra("data",messelecionado);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void carregaLabelsAnual() {
        double meses;
        LocalDate hoje = LocalDate.now();

        LocalDate dateStart = hoje.with( TemporalAdjusters.firstDayOfYear() );

        int m1 = dateStart.getYear() * 12 + dateStart.getMonthValue();
        int m2 = hoje.getYear() * 12 + hoje.getMonthValue();
        meses  = m2 - m1 + 1;


        String data = hoje.with( TemporalAdjusters.firstDayOfYear() ).toString();
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursorMensalReceita = bancoDados.rawQuery("SELECT  sum(totalvalue) as receita, sum(paidvalue) as totalPago, *  from tbpurchaseorder  where createdate >= '"+data+"' ", null);
            meuCursorMensalReceita.moveToFirst();
            Cursor meuCursorVariavel = bancoDados.rawQuery("SELECT  sum(amount) as despesa , *from tbanotherexpenses where paymentdate >= '"+data+"' ", null);
            meuCursorVariavel.moveToFirst();
            Cursor meuCursorMensal = bancoDados.rawQuery("SELECT sum(amount) as despesa,* from tbmonthlyexpenses order by createdate DESC ", null);
            meuCursorMensal.moveToFirst();
            Cursor meuCursorMensalSalario = bancoDados.rawQuery("SELECT sum(earningamount) as salario,* from tbworkerearning where servicedate >= '"+data+"' ", null);
            meuCursorMensalSalario.moveToFirst();

            String var;
            String men;
            String sal;
            if (meuCursorVariavel!=null && meuCursorVariavel.getCount()>0){
                var = meuCursorVariavel.getString(0);
            } else {
                var = "0";
            }
            if (meuCursorMensal!=null && meuCursorMensal.getCount()>0) {
                men = meuCursorMensal.getString(0);
            } else {
                men = "0";
            }
            if (meuCursorMensalSalario!=null && meuCursorMensalSalario.getCount()>0) {
                sal = meuCursorMensalSalario.getString(0);
            } else {
                sal = "0";
            }

            double vJaPago ;
            double vReceita;

            if (meuCursorMensalReceita!=null && meuCursorMensalReceita.getCount()>0) {
                vJaPago = Double.parseDouble(meuCursorMensalReceita.getString(1));
                vReceita = Double.parseDouble(meuCursorMensalReceita.getString(0));;
            } else {
                vJaPago = 0;
                vReceita = 0;
            }


            double mens = Double.parseDouble(men)*meses;
            if (var == null){
                var = "0";
            }
            double vars = Double.parseDouble(var);
            if (sal == null){
                sal = "0";
            }
            double salario = Double.parseDouble(sal);

            Double depending = mens + vars + salario;
            double valorJaPago = vJaPago;
            double valorReceita = vReceita;
            double valorFaturado = valorReceita-depending;
            double desp = depending;

            despesaAnual.setText("Despesas desse ano: "+NumberFormat.getCurrencyInstance().format((desp)));
            receitaAnual.setText("Receitas desse ano: "+NumberFormat.getCurrencyInstance().format((valorReceita)));
            faturadoAnual.setText("Faturado esse ano: "+NumberFormat.getCurrencyInstance().format((valorFaturado)));

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void carregaLabelsMensal() {
        LocalDate hoje = LocalDate.now();

        String data = hoje.with( TemporalAdjusters.firstDayOfMonth() ).toString();
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursorMensalReceita = bancoDados.rawQuery("SELECT  sum(totalvalue) as receita, sum(paidvalue) as totalPago, *  from tbpurchaseorder  where createdate >= '"+data+"' ", null);
            meuCursorMensalReceita.moveToFirst();
            Cursor meuCursorVariavel = bancoDados.rawQuery("SELECT  sum(amount) as despesa , *from tbanotherexpenses where paymentdate >= '"+data+"' ", null);
            meuCursorVariavel.moveToFirst();
            Cursor meuCursorMensal = bancoDados.rawQuery("SELECT sum(amount) as despesa,* from tbmonthlyexpenses order by createdate DESC ", null);
            meuCursorMensal.moveToFirst();
            Cursor meuCursorMensalSalario = bancoDados.rawQuery("SELECT sum(earningamount) as salario,* from tbworkerearning where servicedate >= '"+data+"' ", null);
            meuCursorMensalSalario.moveToFirst();

            String var;
            String men;
            String sal;
            if (meuCursorVariavel!=null && meuCursorVariavel.getCount()>0){
                var = meuCursorVariavel.getString(0);
            } else {
                var = "0";
            }
            if (meuCursorMensal!=null && meuCursorMensal.getCount()>0) {
                men = meuCursorMensal.getString(0);
            } else {
                men = "0";
            }
            if (meuCursorMensalSalario!=null && meuCursorMensalSalario.getCount()>0) {
                sal = meuCursorMensalSalario.getString(0);
            } else {
                sal = "0";
            }

            double vJaPago ;
            double vReceita;

            if (meuCursorMensalReceita!=null && meuCursorMensalReceita.getCount()>0) {
                vJaPago = Double.parseDouble(meuCursorMensalReceita.getString(1));
                vReceita = Double.parseDouble(meuCursorMensalReceita.getString(0));;
            } else {
                vJaPago = 0;
                vReceita = 0;
            }

            double mens = Double.parseDouble(men);
            if (var == null){
                var = "0";
            }
            double vars = Double.parseDouble(var);
            if (sal == null){
                sal = "0";
            }
            double salario = Double.parseDouble(sal);

            Double depending = mens + vars + salario;
            double valorJaPago = vJaPago;
            double valorReceita = vReceita;
            double valorPendente = valorReceita-valorJaPago;
            double valorFaturado = valorReceita-depending;

            double desp = depending;
            despesaMensal.setText("Despesas desse mes : "+NumberFormat.getCurrencyInstance().format((desp)));
            receitaMensal.setText("Receitas desse mes : "+NumberFormat.getCurrencyInstance().format((valorReceita)));
            pendenteMensal.setText("Receitas Pendente desse mes : "+NumberFormat.getCurrencyInstance().format((valorPendente)));
            faturadoMensal.setText("Valor faturado esse mes : "+ NumberFormat.getCurrencyInstance().format((valorFaturado)));


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void activeInativeMensal(int value) {
        cabecalhoMensal.setVisibility(value);
        relatorioMensal.setVisibility(value);
        despesaMensal.setVisibility(value);
        receitaMensal.setVisibility(value);
        pendenteMensal.setVisibility(value);
        faturadoMensal.setVisibility(value);
    }

        private void activeInativeAnual(int value) {
            cabecalhoAnual.setVisibility(value);
            relatorioAnual.setVisibility(value);
            despesaAnual.setVisibility(value);
            receitaAnual.setVisibility(value);
            faturadoAnual.setVisibility(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listaRelatorioAnual() {
        double meses;
        LocalDate hoje = LocalDate.now();

        LocalDate dateStart = hoje.with( TemporalAdjusters.firstDayOfYear() );

        int m1 = dateStart.getYear() * 12 + dateStart.getMonthValue();
        int m2 = hoje.getYear() * 12 + hoje.getMonthValue();
        meses  = m2 - m1 + 1;
        try {
            ArrayList<RelatorioMensalListaCabecalho> listCabecalho = new ArrayList<>();


            ListViewAdapterRelatorioMensal meuAdapter = new
                    ListViewAdapterRelatorioMensal(getActivity(), listCabecalho);
            relatorioAnual.setAdapter(meuAdapter);
            RelatorioMensalListaCabecalho menuItem = null;

            String dataInicio = null;
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT strftime('%Y', createdate) as ano, sum(totalvalue) as receita, sum(paidvalue) as totalPago, *  from tbpurchaseorder group by ano order by createdate DESC ", null);
            Cursor meuCursorVariavel = bancoDados.rawQuery("SELECT strftime('%Y', createdate) as ano, sum(amount) as despesa , *from tbanotherexpenses group by ano order by createdate DESC ", null);


            meuCursor.moveToFirst();
            if (meuCursor.getCount() >= meuCursorVariavel.getCount()) {
                int tamanho = meuCursor.getCount();
                int inicioPassagem = 0;
                meuCursor.moveToFirst();
                while(meuCursor!=null && tamanho > inicioPassagem) {
                    inicioPassagem++;
                    String data = meuCursor.getString(0);

                    Cursor meuCursorMensal = bancoDados.rawQuery("SELECT  " +
                            "strftime('%Y', createdate) as ano, sum(amount) as despesa,* " +
                            "from tbmonthlyexpenses where ano ='"+data+"' group by ano order by createdate DESC ", null);

                    meuCursorMensal.moveToFirst();
                    //String dataMen = meuCursorMensal.getString(0);

                    Cursor meuVariavel = bancoDados.rawQuery("SELECT " +
                            "strftime('%Y', paymentdate) as ano, sum(amount) as despesa,* " +
                            "from tbanotherexpenses where ano = '"+data+"' group by ano", null);

                    Cursor meuCursorMensalSalario = bancoDados.rawQuery("SELECT sum(earningamount) as salario,strftime('%Y', servicedate) as ano,* from tbworkerearning where ano = '"+data+"' ", null);
                    meuCursorMensalSalario.moveToFirst();

                    String sal;
                    String var;
                    String men;
                    meuVariavel.moveToFirst();
                   // String dataVarMen = meuVariavel.getString(0);

                    if (meuVariavel!=null && meuVariavel.getCount()>= 1){
                        var = meuVariavel.getString(1);
                    } else {
                        var = "0";
                    }
                    if (meuCursorMensal!=null &&  meuCursorMensal.getCount()>=1) {
                        men = meuCursorMensal.getString(1);
                    } else {
                        men = "0";
                    }
                    if (meuCursorMensalSalario!=null &&  meuCursorMensalSalario.getCount()>=1) {
                        sal = meuCursorMensalSalario.getString(0);
                    } else {
                        sal = "0";
                    }
                    double salario = Double.parseDouble(sal);
                    double mens = Double.parseDouble(men)*meses;
                    double vars = Double.parseDouble(var);
                    Double depending = mens + vars + salario;

                    double valorJaPago = Double.parseDouble(meuCursor.getString(2));
                    double valorReceita = Double.parseDouble(meuCursor.getString(1));;
                    double valorPendente = valorReceita-valorJaPago;
                    double valorFaturado = valorReceita - depending;


                    menuItem = new RelatorioMensalListaCabecalho();
                    menuItem.setAno("  ");
                    menuItem.setDespeza(NumberFormat.getCurrencyInstance().format((depending)));
                    menuItem.setMes(data);
                    menuItem.setPendente(NumberFormat.getCurrencyInstance().format((valorPendente)));
                    menuItem.setFaturado(NumberFormat.getCurrencyInstance().format((valorFaturado)));
                    menuItem.setReceita(NumberFormat.getCurrencyInstance().format((valorReceita)));
                    listCabecalho.add(menuItem);



                    meuCursor.moveToNext();

                }
            }


           /* for (int i = 2019; i < 2023; i++){
                menuItem = new RelatorioMensalListaCabecalho();
                menuItem.setAno("  ");
                menuItem.setDespeza("9,850.00 ");
                menuItem.setMes(Integer.toString(i));
                menuItem.setPendente("450.00");
                menuItem.setFaturado("3,500.00");
                menuItem.setReceita("5,000.00 ");
                listCabecalho.add(menuItem);

            }*/


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listaRelatorioMensal() {
        int quantMeses = 0;
        try {
            String dataInicio = null;
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbpurchaseorder order by createdate asc ", null);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                String data = meuCursor.getString(8);
                if (!data.isEmpty()){
                    dataInicio =    meuCursor.getString(8);
                    meuCursor = null;
                } else {
                    meuCursor.moveToNext();
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateStart = LocalDate.parse(dataInicio, formatter);
            LocalDate hoje = LocalDate.now();
            int m1 = dateStart.getYear() * 12 + dateStart.getMonthValue();
            int m2 = hoje.getYear() * 12 + hoje.getMonthValue();
            quantMeses  = m2 - m1 + 1;

            ArrayList<RelatorioMensalListaCabecalho> listaDividendos = new ArrayList<>();





            ListViewAdapterRelatorioMensal meuAdapter = new
                    ListViewAdapterRelatorioMensal(getActivity(), listaDividendos);
            relatorioMensal.setAdapter(meuAdapter);
            RelatorioMensalListaCabecalho menuItem = null;
            meuCursor = null;
            meuCursor = bancoDados.rawQuery("SELECT strftime('%m', createdate) as mes, strftime('%Y', createdate) as ano, sum(totalvalue) as receita, sum(paidvalue) as totalPago, *  from tbpurchaseorder group by mes,ano order by createdate DESC ", null);
            List<LocalDate> datasGeradasNaConsulta = new ArrayList<>();
            Cursor meuCursorVariavel = bancoDados.rawQuery("SELECT strftime('%m', paymentdate) as mes,strftime('%Y', paymentdate) as ano, sum(amount) as despesa , *from tbanotherexpenses group by mes order by paymentDate DESC ", null);
            listaDatas = new ArrayList<>();
            List<LocalDate> mesAnoConsultas = new ArrayList<>();
                if (meuCursor.getCount() >= meuCursorVariavel.getCount()) {
                    int tamanho = meuCursor.getCount();
                    int inicioPassagem = 0;
                    meuCursor.moveToFirst();
                    meuCursorVariavel.moveToFirst();
                    int pv = 1;
                    while(meuCursor!=null && tamanho > inicioPassagem) {
                        inicioPassagem++;
                        //String mesAnoReceita = "01-"+meuCursor.getString(0)+"-"+meuCursor.getString(1);
                        //String mesAnoVariavel = "01-"+meuCursorVariavel.getString(0)+"-"+meuCursorVariavel.getString(1);
                        LocalDate localDateReceita = null;
                        LocalDate localDateDespesa = null;
                        if (meuCursor.isNull(0)||meuCursor.getString(0) == null){
                            localDateReceita = null;
                        } else {
                            localDateReceita = LocalDate.of(Integer.parseInt(meuCursor.getString(1)), Integer.parseInt(meuCursor.getString(0)), 1);
                        }

                        if (meuCursorVariavel.getCount() < pv ){
                            localDateDespesa = null;
                        } else {
                            localDateDespesa = LocalDate.of(Integer.parseInt(meuCursorVariavel.getString(1)),Integer.parseInt(meuCursorVariavel.getString(0)),1);
                        }
                        pv ++;
                        if (localDateDespesa == null){
                            mesAnoConsultas.add(localDateReceita);
                            meuCursor.moveToNext();
                        } else if (localDateReceita == null){
                            mesAnoConsultas.add(localDateDespesa);
                            meuCursorVariavel.moveToNext();
                        } else if(localDateDespesa.equals(localDateReceita)){
                            //adcionarreceita
                            mesAnoConsultas.add(localDateReceita);
                            meuCursor.moveToNext();
                            meuCursorVariavel.moveToNext();
                        } else if (localDateDespesa.isAfter(localDateReceita)){
                            //adciona receita
                            //adciona despesa
                            mesAnoConsultas.add(localDateDespesa);
                            mesAnoConsultas.add(localDateReceita);

                            meuCursor.moveToNext();
                            meuCursorVariavel.moveToNext();
                        } else if (localDateDespesa.isBefore(localDateReceita)){
                            //adciona despesa
                            //adciona receita
                            mesAnoConsultas.add(localDateReceita);
                            mesAnoConsultas.add(localDateDespesa);

                            meuCursor.moveToNext();
                            meuCursorVariavel.moveToNext();
                        }
                    }
                     mesAnoConsultas.stream().sorted(Comparator.comparing(localDate -> localDate));


                    Set<LocalDate> datasListaConsulta = new HashSet<>();
                    datasListaConsulta.addAll(mesAnoConsultas);
                    datasListaConsulta.forEach(localDate -> {
                        datasGeradasNaConsulta.add(localDate);
                    });
                    IntStream.range(0, datasGeradasNaConsulta.size())
                            .forEach(idx -> {
                                if (idx < datasGeradasNaConsulta.size()-1) {
                                    LocalDate dt1 = datasGeradasNaConsulta.get(idx + 1);
                                    if (dt1.isAfter(datasGeradasNaConsulta.get(idx))) {
                                        LocalDate dt2 = datasGeradasNaConsulta.get(idx);
                                        datasGeradasNaConsulta.set(idx, dt1);
                                        datasGeradasNaConsulta.set(idx + 1, dt2);
                                    }
                                }
                            })
                    ;



                    System.out.println(datasGeradasNaConsulta);

                }

            for (LocalDate dataParaConsulta : datasGeradasNaConsulta) {
               // String mesAnoQueryData = meuCursor.getString(1) + "-" + meuCursor.getString(0) + "-" + "01";
                String mesAnoQueryData = dataParaConsulta.toString();
                LocalDate dataReferenciaQuery = LocalDate.parse(mesAnoQueryData, formatter);
                String mes;
                if (dataParaConsulta.getMonthValue() < 10){
                    mes = "0"+dataParaConsulta.getMonthValue();
                } else {
                    mes = String.valueOf(dataParaConsulta.getMonthValue());
                }
                listaDatas.add(dataReferenciaQuery);
                Cursor  meuCursorReceitaTotal = bancoDados.rawQuery("SELECT strftime('%m', createdate) as mes, strftime('%Y', createdate) as ano, sum(totalvalue) as receita, sum(paidvalue) as totalPago, *  from tbpurchaseorder where ano = '" + dataParaConsulta.getYear() + "' and mes = '" + mes + "' group by mes order by createdate DESC ", null);

                Cursor meuCursorMensal = bancoDados.rawQuery("SELECT  " +
                        "strftime('%Y', createdate) as ano, sum(amount) as despesa,* " +
                        "from tbmonthlyexpenses where ano ='" + dataParaConsulta.getYear() + "' group by ano order by createdate DESC ", null);

                Cursor meuVariavel = bancoDados.rawQuery("SELECT strftime('%m', paymentdate) as mes, " +
                        "strftime('%Y', paymentdate) as ano, sum(amount) as despesa,* " +
                        "from tbanotherexpenses where ano = '" + dataParaConsulta.getYear() + "' and mes = '" + mes + "' group by mes", null);

                Cursor meuCursorSalario = bancoDados.rawQuery("SELECT sum(earningamount) as salario, strftime('%Y', servicedate) as ano,strftime('%m', servicedate) as mes,* from tbworkerearning where ano = '" + dataParaConsulta.getYear() + "' and mes = '" + mes + "' group by mes order by mes desc", null);
                meuCursorSalario.moveToFirst();


                String sal;
                if (meuCursorSalario != null && meuCursorSalario.getCount() > 0 && meuCursorSalario.getString(0) != null) {
                    sal = meuCursorSalario.getString(0);
                } else {
                    sal = "0";
                }
                String var;
                String men;
                meuVariavel.moveToFirst();
                if (meuVariavel != null && meuVariavel.getCount() > 0 && meuVariavel.getString(0) != null) {
                    var = meuVariavel.getString(2);
                } else {
                    var = "0";
                }
                meuCursorMensal.moveToFirst();
                int plm = meuCursorMensal.getPosition();
                if (meuCursorMensal != null && meuCursorMensal.getCount() > 0 && meuCursorMensal.getString(0) != null) {
                    men = meuCursorMensal.getString(1);
                } else {
                    men = "0";
                }


                double valorJaPago = 0;
                double valorReceita = 0;
                meuCursorReceitaTotal.moveToFirst();
                if (meuCursorReceitaTotal.getCount() <= 0 || meuCursorReceitaTotal.isNull(0)) {
                    valorJaPago = 0;
                    valorReceita = 0;
                } else {
                    valorJaPago = Double.parseDouble(meuCursorReceitaTotal.getString(3));
                    valorReceita = Double.parseDouble(meuCursorReceitaTotal.getString(2));

                }
                double salario = Double.parseDouble(sal);
                double mens = Double.parseDouble(men);
                double vars = Double.parseDouble(var);
                Double depending = mens + vars + salario;

                double valorPendente = valorReceita - valorJaPago;
                double valorFaturado = valorReceita - depending;

                menuItem = new RelatorioMensalListaCabecalho();
                menuItem.setAno("" + dataReferenciaQuery.getYear());
                menuItem.setDespeza(NumberFormat.getCurrencyInstance().format((depending)));
                menuItem.setMes(traduzMes(dataReferenciaQuery.getMonth().toString()));

                menuItem.setPendente(NumberFormat.getCurrencyInstance().format((valorPendente)));
                menuItem.setFaturado(NumberFormat.getCurrencyInstance().format((valorFaturado)));

                menuItem.setReceita(NumberFormat.getCurrencyInstance().format((valorReceita)));
                listaDividendos.add(menuItem);
            }






           //Modificar a query para aceitar todos os meses disponiveis da receita

           /* if (meuCursor.getCount() >= meuCursorVariavel.getCount()){


                int tamanho = meuCursor.getCount();
                int inicioPassagem = 0;
                meuCursor.moveToFirst();
                while(meuCursor!=null && tamanho > inicioPassagem) {
                    inicioPassagem++;
                    //String t = meuCursor.getString(5);
                    if (meuCursor.getString(1) != null) {
                        String mesAnoQueryData = meuCursor.getString(1) + "-" + meuCursor.getString(0) + "-" + "01";
                        LocalDate dataReferenciaQuery = LocalDate.parse(mesAnoQueryData, formatter);
                        listaDatas.add(dataReferenciaQuery);
                        Cursor meuCursorMensal = bancoDados.rawQuery("SELECT  " +
                                "strftime('%Y', createdate) as ano, sum(amount) as despesa,* " +
                                "from tbmonthlyexpenses where ano ='" + meuCursor.getString(1) + "' group by ano order by createdate DESC ", null);

                        Cursor meuVariavel = bancoDados.rawQuery("SELECT strftime('%m', paymentdate) as mes, " +
                                "strftime('%Y', paymentdate) as ano, sum(amount) as despesa,* " +
                                "from tbanotherexpenses where ano = '" + meuCursor.getString(1) + "' and mes = '" + meuCursor.getString(0) + "' group by mes", null);

                        Cursor meuCursorSalario = bancoDados.rawQuery("SELECT sum(earningamount) as salario, strftime('%Y', servicedate) as ano,strftime('%m', servicedate) as mes,* from tbworkerearning where ano = '" + meuCursor.getString(1) + "' and mes = '" + meuCursor.getString(0) + "' group by mes order by mes desc", null);
                        meuCursorSalario.moveToFirst();


                        String sal;
                        if (meuCursorSalario != null && meuCursorSalario.getCount() > 0 && meuCursorSalario.getString(0) != null) {
                            sal = meuCursorSalario.getString(0);
                        } else {
                            sal = "0";
                        }
                        String var;
                        String men;
                        meuVariavel.moveToFirst();
                        if (meuVariavel != null && meuVariavel.getCount() > 0 && meuVariavel.getString(0) != null) {
                            var = meuVariavel.getString(2);
                        } else {
                            var = "0";
                        }
                        meuCursorMensal.moveToFirst();
                        int plm = meuCursorMensal.getPosition();
                        if (meuCursorMensal != null && meuCursorMensal.getCount() > 0 && meuCursorMensal.getString(0) != null) {
                            men = meuCursorMensal.getString(1);
                        } else {
                            men = "0";
                        }

                        double salario = Double.parseDouble(sal);
                        double mens = Double.parseDouble(men);
                        double vars = Double.parseDouble(var);
                        Double depending = mens + vars + salario;
                        double valorJaPago = Double.parseDouble(meuCursor.getString(3));
                        double valorReceita = Double.parseDouble(meuCursor.getString(2));
                        ;
                        double valorPendente = valorReceita - valorJaPago;
                        double valorFaturado = valorReceita - depending;

                        menuItem = new RelatorioMensalListaCabecalho();
                        menuItem.setAno("" + dataReferenciaQuery.getYear());
                        menuItem.setDespeza(NumberFormat.getCurrencyInstance().format((depending)));
                        menuItem.setMes(traduzMes(dataReferenciaQuery.getMonth().toString()));

                        menuItem.setPendente(NumberFormat.getCurrencyInstance().format((valorPendente)));
                        menuItem.setFaturado(NumberFormat.getCurrencyInstance().format((valorFaturado)));

                        menuItem.setReceita(NumberFormat.getCurrencyInstance().format((valorReceita)));
                        listaDividendos.add(menuItem);
                    }
                    meuCursor.moveToNext();

                }

            }*/

           /* ArrayList<String> listaVerificadora = new ArrayList<>();




            for (int i = 1; i < quantMeses; i++){


                //meuCursor = bancoDados.rawQuery("SELECT * from tbpurchaseorder order by createdate DESC ", null);
                meuCursor.moveToFirst();
                double valorReceita = 0d;
                double valorPendente = 0d;
                int tamanho = meuCursor.getCount();
                int inicioPassagem = 0;
                while(meuCursor!=null && tamanho > inicioPassagem){
                    inicioPassagem ++;
                    String data = meuCursor.getString(8);
                    if (!data.isEmpty()){
                        if (!listaVerificadora.contains(meuCursor.getString(0))) {
                            LocalDate datames = LocalDate.parse(data, formatter);
                            String mesAnodataQuery = datames.getMonth()+"-"+datames.getYear();
                            String mesAnoDataPercursor = hoje.getMonth()+"-"+hoje.getYear();
                          //  if (datames.getMonthValue() == dateStart.getMonthValue()) {
                            if (mesAnoDataPercursor.equals(mesAnodataQuery)) {
                                double recei = Double.parseDouble(meuCursor.getString(4));
                                double pemd = Double.parseDouble(meuCursor.getString(3));
                                double p = pemd - recei;
                                listaVerificadora.add(meuCursor.getString(0));
                                valorPendente += p;
                                valorReceita += recei;

                            } else {
                                valorPendente += 0;
                                valorReceita += 0;
                            }
                        }
                    }else {
                        valorPendente += 0;
                        valorReceita += 0;
                             }
                    meuCursor.moveToNext();
                }


                menuItem = new RelatorioMensalListaCabecalho();
                menuItem.setAno(""+hoje.getYear());
                menuItem.setDespeza("9,850.00 ");
                menuItem.setMes(hoje.getMonth().toString());
                menuItem.setPendente(NumberFormat.getCurrencyInstance().format((valorPendente)));
                menuItem.setFaturado("3,500.00");
                menuItem.setReceita(NumberFormat.getCurrencyInstance().format((valorReceita)));
                listaDividendos.add(menuItem);
                hoje = hoje.minusMonths(1);

            }*/
            bancoDados.close();


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void configuraCabecalhoAnual() {
        try {

            ArrayList<RelatorioMensalListaCabecalho> listCabecalho = new ArrayList<>();

            ListViewAdapterRelatorioCabecalhoMensal meuAdapter = new
                    ListViewAdapterRelatorioCabecalhoMensal(getActivity(), listCabecalho);
            cabecalhoAnual.setAdapter(meuAdapter);
            RelatorioMensalListaCabecalho menuItem = new RelatorioMensalListaCabecalho();
            menuItem.setAno("  ");
            menuItem.setDespeza("DESPEZA");
            menuItem.setMes("ANO");
            menuItem.setPendente("PENDENTE");
            menuItem.setFaturado("FATURADO");
            menuItem.setReceita("RECEITA");
            listCabecalho.add(menuItem);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void configuraCabecalho() {
        try {

            ArrayList<RelatorioMensalListaCabecalho> listCabecalho = new ArrayList<>();

            ListViewAdapterRelatorioCabecalhoMensal meuAdapter = new
                    ListViewAdapterRelatorioCabecalhoMensal(getActivity(), listCabecalho);
            cabecalhoMensal.setAdapter(meuAdapter);
            RelatorioMensalListaCabecalho menuItem = new RelatorioMensalListaCabecalho();
            menuItem.setAno(" ANO");
            menuItem.setDespeza("DESPEZA");
            menuItem.setMes("MES");
            menuItem.setPendente("PENDENTE");
            menuItem.setFaturado("FATURADO");
            menuItem.setReceita("RECEITA");
            listCabecalho.add(menuItem);

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void listarDespesaMensal (){
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbmonthlyexpenses ", null);

            meuCursor.moveToFirst();
            while(meuCursor!=null){
                Integer soma = Integer.parseInt(meuCursor.getString(3));
                amount += soma;
                meuCursor.moveToNext();
            }
            bancoDados.close();



        } catch (Exception e){
            e.printStackTrace();
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void listarDespesaVariavel () {
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbanotherexpenses ", null);

            meuCursor.moveToFirst();
            while (meuCursor != null) {


                String data = meuCursor.getString(2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate datePag = LocalDate.parse(data, formatter);
                LocalDate hoje = LocalDate.now();
                if (datePag.getMonthValue() == hoje.getMonthValue()) {
                    Integer soma = Integer.parseInt(meuCursor.getString(3));
                    amount += soma;
                }
                meuCursor.moveToNext();
            }
           // despesa.setText("Total de Despezas: R$"+amount);
            bancoDados.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public OverviewEmpresa montarOverviewTela () {
        OverviewEmpresa response = new OverviewEmpresa();
        try {
            SQLiteDatabase bancoDados = db.getReadableDatabase();
            Cursor meuCursor = bancoDados.rawQuery("SELECT * from tbanotherexpenses where amount is not null ", null);
            meuCursor.moveToFirst();
            Float totalVariavel = 0F;
            int tamanho = meuCursor.getCount();
            int inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s","");
                double df = Double.parseDouble(valorDespesaBanco);
                Float parsed = (float) df;

                totalVariavel += parsed;
                meuCursor.moveToNext();
            }
            meuCursor = bancoDados.rawQuery("SELECT * from tbmonthlyexpenses  ", null);
            meuCursor.moveToFirst();
            Float totalMensal = 0F;
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
               // Float soma = Float.parseFloat(meuCursor.getString(3));
                String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s","");

                double df = Double.parseDouble(valorDespesaBanco);
                Float parsed = (float) df;
                // Monta esquema de datas
                String data = meuCursor.getString(4);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate datePag = LocalDate.parse(data, formatter);
                LocalDate hoje = LocalDate.now();
                //Faz o calculo de meses entre datas
                Integer anoInicio = datePag.getYear();
                LocalDate dataInicioContagem = LocalDate.of(anoInicio,1,1);
                int m1 = dataInicioContagem.getYear() * 12 + dataInicioContagem.getMonthValue();
                int m2 = hoje.getYear() * 12 + hoje.getMonthValue();
                int meses  = m2 - m1 + 1;
                //Soma meses x despesa mensal
                Float somaMes = meses * parsed;
                totalMensal += somaMes;
                meuCursor.moveToNext();
            }
            Float totalDespesas = totalMensal + totalVariavel;
            response.setDespesasGeral("R$ : "+totalDespesas);

            meuCursor = bancoDados.rawQuery("SELECT * from tbpurchaseorder  where paidvalue is not null ", null);
            meuCursor.moveToFirst();
            Float totalReceitas = 0F;
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                double df = Double.parseDouble(meuCursor.getString(4));
                Float soma = (float) df;


                totalReceitas += soma;
                meuCursor.moveToNext();
            }
            response.setReceitasGeral("R$ : "+ totalReceitas);

            meuCursor = bancoDados.rawQuery("SELECT * from tbworkerearning where earningamount is not null", null);
            meuCursor.moveToFirst();
            Float totalFuncionarios = 0F;
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                double df = Double.parseDouble(meuCursor.getString(3));
                Float soma = (float) df;
                totalFuncionarios += soma;
                meuCursor.moveToNext();
            }
            response.setFuncionariosGeral("R$ : "+totalFuncionarios);
            // despesa.setText("Total de Despezas: R$"+amount);

            Float totalFaturado = totalReceitas - totalDespesas - totalFuncionarios;
            response.setFaturadoGeral("R$ : "+totalFaturado);

            /*
             * Abaixo Calcula todos os paramentros para o MES corrente
             *
             * RECEITAS
             * DESPESAS
             * FUNCIONARIOS
             * FATURAMENTO
             *
             *  */

            meuCursor = bancoDados.rawQuery
                    ("SELECT * from tbanotherexpenses where amount is not null ",
                            null);
            meuCursor.moveToFirst();
            Float totalVariavelMesCorrente = 0F;
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;

                LocalDate hoje = LocalDate.now();
                //LocalDate dataPagamento = LocalDate.parse(meuCursor.getString(2));
                String data =meuCursor.getString(2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dataPagamento = LocalDate.parse(data, formatter);
                if (hoje.equals(dataPagamento)) {
                    String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s", "");
                    double df = Double.parseDouble(valorDespesaBanco);
                    Float parsed = (float) df;

                    totalVariavelMesCorrente += parsed;
                }
                meuCursor.moveToNext();
            }

            meuCursor = bancoDados.rawQuery
                    ("SELECT * from tbmonthlyexpenses where amount is not null ",
                            null);
            meuCursor.moveToFirst();
            Float totalMensalMesCorrente = 0F;
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                LocalDate hoje = LocalDate.now();
                String data =meuCursor.getString(4);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dataPagamento = LocalDate.parse(data, formatter);
                if (hoje.equals(dataPagamento)) {
                    String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s", "");
                    double df = Double.parseDouble(valorDespesaBanco);
                    Float parsed = (float) df;

                    totalMensalMesCorrente += parsed;
                }
                meuCursor.moveToNext();
            }
            Float totalDespesaCorrente = totalMensalMesCorrente + totalVariavelMesCorrente;
            response.setDespesaMes(NumberFormat.getCurrencyInstance().format((totalDespesaCorrente)));


            meuCursor = bancoDados.rawQuery
                    ("SELECT * from tbpurchaseorder  where paidvalue is not null ",
                            null);
            meuCursor.moveToFirst();
            Float totalReceitaMesCorrente = 0F;
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                LocalDate hoje = LocalDate.now();

                String data =meuCursor.getString(8);
               if (!data.isEmpty()) {
                   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                   LocalDate dataPagamento = LocalDate.parse(data, formatter);
                   if (hoje.equals(dataPagamento)) {
                       String valorDespesaBanco = meuCursor.getString(3).replaceAll("\\s", "");
                       double df = Double.parseDouble(valorDespesaBanco);
                       Float parsed = (float) df;

                       totalReceitaMesCorrente += parsed;
                   }
               }
                meuCursor.moveToNext();
            }

            response.setReceitaMes(NumberFormat.getCurrencyInstance().format(totalReceitaMesCorrente));


            meuCursor = bancoDados.rawQuery
                    ("SELECT * from tbworkerearning where earningamount is not null", null);
            meuCursor.moveToFirst();
            Float totalFuncionariosMesCorrente = 0F;
            tamanho = meuCursor.getCount();
            inicioPassagem = 0;
            while(meuCursor!=null && tamanho > inicioPassagem){
                inicioPassagem ++;
                LocalDate hoje = LocalDate.now();

                String data =meuCursor.getString(4);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dataPagamento = LocalDate.parse(data, formatter);
                if (hoje.equals(dataPagamento)) {
                    double df = Double.parseDouble(meuCursor.getString(3));
                    Float soma = (float) df;
                    totalFuncionariosMesCorrente += soma;
                }
                meuCursor.moveToNext();
            }
            response.setFuncionariosMes(NumberFormat.getCurrencyInstance().format(totalFuncionariosMesCorrente));
            // despesa.setText("Total de Despezas: R$"+amount);
            bancoDados.close();

            Float totalFaturadoMesCorrente = totalReceitaMesCorrente - totalDespesaCorrente - totalFuncionariosMesCorrente;
            response.setFaturadoMes(NumberFormat.getCurrencyInstance().format(totalFaturadoMesCorrente));



        } catch (Exception e) {
            e.printStackTrace();
        }



        return response;
    }


    public String traduzMes (String nome) {
        String response = nome;

        switch (response) {
            case "JANUARY":
            response = "Janeiro";
            break;

            case "FEBRUARY":
                response = "Fevereiro";
                break;

            case "MARCH":
                response = "Março";
                break;
            case "APRIL":
                response = "Abril";
                break;
            case "MAY":
                response = "Maio";
                break;
            case "JUNE":
                response = "Junho";
                break;
            case "JULY":
                response = "Julho";
                break;
            case "AUGUST":
                response = "Agosto";
                break;
            case "SEPTEMBER":
                response = "Setembro";
                break;
            case "OCTOBER":
                response = "Outubro";
                break;
            case "NOVEMBER":
                response = "Novembro";
                break;
            case "DECEMBER":
                response = "Dezembro";
                break;

        }


           return response;
    }
}