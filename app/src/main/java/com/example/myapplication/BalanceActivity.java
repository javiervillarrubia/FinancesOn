package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class BalanceActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        TextView textoTotal = findViewById(R.id.textoTotal);
        double textoTotalVar = 0;
        TextView textoSin = findViewById(R.id.textoSin);
        double textoSinVar = 0;
        TextView textoCon = findViewById(R.id.textoCon);
        double textoConVar = 0;

        DecimalFormat formato2 = new DecimalFormat("#,##0.00");
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM TOTALESCARTERA", null);
        c.moveToFirst();
        if (c.getCount() != 0) {
            for (int i = 0; i < c.getCount(); i++) {
                textoTotalVar = textoTotalVar + c.getDouble(c.getColumnIndex("dineroActualDisponible"));
                textoSinVar = textoSinVar + c.getDouble(c.getColumnIndex("dineroTotalInvertido"));
                textoConVar = textoConVar + c.getDouble(c.getColumnIndex("dineroTotalInvertidoConComisiones"));
                c.moveToNext();
            }
            textoSinVar = ((textoTotalVar - textoSinVar) / textoSinVar) * 100;
            textoConVar = ((textoTotalVar - textoConVar) / textoConVar) * 100;
            textoTotal.setText(formato2.format(textoTotalVar));
            textoSin.setText(formato2.format(textoSinVar));
            textoCon.setText(formato2.format(textoConVar));
        }

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), MenuActivity.class);
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