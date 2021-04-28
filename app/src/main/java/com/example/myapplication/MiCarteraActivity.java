package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MiCarteraActivity extends AppCompatActivity {

    ArrayList<EmpresaItem> listaEmpresas;
    EmpresaAdapter mAdapter;
    String nombreEmpresa = new String();
    Integer item = 0, posicion = 0, numFilas;
    ArrayList<String> compras, titulosMostrarCompras, titulosTotales;
    List<String> datos = new ArrayList<>();
    ArrayList<String> totales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_cartera);

        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        MisFunciones.meterDatos(datos);
        Context context = getApplicationContext();
        LinearLayout linearLayout = findViewById(R.id.linearLay);
        listaEmpresas = MisFunciones.initList();
        Spinner spinnerCompras = findViewById(R.id.spinnerCompras);
        mAdapter = new EmpresaAdapter(this, listaEmpresas);
        spinnerCompras.setAdapter(mAdapter);
        spinnerCompras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayout = findViewById(R.id.linearLay);
                linearLayout.removeAllViews();
                posicion = position;
                EmpresaItem clickedItem = (EmpresaItem) parent.getItemAtPosition(position);
                nombreEmpresa = clickedItem.getNombreEmpresa();
                item = clickedItem.getItem();
                numFilas = visualizarCartera(posicion-1);
                Context context = getApplicationContext();
                System.out.println("ESTOS SON LOS NUMEROS DE FILA: " + numFilas);
                if(position == 0)
                    mostrarCarteraInicial(linearLayout);
                else {
                    if (numFilas > 0)
                        mostrarCartera(linearLayout, context);
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
                Intent intent = new Intent (v.getContext(), CarteraActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        db.close();
    }

    @SuppressLint("ResourceType")
    private void mostrarCarteraInicial(LinearLayout linearLayout) {
        Context context = getApplicationContext();
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM CARTERA", null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            TextView textView0 = new TextView(context);
            textView0.setId(1);
            textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
            textView0.setHint("- CARTERAS -");
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
                int id = cursor.getInt(cursor.getColumnIndex("idEmpresa"));
                TextView textView2 = new TextView(context);
                textView2.setId(i);
                textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 350));
                String empresa = MisFunciones.meterValorEmpresa(id);
                textView2.setHint("Cartera Nº" + (i+1) + ": " + empresa);
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
            textView0.setHint("- CARTERAS -");
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
            textView2.setHint("No se ha introducido ninguna Cartera.");
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
    private void mostrarCartera(LinearLayout linearLayout, Context context) {
        meterTitulosMostrarCartera();
        for(int i=0; i<titulosMostrarCompras.size(); i++) {
            //linearLayout.getContext();
            TextView textView0 = new TextView(context);
            textView0.setId(i);
            if(i == 9 || i == 10 || i == 14 || i == 15)
                textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 200));
            else
                textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
            textView0.setHint(titulosMostrarCompras.get(i));
            textView0.setTextSize(20);
            textView0.setHintTextColor(Color.rgb(255, 128, 56));
            textView0.setGravity(Gravity.CENTER);
            linearLayout.addView(textView0);
            if(i != 0) {
                TextView textView2 = new TextView(context);
                textView2.setId(i);
                textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
                textView2.setHint(compras.get(i));
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
        View viewTemp = new View(context);
        viewTemp.setMinimumHeight(10);
        viewTemp.setBackgroundColor(Color.rgb(255, 128, 56));
        linearLayout.addView(viewTemp);
    }


    private void meterTitulosMostrarCartera() {
        titulosMostrarCompras = new ArrayList<>();
        titulosMostrarCompras.add("- CARTERA -\n");
        titulosMostrarCompras.add("\nDiv sobre Precio Actual: \n");
        titulosMostrarCompras.add("\n%AC INV: \n");
        titulosMostrarCompras.add("\nNúmero Acciones: \n");
        titulosMostrarCompras.add("\nCotización: \n");
        titulosMostrarCompras.add("\nDinero Actual: \n");
        titulosMostrarCompras.add("\nBalance Sin Comisiones: \n");
        titulosMostrarCompras.add("\nComisiones: \n");
        titulosMostrarCompras.add("\nBalance Incluyendo Comisiones: \n");
        titulosMostrarCompras.add("\nPrecio Medio de Adquisición Sin Comisiones: \n");
        titulosMostrarCompras.add("\nPrecio Medio de Adquisición Con Comisiones: \n");
        titulosMostrarCompras.add("\nDinero Total Invertido: \n");
        titulosMostrarCompras.add("\nDinero Total Invertido Con Comisiones: \n");
        titulosMostrarCompras.add("\nBalance Incluyendo Comisiones (%): \n");
        titulosMostrarCompras.add("\nPeso en la Cartera Invertido Con Comisiones: \n");
        titulosMostrarCompras.add("\nPeso en la Cartera Actual Disponible Con Comisiones: \n");
        titulosMostrarCompras.add("\nBalance en el Peso de la Cartera: \n");
        titulosMostrarCompras.add("\nNombre en el Gráfico: \n");
        titulosMostrarCompras.add("\nDividendo Estimado al Año: \n");
        titulosMostrarCompras.add("\n% en el Total de los Dividendos recibidos: \n");
    }

    private Integer visualizarCartera(Integer posicion) {
        compras = new ArrayList<>();
        System.out.println("ESTA ES LA POSICION: " + posicion);
        if (posicion >= 0) {
            ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
            SQLiteDatabase db = conexion.getReadableDatabase();
            DecimalFormat formato = new DecimalFormat("#,##0.00");
            @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM CARTERA WHERE idEmpresa = " + posicion, null);
            cursor.moveToFirst();
            if(cursor.getCount() != 0) {
                compras.add("temp");
                for (int y = 0; y < cursor.getColumnCount(); y++) {
                    if (y == 2 || y == 3)
                        continue;
                    else if(y == 5 || y == 6 || y == 7 || y == 8 || y == 9 || y == 10 || y == 11 || y == 12 || y == 13 || y == 14 || y == 17 || y == 19)
                        compras.add(formato.format(cursor.getDouble(y)) + "€");
                    else if(y == 18)
                        compras.add(cursor.getString(y));
                    else
                        compras.add(formato.format(cursor.getDouble(y)));
                    System.out.println("AQUÍ ESCRIBO LO QUE HAY DE CADA UNO: " + cursor.getString(y));
                }
                cursor.moveToNext();
                return cursor.getCount();
            }
            else
                return 0;
        }
        else
            return 0;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), CarteraActivity.class);
        startActivityForResult(intent, 0);
    }
}