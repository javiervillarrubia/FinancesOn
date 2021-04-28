package com.example.myapplication;

public class Sentencias {

    public static final String TABLA_COMPRAS = "compras";
    public static final String TABLA_EMPRESAS = "empresas";
    public static final String TABLA_DIVIDENDOS_ESTIMADOS = "dividendosEstimados";
    public static final String TABLA_DIVIDENDOS_RECIBIDOS = "dividendosRecibidos";
    public static final String TABLA_COTIZACIONES = "cotizaciones";
    public static final String TABLA_CARTERA = "cartera";
    public static final String TABLA_TOTALES_CARTERA = "totalesCartera";

    public static final String CAMPO_ID = "id";
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_EMPRESA = "empresa";
    public static final String CAMPO_NOMBRE_EMPRESA = "nombreEmpresa";
    public static final String CAMPO_NUM_ACCIONES = "numeroAcciones";
    public static final String CAMPO_PRECIO = "precio";
    public static final String CAMPO_TOTAL = "total";
    public static final String CAMPO_BALANCE = "balance";
    public static final String CAMPO_TANTO_PORCIENTO = "tantoPorciento";
    public static final String CAMPO_COMISION = "comision";
    public static final String CAMPO_DIVIDENDO_ANUAL = "dividendoAnual";
    public static final String CAMPO_DIVIDENDO_NETO = "dividendoNeto";
    public static final String CAMPO_DIVIDENDO_BRUTO = "dividendoBruto";
    public static final String CAMPO_COTIZACION = "cotizacion";
    public static final String CAMPO_TP_DIV_SOBRE_PRECIO_ACTUAL = "DIVSobrePecioActual";
    public static final String CAMPO_TP_AC_INV = "tantoPorCientoAcInv";
    public static final String CAMPO_ID_EMPRESA = "idEmpresa";
    public static final String CAMPO_TICKER = "ticker";
    public static final String CAMPO_COTIZACION_ACTUAL = "cotizacionActual";
    public static final String CAMPO_DINERO_ACTUAL_DISPONIBLE = "dineroActualDisponible";
    public static final String CAMPO_BALANCE_SIN_COMISIONES = "balanceSinComisiones";
    public static final String CAMPO_COMISIONES = "comisiones";
    public static final String CAMPO_BALANCE_CON_COMISIONES = "balanceIncluyendoComisiones";
    public static final String CAMPO_PM_ADQUISICION_SIN_COMISIONES = "precioMedioAdquisicionSinComisiones";
    public static final String CAMPO_PM_ADQUISICION_CON_COMISIONES = "precioMedioAdquisicionConComisiones";
    public static final String CAMPO_DINERO_TOTAL_INVERTIDO = "dineroTotalInvertido";
    public static final String CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES = "dineroTotalInvertidoConComisiones";
    public static final String CAMPO_BALANCE_TP_CON_COMISIONES = "balanceTantoPorCientoConComisiones";
    public static final String CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES = "pesoCarteraInvertidoConComisiones";
    public static final String CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES = "pesoCarteraActualDisponibleConComisiones";
    public static final String CAMPO_BALANCE_PESO_CARTERA = "balancePesoCartera";
    public static final String CAMPO_NOMBRE_GRAFICO = "nombreGrafico";
    public static final String CAMPO_DIVIDENDO_ESTIMADO_ANIO = "dividendoEstimadoAnio";
    public static final String CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS = "tantoPorCientoTotalDividendosRecibidos";
    public static final String CAMPO_RETENCION_EN_ESPANIA = "retencionEnEspania";


    public static final String CREAR_TABLA_EMPRESAS = "CREATE TABLE IF NOT EXISTS " + TABLA_EMPRESAS + " (" + CAMPO_ID + " INTEGER, " +
            CAMPO_NOMBRE_EMPRESA + " TEXT)";

