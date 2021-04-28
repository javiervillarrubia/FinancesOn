package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

public class DividendosRecibidosActivity extends AppCompatActivity {

    ArrayList<EmpresaItem> listaEmpresas;
    EmpresaAdapter mAdapter;
    String nombreEmpresa = new String();
    Integer item = 0, posicion = 0, numFilas;
    ArrayList<String> titulosDividendosRecibidos, dividendosRecibidos;
    ArrayList<Integer> idDividendos;
    List<String> datos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dividendos_recibidos);

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
                numFilas = visualizarDividendosRecibidos(posicion-1);
                Context context = getApplicationContext();
                System.out.println("ESTOS SON LOS NUMEROS DE FILA: " + numFilas);
                if(position == 0)
                    mostrarDividendosRecibidosInicial(linearLayout);
                else {
                    if (numFilas > 0) {
                        int x=0;
                        for(int k=0; k<numFilas; k++) {
                            mostrarDividendosRecibidos(linearLayout, context, x, k);
                            x = titulosDividendosRecibidos.size() - 1;
                        }
                        System.out.println("NUMERO DE FILAS: " + numFilas);
                        for (int j = 0; j < numFilas; j++) {
                            @SuppressLint("ResourceType") View btn = findViewById(j + 10);
                            int finalJ = j;
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(DividendosRecibidosActivity.this);
                                    dialogo1.setTitle("Confirmar Eliminación");
                                    dialogo1.setMessage("¿Está seguro de que quiere eliminar el Dividendo Recibido ?");
                                    dialogo1.setCancelable(false);
                                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            SQLiteDatabase db = conexion.getWritableDatabase();
                                            System.out.println("ESTO ES ACEPTAR");
                                            db.execSQL("DELETE FROM DIVIDENDOSRECIBIDOS WHERE empresa = " + (posicion - 1) + " AND id = " + idDividendos.get(finalJ));
                                            Intent intent = new Intent(v.getContext(), DividendosRecibidosActivity.class);
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
                    } else {
                        if (posicion != 0) {
                            mostrarDividendosRecibidosVacio(linearLayout, context);
                            int idTemp = 2;
                            View btn = findViewById(idTemp);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), NuevoDividendoRecibidoActivity.class);
                                    intent.putExtra("posicion", posicion);
                                    startActivityForResult(intent, 0);
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

        Button btnModificar = findViewById(R.id.btnModificar);
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), NuevoDividendoRecibidoActivity.class);
                intent.putExtra("posicion", posicion);
                startActivityForResult(intent, 0);
            }
        });

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), MenuActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        db.close();
    }

    @SuppressLint("ResourceType")
    private void mostrarDividendosRecibidosInicial(LinearLayout linearLayout) {
        Context context = getApplicationContext();
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM DIVIDENDOSRECIBIDOS", null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            TextView textView0 = new TextView(context);
            textView0.setId(1);
            textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
            textView0.setHint("- DIVIDENDOS RECIBIDOS -");
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
                textView2.setHint("Dividendo Recibido Nº" + (i+1) + ": " + empresa);
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
            textView0.setHint("- DIVIDENDOS RECIBIDOS -");
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
            textView2.setHint("No se ha introducido ningun Dividendo Recibido.");
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

    @SuppressLint("ResourceType")
    private void mostrarDividendosRecibidosVacio(LinearLayout linearLayout, Context context) {
        linearLayout.removeAllViews();
        TextView textView = new TextView(context);
        textView.setId(1);
        textView.setLayoutParams(new LinearLayout.LayoutParams(1080, 50));
        textView.setHint("");
        textView.setTextSize(35);
        textView.setHintTextColor(Color.rgb(255, 128, 56));
        textView.setGravity(Gravity.CENTER);
        linearLayout.addView(textView);
        View viewTemp = new View(context);
        viewTemp.setMinimumHeight(10);
        viewTemp.setBackgroundColor(Color.rgb(255, 128, 56));
        linearLayout.addView(viewTemp);
        TextView textView0 = new TextView(context);
        textView0.setId(2);
        textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 750));
        textView0.setHint("No se han introducido 'Dividendos Recibidos' para esta empresa.\nClick aquí para introducir nuevos datos.");
        textView0.isClickable();
        textView0.setTextSize(35);
        textView0.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        textView0.setHintTextColor(Color.rgb(255, 128, 56));
        textView0.setGravity(Gravity.CENTER);
        View viewTemp2 = new View(context);
        viewTemp2.setMinimumHeight(10);
        viewTemp2.setBackgroundColor(Color.rgb(255, 128, 56));
        linearLayout.addView(textView0);
        linearLayout.addView(viewTemp2);
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n", "ResourceType"})
    private void mostrarDividendosRecibidos(LinearLayout linearLayout, Context context, int x, int j) {
        meterTitulosMostrarDividendosRecibidos();

        linearLayout.getContext();
        TextView textView = new TextView(context);
        textView.setId(j+10);
        textView.setHint("Click aquí para eliminar Dividendo Recibido.");
        textView.setLayoutParams(new LinearLayout.LayoutParams(1080, 250));
        textView.setTextSize(20);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        textView.setHintTextColor(Color.rgb(158, 158, 158));
        textView.setGravity(Gravity.CENTER);
        textView.isClickable();

        for(int i=0; i<titulosDividendosRecibidos.size(); i++) {
            TextView textView0 = new TextView(context);
            textView0.setId(0);
            textView0.setLayoutParams(new LinearLayout.LayoutParams(1080, 200));
            textView0.setHint(titulosDividendosRecibidos.get(i));
            textView0.setTextSize(20);
            textView0.setHintTextColor(Color.rgb(255, 128, 56));
            textView0.setGravity(Gravity.CENTER);
            linearLayout.addView(textView0);
            if(i != 0) {
                TextView textView2 = new TextView(context);
                textView2.setId(0);
                textView2.setLayoutParams(new LinearLayout.LayoutParams(1080, 150));
                textView2.setHint(dividendosRecibidos.get(x++));
                textView2.setTextSize(20);
                textView2.setHintTextColor(Color.WHITE);
                textView2.setGravity(Gravity.CENTER);
                linearLayout.addView(textView2);
                if(i != titulosDividendosRecibidos.size() - 1) {
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

    private void meterTitulosMostrarDividendosRecibidos() {
        titulosDividendosRecibidos = new ArrayList<>();
        titulosDividendosRecibidos.add("- DIVIDENDO RECIBIDO -\n");
        titulosDividendosRecibidos.add("\nFecha: \n");
        titulosDividendosRecibidos.add("\nDividendo Neto: \n");
        titulosDividendosRecibidos.add("\nDividendo Bruto: \n");
        titulosDividendosRecibidos.add("\nRetención en España: \n");
    }

    private Integer visualizarDividendosRecibidos(Integer posicion) {
        dividendosRecibidos = new ArrayList<>();
        idDividendos = new ArrayList<>();
        System.out.println("ESTA ES LA POSICION: " + posicion);
        if (posicion >= 0) {
            ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
            SQLiteDatabase db = conexion.getReadableDatabase();

            @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM DIVIDENDOSRECIBIDOS WHERE empresa = " + posicion, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                idDividendos.add(cursor.getInt(cursor.getColumnIndex("id")));
                for (int y = 0; y < cursor.getColumnCount(); y++) {
                    if (y == 0 || y == 2)
                        continue;
                    else
                        dividendosRecibidos.add(cursor.getString(y));
                    System.out.println("AQUÍ ESCRIBO LO QUE HAY DE CADA UNO: " + cursor.getString(y));
                }
                cursor.moveToNext();
            }
            return cursor.getCount();
        }
        else
            return 0;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), MenuActivity.class);
        startActivityForResult(intent, 0);
    }
}