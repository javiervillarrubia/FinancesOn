package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerarExcelComprasActivity extends AppCompatActivity {

    TextView etNombreArchivoExcel;
    String nombreFichero, nombreHojaExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_excel_compras);

        etNombreArchivoExcel = findViewById(R.id.etNombreArchivoExcel);

        Button btnExcel = findViewById(R.id.btnExcel);
        btnExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarNombres(etNombreArchivoExcel.getText().toString());
                try {
                    generarExcel();
                    abrirExcel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent (v.getContext(), ComprasActivity.class);
                intent.putExtra("mensaje", 1);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void abrirExcel() {

        File file = new File("/storage/emulated/0/Download/", nombreFichero);
        Intent actionViewIntent = new Intent(Intent.ACTION_VIEW);
        Uri fileURI = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID + ".provider", file);
        actionViewIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(actionViewIntent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe){
            for (ResolveInfo resolveInfo : activities) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, fileURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    private void comprobarNombres(String etNombreArchivoExcel) {
        if(etNombreArchivoExcel.isEmpty())
            nombreFichero = "FinancesOn_Compras.xls";
        else
            nombreFichero = etNombreArchivoExcel + ".xls";
    }

    private boolean generarExcel() throws IOException {

        boolean success = false;

        //New Workbook

        Workbook wb = new HSSFWorkbook();

        List<String> datos = new ArrayList<>();
        List<String> titulos = new ArrayList<>();

        long numFilas = calcularFilas();
        numFilas = Integer.parseInt(String.valueOf(numFilas));
        meterTitulos(titulos);
        meterDatos(datos);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("COMPRAS");
        // Generate column headings
        sheet1.setColumnWidth(0, (15 * 300));
        sheet1.setColumnWidth(1, (15 * 300));
        sheet1.setColumnWidth(2, (15 * 300));
        sheet1.setColumnWidth(3, (15 * 300));
        sheet1.setColumnWidth(4, (15 * 320));
        sheet1.setColumnWidth(5, (15 * 320));
        sheet1.setColumnWidth(6, (15 * 280));
        sheet1.setColumnWidth(7, (15 * 850));
        sheet1.setColumnWidth(8, (15 * 200));
        sheet1.setColumnWidth(9, (15 * 250));
        sheet1.setColumnWidth(10, (15 * 500));

        //Para usar varias columnas
        int start_col = 0; //para ser row 1
        int end_col = 0; // para ser row 1
        int start_cell = 0; // para ser A1
        int end_cell = 10;// para ser H1
        sheet1.addMergedRegion(new CellRangeAddress(start_col,end_col,start_cell,end_cell));

        Row row = sheet1.createRow(0);
        Cell cel = row.createCell(0);
        cel.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        cel.setCellValue(("COMPRAS"));

        //Generamos Titulos
        Row row2 = sheet1.createRow(1);
        int numeroCelda = 0;
        for (String cadena : titulos) {
            Cell cell = row2.createCell(numeroCelda++);
            cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
            cell.setCellValue(cadena);
        }

        //Metemos los datos
        Row row3;
        int z=0;
        int temp=1;
        for (int y=3; y<numFilas+3; y++) {
            row3 = sheet1.createRow(y);
            Cell cell = row3.createCell(0);
            cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
            cell.setCellValue(temp++);
            for(int i=0; i<11; i++) {
                if(i == 5 || i == 6){
                    cell = row3.createCell(i);
                    cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                    System.out.println("Esto es lo que metemos: " + datos.get(z));
                    String cadena = "$" + datos.get(z);
                    cell.setCellValue(cadena);
                    z++;
                }
                else {
                    cell = row3.createCell(i);
                    cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                    System.out.println("Esto es lo que metemos: " + datos.get(z));
                    cell.setCellValue(datos.get(z));
                    z++;
                }
            }
        }

        //Aquí está la ruta
        File file = new File("/storage/emulated/0/Download/", nombreFichero);
        System.out.println("ESTA ES LA RUTA: " + file);
        try (FileOutputStream os = new FileOutputStream(file)) {
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        }
        return success;
    }

    private long calcularFilas() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM COMPRAS", null);
        return cursor.getCount();
    }

    private void meterTitulos(List<String> titulos) {
        titulos.add("Id");
        titulos.add("Fecha de Operación");
        titulos.add("Tipo de Operación");
        titulos.add("Valor");
        titulos.add("Número de Acciones");
        titulos.add("Precio Compra/Venta");
        titulos.add("Total Operación");
        titulos.add("Balance Operación a día de hoy Incluyendo la Comisión");
        titulos.add("%");
        titulos.add("Comisión");
        titulos.add("Dividendo Anual Estimado (NETO)");
    }

    private void meterDatos(List<String> datos) {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM COMPRAS", null);
        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++){
            for(int y=0; y<cursor.getColumnCount(); y++){
                if(y==2){
                    datos.add("C");;
                    int empresa = cursor.getInt(cursor.getColumnIndex("empresa"));
                    datos.add(MisFunciones.meterValorEmpresa(empresa));
                }
                else
                    datos.add(cursor.getString(y));
            }
            cursor.moveToNext();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), ComprasActivity.class);
        startActivityForResult(intent, 0);
    }
}