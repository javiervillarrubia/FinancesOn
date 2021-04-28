package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private final int STORAGE_PERMISSION_CODE = 1;
    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static String[] PERMISOS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Toast toast;
    List<Integer> listaNumeros = new ArrayList<>();
    List<Float> listaCotizaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        int posicionRecuperada = getIntent().getIntExtra("mensaje", 0);
        if(posicionRecuperada == 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Se ha generado el archivo en la carpeta \"Descargas\" en el almacenamiento interno de su dispositivo.", Toast.LENGTH_LONG);
            toast.show();
        }

        permisos();
        conexionBBDD();
        crearTablaEmpresas();
        crearTablaDividendos();
        parsearPaginaWeb();
        createNotificationChannel();

        Button btnCompras = findViewById(R.id.btnCompras);
        btnCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), ComprasActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnCartera = findViewById(R.id.btnCartera);
        btnCartera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), CarteraActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnBalance = findViewById(R.id.btnBalance);
        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), BalanceActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnDividendosRecibidos = findViewById(R.id.btnDividendosRecibidos);
        btnDividendosRecibidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DividendosRecibidosActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnDividendosEstimados = findViewById(R.id.btnDividendosEstimados);
        btnDividendosEstimados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DividendosEstimadosActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnGenerarExcel = findViewById(R.id.btnGenerarExcel);
        btnGenerarExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), GenerarExcelActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "name";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void parsearPaginaWeb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://www.expansion.com/mercados/cotizaciones/indices/ibex35_I.IB.html").get();
                    Elements links = doc.select("td");
                    cogerDatosDeLosLinks(links);
                    crearTablaCotizaciones(links);
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }
            }
        }).start();
    }

    private void cogerDatosDeLosLinks(Elements links) {

        String cadena;
        int contador = 0;
        for (Element link : links) {
            cadena = String.valueOf(link);
            if(cadena.contains("title=\"ACCIONA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"ACERINOX\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"ACS\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"AENA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"ALMIRALL\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"AMADEUS IT GROUP\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"ARCELORMITTAL\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"BANCO SABADELL\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"BANKINTER\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"BBVA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"CAIXABANK\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"CELLNEX TELECOM\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"CIE. AUTOMOTIVE\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"COLONIAL\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"ENAGAS\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"ENDESA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"FERROVIAL\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"FLUIDRA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"GRIFOLS\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"IAG\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"IBERDROLA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"INDITEX\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"INDRA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"MAPFRE\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"MELIÁ HOTELS\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"MERLIN PROP.\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"NATURGY\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"PHARMA MAR\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"RED ELÉCTRICA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"REPSOL\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"SANTANDER\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"SIEMENS GAMESA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"SOLARIA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"TELEFÓNICA\"")){
                listaNumeros.add(contador+1);
            }
            else if(cadena.contains("title=\"VISCOFAN\"")){
                listaNumeros.add(contador+1);
            }
            contador++;
        }
        String temporal = null;
        for(int i=0; i<listaNumeros.size(); i++) {
            String[] parts = String.valueOf(links.get(listaNumeros.get(i))).split("</td>");
            String[] parts2 = parts[0].split("<td>");
            for(int j=0; j<parts2[1].length(); j++){
                if(parts2[1].charAt(j) == ',')
                    temporal = changeCharInPosition(j, '.',parts2[1]);
            }
            if(temporal == null)
                temporal =parts2[1];
            listaCotizaciones.add(Float.parseFloat(temporal));
        }
    }

    private void crearTablaCotizaciones(Elements links) {

        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        DecimalFormat formato2 = new DecimalFormat("#.###");
        if(checkEmpty(Sentencias.TABLA_COTIZACIONES)) {
            ContentValues values = new ContentValues();
            values.put(Sentencias.CAMPO_ID, 0);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(0)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 1);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(1)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 2);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(2)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 3);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(3)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 4);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(4)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 5);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(5)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 6);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(6)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 7);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(7)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 8);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(8)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 9);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(9)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 10);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(10)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 11);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(11)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 12);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(12)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 13);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(13)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 14);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(14)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 15);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(15)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 16);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(16)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 17);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(17)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 18);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(18)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 19);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(19)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 20);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(20)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 21);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(21)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 22);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(22)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 23);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(23)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 24);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(24)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 25);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(25)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 26);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(26)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 27);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(27)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 28);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(28)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 29);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(29)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 30);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(30)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 31);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(31)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 32);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(32)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 33);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(33)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 34);
            values.put(Sentencias.CAMPO_COTIZACION, formato2.format(listaCotizaciones.get(34)));
            db.insert(Sentencias.TABLA_COTIZACIONES, Sentencias.CAMPO_ID, values);
            db.close();
        }

    }

    private void crearTablaDividendos() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        if(checkEmpty(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS)) {
            ContentValues values = new ContentValues();
            values.put(Sentencias.CAMPO_ID, 0);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 1.94);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 1.94);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 1);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.4);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.4);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 2);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 1.99);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 1.99);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 3);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 7.58);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 7.58);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 4);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.2);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.2);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 5);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.56);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.56);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 6);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.08);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.08);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 7);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.02);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.02);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 8);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.16);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.16);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 9);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.16);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.16);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 10);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.07);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.07);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 11);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.06);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.06);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 12);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.62);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.62);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 13);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.16);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.16);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 14);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 1.6);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 1.6);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 15);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 1.48);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 1.48);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 16);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.2);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.2);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 17);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.85);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.85);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 18);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.36);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.36);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 19);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.27);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.27);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 20);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.4);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.4);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 21);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.35);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.35);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 22);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.54);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.54);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 23);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.14);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.14);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 24);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.18);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.18);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 25);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.35);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.35);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 26);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 1.41);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 1.41);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 27);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.48);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.48);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 28);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 1.05);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 1.05);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 29);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.76);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.76);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 30);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.2);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.2);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 31);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.05);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.05);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 32);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.02);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.02);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 33);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 0.39);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 0.39);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 34);
            values.put(Sentencias.CAMPO_DIVIDENDO_NETO, 0.81 * 2.36);
            values.put(Sentencias.CAMPO_DIVIDENDO_BRUTO, 2.36);
            db.insert(Sentencias.TABLA_DIVIDENDOS_ESTIMADOS, Sentencias.CAMPO_ID, values);
            db.close();
        }
    }

    private void conexionBBDD() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        conexion.onCreate(conexion.getWritableDatabase());
    }

    private void crearTablaEmpresas() {

        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        if(checkEmpty(Sentencias.TABLA_EMPRESAS)) {
            ContentValues values = new ContentValues();
            values.put(Sentencias.CAMPO_ID, 0);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Acciona");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 1);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Acerinox");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 2);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "ACS");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 3);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Aena");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 4);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Almirall");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 5);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Amadeus");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 6);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Arcelormittal");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 7);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Banco Sabadell");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 8);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Bankinter");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 9);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "BBVA");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 10);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Caixabank");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 11);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Cellnex Telecom");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 12);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "CIE Automotive");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 13);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Colonial");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 14);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Enagas");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 15);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Endesa");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 16);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Ferrovial");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 17);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Fluidra");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 18);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Grifols");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 19);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "IAG");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 20);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Iberdrola");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 21);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Inditex");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 22);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Indra");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 23);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Mapfre");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 24);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Melia Hotels");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 25);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Merlin Properties");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 26);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Naturgy");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 27);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Pharma Mar");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 28);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Red Electrica");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 29);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Repsol");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 30);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Santander");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 31);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Siemens Gamesa");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 32);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Solaria");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 33);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Telefonica");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            values.put(Sentencias.CAMPO_ID, 34);
            values.put(Sentencias.CAMPO_NOMBRE_EMPRESA, "Viscofan");
            db.insert(Sentencias.TABLA_EMPRESAS, Sentencias.CAMPO_ID, values);
            db.close();
        }
    }

    public boolean checkEmpty(String tabla){
        int count = 0;

        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + tabla, null);

        try {
            if(cursor != null)
                if(cursor.getCount() > 0){
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                    //System.out.println("ESTE ES EL VALOR DE COUNT: " + count);
                }
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        if(count>0)
            return false;
        else
            return true;
    }

    private void permisos() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // Si aún no tiene el permiso concedido
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            } else{
                ActivityCompat.requestPermissions(this, PERMISOS, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, int[] grantResults){
        String mensaje = "";
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            mensaje = "Permiso concedido";
        }else{
            mensaje = "Permiso no concedido";
        }
        toast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        toast.show();
    }

    public String changeCharInPosition(int position, char ch, String str){
        char[] charArray = str.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), MenuActivity.class);
        startActivityForResult(intent, 0);
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}