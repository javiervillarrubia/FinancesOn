package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class NuevoDividendoEstimadoActivity extends AppCompatActivity {

    ArrayList<EmpresaItem> listaEmpresas;
    EmpresaAdapter mAdapter;
    String nombreEmpresa;
    Integer item = 0;
    Integer posicion = 0;
    EditText etDividendoBruto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_dividendo_estimado);

        int posicionRecuperada = getIntent().getIntExtra("posi", 0);
        listaEmpresas = MisFunciones.initList();
        Spinner spinnerEmpresas = findViewById(R.id.valorSpinner);
        mAdapter = new EmpresaAdapter(this, listaEmpresas);
        spinnerEmpresas.setAdapter(mAdapter);
        System.out.println("ESTA ES LA POSICION QUE NOS LLEGA: " + posicionRecuperada);
        spinnerEmpresas.setSelection(posicionRecuperada);
        etDividendoBruto = findViewById(R.id.etDividendoBruto);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DividendosEstimadosActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                //DATOS ERRONEOS, VUELVE A METERLOS EL USUARIO
                if (comprobarDatos() == 1) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Compruebe bien el formato del número del dividendo bruto introducido.", Toast.LENGTH_LONG);
                    toast.show();
                    etDividendoBruto.setText("");
                } else if (comprobarDatos() == 4) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Hay algún dato que no ha introducido. Por favor, introduzca todos los datos que se piden.", Toast.LENGTH_LONG);
                    toast.show();
                } else if (comprobarDatos() == 5) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No ha elegido la empresa correspondiente a su compra.", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    registrarDividendoRecibido();
                    Intent intent = new Intent(v.getContext(), DividendosEstimadosActivity.class);
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

    private void registrarDividendoRecibido() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        DecimalFormat formato2 = new DecimalFormat("#.00");
        String temporal = null;
        double dividendoBruto;
        for (int i = 0; i < String.valueOf(etDividendoBruto.getText()).length(); i++) {
            if (String.valueOf(etDividendoBruto.getText()).charAt(i) == ',')
                temporal = changeCharInPosition(i, '.', String.valueOf(etDividendoBruto.getText()));
        }
        if (temporal == null)
            temporal = String.valueOf(etDividendoBruto.getText());
        dividendoBruto = Double.parseDouble(temporal);
        Object[] objetos = new Object[3];
        double dividendoNeto = dividendoBruto * 0.81;
        objetos[0] = formato2.format(dividendoNeto);
        objetos[1] = formato2.format(dividendoBruto);
        objetos[2] = posicion - 1;
        String cadena = "UPDATE DIVIDENDOSESTIMADOS SET dividendoNeto = " + "?";
        cadena = cadena + ", dividendoBruto = " + "?";
        cadena = cadena + " WHERE id = " + "?";
        db.execSQL(cadena, objetos);
        db.close();
    }

    private int comprobarDatos() {
        int solucion;

        String[] dividendoBruto = etDividendoBruto.getText().toString().split("\\.");
        String[] dividendoBruto2 = etDividendoBruto.getText().toString().split(",");
        if (dividendoBruto.length == 2) {
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 0\n");
            if (isNumeric(dividendoBruto[0]) && isNumeric(dividendoBruto[1])) {
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 1\n");
                if (Integer.parseInt(dividendoBruto[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        } else if (dividendoBruto2.length == 2) {
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 2\n");
            if (isNumeric(dividendoBruto2[0]) && isNumeric(dividendoBruto2[1])) {
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 3\n");
                if (Integer.parseInt(dividendoBruto2[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        } else if (dividendoBruto2.length == 1) {
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 4\n");
            if (isNumeric(dividendoBruto2[0])) {
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 5\n");
                if (Integer.parseInt(dividendoBruto2[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        } else return 1;

        return solucion;
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

    public String changeCharInPosition(int position, char ch, String str) {
        char[] charArray = str.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), DividendosEstimadosActivity.class);
        startActivityForResult(intent, 0);
    }
}