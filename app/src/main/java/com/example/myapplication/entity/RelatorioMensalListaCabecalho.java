package com.example.myapplication.entity;

public class RelatorioMensalListaCabecalho {

    private String mes;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPendente() {
        return pendente;
    }

    public void setPendente(String pendente) {
        this.pendente = pendente;
    }

    public String getReceita() {
        return receita;
    }

    public void setReceita(String receita) {
        this.receita = receita;
    }

    public String getDespeza() {
        return despeza;
    }

    public void setDespeza(String despeza) {
        this.despeza = despeza;
    }

    public String getFaturado() {
        return faturado;
    }

    public void setFaturado(String faturado) {
        this.faturado = faturado;
    }

    private String ano;
    private String pendente;
    private String receita;
    private String despeza;
    private String faturado;
}
