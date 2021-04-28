package com.example.myapplication;

public class EmpresaItem {
    private String nombreEmpresa;
    private int item;

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public int getItem() {
        return item;
    }

    public EmpresaItem(String nombreEmpresa, int item){
        this.nombreEmpresa = nombreEmpresa;
        this.item = item;
    }

}
