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

public class NuevoDividendoRecibidoActivity extends AppCompatActivity {

    ArrayList<EmpresaItem> listaEmpresas;
    EmpresaAdapter mAdapter;
    String nombreEmpresa;
    Integer item = 0;
    Integer posicion = 0;
    EditText etFecha, etDividendoBruto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_dividendo_recibido);

        int posicionRecuperada = getIntent().getIntExtra("posicion", 0);
        listaEmpresas = MisFunciones.initList();
        Spinner spinnerEmpresas = findViewById(R.id.valorSpinner);
        mAdapter = new EmpresaAdapter(this, listaEmpresas);
        spinnerEmpresas.setAdapter(mAdapter);
        spinnerEmpresas.setSelection(posicionRecuperada);
        etFecha = findViewById(R.id.etFecha);
        etDividendoBruto = findViewById(R.id.etDividendoBruto);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DividendosRecibidosActivity.class);
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
                    Toast toast = Toast.makeText(getApplicationContext(), "Compruebe bien el formato del número del dividendo bruto introducido.", Toast.LENGTH_LONG);
                    toast.show();
                    etDividendoBruto.setText("");
                }
                else if(comprobarDatos() == 4) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Hay algún dato que no ha introducido. Por favor, introduzca todos los datos que se piden.", Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(comprobarDatos() == 5) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No ha elegido la empresa correspondiente a su compra.", Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    registrarDividendoRecibido();
                    Intent intent = new Intent(v.getContext(), DividendosRecibidosActivity.class);
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
        String temporal = null;

        DecimalFormat formato = new DecimalFormat("#.00");
        ContentValues values = new ContentValues();

        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM DIVIDENDOSRECIBIDOS", null);
        int id;
        if(c.getCount()>0){
            c.moveToLast();
            id = c.getInt(c.getColumnIndex("id")) + 1;
        }
        else
            id = 1;
        values.put(Sentencias.CAMPO_ID, id);
        String fecha = etFecha.getText().toString();
        values.put(Sentencias.CAMPO_FECHA, fecha);
        int empresa = posicion - 1;
        values.put(Sentencias.CAMPO_EMPRESA, empresa);
        double dividendoBruto;
        for(int i=0; i<String.valueOf(etDividendoBruto.getText()).length(); i++){
            if(String.valueOf(etDividendoBruto.getText()).charAt(i) == ',')
                temporal = changeCharInPosition(i, '.', String.valueOf(etDividendoBruto.getText()));
        }
        if(temporal == null)
            temporal = String.valueOf(etDividendoBruto.getText());
        dividendoBruto = Double.parseDouble(temporal);
        double dividendoNeto = dividendoBruto - (dividendoBruto * 0.19);
        values.put(Sentencias.CAMPO_DIVIDENDO_NETO, formato.format(dividendoNeto));
        values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, formato.format(dividendoBruto));
        double retencionEspania = dividendoBruto * 0.19;
        values.put(Sentencias.CAMPO_RETENCION_EN_ESPANIA, formato.format(retencionEspania));
        db.insert(Sentencias.TABLA_DIVIDENDOS_RECIBIDOS, Sentencias.CAMPO_ID, values);
        db.close();
    }

    private int comprobarDatos() {
        String[] fecha = etFecha.getText().toString().split("/");
        int solucion;
        if(etDividendoBruto.getText().toString().isEmpty() || etFecha.getText().toString().isEmpty())
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

        String[] dividendoBruto = etDividendoBruto.getText().toString().split("\\.");
        String[] dividendoBruto2 = etDividendoBruto.getText().toString().split(",");
        if(dividendoBruto.length == 2){
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 0\n");
            if(isNumeric(dividendoBruto[0]) && isNumeric(dividendoBruto[1])){
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 1\n");
                if(Integer.parseInt(dividendoBruto[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        }
        else if(dividendoBruto2.length == 2){
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 2\n");
            if(isNumeric(dividendoBruto2[0]) && isNumeric(dividendoBruto2[1])){
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 3\n");
                if(Integer.parseInt(dividendoBruto2[0]) > 0)
                    solucion = -1;
                else return 1;
            } else return 1;
        }
        else if(dividendoBruto2.length == 1){
            System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 4\n");
            if(isNumeric(dividendoBruto2[0])){
                System.out.println("\nESTE ES EL NÚMERO DONDE FALLA 5\n");
                if(Integer.parseInt(dividendoBruto2[0]) > 0)
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

    public String changeCharInPosition(int position, char ch, String str){
        char[] charArray = str.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), DividendosRecibidosActivity.class);
        startActivityForResult(intent, 0);
    }
}