package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class CarteraActivity extends AppCompatActivity {

    List<String> listaParaBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartera);

        int posicionRecuperada = getIntent().getIntExtra("mensaje", 0);
        if(posicionRecuperada == 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Se ha generado el archivo en la carpeta \"Descargas\" en el almacenamiento interno de su dispositivo.", Toast.LENGTH_LONG);
            toast.show();
        }
        Button btnMiCartera = findViewById(R.id.btnMiCartera);
        btnMiCartera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), MiCarteraActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnMisTotales = findViewById(R.id.btnMisTotales);
        btnMisTotales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), MisTotalesActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnGenerarExcel = findViewById(R.id.btnGenerarExcel);
        btnGenerarExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), GenerarExcelCarteraActivity.class);
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