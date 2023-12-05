package com.example.myapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.ui.despezas.CriarDespesa;
import com.example.myapplication.ui.funcionarios.activity_cadastro_funcionario;
import com.example.myapplication.ui.gallery.CriarComanda;
import com.example.myapplication.ui.slideshow.CadastroCliente;
import com.example.myapplication.ui.tablePrices.CriarNovoItem;
import com.example.myapplication.ui.tablePrices.CriarNovoServico;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Button novaComanda;
    Button novoCliente;
    Button novoServico;
    Button novoFuncionario;
    Button novoProduto;
    Button novaDespesa;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        novaComanda = (Button) root.findViewById(R.id.buttonNewComand);
        novoCliente = (Button) root.findViewById(R.id.buttonNewCustomer);
        novoServico = (Button) root.findViewById(R.id.buttonNewService);
        novoFuncionario = (Button) root.findViewById(R.id.buttonNewWorker2);
        novoProduto = (Button) root.findViewById(R.id.buttonNewProduct);
        novaDespesa = (Button) root.findViewById(R.id.buttonNewExpense2);

        novaDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNovaDespesa(v);
            }
        });
        novoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNovoProduto(v);
            }
        });
        novoFuncionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNovoFuncionario(v);
            }
        });
        novoServico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNovoServico(v);
            }
        });
        novaComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastroComanda(v);
            }
        });
        novoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastroCliente(v);
            }
        });
       // final TextView textView = root.findViewById(R.id.textView2);
       // homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
       //     @Override
      //      public void onChanged(@Nullable String s) {
      //          textView.setText(s);
       //     }
      //  });
        return root;
    }

    private void abrirNovaDespesa(View v) {
        Intent intent = new Intent(v.getContext(), CriarDespesa.class);
        startActivity(intent);
    }

    private void abrirNovoProduto(View v) {
        Intent intent = new Intent(v.getContext(), CriarNovoItem.class);
        startActivity(intent);
    }

    private void abrirNovoFuncionario(View v) {
        Intent intent = new Intent(v.getContext(), activity_cadastro_funcionario.class);
        startActivity(intent);
    }

    private void abrirNovoServico(View v) {
        Intent intent = new Intent(v.getContext(), CriarNovoServico.class);
        startActivity(intent);
    }

    public void abrirTelaCadastroCliente(View v){
        Intent intent = new Intent(v.getContext(), CadastroCliente.class);
        startActivity(intent);
    }

    public void abrirTelaCadastroComanda(View v){
        Intent intent = new Intent(v.getContext(), CriarComanda.class);
        startActivity(intent);
    }
}