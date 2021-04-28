package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DividendosEstimadosActivity extends AppCompatActivity {

    ArrayList<EmpresaItem> listaEmpresas;
    EmpresaAdapter mAdapter;
    Integer posicion = 0;
    String nombreEmpresa;
    Integer item = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dividendos_estimados);

        TextView textoNeto = findViewById(R.id.textoNeto);
        TextView textoBruto = findViewById(R.id.textoBruto);

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        DecimalFormat formato2 = new DecimalFormat("#,##0.000");
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        listaEmpresas = MisFunciones.initList();
        Spinner spinnerCompras = findViewById(R.id.spinnerCompras);
        mAdapter = new EmpresaAdapter(this, listaEmpresas);
        spinnerCompras.setAdapter(mAdapter);
        spinnerCompras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint({"ResourceAsColor", "SetTextI18n"})
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicion = position;
                EmpresaItem clickedItem = (EmpresaItem) parent.getItemAtPosition(position);
                nombreEmpresa = clickedItem.getNombreEmpresa();
                item = clickedItem.getItem();
                if(position == 0){
                    textoNeto.setText("----");
                    textoBruto.setText("----");
                }
                @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM DIVIDENDOSESTIMADOS WHERE id = " + (position-1), null);
                c.moveToFirst();
                System.out.println("CGETCOUNT: " + c.getCount());
                if(c.getCount() != 0) {
                    double dividendoNeto = c.getDouble(c.getColumnIndex("dividendoNeto"));
                    double dividendoBruto = c.getDouble(c.getColumnIndex("dividendoBruto"));
                    textoNeto.setText(formato2.format(dividendoNeto));
                    textoBruto.setText(formato.format(dividendoBruto));
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
                Intent intent = new Intent (v.getContext(), MenuActivity.class);
                startActivityForResult(intent, 0);

            }
        });

        Button btnModificar = findViewById(R.id.btnModificar);
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), NuevoDividendoEstimadoActivity.class);
                intent.putExtra("posi", posicion);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), MenuActivity.class);
        startActivityForResult(intent, 0);
    }
}