package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NuevaCompraActivity extends AppCompatActivity {

    ArrayList<EmpresaItem> listaEmpresas;
    EmpresaAdapter mAdapter;
    String nombreEmpresa, temporal = null;;
    Integer item = 0;
    Integer posicion = 0;
    EditText etFecha, etNumeroAcciones, etPrecio, etComision, etDividendoEstimado;
    double dividendoNeto, cotizacion, precio, numAcciones, comision;
    List<String> datos = new ArrayList<>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_compra);

        listaEmpresas = MisFunciones.initList();
        MisFunciones.meterDatos(datos);
        Spinner spinnerEmpresas = findViewById(R.id.valorSpinner);
        mAdapter = new EmpresaAdapter(this, listaEmpresas);
        spinnerEmpresas.setAdapter(mAdapter);
        etFecha = findViewById(R.id.etFecha);
        etNumeroAcciones = findViewById(R.id.etNumeroAcciones);
        etPrecio = findViewById(R.id.etPrecio);
        etComision = findViewById(R.id.etComision);
        etDividendoEstimado = findViewById(R.id.etDividendoEstimado);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), ComprasActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                //DATOS ERRONEOS, VUELVE A METERLOS EL USUARIO
                if(comprobarDatos() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Compruebe bien el formato de la fecha introducida.", Toast.LENGTH_LONG);
                    toast.show();
                    etFecha.setText("");
                } else if(comprobarDatos() == 1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Compruebe bien el formato del número de acciones introducido.", Toast.LENGTH_LONG);
                    toast.show();
                    etNumeroAcciones.setText("");
                }
                else if(comprobarDatos() == 2) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Compruebe bien el formato del precio introducido.", Toast.LENGTH_LONG);
                    toast.show();
                    etPrecio.setText("");
                }
                else if(comprobarDatos() == 3) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Compruebe bien el formato de la comisión introducida.", Toast.LENGTH_LONG);
                    toast.show();
                    etComision.setText("");
                }
                else if(comprobarDatos() == 4) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Hay algún dato que no ha introducido. Por favor, introduzca todos los datos que se piden.", Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(comprobarDatos() == 5) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No ha elegido la empresa correspondiente a su compra.", Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(comprobarDatos() == 6) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Compruebe bien el formato del Dividendo Estimado introducido.", Toast.LENGTH_LONG);
                    toast.show();
                    etDividendoEstimado.setText("");
                }
                else {
                    if(!etDividendoEstimado.getText().toString().isEmpty())
                        cambiarDividendoEstimado();
                    calcularValores();
                    registrarTotalesCartera();
                    registrarCartera();
                    registrarCompra();
                    Intent intent = new Intent(v.getContext(), ComprasActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        });

        spinnerEmpresas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicion = position;
                EmpresaItem clickedItem = (EmpresaItem) parent.getItemAtPosition(position);
                nombreEmpresa = clickedItem.getNombreEmpresa();
                item = clickedItem.getItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cambiarDividendoEstimado() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        DecimalFormat formato = new DecimalFormat("#.###");
        DecimalFormat formato2 = new DecimalFormat("#.##");
        Object[] objetos = new Object[3];
        for(int i=0; i<String.valueOf(etDividendoEstimado.getText()).length(); i++){
            if(String.valueOf(etDividendoEstimado.getText()).charAt(i) == ',')
                temporal = changeCharInPosition(i, '.', String.valueOf(etDividendoEstimado.getText()));
        }
        if(temporal == null)
            temporal = String.valueOf(etDividendoEstimado.getText());
        double dividendoBruto = Double.parseDouble(temporal);
        double dividendoNeto = dividendoBruto * 0.81;
        System.out.println("ESTE ES EL DIVIDENDO BRUTO");
        objetos[0] = formato.format(dividendoNeto);
        objetos[1] = formato2.format(dividendoBruto);
        objetos[2] = posicion - 1;
        String cadena = "UPDATE DIVIDENDOSESTIMADOS SET dividendoNeto = " + "?";
        cadena = cadena + ", dividendoBruto = " + "?";
        cadena = cadena + " WHERE id = " + "?";
        db.execSQL(cadena, objetos);
    }

    private void calcularValores() {
        for(int i=0; i<String.valueOf(etPrecio.getText()).length(); i++){
            if(String.valueOf(etPrecio.getText()).charAt(i) == ',')
                temporal = changeCharInPosition(i, '.', String.valueOf(etPrecio.getText()));
        }
        if(temporal == null)
            temporal = String.valueOf(etPrecio.getText());
        precio = Double.parseDouble(temporal);
        temporal = null;
        for(int i=0; i<String.valueOf(etNumeroAcciones.getText()).length(); i++){
            if(String.valueOf(etNumeroAcciones.getText()).charAt(i) == ',')
                temporal = changeCharInPosition(i, '.', String.valueOf(etNumeroAcciones.getText()));
        }
        if(temporal == null)
            temporal = String.valueOf(etNumeroAcciones.getText());
        numAcciones = Double.parseDouble(temporal);
        temporal = null;
        for(int i=0; i<String.valueOf(etComision.getText()).length(); i++){
            if(String.valueOf(etComision.getText()).charAt(i) == ',')
                temporal = changeCharInPosition(i, '.', String.valueOf(etComision.getText()));
        }
        if(temporal == null)
            temporal = String.valueOf(etComision.getText());
        comision = Double.parseDouble(temporal);
        temporal = null;

        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c2_1 = db.rawQuery("SELECT * FROM DIVIDENDOSESTIMADOS WHERE ID = " + (posicion-1), null);
        c2_1.moveToFirst();
        dividendoNeto = c2_1.getDouble(c2_1.getColumnIndex("dividendoNeto"));
        @SuppressLint("Recycle") Cursor c2_2 = db.rawQuery("SELECT * FROM COTIZACIONES WHERE ID = " + (posicion-1), null);
        c2_2.moveToFirst();
        cotizacion =  c2_2.getDouble(c2_2.getColumnIndex("cotizacion"));
        db.close();
    }

    //Devuelve 0 si no están bien.
    private int comprobarDatos() {
        String[] fecha = etFecha.getText().toString().split("/");
        int solucion;
        if(etComision.getText().toString().isEmpty() || etPrecio.getText().toString().isEmpty() || etNumeroAcciones.getText().toString().isEmpty() || etFecha.getText().toString().isEmpty())
            return 4;
        if(fecha.length == 3) {
            if(isNumeric(fecha[0]) && isNumeric(fecha[1]) && isNumeric(fecha[2])) {
                if (Integer.parseInt(fecha[1]) <= 12 && Integer.parseInt(fecha[1]) >= 1) {
                    if (Integer.parseInt(fecha[1]) == 1 || Integer.parseInt(fecha[1]) == 3 || Integer.parseInt(fecha[1]) == 5 || Integer.parseInt(fecha[1]) == 7 ||
                            Integer.parseInt(fecha[1]) == 8 || Integer.parseInt(fecha[1]) == 10 || Integer.parseInt(fecha[1]) == 12) {
                        if (Integer.parseInt(fecha[0]) <= 31 && Integer.parseInt(fecha[0]) >= 1) {
                            if (Integer.parseInt(fecha[2]) == 2021 || Integer.parseInt(fecha[2]) == 2020 || Integer.parseInt(fecha[2]) == 2019)
                                solucion = -1;
                            else return 0;
                        } else return 0;
                    } else if (Integer.parseInt(fecha[1]) == 4 || Integer.parseInt(fecha[1]) == 6 || Integer.parseInt(fecha[1]) == 9 || Integer.parseInt(fecha[1]) == 11) {
                        if (Integer.parseInt(fecha[0]) <= 30 && Integer.parseInt(fecha[0]) >= 1) {
                            if (Integer.parseInt(fecha[2]) == 2021 || Integer.parseInt(fecha[2]) == 2020 || Integer.parseInt(fecha[2]) == 2019)
                                solucion = -1;
                            else return 0;
                        } else return 0;
                    } else if (Integer.parseInt(fecha[1]) == 2) {
                        if (Integer.parseInt(fecha[0]) <= 28 && Integer.parseInt(fecha[0]) >= 1) {
                            if (Integer.parseInt(fecha[2]) == 2021 || Integer.parseInt(fecha[2]) == 2020 || Integer.parseInt(fecha[2]) == 2019)
                                solucion = -1;
                            else return 0;
                        } else return 0;
                    } else return 0;
                } else return 0;
            } else return 0;
        } else return 0;

        String[] nAcciones = etNumeroAcciones.getText().toString().split("\\.");
        String[] nAcciones2 = etNumeroAcciones.getText().toString().split(",");
        if(nAcciones.length == 2){
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 0\n");
            if(isNumeric(nAcciones[0]) && isNumeric(nAcciones[1])){
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 1\n");
                if(Integer.parseInt(nAcciones[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        }
        else if(nAcciones2.length == 2){
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 2\n");
            if(isNumeric(nAcciones2[0]) && isNumeric(nAcciones2[1])){
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 3\n");
                if(Integer.parseInt(nAcciones2[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        }
        else if(nAcciones2.length == 1){
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 4\n");
            if(isNumeric(nAcciones2[0])){
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 5\n");
                if(Integer.parseInt(nAcciones2[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        } else return 1;

        String[] precioCompra = etPrecio.getText().toString().split("\\.");
        String[] precioCompra2 = etPrecio.getText().toString().split(",");
        if(precioCompra.length == 2){
            if(isNumeric(precioCompra[0]) && isNumeric(precioCompra[1])){
                if(Integer.parseInt(precioCompra[0]) > 0)
                    solucion = -1;
                else return 2;
            }else return 2;
        }
        else if(precioCompra2.length == 2){
            if(isNumeric(precioCompra2[0]) && isNumeric(precioCompra2[1])){
                if(Integer.parseInt(precioCompra2[0]) > 0)
                    solucion = -1;
                else return 2;
            } else return 2;
        }
        else if(precioCompra2.length == 1){
            if(isNumeric(precioCompra2[0])){
                if(Integer.parseInt(precioCompra2[0]) > 0)
                    solucion = -1;
                else return 2;
            } else return 2;
        } else return 2;

        String[] comisionCompra = etComision.getText().toString().split("\\.");
        String[] comisionCompra2 = etComision.getText().toString().split(",");
        if(comisionCompra.length == 2){
            if(isNumeric(comisionCompra[0]) && isNumeric(comisionCompra[1])){
                if(Integer.parseInt(comisionCompra[0]) > 0)
                    solucion = -1;
                else return 3;
            } else return 3;
        }
        else if(comisionCompra2.length == 2){
            if(isNumeric(comisionCompra2[0]) && isNumeric(comisionCompra2[1])){
                if(Integer.parseInt(comisionCompra2[0]) > 0)
                    solucion = -1;
                else return 3;
            } else return 3;
        }
        else if(comisionCompra2.length == 1){
            if(isNumeric(comisionCompra2[0])){
                if(Integer.parseInt(comisionCompra2[0]) > 0)
                    solucion = -1;
                else return 3;
            } else return 3;
        } else return 3;

        if(posicion == 0)
            return 5;

        return solucion;
    }

    private void registrarCartera() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        DecimalFormat formato = new DecimalFormat("#.000");
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM COMPRAS", null);
        //Primera compra
        if(c.getCount() == 0){
            ContentValues values = new ContentValues();
            double tpDivSobrePrecioActual = 100 * dividendoNeto / cotizacion;
            values.put(Sentencias.CAMPO_TP_DIV_SOBRE_PRECIO_ACTUAL, formato2.format(tpDivSobrePrecioActual));
            double tpAcInv = 100;
            values.put(Sentencias.CAMPO_TP_AC_INV, formato2.format(tpAcInv));
            int idEmpresa = posicion - 1;
            values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
            String ticker = datos.get(posicion-1);
            values.put(Sentencias.CAMPO_TICKER, ticker);
            values.put(Sentencias.CAMPO_NUM_ACCIONES, formato.format(numAcciones));
            values.put(Sentencias.CAMPO_COTIZACION_ACTUAL, formato.format(cotizacion));
            double dineroActualDisponible = numAcciones * cotizacion;
            values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponible));
            double balanceSinComisiones = (numAcciones * cotizacion) - (numAcciones * precio);
            values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisiones));
            double comisiones = comision * (-1);
            values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisiones));
            double balanceConComisiones = balanceSinComisiones + comisiones;
            values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceConComisiones));
            double pmAdquisicionSinComisiones = (numAcciones * precio) / numAcciones;
            values.put(Sentencias.CAMPO_PM_ADQUISICION_SIN_COMISIONES, formato.format(pmAdquisicionSinComisiones));
            double pmAdquisicionConComisiones = ((numAcciones * precio) - comisiones) / numAcciones;
            values.put(Sentencias.CAMPO_PM_ADQUISICION_CON_COMISIONES, formato.format(pmAdquisicionConComisiones));
            double dineroTotalInvertido= numAcciones * precio;
            values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertido));
            double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
            values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisiones));
            double balanceTPConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
            values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTPConComisiones));
            double pesoCarteraInvertidoConComisiones = 100;
            values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisiones));
            double pesoCarteraActualDisponibleConComisiones = 100;
            values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisiones));
            double balancePesoCartera = 0;
            values.put(Sentencias.CAMPO_BALANCE_PESO_CARTERA, formato2.format(balancePesoCartera));
            String nombreGrafico = ticker + " - " + MisFunciones.meterValorEmpresa(idEmpresa) + " (" + tpAcInv + "%)";
            values.put(Sentencias.CAMPO_NOMBRE_GRAFICO, nombreGrafico);
            double dividendoEstimadoAnio = numAcciones * dividendoNeto;
            values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnio));
            double tantoPorCientoTotalDividendosRecibidos =  100;
            values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidos));
            db.insert(Sentencias.TABLA_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
        }
        //Más de una compra. Puede ser de diferente empresa.
        else{
            int idEmpresa = posicion - 1;
            @SuppressLint("Recycle") Cursor cur = db.rawQuery("SELECT * FROM TOTALESCARTERA WHERE idEmpresa = " + idEmpresa, null);
            cur.moveToFirst();
            double numeroAccionesBD = cur.getDouble(cur.getColumnIndex("numeroAcciones"));
            double dineroActualDisponibleBD = cur.getDouble(cur.getColumnIndex("dineroActualDisponible"));
            double balanceSinComisionesBD = cur.getDouble(cur.getColumnIndex("balanceSinComisiones"));
            double comisionesBD = cur.getDouble(cur.getColumnIndex("comisiones"));
            double balanceIncluyendoComisionesBD = cur.getDouble(cur.getColumnIndex("balanceIncluyendoComisiones"));
            double dineroTotalInvertidoBD = cur.getDouble(cur.getColumnIndex("dineroTotalInvertido"));
            double dineroTotalInvertidoConComisionesBD = cur.getDouble(cur.getColumnIndex("dineroTotalInvertidoConComisiones"));
            double balanceTantoPorCientoConComisionesBD = cur.getDouble(cur.getColumnIndex("balanceTantoPorCientoConComisiones"));
            double dividendoEstimadoAnioBD = cur.getDouble(cur.getColumnIndex("dividendoEstimadoAnio"));
            double pesoCarteraInvertidoConComisionesBD = cur.getDouble(cur.getColumnIndex("pesoCarteraInvertidoConComisiones"));
            double pesoCarteraActualDisponibleConComisionesBD = cur.getDouble(cur.getColumnIndex("pesoCarteraActualDisponibleConComisiones"));
            double tantoPorCientoTotalDividendosRecibidosBD = cur.getDouble(cur.getColumnIndex("tantoPorCientoTotalDividendosRecibidos"));
            @SuppressLint("Recycle") Cursor cc = db.rawQuery("SELECT * FROM COMPRAS WHERE empresa = " + idEmpresa, null);
            cc.moveToFirst();
            @SuppressLint("Recycle") Cursor ccc = db.rawQuery("SELECT * FROM TOTALESCARTERA WHERE idEmpresa = " + idEmpresa, null);
            ccc.moveToFirst();
            //compra diferente empresa
            if(cc.getCount() == 0){
                ContentValues values = new ContentValues();
                double tpDivSobrePrecioActual = 100 * dividendoNeto / cotizacion;
                values.put(Sentencias.CAMPO_TP_DIV_SOBRE_PRECIO_ACTUAL, formato2.format(tpDivSobrePrecioActual));
                values.put(Sentencias.CAMPO_TP_AC_INV, formato2.format(pesoCarteraInvertidoConComisionesBD));
                values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
                String ticker = datos.get(posicion-1);
                values.put(Sentencias.CAMPO_TICKER, ticker);
                values.put(Sentencias.CAMPO_NUM_ACCIONES, formato.format(numAcciones));
                values.put(Sentencias.CAMPO_COTIZACION_ACTUAL, formato.format(cotizacion));
                double dineroActualDisponible = numAcciones * cotizacion;
                values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponible));
                double balanceSinComisiones = (numAcciones * cotizacion) - (numAcciones * precio);
                values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisiones));
                double comisiones = comision * (-1);
                values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisiones));
                double balanceConComisiones = balanceSinComisiones + comisiones;
                values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceConComisiones));
                double pmAdquisicionSinComisiones = (numAcciones * precio) / numAcciones;
                values.put(Sentencias.CAMPO_PM_ADQUISICION_SIN_COMISIONES, formato.format(pmAdquisicionSinComisiones));
                double pmAdquisicionConComisiones = ((numAcciones * precio) - comisiones) / numAcciones;
                values.put(Sentencias.CAMPO_PM_ADQUISICION_CON_COMISIONES, formato.format(pmAdquisicionConComisiones));
                double dineroTotalInvertido= numAcciones * precio;
                values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertido));
                double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
                values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisiones));
                double balanceTPConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
                values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTPConComisiones));
                values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisionesBD));
                values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisionesBD));
                double balancePesoCartera = pesoCarteraActualDisponibleConComisionesBD - pesoCarteraInvertidoConComisionesBD;
                values.put(Sentencias.CAMPO_BALANCE_PESO_CARTERA, formato2.format(balancePesoCartera));
                String nombreGrafico = ticker + " - " + MisFunciones.meterValorEmpresa(idEmpresa) + " (" + formato2.format(pesoCarteraInvertidoConComisionesBD) + "%)";
                values.put(Sentencias.CAMPO_NOMBRE_GRAFICO, nombreGrafico);
                double dividendoEstimadoAnio = numAcciones * dividendoNeto;
                values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnio));
                values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidosBD));
                db.insert(Sentencias.TABLA_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                actualizarCartera(db, idEmpresa);
                System.out.println("NUEVA-CARTERA:\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisionesBD) + "\nPESO 2: " + formato2.format(pesoCarteraActualDisponibleConComisionesBD));
            }
            //Compra misma empresa.
            else{
                db.execSQL("DELETE FROM " + Sentencias.TABLA_CARTERA + " WHERE idEmpresa = " + idEmpresa);
                ContentValues values = new ContentValues();
                double tpDivSobrePrecioActual = 100 * dividendoNeto / cotizacion;
                values.put(Sentencias.CAMPO_TP_DIV_SOBRE_PRECIO_ACTUAL, formato2.format(tpDivSobrePrecioActual));
                values.put(Sentencias.CAMPO_TP_AC_INV, formato2.format(pesoCarteraInvertidoConComisionesBD));
                values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
                String ticker = datos.get(posicion-1);
                values.put(Sentencias.CAMPO_TICKER, ticker);
                values.put(Sentencias.CAMPO_NUM_ACCIONES, formato.format(numeroAccionesBD));
                values.put(Sentencias.CAMPO_COTIZACION_ACTUAL, formato.format(cotizacion));
                values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponibleBD));
                values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisionesBD));
                values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisionesBD));
                values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceIncluyendoComisionesBD));
                double pmAdquisicionSinComisiones = dineroTotalInvertidoBD / numeroAccionesBD;
                values.put(Sentencias.CAMPO_PM_ADQUISICION_SIN_COMISIONES, formato.format(pmAdquisicionSinComisiones));
                double pmAdquisicionConComisiones = dineroTotalInvertidoConComisionesBD / numeroAccionesBD;
                System.out.println("ESTOS SON LOS PM: pmAdquisicionSinComisiones: " + pmAdquisicionSinComisiones + "  ----  pmAdquisicionConComisiones: " + pmAdquisicionConComisiones);
                values.put(Sentencias.CAMPO_PM_ADQUISICION_CON_COMISIONES, formato.format(pmAdquisicionConComisiones));
                values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertidoBD));
                values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisionesBD));
                values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTantoPorCientoConComisionesBD));
                values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisionesBD));
                values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisionesBD));
                double balancePesoCartera = pesoCarteraActualDisponibleConComisionesBD - pesoCarteraInvertidoConComisionesBD;
                values.put(Sentencias.CAMPO_BALANCE_PESO_CARTERA, formato2.format(balancePesoCartera));
                String nombreGrafico = ticker + " - " + MisFunciones.meterValorEmpresa(idEmpresa) + " (" + formato2.format(pesoCarteraInvertidoConComisionesBD) + "%)";
                values.put(Sentencias.CAMPO_NOMBRE_GRAFICO, nombreGrafico);
                values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnioBD));
                values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidosBD));
                db.insert(Sentencias.TABLA_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                actualizarCartera(db, idEmpresa);
                System.out.println("NUEVA-CARTERA_MISMA_EMPRESA:\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisionesBD) + "\nPESO 2: " + formato2.format(pesoCarteraActualDisponibleConComisionesBD));
            }
        }
        db.close();
    }

    private void actualizarCartera(SQLiteDatabase db, int idEmpresa) {
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        c.moveToFirst();
        for(int j=0; j<c.getCount(); j++) {
            int empresaBucle = c.getInt(c.getColumnIndex("idEmpresa"));
            if (empresaBucle != idEmpresa) {
                double pesoCarteraInvertidoConComisiones = c.getDouble(c.getColumnIndex("pesoCarteraInvertidoConComisiones"));
                double pesoCarteraActualDisponibleConComisiones = c.getDouble(c.getColumnIndex("pesoCarteraActualDisponibleConComisiones"));
                double tantoPorCientoTotalDividendosRecibidos = c.getDouble(c.getColumnIndex("tantoPorCientoTotalDividendosRecibidos"));
                double balancePesoCartera = pesoCarteraActualDisponibleConComisiones - pesoCarteraInvertidoConComisiones;
                String ticker = datos.get(posicion-1);
                String nombreGrafico = ticker + " - " + MisFunciones.meterValorEmpresa(empresaBucle) + " (" + formato2.format(pesoCarteraInvertidoConComisiones) + "%)";
                System.out.println("NUEVA-CARTERA-ACTUALIZADA:\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisiones) + "\nPESO 2: " + formato2.format(pesoCarteraActualDisponibleConComisiones));
                Object[] objetos = new Object[7];
                objetos[0] = formato2.format(pesoCarteraInvertidoConComisiones);
                objetos[1] = formato2.format(balancePesoCartera);
                objetos[2] = formato2.format(pesoCarteraInvertidoConComisiones);
                objetos[3] = formato2.format(pesoCarteraActualDisponibleConComisiones);
                objetos[4] = formato2.format(tantoPorCientoTotalDividendosRecibidos);
                objetos[5] = nombreGrafico;
                objetos[6] = empresaBucle;
                String cadena = "UPDATE CARTERA SET tantoPorCientoAcInv = " + "?";
                cadena = cadena + ", balancePesoCartera = " + "?";
                cadena = cadena + ", pesoCarteraInvertidoConComisiones = " + "?";
                cadena = cadena + ", pesoCarteraActualDisponibleConComisiones = " + "?";
                cadena = cadena + ", tantoPorCientoTotalDividendosRecibidos = " + "?";
                cadena = cadena + ", nombreGrafico = " + "?";
                cadena = cadena + " WHERE idEmpresa = " + "?";
                db.execSQL(cadena, objetos);
            }
            c.moveToNext();
        }
        db.close();
    }

    private void registrarTotalesCartera() {
        List<String> datos = new ArrayList<>();
        List<Double> totales;
        MisFunciones.meterDatos(datos);
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        DecimalFormat formato = new DecimalFormat("#.000");
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        //Entramos porque ya hay entradas en cartera
        if(c.getCount() != 0) {
            //Hay una entrada
            if(c.getCount() == 1) {
                c.moveToFirst();
                int empresa = c.getInt(c.getColumnIndex("idEmpresa"));
                double dineroTotalInvertidoBD = c.getDouble(c.getColumnIndex("dineroTotalInvertido"));
                double comisionesBD = c.getDouble(c.getColumnIndex("comisiones"));
                double numeroAccionesTotalesBD = c.getDouble(c.getColumnIndex("numeroAcciones"));
                ContentValues values = new ContentValues();
                //Cuando no es la misma empresa, ya tenemos que hacer el primer cálculo, ya que tenemos ahora dos entradas en la cartera.
                if((posicion-1) == empresa){
                    db.execSQL("DELETE FROM " + Sentencias.TABLA_TOTALES_CARTERA);
                    int idEmpresa = posicion - 1;
                    values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
                    double numeroAccionesTotales = numAcciones + numeroAccionesTotalesBD;
                    values.put(Sentencias.CAMPO_NUM_ACCIONES, numeroAccionesTotales);
                    double dineroActualDisponible = numeroAccionesTotales * cotizacion;
                    values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponible));
                    double balanceSinComisiones = (dineroActualDisponible - ((numAcciones * precio) + dineroTotalInvertidoBD));
                    values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisiones));
                    double comisiones = comisionesBD - comision;
                    values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisiones));
                    double balanceConComisiones = balanceSinComisiones + comisiones;
                    values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceConComisiones));
                    double dineroTotalInvertido = (numAcciones * precio) + dineroTotalInvertidoBD;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertido));
                    double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisiones));
                    double balanceTPConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
                    values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTPConComisiones));
                    double dividendoEstimadoAnio = numeroAccionesTotales * dividendoNeto;
                    values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnio));
                    double pesoCarteraInvertidoConComisiones = 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisiones));
                    double pesoCarteraActualDisponibleConComisiones = 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisiones));
                    double tantoPorCientoTotalDividendosRecibidos = 100;
                    values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidos));
                    db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                }
                //Cuando la empresa es diferente, hay que hacer nueva entrada en totales
                else{
                    int idEmpresa = posicion - 1;
                    values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
                    values.put(Sentencias.CAMPO_NUM_ACCIONES, numAcciones);
                    double dineroActualDisponible = (numAcciones * cotizacion);
                    values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponible));
                    double balanceSinComisiones = (dineroActualDisponible - (numAcciones * precio));
                    values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisiones));
                    double comisiones = comision * (-1);
                    values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisiones));
                    double balanceConComisiones = balanceSinComisiones + comisiones;
                    values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceConComisiones));
                    double dineroTotalInvertido = numAcciones * precio;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertido));
                    double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisiones));
                    double balanceTPConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
                    values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTPConComisiones));
                    double dividendoEstimadoAnio = numAcciones * dividendoNeto;
                    values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnio));

                    totales = calcularTotales(db, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio, idEmpresa, 1);
                    double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisiones));
                    double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisiones));
                    double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio / totales.get(2) * 100;
                    values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidos));
                    db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                    actualizarDatosTotales(db, idEmpresa, 3);
                    System.out.println("NUEVA:\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisiones) + "\nPESO 2: " + formato2.format(pesoCarteraActualDisponibleConComisiones));                }
            }
            else {
                int empresa = posicion - 1;
                @SuppressLint("Recycle") Cursor cc = db.rawQuery("SELECT * FROM TOTALESCARTERA WHERE idEmpresa = " + empresa, null);
                cc.moveToFirst();
                ContentValues values = new ContentValues();
                //Entra una entrada nueva
                if(cc.getCount() == 0){
                    int idEmpresa = posicion - 1;
                    values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
                    values.put(Sentencias.CAMPO_NUM_ACCIONES, numAcciones);
                    double dineroActualDisponible = (numAcciones * cotizacion);
                    values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponible));
                    double balanceSinComisiones = (dineroActualDisponible - (numAcciones * precio));
                    values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisiones));
                    double comisiones = comision * (-1);
                    values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisiones));
                    double balanceConComisiones = balanceSinComisiones + comisiones;
                    values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceConComisiones));
                    double dineroTotalInvertido = numAcciones * precio;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertido));
                    double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisiones));
                    double balanceTPConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
                    values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTPConComisiones));
                    double dividendoEstimadoAnio = numAcciones * dividendoNeto;
                    values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnio));

                    totales = calcularTotales(db, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio, idEmpresa, 2);
                    double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisiones));
                    double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisiones));
                    double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio * 100 / totales.get(2);
                    values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidos));
                    db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                    actualizarDatosTotales(db, idEmpresa, 3);
                    System.out.println("NUEVA:\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisiones) + "\nPESO 2: " + formato2.format(pesoCarteraActualDisponibleConComisiones));                }
                //Se repite entrada
                else{
                    double cotizacionBD = cc.getDouble(cc.getColumnIndex("dineroActualDisponible"));
                    double dineroTotalInvertidoBD = cc.getDouble(cc.getColumnIndex("dineroTotalInvertido"));
                    double comisionesBD = cc.getDouble(cc.getColumnIndex("comisiones"));
                    double dividendoEstimadoAnioBD = cc.getDouble(cc.getColumnIndex("dividendoEstimadoAnio"));
                    double numeroAccionesTotalesBD = cc.getDouble(cc.getColumnIndex("numeroAcciones"));
                    int idEmpresa = posicion - 1;
                    values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
                    double numeroAccionesTotales = numAcciones + numeroAccionesTotalesBD;
                    values.put(Sentencias.CAMPO_NUM_ACCIONES, numeroAccionesTotales);
                    double dineroActualDisponible = (numAcciones * cotizacion) + cotizacionBD;
                    values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponible));
                    double balanceSinComisiones = (dineroActualDisponible - ((numAcciones * precio) + dineroTotalInvertidoBD));
                    values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisiones));
                    double comisiones = comisionesBD - comision;
                    values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisiones));
                    double balanceConComisiones = balanceSinComisiones + comisiones;
                    values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceConComisiones));
                    double dineroTotalInvertido = (numAcciones * precio) + dineroTotalInvertidoBD;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertido));
                    double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
                    values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisiones));
                    double balanceTPConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
                    values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTPConComisiones));
                    double dividendoEstimadoAnio = (numAcciones * dividendoNeto) + dividendoEstimadoAnioBD;
                    values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnio));

                    totales = calcularTotales(db, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio, idEmpresa, 3);
                    double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato.format(pesoCarteraInvertidoConComisiones));
                    double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato.format(pesoCarteraActualDisponibleConComisiones));
                    double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio / totales.get(2) * 100;
                    values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato.format(tantoPorCientoTotalDividendosRecibidos));
                    db.execSQL("DELETE FROM TOTALESCARTERA WHERE idEmpresa = " + (posicion-1));
                    db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                    actualizarDatosTotales(db, idEmpresa, 3);
                    System.out.println("NUEVA:\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisiones) + "\nPESO 2: " + formato2.format(pesoCarteraActualDisponibleConComisiones));
                }
            }
        }
        else if(c.getCount() == 0){
            ContentValues values = new ContentValues();
            int idEmpresa = posicion - 1;
            values.put(Sentencias.CAMPO_ID_EMPRESA, idEmpresa);
            values.put(Sentencias.CAMPO_NUM_ACCIONES, numAcciones);
            double dineroActualDisponible = (numAcciones * cotizacion);
            values.put(Sentencias.CAMPO_DINERO_ACTUAL_DISPONIBLE, formato.format(dineroActualDisponible));
            double balanceSinComisiones = (dineroActualDisponible - (numAcciones * precio));
            values.put(Sentencias.CAMPO_BALANCE_SIN_COMISIONES, formato.format(balanceSinComisiones));
            double comisiones = comision * (-1);
            values.put(Sentencias.CAMPO_COMISIONES, formato.format(comisiones));
            double balanceConComisiones = balanceSinComisiones + comisiones;
            values.put(Sentencias.CAMPO_BALANCE_CON_COMISIONES, formato.format(balanceConComisiones));
            double dineroTotalInvertido = numAcciones * precio;
            values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO, formato.format(dineroTotalInvertido));
            double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
            values.put(Sentencias.CAMPO_DINERO_TOTAL_INVERTIDO_CON_COMISIONES, formato.format(dineroTotalInvertidoConComisiones));
            double balanceTPConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
            values.put(Sentencias.CAMPO_BALANCE_TP_CON_COMISIONES, formato2.format(balanceTPConComisiones));
            double dividendoEstimadoAnio = numAcciones * dividendoNeto;
            values.put(Sentencias.CAMPO_DIVIDENDO_ESTIMADO_ANIO, formato.format(dividendoEstimadoAnio));
            double pesoCarteraInvertidoConComisiones = 100;
            values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisiones));
            double pesoCarteraActualDisponibleConComisiones = 100;
            values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisiones));
            double tantoPorCientoTotalDividendosRecibidos = 100;
            values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidos));
            db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
        }
        db.close();
    }

    static void actualizarDatosTotales(SQLiteDatabase db, int idEmpresa, int i) {
        List<Double> totales = new ArrayList<>();
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        c.moveToFirst();
        int empresaBucle;
        for(int j=0; j<c.getCount(); j++) {
            empresaBucle = c.getInt(c.getColumnIndex("idEmpresa"));
            if (empresaBucle != idEmpresa) {
                double dineroTotalInvertidoConComisiones = c.getDouble(c.getColumnIndex("dineroTotalInvertidoConComisiones"));
                double dineroActualDisponible = c.getDouble(c.getColumnIndex("dineroActualDisponible"));
                double dividendoEstimadoAnio = c.getDouble(c.getColumnIndex("dividendoEstimadoAnio"));
                totales = calcularTotales(db, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio, empresaBucle, i);
                double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
                double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
                double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio * 100 / totales.get(2);
                System.out.println("ACTUALIZADO " + j + ":\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisiones) + "\nPESO 2: " +
                        formato2.format(pesoCarteraActualDisponibleConComisiones));
                Object[] objetos = new Object[4];
                objetos[0] = formato2.format(pesoCarteraInvertidoConComisiones);
                objetos[1] = formato2.format(pesoCarteraActualDisponibleConComisiones);
                objetos[2] = formato2.format(tantoPorCientoTotalDividendosRecibidos);
                objetos[3] = empresaBucle;
                String cadena = "UPDATE TOTALESCARTERA SET  pesoCarteraInvertidoConComisiones= " + "?";
                cadena = cadena + ", pesoCarteraActualDisponibleConComisiones = " + "?";
                cadena = cadena + " , " + "tantoPorCientoTotalDividendosRecibidos = ";
                cadena = cadena + "?" + " WHERE idEmpresa = ";
                cadena = cadena + "?";
                db.execSQL(cadena, objetos);
            }
            if(j != (c.getColumnCount() - 1))
                c.moveToNext();
        }
        db.close();
    }

    static List<Double> calcularTotales(SQLiteDatabase db, double dineroTotalInvertidoConComisiones, double dineroActualDisponible, double dividendoEstimadoAnio, int empresa, int i) {
        List<Double> lista = new ArrayList<>();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        c.moveToFirst();
        //Cuando hay una empresa en la cartera. Hay que añadir la nueva que se mete por parámetro;
        if (i == 1) {
            double dineroTotalInvertidoConComisiones2 = c.getDouble(c.getColumnIndex("dineroTotalInvertidoConComisiones"));
            double dineroActualDisponible2 = c.getDouble(c.getColumnIndex("dineroActualDisponible"));
            double dividendoEstimadoAnio2 = c.getDouble(c.getColumnIndex("dividendoEstimadoAnio"));
            lista.add(dineroTotalInvertidoConComisiones + dineroTotalInvertidoConComisiones2);
            lista.add(dineroActualDisponible + dineroActualDisponible2);
            lista.add(dividendoEstimadoAnio + dividendoEstimadoAnio2);
            return lista;
        }
        //Cuando hay más de una empresa en la cartera. Hay que añadir la nueva que se mete por parámetro;
        else if(i == 2){
            double dineroTotalInvertidoConComisionesTotal = 0;
            double dineroActualDisponibleTotal = 0;
            double dividendoEstimadoAnioTotal = 0;
            for(int j=0; j<c.getCount(); j++){
                double dineroTotalInvertidoConComisiones2 = c.getDouble(c.getColumnIndex("dineroTotalInvertidoConComisiones"));
                double dineroActualDisponible2 = c.getDouble(c.getColumnIndex("dineroActualDisponible"));
                double dividendoEstimadoAnio2 = c.getDouble(c.getColumnIndex("dividendoEstimadoAnio"));
                dineroTotalInvertidoConComisionesTotal = dineroTotalInvertidoConComisionesTotal + dineroTotalInvertidoConComisiones2;
                dineroActualDisponibleTotal = dineroActualDisponibleTotal + dineroActualDisponible2;
                dividendoEstimadoAnioTotal = dividendoEstimadoAnioTotal + dividendoEstimadoAnio2;
                c.moveToNext();
            }
            lista.add(dineroTotalInvertidoConComisiones + dineroTotalInvertidoConComisionesTotal);
            lista.add(dineroActualDisponible + dineroActualDisponibleTotal);
            lista.add(dividendoEstimadoAnio + dividendoEstimadoAnioTotal);
            return lista;
        }
        //Cuando hay más de una empresa en la cartera. Hay que actualizar la que ya tenemos, porque no hay que meter una entrada nueva;
        else if(i == 3){
            double dineroTotalInvertidoConComisionesTotal = 0;
            double dineroActualDisponibleTotal = 0;
            double dividendoEstimadoAnioTotal = 0;
            for(int j=0; j<c.getCount(); j++){
                if(c.getInt(c.getColumnIndex("idEmpresa")) == empresa){
                    dineroTotalInvertidoConComisionesTotal = dineroTotalInvertidoConComisionesTotal + dineroTotalInvertidoConComisiones;
                    dineroActualDisponibleTotal = dineroActualDisponibleTotal + dineroActualDisponible;
                    dividendoEstimadoAnioTotal = dividendoEstimadoAnioTotal + dividendoEstimadoAnio;
                }
                else {
                    double dineroTotalInvertidoConComisiones2 = c.getDouble(c.getColumnIndex("dineroTotalInvertidoConComisiones"));
                    double dineroActualDisponible2 = c.getDouble(c.getColumnIndex("dineroActualDisponible"));
                    double dividendoEstimadoAnio2 = c.getDouble(c.getColumnIndex("dividendoEstimadoAnio"));
                    dineroTotalInvertidoConComisionesTotal = dineroTotalInvertidoConComisionesTotal + dineroTotalInvertidoConComisiones2;
                    dineroActualDisponibleTotal = dineroActualDisponibleTotal + dineroActualDisponible2;
                    dividendoEstimadoAnioTotal = dividendoEstimadoAnioTotal + dividendoEstimadoAnio2;
                }
                c.moveToNext();
            }
            lista.add(dineroTotalInvertidoConComisionesTotal);
            lista.add(dineroActualDisponibleTotal);
            lista.add(dividendoEstimadoAnioTotal);
            return lista;
        }
        db.close();
        return lista;
    }


    private void registrarCompra() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        String temporal = null;

        DecimalFormat formato = new DecimalFormat("#.000");
        DecimalFormat formato2 = new DecimalFormat("#.00");
        ContentValues values = new ContentValues();

        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM COMPRAS", null);
        int id;
        if(c.getCount()>0){
            c.moveToLast();
            id = c.getInt(c.getColumnIndex("id")) + 1;
        }
        else
            id = 1;
        System.out.println("ESTE ES EL ID: " + id);
        values.put(Sentencias.CAMPO_ID, id);
        String fecha = etFecha.getText().toString();
        values.put(Sentencias.CAMPO_FECHA, fecha);
        int empresa = posicion - 1;
        values.put(Sentencias.CAMPO_EMPRESA, empresa);
        values.put(Sentencias.CAMPO_NUM_ACCIONES, formato.format(numAcciones));
        values.put(Sentencias.CAMPO_PRECIO, formato.format(precio));
        double total = numAcciones * precio;
        values.put(Sentencias.CAMPO_TOTAL, formato.format(total));
        double balance =  ((numAcciones * cotizacion) - total) - comision;
        values.put(Sentencias.CAMPO_BALANCE, formato.format(balance));
        double tantoPorCiento = (balance / total) * 100;
        values.put(Sentencias.CAMPO_TANTO_PORCIENTO, formato2.format(tantoPorCiento));
        values.put(Sentencias.CAMPO_COMISION, formato.format(comision));
        double dividendoAnual = numAcciones * dividendoNeto;
        values.put(Sentencias.CAMPO_DIVIDENDO_ANUAL, formato.format(dividendoAnual));
        db.insert(Sentencias.TABLA_COMPRAS, Sentencias.CAMPO_ID, values);
        db.close();
    }

    public String changeCharInPosition(int position, char ch, String str){
        char[] charArray = str.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }

    public static boolean isNumeric(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), ComprasActivity.class);
        startActivityForResult(intent, 0);
    }

}