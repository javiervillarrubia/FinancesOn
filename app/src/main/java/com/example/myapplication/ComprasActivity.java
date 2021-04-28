package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ComprasActivity extends AppCompatActivity {

    List<String> listaParaBD, datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras);

        int posicionRecuperada = getIntent().getIntExtra("mensaje", 0);
        if(posicionRecuperada == 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Se ha generado el archivo en la carpeta \"Descargas\" en el almacenamiento interno de su dispositivo.", Toast.LENGTH_LONG);
            toast.show();
        }
        Button btnNuevaCompra = findViewById(R.id.btnNuevaCompra);
        btnNuevaCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), NuevaCompraActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnMisCompras= findViewById(R.id.btnMisCompras);
        btnMisCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), MisComprasActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnGenerarExcel = findViewById(R.id.btnGenerarExcel);
        btnGenerarExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), GenerarExcelComprasActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnImportarExcel = findViewById(R.id.btnImportarExcel);
        btnImportarExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (!data.getData().getPath().isEmpty() && resultCode == RESULT_OK) {
                StringBuilder direccion = new StringBuilder(data.getData().getPath());
                super.onActivityResult(requestCode, resultCode, data);
                System.out.println("ESTO ES LO QUE HEMOS RECOGIDO: " + direccion);
                String[] dir = direccion.toString().split("/");
                direccion = new StringBuilder();
                for (int i = 0; i < dir.length; i++) {
                    if (i != 0 && i != 1 && i != 2) {
                        direccion.append("/");
                        direccion.append(dir[i]);
                    }
                }
                System.out.println("ESTA ES LA DIRECCION SACADA: " + direccion);
                try {
                    leerDatosExcel(direccion);
                } catch (IOException | BiffException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void leerDatosExcel(StringBuilder direccion) throws IOException, BiffException {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        listaParaBD = new ArrayList<>();
        FileInputStream fi = new FileInputStream(String.valueOf(direccion));
        File f = new File(String.valueOf(direccion));
        if(!f.exists())
            popUpError(null, 0, 0, 4);
        else {
            Workbook wb = Workbook.getWorkbook(fi);
            Sheet[] tempS = wb.getSheets();
            if (tempS.length >= 2)
                popUpError(null, 0, 0, 5);
            else {
                Sheet sheet = wb.getSheet(0);
                int rows = sheet.getRows();
                int columnns = sheet.getColumns();
                System.out.println("ESTAS SON LAS ROWS: " + rows + "\nEstas son las columnas: " + columnns);
                Cell[] row1 = sheet.getRow(1);
                int flag = comprobarNombresColumnas(row1);
                if (flag == 0) {
                    for (int i = 3; i < rows; i++) {
                        Cell[] row3 = sheet.getRow(i);
                        for (int j = 0; j < columnns; j++) {
                            Cell c = row3[j];
                            if (c.getContents().isEmpty())
                                popUpError(c.getContents(), i + 1, j + 1, 3);
                            if (!c.getContents().equals("C")) {
                                if (!isNumeric(c.getContents()) && j != 3 && j != 1)
                                    popUpError(c.getContents(), i + 1, j + 1, 1);
                                else {
                                    if (j == 3) {
                                        String empresa = MisFunciones.calcularEmpresa(c.getContents());
                                        listaParaBD.add(empresa);
                                    }
                                    else if(j == 5 || j == 6){
                                        System.out.println("ESTA ES LA CADENA CON SUBSTRING: " + c.getContents().substring(1));
                                        listaParaBD.add(c.getContents().substring(1));
                                    }
                                    else
                                        listaParaBD.add(c.getContents());
                                    System.out.println("ESTE ES EL CONTENIDO DE LA ROW3: " + c.getContents());
                                }
                            }
                        }
                    }
                    @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM COMPRAS", null);
                    if (cursor.getCount() == 0) {
                        int x = 0;
                        for (int i = 0; i < rows - 3; i++) {
                            Object[] objetos = new Object[10];
                            objetos[0] = listaParaBD.get(x++);
                            objetos[1] = listaParaBD.get(x++);
                            objetos[2] = listaParaBD.get(x++);
                            objetos[3] = listaParaBD.get(x++);
                            objetos[4] = listaParaBD.get(x++);
                            objetos[5] = listaParaBD.get(x++);
                            objetos[6] = listaParaBD.get(x++);
                            objetos[7] = listaParaBD.get(x++);
                            objetos[8] = listaParaBD.get(x++);
                            objetos[9] = listaParaBD.get(x++);
                            String cadena = "INSERT INTO COMPRAS VALUES (" + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ")";
                            db.execSQL(cadena, objetos);
                        }
                        meterEnCartera(db);
                    } else {
                        int x = 0;
                        int id = cursor.getCount() + 1;
                        for (int i = 0; i < rows - 3; i++) {
                            Object[] objetos = new Object[10];
                            x++;
                            objetos[0] = id++;
                            objetos[1] = listaParaBD.get(x++);
                            objetos[2] = listaParaBD.get(x++);
                            objetos[3] = listaParaBD.get(x++);
                            objetos[4] = listaParaBD.get(x++);
                            objetos[5] = listaParaBD.get(x++);
                            objetos[6] = listaParaBD.get(x++);
                            objetos[7] = listaParaBD.get(x++);
                            objetos[8] = listaParaBD.get(x++);
                            objetos[9] = listaParaBD.get(x++);
                            String cadena = "INSERT INTO COMPRAS VALUES (" + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ", " + "?";
                            cadena = cadena + ")";
                            db.execSQL(cadena, objetos);
                        }
                        meterEnCartera(db);
                    }
                } else {
                    popUpError(null, 0, 0, 2);
                }
            }
        }
    }

    private void meterEnCartera(SQLiteDatabase db) {
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM COMPRAS", null);
        cursor.moveToFirst();
        datos = new ArrayList<>();
        MisFunciones.meterDatos(datos);
        db.execSQL("DELETE FROM CARTERA");
        db.execSQL("DELETE FROM TOTALESCARTERA");
        for(int i=0; i<cursor.getCount(); i++) {
            int posicion = cursor.getInt(cursor.getColumnIndex("empresa")) + 1;
            System.out.println("ESTA ES LA POSICION DE METER CARTERA: " + posicion);
            double numAcciones = cursor.getDouble(cursor.getColumnIndex("numeroAcciones"));
            double precio = cursor.getDouble(cursor.getColumnIndex("precio"));
            double comision = cursor.getDouble(cursor.getColumnIndex("comision"));
            @SuppressLint("Recycle") Cursor c2_1 = db.rawQuery("SELECT * FROM DIVIDENDOSESTIMADOS WHERE ID = " + (posicion-1), null);
            c2_1.moveToFirst();
            double dividendoNeto = c2_1.getDouble(c2_1.getColumnIndex("dividendoNeto"));
            @SuppressLint("Recycle") Cursor c2_2 = db.rawQuery("SELECT * FROM COTIZACIONES WHERE ID = " + (posicion-1), null);
            c2_2.moveToFirst();
            double cotizacion =  c2_2.getDouble(c2_2.getColumnIndex("cotizacion"));
            System.out.println("ESTA ES LA COTIZACIÓN RECOGIDA: " + cotizacion);
            registrarTotalesCartera(posicion, numAcciones, precio, comision, cotizacion, dividendoNeto);
            registarCartera(posicion, numAcciones, precio, comision, cotizacion, dividendoNeto);
            cursor.moveToNext();
        }
    }

    private void registarCartera(int posicion, double numAcciones, double precio, double comision, double cotizacion, double dividendoNeto) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        DecimalFormat formato = new DecimalFormat("#.000");
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        //Primera compra
        if(c.getCount() == 1){
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
                actualizarCartera(db, idEmpresa, posicion);
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
                actualizarCartera(db, idEmpresa, posicion);
                System.out.println("NUEVA-CARTERA_MISMA_EMPRESA:\nPESO 1 : " + formato2.format(pesoCarteraInvertidoConComisionesBD) + "\nPESO 2: " + formato2.format(pesoCarteraActualDisponibleConComisionesBD));
            }
        }
        db.close();
    }

    private void actualizarCartera(SQLiteDatabase db, int idEmpresa, int posicion) {
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
                String ticker = datos.get(posicion);
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

    public void registrarTotalesCartera(int posicion, double numAcciones, double precio, double comision, double cotizacion, double dividendoNeto) {
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

                    totales = NuevaCompraActivity.calcularTotales(db, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio, idEmpresa, 1);
                    double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisiones));
                    double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisiones));
                    double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio / totales.get(2) * 100;
                    values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidos));
                    db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                    NuevaCompraActivity.actualizarDatosTotales(db, idEmpresa, 3);
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

                    totales = NuevaCompraActivity.calcularTotales(db, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio, idEmpresa, 2);
                    double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato2.format(pesoCarteraInvertidoConComisiones));
                    double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato2.format(pesoCarteraActualDisponibleConComisiones));
                    double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio * 100 / totales.get(2);
                    values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato2.format(tantoPorCientoTotalDividendosRecibidos));
                    db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                    NuevaCompraActivity.actualizarDatosTotales(db, idEmpresa, 3);
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

                    totales = NuevaCompraActivity.calcularTotales(db, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio, idEmpresa, 3);
                    double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_INVERTIDO_CON_COMISIONES, formato.format(pesoCarteraInvertidoConComisiones));
                    double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
                    values.put(Sentencias.CAMPO_PESO_CARTERA_ACTUAL_DISPONIBLE_CON_COMISIONES, formato.format(pesoCarteraActualDisponibleConComisiones));
                    double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio / totales.get(2) * 100;
                    values.put(Sentencias.CAMPO_TP_TOTAL_DIVIDENDOS_RECIBIDOS, formato.format(tantoPorCientoTotalDividendosRecibidos));
                    db.execSQL("DELETE FROM TOTALESCARTERA WHERE idEmpresa = " + (posicion-1));
                    db.insert(Sentencias.TABLA_TOTALES_CARTERA, Sentencias.CAMPO_ID_EMPRESA, values);
                    NuevaCompraActivity.actualizarDatosTotales(db, idEmpresa, 3);
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

    private void popUpError(String contents, int fila, int col, int i) {
        System.out.println("ENTRAMOS EN POPUPERRROR");
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ComprasActivity.this);
        dialogo1.setTitle("Error en la lectura del archivo.");
        if(i == 1){
            System.out.println("POPUP 1");
            dialogo1.setMessage("El dato correspondiente a la fila " + fila + " y a la columna " + col + " es: \"" + contents + "\"\nEste dato es un número real. Por favor, modifique dicha celda.");
        }
        else if (i == 2){
            System.out.println("POPUP 2");
            dialogo1.setMessage("El formato del archivo excel es diferente al pedido. Si quiere puede descargarlo en \"Generar Excel\" para ver cómo es el formato pedido.");
        }
        else if (i == 3){
            System.out.println("POPUP 3");
            dialogo1.setMessage("El dato correspondiente a la fila " + fila + " y a la columna " + col + " está vacío. Por favor, compruebe que todas las celdas están completas.");
        }
        else if (i == 4){
            System.out.println("POPUP 4");
            dialogo1.setMessage("No es posible abrir el archivo seleccionado.");
        }
        else if (i == 5){
            System.out.println("POPUP 5");
            dialogo1.setMessage("El formato del archivo tiene que tener solo la hoja \"COMPRAS\"");
        }
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                System.out.println("ESTO ES ACEPTAR");
                Intent intent = new Intent (getApplicationContext(), ComprasActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        dialogo1.show();
    }

    private int comprobarNombresColumnas(Cell[] row) {
        List<String> lista = new ArrayList<>();
        lista.add("Id");
        lista.add("Fecha de Operaci�n");
        lista.add("Tipo de Operaci�n");
        lista.add("Valor");
        lista.add("N�mero de Acciones");
        lista.add("Precio Compra/Venta");
        lista.add("Total Operaci�n");
        lista.add("Balance Operaci�n a d�a de hoy Incluyendo la Comisi�n");
        lista.add("%");
        lista.add("Comisi�n");
        lista.add("Dividendo Anual Estimado (NETO)");
        if(row.length != 11)
            return -1;
        for(int i=0; i<row.length; i++){
            if(!lista.get(i).equals(row[i].getContents()))
                return -1;
        }
        return 0;
    }

    public static boolean isNumeric(String cadena) {
        boolean resultado;
        try {
            if(cadena.charAt(0) == '$')
                return true;
            Double.parseDouble(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }
        return resultado;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), MenuActivity.class);
        startActivityForResult(intent, 0);
    }
}