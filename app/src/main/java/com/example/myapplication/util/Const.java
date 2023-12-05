package com.example.myapplication.util;

import android.database.sqlite.SQLiteDatabase;

public class Const {

    //public static String NAME_DATABASE = "JL_CABELO_DB";
    public static final String NAME_DATABASE = "APP_DB";

    public static final String[] LIST_DOCUMENTS_TYPE = {"CPF", "RG", "CNH", "PASSAPORTE"};

    public static final String[] LIST_PAYMENT_METHOD = {"DINHEIRO", "CARTÃO CRÉDITO", "CARTÃO DÉBITO", "PENDENTE"};

    public static final int DATABASE_VERSION = 1;

    public static final String MGS_BOX_CLIENTES = "O que deseja fazer ?";

    public static final String[] LIST_TESTE = {"produto_0",
            "produto_1",
            "produto_2",
            "produto_3",
            "produto_4",
            "produto_5",
            "produto_6",
            "produto_7",
            "produto_8",
            "produto_9",
            "produto_10",
            "produto_11",
            "produto_12",
            "produto_13",
            "produto_14",
            "produto_15"};

    public static final String[] LIST_SERVICO = {"servico_0",
            "servico_1                                        R$ 250",
            "servico_2                                        R$ 250",
            "servico_3                                        R$ 250",
            "servico_4                                        R$ 250",
            "servico_5                                        R$ 250",
            "servico_6                                        R$ 250",
            "servico_7                                        R$ 250",
            "servico_8                                        R$ 250",
            "servico_9                                        R$ 250",
            "servico_10                                        R$ 250",
            "servico_11                                        R$ 250",
            "servico_12                                        R$ 250",
            "servico_13                                        R$ 250",
            "servico_14                                        R$ 250",
            "servico_15                                        R$ 250"};

    public static final CharSequence[]  MGS_BOX_PRODUTO_ALERTA_CAMPOS = {"- NOME DO PRODUTO",
            "- VALOR DO PRODUTO",
            "- TIPO DO PRODUTO"};

    public static final CharSequence[]  MGS_BOX_SERVICO_ALERTA_CAMPOS = {"- NOME DO SERVICO",
            "- VALOR DO SERVICO"};

    public static final CharSequence[]  MGS_BOX_CLIENTE_ALERTA_CAMPOS = {"- NOME DO CLIENTE",
            "- DATA DE NASCIMENTO"};

    public static final CharSequence[]  MGS_BOX_FUNCIONARIO_ALERTA_CAMPOS = {"- NOME DO FUNCIONARIO",
            "- DIA DE PAGAMENTO", "- FUNÇÃO REALIZADA"};

    public static final CharSequence[]  MGS_BOX_COMISSAO_ALERTA_CAMPOS = {"- VALOR DA COMISSAO"};


}