    public static final String CREAR_TABLA_COMPRA = "CREATE TABLE IF NOT EXISTS " + TABLA_COMPRAS + " (" + CAMPO_ID + " INTEGER, " +
            CAMPO_FECHA + " TEXT, " + CAMPO_EMPRESA + " INTEGER, " + CAMPO_NUM_ACCIONES + " REAL, " + CAMPO_PRECIO +
            " REAL, " + CAMPO_TOTAL + " REAL, " + CAMPO_BALANCE + " REAL, " + CAMPO_TANTO_PORCIENTO + " REAL, " +
            CAMPO_COMISION + " REAL, " + CAMPO_DIVIDENDO_ANUAL + " REAL)";

    public static final String CREAR_TABLA_DIVIDENDOS_ESTIMADOS = "CREATE TABLE IF NOT EXISTS " + TABLA_DIVIDENDOS_ESTIMADOS + " (" + CAMPO_ID + " INTEGER, " +
            CAMPO_DIVIDENDO_NETO + " REAL, " + CAMPO_DIVIDENDO_BRUTO + " REAL)";
    public static final String CREAR_TABLA_DIVIDENDOS_RECIBIDOS = "CREATE TABLE IF NOT EXISTS " + TABLA_DIVIDENDOS_RECIBIDOS + " (" + CAMPO_ID + " INTEGER, " +
            CAMPO_FECHA + " TEXT, " + CAMPO_EMPRESA + " INTEGER, " + CAMPO_DIVIDENDO_NETO + " REAL, " + CAMPO_DIVIDENDO_BRUTO + " REAL, " + CAMPO_RETENCION_EN_ESPANIA + " REAL)";

    public static final String CREAR_TABLA_COTIZACIONES = "CREATE TABLE IF NOT EXISTS " + TABLA_COTIZACIONES + " (" + CAMPO_ID + " INTEGER, " +
            CAMPO_COTIZACION + " FLOAT)";

    public static final String CREAR_TABLA_CARTERA = "CREATE TABLE IF NOT EXISTS " + TABLA_CARTERA + " (" + CAMPO_TP_DIV_SOBRE_PRECIO_ACTUAL + " REAL, " +
            CAMPO_TP_AC_INV + " REAL, " + CAMPO_ID_EMPRESA + " INTEGER, " + CAMPO_TICKER + " TEXT, " + CAMPO_NUM_ACCIONES + " REAL, "
            + CAMPO_COTIZACION_ACTUAL + " REAL, " + CAMPO_DINERO_ACTUAL_DISPONIBLE + " REAL, " + CAMPO_BALANCE_SIN_COMISIONES + " REAL, "
            + CAMPO_COMISIONES + " REAL, " + CAMPO_BALANCE_CON_COMISIONES + " REAL, " + CAMPO_PM_ADQUISICION_SIN_COMISIONES
            + " REAL, " + CAMPO_PM_ADQUISICION_CON_COMISIONES + " REAL, " + CAMPO_DINERO_TOTAL_INVERTIDO + " REAL, " +
            CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES + " REAL, " + CAMPO_BALANCE_TP_CON_COMISIONES + " REAL, " +
            CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES + " REAL, " + CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES + " REAL, " +
            CAMPO_BALANCE_PESO_CARTERA + " REAL, " + CAMPO_NOMBRE_GRAFICO + " TEXT, " + CAMPO_DIVIDENDO_ESTIMADO_ANIO + " REAL, " +
            CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS + " REAL)";

    public static final String CREAR_TABLA_TOTALES_CARTERA = "CREATE TABLE IF NOT EXISTS " + TABLA_TOTALES_CARTERA + " (" + CAMPO_ID_EMPRESA +
            " INTEGER, " +  CAMPO_NUM_ACCIONES + " REAL, " +
            CAMPO_DINERO_ACTUAL_DISPONIBLE + " REAL, " + CAMPO_BALANCE_SIN_COMISIONES + " REAL, " + CAMPO_COMISIONES + " REAL, " +
            CAMPO_BALANCE_CON_COMISIONES + " REAL, " + CAMPO_DINERO_TOTAL_INVERTIDO + " REAL, " + CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES +
            " REAL, " + CAMPO_BALANCE_TP_CON_COMISIONES + " REAL, " + CAMPO_DIVIDENDO_ESTIMADO_ANIO + " REAL, " + CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES + " REAL, " +
            CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES + " REAL, " + CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS + " REAL)";
}
