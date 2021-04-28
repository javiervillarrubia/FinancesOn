package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MisTotalesActivity extends AppCompatActivity {

    ArrayList<String> totales, titulosTotales;
    int flag = 0;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_totales);

        LinearLayout linearLayout = findViewById(R.id.linearLay);
        linearLayout.removeAllViews();
        calcularTotales();
        if(flag == 1){
            TextView textView0 = new TextView(getApplicationContext());
            textView0.setId(1);
            textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
            textView0.setHint("- TOTALES DE LA CARTERA -");
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
            TextView textView2 = new TextView(getApplicationContext());
            textView2.setId(1);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 350));
            textView2.setHint("No se ha introducido ninguna Compra.\n\nNo hay Totales.");
            textView2.setTextSize(20);
            textView2.setHintTextColor(Color.WHITE);
            textView2.setGravity(Gravity.CENTER);
            linearLayout.addView(textView2);
            View viewTemp = new View(getApplicationContext());
            viewTemp.setMinimumHeight(10);
            viewTemp.setBackgroundColor(Color.rgb(255, 128, 56));
            linearLayout.addView(viewTemp);
        }
        else {
            for (int i = 0; i < titulosTotales.size(); i++) {
                TextView textView0 = new TextView(getApplicationContext());
                textView0.setId(i);
                textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
                textView0.setHint(titulosTotales.get(i));
                textView0.setTextSize(20);
                textView0.setHintTextColor(Color.rgb(255, 128, 56));
                textView0.setGravity(Gravity.CENTER);
                linearLayout.addView(textView0);
                if (i != 0) {
                    TextView textView2 = new TextView(getApplicationContext());
                    textView2.setId(i);
                    textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
                    textView2.setHint(totales.get(i));
                    textView2.setTextSize(20);
                    textView2.setHintTextColor(Color.WHITE);
                    textView2.setGravity(Gravity.CENTER);
                    linearLayout.addView(textView2);
                    if (i != titulosTotales.size() - 1) {
                        View viewTemp2 = new View(getApplicationContext());
                        viewTemp2.setMinimumHeight(10);
                        viewTemp2.setBackgroundColor(Color.rgb(255, 255, 255));
                        linearLayout.addView(viewTemp2);
                    }
                } else {
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
            View viewTemp2 = new View(getApplicationContext());
            viewTemp2.setMinimumHeight(10);
            viewTemp2.setBackgroundColor(Color.rgb(255, 128, 56));
            linearLayout.addView(viewTemp2);
        }

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), CarteraActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void calcularTotales() {
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM CARTERA", null);
        cursor.moveToFirst();
        if(cursor.getCount() == 0)
            flag = 1;
        double dineroActualDisponible = 0;
        double balanceSinComisiones = 0;
        double comisiones = 0;
        double dineroTotalInvertido = 0;
        double dineroTotalInvertidoConComisiones = 0;
        double dividendoEstimadoAnio = 0;
        for(int i=0; i<cursor.getCount(); i++){
            double dineroActualDisponibleBD =  cursor.getDouble(cursor.getColumnIndex("dineroActualDisponible"));
            double balanceSinComisionesBD =  cursor.getDouble(cursor.getColumnIndex("balanceSinComisiones"));
            double comisionesBD =  cursor.getDouble(cursor.getColumnIndex("comisiones"));
            double dineroTotalInvertidoBD =  cursor.getDouble(cursor.getColumnIndex("dineroTotalInvertido"));
            double dineroTotalInvertidoConComisionesBD =  cursor.getDouble(cursor.getColumnIndex("dineroTotalInvertidoConComisiones"));
            double dividendoEstimadoAnioBD =  cursor.getDouble(cursor.getColumnIndex("dividendoEstimadoAnio"));

            dineroActualDisponible = dineroActualDisponible + dineroActualDisponibleBD;
            balanceSinComisiones = balanceSinComisiones + balanceSinComisionesBD;
            comisiones = comisiones + comisionesBD;
            dineroTotalInvertido = dineroTotalInvertido + dineroTotalInvertidoBD;
            dineroTotalInvertidoConComisiones = dineroTotalInvertidoConComisiones + dineroTotalInvertidoConComisionesBD;
            dividendoEstimadoAnio = dividendoEstimadoAnio + dividendoEstimadoAnioBD;
            cursor.moveToNext();
        }
        totales = new ArrayList<>();
        totales.add("Temp");
        totales.add(formato.format(dineroActualDisponible) + "€");
        totales.add(formato.format(balanceSinComisiones) + "€");
        totales.add(formato.format(comisiones) + "€");
        totales.add(formato.format(balanceSinComisiones + comisiones) + "€");
        totales.add(formato.format(dineroTotalInvertido) + "€");
        totales.add(formato.format(dineroTotalInvertidoConComisiones) + "€");
        totales.add(formato.format((dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100) + "€");
        totales.add(formato.format(dividendoEstimadoAnio) + "€");

        titulosTotales = new ArrayList<>();
        titulosTotales.add("- TOTALES DE LA CARTERA -");
        titulosTotales.add("\nDinero Actual: \n");
        titulosTotales.add("\nBalance Sin Comisiones: \n");
        titulosTotales.add("\nComisiones: \n");
        titulosTotales.add("\nBalance Incluyendo Comisiones: \n");
        titulosTotales.add("\nDinero Total Invertido: \n");
        titulosTotales.add("\nDinero Total Invertido Con Comisiones: \n");
        titulosTotales.add("\nBalance Incluyendo Comisiones (%): \n");
        titulosTotales.add("\nDividendo Estimado al Año: \n");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), CarteraActivity.class);
        startActivityForResult(intent, 0);
    }
}