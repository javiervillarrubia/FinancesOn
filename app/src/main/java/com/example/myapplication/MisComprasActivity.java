package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MisComprasActivity extends AppCompatActivity {

    ArrayList<EmpresaItem> listaEmpresas;
    EmpresaAdapter mAdapter;
    String nombreEmpresa = new String();
    Integer item = 0, posicion = 0, numFilas;
    double numeroAcciones,  precio, comision, dividendoNeto, cotizacion;
    ArrayList<String> compras, titulosMostrarCompras;
    List<Integer> idCompra = new ArrayList<>();
    List<String> datos = new ArrayList<>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_compras);
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        MisFunciones.meterDatos(datos);
        listaEmpresas = MisFunciones.initList();
        Spinner spinnerCompras = findViewById(R.id.spinnerCompras);
        mAdapter = new EmpresaAdapter(this, listaEmpresas);
        spinnerCompras.setAdapter(mAdapter);
        spinnerCompras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint({"ResourceAsColor", "RtlHardcoded"})
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayout = findViewById(R.id.linearLay);
                linearLayout.removeAllViews();
                posicion = position;
                EmpresaItem clickedItem = (EmpresaItem) parent.getItemAtPosition(position);
                nombreEmpresa = clickedItem.getNombreEmpresa();
                item = clickedItem.getItem();
                numFilas = visualizarCompras(posicion-1);
                Context context = getApplicationContext();
                System.out.println("ESTOS SON LOS NUMEROS DE FILA: " + numFilas);
                if(position == 0)
                    mostrarCompraInicial(linearLayout);
                else {
                    if (numFilas > 0) {
                        int x = 0;
                        for (int y = 0; y < numFilas; y++) {
                            mostrarCompras(linearLayout, context, y, x);
                            x = titulosMostrarCompras.size() - 1;
                        }
                        System.out.println("NUMERO DE FILAS: " + numFilas);
                        for (int j = 0; j < numFilas; j++) {
                            View btn = findViewById(j + 10);
                            int finalJ = j;
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(MisComprasActivity.this);
                                    dialogo1.setTitle("Confirmar Eliminación");
                                    dialogo1.setMessage("¿Está seguro de que quiere eliminar la compra Número " + (finalJ + 1) + "?");
                                    dialogo1.setCancelable(false);
                                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            System.out.println("ESTO ES ACEPTAR");
                                            //AQUÍ TENEMOS QUE ADAPTAR EL CÓDIGO PARA QUE ELIMINE LA COMPRA
                                            if (numFilas == 1) {
                                                SQLiteDatabase db = conexion.getWritableDatabase();
                                                db.execSQL("DELETE FROM CARTERA WHERE idEmpresa = " + (posicion - 1));
                                                db.execSQL("DELETE FROM TOTALESCARTERA WHERE idEmpresa = " + (posicion - 1));
                                                actualizarBBDD(db, idCompra.get(finalJ), 1, posicion - 1);
                                            } else
                                                actualizarBBDD(db, idCompra.get(finalJ), 2, posicion - 1);
                                            Intent intent = new Intent(v.getContext(), MisComprasActivity.class);
                                            startActivityForResult(intent, 0);
                                        }
                                    });
                                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            System.out.println("ESTO ES CANCELAR");
                                        }
                                    });
                                    dialogo1.show();
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), ComprasActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        db.close();
    }

    @SuppressLint("ResourceType")
    private void mostrarCompraInicial(LinearLayout linearLayout) {
        Context context = getApplicationContext();
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM COMPRAS", null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            TextView textView0 = new TextView(context);
            textView0.setId(1);
            textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
            textView0.setHint("- COMPRAS -");
            textView0.setTextSize(20);
            textView0.setHintTextColor(Color.rgb(255, 128, 56));
            textView0.setGravity(Gravity.CENTER);
            linearLayout.addView(textView0);
            View viewTemp2 = new View(getApplicationContext());
            viewTemp2.setMinimumHeight(10);
            viewTemp2.setBackgroundColor(Color.rgb(255, 255, 255));
            linearLayout.addView(viewTemp2);
            View viewTemp3 = new View(getApplicationContext());
            viewTemp3.setLayoutParams(new LinearLayout.LayoutParams(1080, 25));
            viewTemp3.setMinimumHeight(10);
            viewTemp3.setBackgroundColor(Color.rgb(3, 50, 73));
            linearLayout.addView(viewTemp3);
            View viewTemp4 = new View(getApplicationContext());
            viewTemp4.setMinimumHeight(10);
            viewTemp4.setBackgroundColor(Color.rgb(255, 255, 255));
            linearLayout.addView(viewTemp4);
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(cursor.getColumnIndex("empresa"));
                TextView textView2 = new TextView(context);
                textView2.setId(i);
                textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 350));
                String empresa = MisFunciones.meterValorEmpresa(id);
                textView2.setHint("Compra Nº" + (i+1) + ": " + empresa);
                textView2.setTextSize(20);
                textView2.setHintTextColor(Color.WHITE);
                textView2.setGravity(Gravity.CENTER);
                linearLayout.addView(textView2);
                if(i != cursor.getCount() - 1) {
                    viewTemp2 = new View(getApplicationContext());
                    viewTemp2.setMinimumHeight(10);
                    viewTemp2.setBackgroundColor(Color.rgb(255, 255, 255));
                    linearLayout.addView(viewTemp2);
                }
                cursor.moveToNext();
            }
            View viewTemp = new View(context);
            viewTemp.setMinimumHeight(10);
            viewTemp.setBackgroundColor(Color.rgb(255, 128, 56));
            linearLayout.addView(viewTemp);
        }
        else{
            TextView textView0 = new TextView(context);
            textView0.setId(1);
            textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
            textView0.setHint("- COMPRAS -");
            textView0.setTextSize(20);
            textView0.setHintTextColor(Color.rgb(255, 128, 56));
            textView0.setGravity(Gravity.CENTER);
            linearLayout.addView(textView0);
            View viewTemp2 = new View(getApplicationContext());
            viewTemp2.setMinimumHeight(10);
            viewTemp2.setBackgroundColor(Color.rgb(255, 255, 255));
            linearLayout.addView(viewTemp2);
            View viewTemp3 = new View(getApplicationContext());
            viewTemp3.setLayoutParams(new LinearLayout.LayoutParams(1080, 25));
            viewTemp3.setMinimumHeight(10);
            viewTemp3.setBackgroundColor(Color.rgb(3, 50, 73));
            linearLayout.addView(viewTemp3);
            View viewTemp4 = new View(getApplicationContext());
            viewTemp4.setMinimumHeight(10);
            viewTemp4.setBackgroundColor(Color.rgb(255, 255, 255));
            linearLayout.addView(viewTemp4);
            TextView textView2 = new TextView(context);
            textView2.setId(1);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 350));
            textView2.setHint("No se ha introducido ninguna Compra.");
            textView2.setTextSize(20);
            textView2.setHintTextColor(Color.WHITE);
            textView2.setGravity(Gravity.CENTER);
            linearLayout.addView(textView2);
            cursor.moveToNext();
            View viewTemp = new View(context);
            viewTemp.setMinimumHeight(10);
            viewTemp.setBackgroundColor(Color.rgb(255, 128, 56));
            linearLayout.addView(viewTemp);
        }
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    private void mostrarCompras(LinearLayout linearLayout, Context context, int y, int x) {
        meterTitulosMostrarCompras(y);

        linearLayout.getContext();
        TextView textView = new TextView(context);
        textView.setId(y+10);
        textView.setHint("Click aquí para eliminar Compra Número " + (y+1));
        textView.setLayoutParams(new LinearLayout.LayoutParams(1080, 200));
        textView.setTextSize(20);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        textView.setHintTextColor(Color.rgb(158, 158, 158));
        textView.setGravity(Gravity.CENTER);
        textView.isClickable();

        for(int i=0; i<titulosMostrarCompras.size(); i++) {
            //linearLayout.getContext();
            TextView textView0 = new TextView(context);
            textView0.setId(y);
            textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
            textView0.setHint(titulosMostrarCompras.get(i));
            textView0.setTextSize(20);
            textView0.setHintTextColor(Color.rgb(255, 128, 56));
            textView0.setGravity(Gravity.CENTER);
            linearLayout.addView(textView0);
            if(i != 0) {
                TextView textView2 = new TextView(context);
                textView2.setId(y);
                textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
                textView2.setHint(compras.get(x++));
                textView2.setTextSize(20);
                textView2.setHintTextColor(Color.WHITE);
                textView2.setGravity(Gravity.CENTER);
                linearLayout.addView(textView2);
                if(i != titulosMostrarCompras.size() - 1) {
                    View viewTemp2 = new View(getApplicationContext());
                    viewTemp2.setMinimumHeight(10);
                    viewTemp2.setBackgroundColor(Color.rgb(255, 255, 255));
                    linearLayout.addView(viewTemp2);
                }
            }
            else{
                View viewTemp2 = new View(getApplicationContext());
                viewTemp2.setMinimumHeight(10);
                viewTemp2.setBackgroundColor(Color.rgb(255, 255, 255));
                linearLayout.addView(viewTemp2);
                View viewTemp3 = new View(getApplicationContext());
                viewTemp3.setLayoutParams(new LinearLayout.LayoutParams(1080, 25));
                viewTemp3.setMinimumHeight(10);
                viewTemp3.setBackgroundColor(Color.rgb(3, 50, 73));
                linearLayout.addView(viewTemp3);
                View viewTemp4 = new View(getApplicationContext());
                viewTemp4.setMinimumHeight(10);
                viewTemp4.setBackgroundColor(Color.rgb(255, 255, 255));
                linearLayout.addView(viewTemp4);
            }
        }
        linearLayout.addView(textView);
        View viewTemp = new View(context);
        viewTemp.setMinimumHeight(10);
        viewTemp.setBackgroundColor(Color.rgb(255, 128, 56));
        linearLayout.addView(viewTemp);
    }

    private void meterTitulosMostrarCompras(int y) {
        titulosMostrarCompras = new ArrayList<>();
        titulosMostrarCompras.add("- Compra Número " + (y + 1) + " -\n");
        titulosMostrarCompras.add("\nFecha: \n");
        titulosMostrarCompras.add("\nNúmero de Acciones: \n");
        titulosMostrarCompras.add("\nPrecio: \n");
        titulosMostrarCompras.add("\nTotal: \n");
        titulosMostrarCompras.add("\nBalance: \n");
        titulosMostrarCompras.add("\nPorcentaje (%): \n");
        titulosMostrarCompras.add("\nComisión: \n");
        titulosMostrarCompras.add("\nDividendo Anual: \n");
    }

    private Integer visualizarCompras(Integer posicion) {
        compras = new ArrayList<>();
        System.out.println("ESTA ES LA POSICION: " + posicion);
        if (posicion >= 0) {
            ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
            SQLiteDatabase db = conexion.getReadableDatabase();
            DecimalFormat formato = new DecimalFormat("#,##0.00");
            @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM COMPRAS WHERE empresa = " + posicion, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                idCompra.add(cursor.getInt(cursor.getColumnIndex("id")));
                for (int y = 0; y < cursor.getColumnCount(); y++) {
                    if (y == 0 || y == 2)
                        continue;
                    else if(y == 1)
                        compras.add(cursor.getString(y));
                    else if(y == 4 || y == 5 || y == 6 || y == 8)
                        compras.add(formato.format(cursor.getDouble(y)) + "€");
                    else
                        compras.add(formato.format(cursor.getDouble(y)));
                    System.out.println("AQUÍ ESCRIBO LO QUE HAY DE CADA UNO: " + cursor.getString(y));
                }
                cursor.moveToNext();
            }
            return cursor.getCount();
        }
        else
            return 0;
    }

    private void actualizarBBDD(SQLiteDatabase db, int idCompra, int i, int posicion) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        db = conexion.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c2_1 = db.rawQuery("SELECT * FROM DIVIDENDOSESTIMADOS WHERE ID = " + posicion, null);
        c2_1.moveToFirst();
        dividendoNeto = c2_1.getDouble(c2_1.getColumnIndex("dividendoNeto"));
        @SuppressLint("Recycle") Cursor c2_2 = db.rawQuery("SELECT * FROM COTIZACIONES WHERE ID = " + posicion, null);
        c2_2.moveToFirst();
        cotizacion =  c2_2.getDouble(c2_2.getColumnIndex("cotizacion"));
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM COMPRAS WHERE id = " + idCompra, null);
        cursor.moveToFirst();
        numeroAcciones = cursor.getDouble(cursor.getColumnIndex("numeroAcciones"));
        precio = cursor.getDouble(cursor.getColumnIndex("precio"));
        comision = cursor.getDouble(cursor.getColumnIndex("comision"));
        db.execSQL("DELETE FROM COMPRAS WHERE id = " + idCompra);
        //Cuando eliminamos de cartera.
        if(i == 1){
            actualizarTotalesCartera(db, i, posicion);
            actualizarCartera(db, i, posicion);
        }
        //Cuando eliminamos compra, pero no de la cartera. Hay que actualizar la que tenemos.
        else if(i ==2){
            actualizarTotalesCartera(db, i, posicion);
            actualizarCartera(db, i, posicion);
        }
    }

    private void actualizarTotalesCartera(SQLiteDatabase db, int i, int idEmpresa) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        db = conexion.getWritableDatabase();
        List<Double> totales;
        int empresaBucle;
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        c.moveToFirst();
        if(i == 2) {
            actualizarTotalesCarteraModificada(db, idEmpresa);
        }
        for (int j = 0; j < c.getCount(); j++) {
            empresaBucle = c.getInt(c.getColumnIndex("idEmpresa"));
            if (empresaBucle != idEmpresa) {
                double dineroTotalInvertidoConComisiones = c.getDouble(c.getColumnIndex("dineroTotalInvertidoConComisiones"));
                double dineroActualDisponible = c.getDouble(c.getColumnIndex("dineroActualDisponible"));
                double dividendoEstimadoAnio = c.getDouble(c.getColumnIndex("dividendoEstimadoAnio"));
                totales = calcularTotales(db, empresaBucle, 1, 0, 0, 0);
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
            if (j != (c.getColumnCount() - 1))
                c.moveToNext();
        }
    }

    private void actualizarCarteraModificada(SQLiteDatabase db, int idEmpresa) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        db = conexion.getWritableDatabase();
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA WHERE idEmpresa = " + idEmpresa, null);
        c.moveToFirst();
        double nAcciones = c.getDouble(c.getColumnIndex("numeroAcciones"));
        double dineroActualDisponible = c.getDouble(c.getColumnIndex("dineroActualDisponible"));
        double dineroTotalInvertido = c.getDouble(c.getColumnIndex("dineroTotalInvertido"));
        double balanceSinComisiones = c.getDouble(c.getColumnIndex("balanceSinComisiones"));
        double comisiones = c.getDouble(c.getColumnIndex("comisiones"));
        double balanceIncluyendoComisiones = c.getDouble(c.getColumnIndex("balanceIncluyendoComisiones"));
        double dineroTotalInvertidoConComisiones = c.getDouble(c.getColumnIndex("dineroTotalInvertidoConComisiones"));
        double balanceTantoPorCientoConComisiones = c.getDouble(c.getColumnIndex("balanceTantoPorCientoConComisiones"));
        double dividendoEstimadoAnio = c.getDouble(c.getColumnIndex("dividendoEstimadoAnio"));
        double pesoCarteraInvertidoConComisiones = c.getDouble(c.getColumnIndex("pesoCarteraInvertidoConComisiones"));
        double pesoCarteraActualDisponibleConComisiones = c.getDouble(c.getColumnIndex("pesoCarteraActualDisponibleConComisiones"));
        double tantoPorCientoTotalDividendosRecibidos = c.getDouble(c.getColumnIndex("tantoPorCientoTotalDividendosRecibidos"));
        double balancePesoCartera = pesoCarteraActualDisponibleConComisiones - pesoCarteraInvertidoConComisiones;
        String ticker = datos.get(posicion-1);
        String nombreGrafico = ticker + " - " + MisFunciones.meterValorEmpresa(idEmpresa) + " (" + formato2.format(pesoCarteraInvertidoConComisiones) + "%)";
        double precioMedioAdquisicionSinComisiones = (dineroTotalInvertido / nAcciones);
        double precioMedioAdquisicionConComisiones = (dineroTotalInvertidoConComisiones / nAcciones);
        Object[] objetos = new Object[18];
        objetos[0] = formato2.format(pesoCarteraInvertidoConComisiones);
        objetos[1] = formato2.format(nAcciones);
        objetos[2] = formato2.format(dineroActualDisponible);
        objetos[3] = formato2.format(balanceSinComisiones);
        objetos[4] = formato2.format(comisiones);
        objetos[5] = formato2.format(balanceIncluyendoComisiones);
        objetos[6] = formato2.format(precioMedioAdquisicionSinComisiones);
        objetos[7] = formato2.format(precioMedioAdquisicionConComisiones);
        objetos[8] = formato2.format(dineroTotalInvertido);;
        objetos[9] = formato2.format(dineroTotalInvertidoConComisiones);
        objetos[10] = formato2.format(balanceTantoPorCientoConComisiones);
        objetos[11] = formato2.format(pesoCarteraInvertidoConComisiones);
        objetos[12] = formato2.format(pesoCarteraActualDisponibleConComisiones);
        objetos[13] = formato2.format(balancePesoCartera);
        objetos[14] = nombreGrafico;
        objetos[15] = formato2.format(dividendoEstimadoAnio);
        objetos[16] = formato2.format(tantoPorCientoTotalDividendosRecibidos);
        objetos[17] = idEmpresa;
        String cadena = "UPDATE CARTERA SET tantoPorCientoAcInv = " + "?";
        cadena = cadena + ", numeroAcciones = " + "?";
        cadena = cadena + ", dineroActualDisponible = " + "?";
        cadena = cadena + ", balanceSinComisiones = " + "?";
        cadena = cadena + ", comisiones = " + "?";
        cadena = cadena + ", balanceIncluyendoComisiones = " + "?";
        cadena = cadena + ", precioMedioAdquisicionSinComisiones = " + "?";
        cadena = cadena + ", precioMedioAdquisicionConComisiones = " + "?";
        cadena = cadena + ", dineroTotalInvertido = " + "?";
        cadena = cadena + ", dineroTotalInvertidoConComisiones = " + "?";
        cadena = cadena + ", balanceTantoPorCientoConComisiones = " + "?";
        cadena = cadena + ", pesoCarteraInvertidoConComisiones = " + "?";
        cadena = cadena + ", pesoCarteraActualDisponibleConComisiones = " + "?";
        cadena = cadena + ", balancePesoCartera = " + "?";
        cadena = cadena + ", nombreGrafico = " + "?";
        cadena = cadena + ", dividendoEstimadoAnio = " + "?";
        cadena = cadena + ", tantoPorCientoTotalDividendosRecibidos = " + "?";
        cadena = cadena + " WHERE idEmpresa = " + "?";
        db.execSQL(cadena, objetos);
    }

    private void actualizarTotalesCarteraModificada(SQLiteDatabase db, int idEmpresa) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        db = conexion.getWritableDatabase();
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA WHERE idEmpresa = " + idEmpresa, null);
        c.moveToFirst();
        double nAcciones = c.getDouble(c.getColumnIndex("numeroAcciones"));
        nAcciones = nAcciones - numeroAcciones;
        double dineroActualDisponible = cotizacion * nAcciones;
        double dineroTotalInvertido = c.getDouble(c.getColumnIndex("dineroTotalInvertido"));
        dineroTotalInvertido = dineroTotalInvertido - (numeroAcciones * precio);
        double balanceSinComisiones = (nAcciones * cotizacion) - dineroTotalInvertido;
        double comisiones = c.getDouble(c.getColumnIndex("comisiones"));
        comisiones = comisiones + comision;
        double balanceIncluyendoComisiones = balanceSinComisiones + comisiones;
        double dineroTotalInvertidoConComisiones = dineroTotalInvertido - comisiones;
        double balanceTantoPorCientoConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
        double dividendoEstimadoAnio = nAcciones * dividendoNeto;
        List<Double> totales = calcularTotales(db, idEmpresa, 2, dineroTotalInvertidoConComisiones, dineroActualDisponible, dividendoEstimadoAnio);
        double pesoCarteraInvertidoConComisiones = dineroTotalInvertidoConComisiones / totales.get(0) * 100;
        double pesoCarteraActualDisponibleConComisiones = dineroActualDisponible / totales.get(1) * 100;
        double tantoPorCientoTotalDividendosRecibidos = dividendoEstimadoAnio / totales.get(2) * 100;
        Object[] objetos = new Object[13];
        objetos[0] = formato2.format(nAcciones);
        objetos[1] = formato2.format(dineroActualDisponible);
        objetos[2] = formato2.format(balanceSinComisiones);
        objetos[3] = formato2.format(comisiones);
        objetos[4] = formato2.format(balanceIncluyendoComisiones);
        objetos[5] = formato2.format(dineroTotalInvertido);;
        objetos[6] = formato2.format(dineroTotalInvertidoConComisiones);
        objetos[7] = formato2.format(balanceTantoPorCientoConComisiones);
        objetos[8] = formato2.format(dividendoEstimadoAnio);
        objetos[9] = formato2.format(pesoCarteraInvertidoConComisiones);
        objetos[10] = formato2.format(pesoCarteraActualDisponibleConComisiones);
        objetos[11] = formato2.format(tantoPorCientoTotalDividendosRecibidos);
        objetos[12] = idEmpresa;
        String cadena = "UPDATE TOTALESCARTERA SET numeroAcciones = " + "?";
        cadena = cadena + ", dineroActualDisponible = " + "?";
        cadena = cadena + ", balanceSinComisiones = " + "?";
        cadena = cadena + ", comisiones = " + "?";
        cadena = cadena + ", balanceIncluyendoComisiones = " + "?";
        cadena = cadena + ", dineroTotalInvertido = " + "?";
        cadena = cadena + ", dineroTotalInvertidoConComisiones = " + "?";
        cadena = cadena + ", balanceTantoPorCientoConComisiones = " + "?";
        cadena = cadena + ", dividendoEstimadoAnio = " + "?";
        cadena = cadena + ", pesoCarteraInvertidoConComisiones = " + "?";
        cadena = cadena + ", pesoCarteraActualDisponibleConComisiones = " + "?";
        cadena = cadena + ", tantoPorCientoTotalDividendosRecibidos = " + "?";
        cadena = cadena + " WHERE idEmpresa = " + "?";
        db.execSQL(cadena, objetos);
    }

    private void actualizarCartera(SQLiteDatabase db, int i, int idEmpresa) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        db = conexion.getWritableDatabase();
        DecimalFormat formato2 = new DecimalFormat("#.00");
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        c.moveToFirst();
        if(i == 2) {
            actualizarCarteraModificada(db, idEmpresa);
        }
        for (int j = 0; j < c.getCount(); j++) {
            int empresaBucle = c.getInt(c.getColumnIndex("idEmpresa"));
            if (empresaBucle != idEmpresa) {
                double pesoCarteraInvertidoConComisiones = c.getDouble(c.getColumnIndex("pesoCarteraInvertidoConComisiones"));
                double pesoCarteraActualDisponibleConComisiones = c.getDouble(c.getColumnIndex("pesoCarteraActualDisponibleConComisiones"));
                double tantoPorCientoTotalDividendosRecibidos = c.getDouble(c.getColumnIndex("tantoPorCientoTotalDividendosRecibidos"));
                double balancePesoCartera = pesoCarteraActualDisponibleConComisiones - pesoCarteraInvertidoConComisiones;
                String ticker = datos.get(posicion - 1);
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
            if (j != (c.getColumnCount() - 1))
                c.moveToNext();
        }
    }


    private List<Double> calcularTotales(SQLiteDatabase db, int empresa, int i, double dineroTotalInvertidoConComisiones, double dineroActualDisponible, double dividendoEstimadoAnio) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        db = conexion.getWritableDatabase();
        List<Double> lista = new ArrayList<>();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        c.moveToFirst();
        //Cuando eliminamos de cartera.
        if(i == 1){
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
            lista.add(dineroTotalInvertidoConComisionesTotal);
            lista.add(dineroActualDisponibleTotal);
            lista.add(dividendoEstimadoAnioTotal);
            return lista;
        }
        //Cuando eliminamos compra, pero no de la cartera
        else if(i == 2){
            @SuppressLint("Recycle") Cursor cc = db.rawQuery("SELECT * FROM TOTALESCARTERA WHERE idEmpresa = " + empresa, null);
            cc.moveToFirst();
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
        return lista;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), ComprasActivity.class);
        startActivityForResult(intent, 0);
    }
}