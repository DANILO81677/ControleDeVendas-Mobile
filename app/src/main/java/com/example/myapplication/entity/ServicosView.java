package com.example.myapplication.entity;

public class ServicosView {

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    private Integer idServico;
    private String quantidade;

    public Integer getIdFuncioanario() {
        return idFuncioanario;
    }

    public void setIdFuncioanario(Integer idFuncioanario) {
        this.idFuncioanario = idFuncioanario;
    }

    private Integer idFuncioanario;

    public String getValorFuncionario() {
        return valorFuncionario;
    }

    public void setValorFuncionario(String valorFuncionario) {
        this.valorFuncionario = valorFuncionario;
    }

    private String valorFuncionario;
}
