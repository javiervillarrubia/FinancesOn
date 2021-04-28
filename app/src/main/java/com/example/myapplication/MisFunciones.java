package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class MisFunciones {

    public static String calcularEmpresa(String contents) {
        if(contents.equals("Acciona"))
            return "0";
        if(contents.equals("Acerinox"))
            return "1";
        if(contents.equals("ACS"))
            return "2";
        if(contents.equals("Aena"))
            return "3";
        if(contents.equals("Almirall"))
            return "4";
        if(contents.equals("Amadeus IT Group"))
            return "5";
        if(contents.equals("Arcelormittal"))
            return "6";
        if(contents.equals("Banco Sabadell"))
            return "7";
        if(contents.equals("Bankinter"))
            return "8";
        if(contents.equals("BBVA"))
            return "9";
        if(contents.equals("Caixabank"))
            return "10";
        if(contents.equals("Cellnex Telecom"))
            return "11";
        if(contents.equals("CIE Automotive"))
            return "12";
        if(contents.equals("Colonial"))
            return "13";
        if(contents.equals("Enagas"))
            return "14";
        if(contents.equals("Endesa"))
            return "15";
        if(contents.equals("Ferrovial"))
            return "16";
        if(contents.equals("Fluidra"))
            return "17";
        if(contents.equals("Grifols"))
            return "18";
        if(contents.equals("IAG"))
            return "19";
        if(contents.equals("Iberdrola"))
            return "20";
        if(contents.equals("Inditex"))
            return "21";
        if(contents.equals("Indra"))
            return "22";
        if(contents.equals("Mapfre"))
            return "23";
        if(contents.equals("Meli� Hotels"))
            return "24";
        if(contents.equals("Merlin Properties"))
            return "25";
        if(contents.equals("Naturgy"))
            return "26";
        if(contents.equals("Pharma Mar"))
            return "27";
        if(contents.equals("Red El�ctrica"))
            return "28";
        if(contents.equals("Repsol"))
            return "29";
        if(contents.equals("Santander"))
            return "30";
        if(contents.equals("Siemens Gamesa"))
            return "31";
        if(contents.equals("Solaria"))
            return "32";
        if(contents.equals("Telef�nica"))
            return "33";
        if(contents.equals("Viscofan"))
            return "34";
        return "0";
    }

    public static String meterValorEmpresa(Integer numero) {
        switch (numero){
            case 0:
                return "Acciona";
            case 1:
                return "Acerinox";
            case 2:
                return "ACS";
            case 3:
                return "Aena";
            case 4:
                return "Almirall";
            case 5:
                return "Amadeus IT Group";
            case 6:
                return "Arcelormittal";
            case 7:
                return "Banco Sabadell";
            case 8:
                return "Bankinter";
            case 9:
                return "BBVA";
            case 10:
                return "Caixabank";
            case 11:
                return "Cellnex Telecom";
            case 12:
                return "CIE Automotive";
            case 13:
                return "Colonial";
            case 14:
                return "Enagas";
            case 15:
                return "Endesa";
            case 16:
                return "Ferrovial";
            case 17:
                return "Fluidra";
            case 18:
                return "Grifols";
            case 19:
                return "IAG";
            case 20:
                return "Iberdrola";
            case 21:
                return "Inditex";
            case 22:
                return "Indra";
            case 23:
                return "Mapfre";
            case 24:
                return "Meliá Hotels";
            case 25:
                return "Merlin Properties";
            case 26:
                return "Naturgy";
            case 27:
                return "Pharma Mar";
            case 28:
                return "Red Eléctrica";
            case 29:
                return "Repsol";
            case 30:
                return "Santander";
            case 31:
                return "Siemens Gamesa";
            case 32:
                return "Solaria";
            case 33:
                return "Telefónica";
            case 34:
                return "Viscofan";
        }
        return "";
    }

    public static void meterDatos(List<String> datos) {
        datos.add("BME:ANA");
        datos.add("BME:ACX");
        datos.add("BME:ACS");
        datos.add("BME:AENA");
        datos.add("BME:ALM");
        datos.add("BME:AMS");
        datos.add("BME:MTS");
        datos.add("BME:SAB");
        datos.add("BME:BKT");
        datos.add("BME:BBVA");
        datos.add("BME:CABK");
        datos.add("BME:CLNX");
        datos.add("BME:CIE");
        datos.add("BME:COL");
        datos.add("BME:ENG");
        datos.add("BME:ELE");
        datos.add("BME:FER");
        datos.add("BME:FDR");
        datos.add("BME:GRF");
        datos.add("BME:IAG");
        datos.add("BME:IBE");
        datos.add("BME:ITX");
        datos.add("BME:IDR");
        datos.add("BME:MAP");
        datos.add("BME:MEL");
        datos.add("BME:MRL");
        datos.add("BME:NTGY");
        datos.add("BME:PHM");
        datos.add("BME:REE");
        datos.add("BME:REP");
        datos.add("BME:SAN");
        datos.add("BME:SGRE");
        datos.add("BME:SLR");
        datos.add("BME:TEF");
        datos.add("BME:VIS");
    }

    public static ArrayList<EmpresaItem> initList() {
        ArrayList<EmpresaItem> listaEmpresas = new ArrayList<>();
        listaEmpresas.add(new EmpresaItem("Elija su empresa", R.drawable.arriba));
        listaEmpresas.add(new EmpresaItem("Acciona", R.drawable.acciona));
        listaEmpresas.add(new EmpresaItem("Acerinox", R.drawable.acerinox));
        listaEmpresas.add(new EmpresaItem("Acs", R.drawable.acs));
        listaEmpresas.add(new EmpresaItem("Aena", R.drawable.aena));
        listaEmpresas.add(new EmpresaItem("Almirall", R.drawable.almirall));
        listaEmpresas.add(new EmpresaItem("Amadeus", R.drawable.amadeus));
        listaEmpresas.add(new EmpresaItem("Arcelormitall", R.drawable.arcelormitall));
        listaEmpresas.add(new EmpresaItem("Banco Sabadell", R.drawable.banco_sabadel));
        listaEmpresas.add(new EmpresaItem("Bankinter", R.drawable.bankinter));
        listaEmpresas.add(new EmpresaItem("BBVA", R.drawable.bbva));
        listaEmpresas.add(new EmpresaItem("Caixabank", R.drawable.caixabank));
        listaEmpresas.add(new EmpresaItem("Cellnext Telecom", R.drawable.cellnex_telecom));
        listaEmpresas.add(new EmpresaItem("CIE Automotive", R.drawable.cie_automotive));
        listaEmpresas.add(new EmpresaItem("Colonial", R.drawable.colonial));
        listaEmpresas.add(new EmpresaItem("Enagas", R.drawable.enagas));
        listaEmpresas.add(new EmpresaItem("Endesa", R.drawable.endesa));
        listaEmpresas.add(new EmpresaItem("Ferrovial", R.drawable.ferrovial));
        listaEmpresas.add(new EmpresaItem("Fluidra", R.drawable.fluidra));
        listaEmpresas.add(new EmpresaItem("Grifols", R.drawable.grifols));
        listaEmpresas.add(new EmpresaItem("IAG", R.drawable.iag));
        listaEmpresas.add(new EmpresaItem("Iberdrola", R.drawable.iberdrola));
        listaEmpresas.add(new EmpresaItem("Inditex", R.drawable.inditex));
        listaEmpresas.add(new EmpresaItem("Indra", R.drawable.indra));
        listaEmpresas.add(new EmpresaItem("Mapfre", R.drawable.mapfre));
        listaEmpresas.add(new EmpresaItem("Melia Hotels", R.drawable.melia_hotels));
        listaEmpresas.add(new EmpresaItem("Merlin Properties", R.drawable.merlin_properties));
        listaEmpresas.add(new EmpresaItem("Naturgy", R.drawable.naturgy));
        listaEmpresas.add(new EmpresaItem("Pharma Mar", R.drawable.pharma_mar));
        listaEmpresas.add(new EmpresaItem("Red Eléctrica", R.drawable.red_electrica));
        listaEmpresas.add(new EmpresaItem("Repsol", R.drawable.repsol));
        listaEmpresas.add(new EmpresaItem("Santander", R.drawable.santander));
        listaEmpresas.add(new EmpresaItem("Siemens Gamesa", R.drawable.siemens_gamesa));
        listaEmpresas.add(new EmpresaItem("Solaria", R.drawable.solaria));
        listaEmpresas.add(new EmpresaItem("Telefónica", R.drawable.telefonica));
        listaEmpresas.add(new EmpresaItem("Viscofan", R.drawable.viscofan));
        return listaEmpresas;
    }
}
