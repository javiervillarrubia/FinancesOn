package com.example.myapplication;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GenerarExcelCarteraActivity extends AppCompatActivity {

    TextView etNombreArchivoExcel;
    String nombreFichero, nombreHojaExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_excel_cartera);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        ComponentActivity requireActivity = new ComponentActivity();
        requireActivity.getOnBackPressedDispatcher().addCallback(this, callback);

        etNombreArchivoExcel = findViewById(R.id.etNombreArchivoExcel);

        Button btnExcel = findViewById(R.id.btnExcel);
        btnExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarNombres(etNombreArchivoExcel.getText().toString());
                try {
                    generarExcel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent (v.getContext(), CarteraActivity.class);
                intent.putExtra("mensaje", 1);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void comprobarNombres(String etNombreArchivoExcel) {
        if(etNombreArchivoExcel.isEmpty())
            nombreFichero = "FinancesOn_Cartera.xls";
        else
            nombreFichero = etNombreArchivoExcel + ".xls";
    }

    private boolean generarExcel() throws IOException {

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        List<String> datos = new ArrayList<>();
        List<String> datos22 = new ArrayList<>();
        List<String> titulos = new ArrayList<>();
        long numFilas = calcularFilas();

        //long numFilas = calcularFilas();
        //numFilas = Integer.parseInt(String.valueOf(numFilas));

        meterTitulos(titulos);
        meterDatos(datos, datos22);

        //New Sheet
        Sheet sheet2 = null;
        sheet2 = wb.createSheet("CARTERA");
        // Generate column headings
        sheet2.setColumnWidth(0, (15 * 450));
        sheet2.setColumnWidth(1, (15 * 100));
        sheet2.setColumnWidth(2, (15 * 200));
        sheet2.setColumnWidth(3, (15 * 300));
        sheet2.setColumnWidth(4, (15 * 200));
        sheet2.setColumnWidth(5, (15 * 280));
        sheet2.setColumnWidth(6, (15 * 300));
        sheet2.setColumnWidth(7, (15 * 350));
        sheet2.setColumnWidth(8, (15 * 350));
        sheet2.setColumnWidth(9, (15 * 200));
        sheet2.setColumnWidth(10, (15 * 500));
        sheet2.setColumnWidth(11, (15 * 750));
        sheet2.setColumnWidth(12, (15 * 750));
        sheet2.setColumnWidth(13, (15 * 350));
        sheet2.setColumnWidth(14, (15 * 750));
        sheet2.setColumnWidth(15, (15 * 750));
        sheet2.setColumnWidth(16, (15 * 750));
        sheet2.setColumnWidth(17, (15 * 800));
        sheet2.setColumnWidth(18, (15 * 500));
        sheet2.setColumnWidth(19, (15 * 600));
        sheet2.setColumnWidth(20, (15 * 500));
        sheet2.setColumnWidth(21, (15 * 600));

        //Para usar varias columnas
        int start_col2 = 0; //para ser row 1
        int end_col2 = 0; // para ser row 1
        int start_cell2 = 0; // para ser A1
        int end_cell2 = 21;// para ser H1
        sheet2.addMergedRegion(new CellRangeAddress(start_col2,end_col2,start_cell2,end_cell2));

        Row row_2 = sheet2.createRow(0);
        Cell cel_2 = row_2.createCell(0);
        cel_2.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        cel_2.setCellValue(("CARTERA"));

        //Generamos Titulos
        Row row2_2 = sheet2.createRow(1);
        int numeroCelda_2 = 0;
        for (String cadena : titulos) {
            Cell cell = row2_2.createCell(numeroCelda_2++);
            cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
            cell.setCellValue(cadena);
        }

        //Metemos los datos
        Row row3C;
        int c=0;
        for (int y=3; y<numFilas+4; y++) {
            if(y == numFilas+3){
                row3C = sheet2.createRow(y+1);
                int j=0;
                for (int i = 0; i < 22; i++) {
                    if (i == 2) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: 100");
                        cell.setCellValue("100");
                    }
                    else if (i == 4) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: TOTAL");
                        cell.setCellValue("TOTAL");
                    }
                    else if (i == 7) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("DOLLAR(" + datos22.get(j) + ", 2)");
                        j++;
                    }
                    else if (i == 8) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("ROUND((" + datos22.get(j) + "),2)");
                        j++;
                    }
                    else if (i == 9) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("ROUND((" + datos22.get(j) + "),2)");
                        j++;
                    }
                    else if (i == 10) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("ROUND((" + datos22.get(j) + "),2)");
                        j++;
                    }
                    else if (i == 13) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("DOLLAR(" + datos22.get(j) + ", 2)");
                        j++;
                    }
                    else if (i == 14) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("DOLLAR(" + datos22.get(j) + ", 2)");
                        j++;
                    }
                    else if (i == 15) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("ROUND((" + datos22.get(j) + "),2)");
                        j++;
                    }
                    else if (i == 19) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: DIVIDENDOS NETOS ESTIMADOS");
                        cell.setCellValue("DIVIDENDOS NETOS ESTIMADOS");
                    }
                    else if (i == 20) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: " + datos22.get(j));
                        cell.setCellFormula("ROUND((" + datos22.get(j) + "),2)");
                        j++;
                    }
                    else if (i == 21) {
                        Cell cell = row3C.createCell(i);
                        cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                        System.out.println("Esto es lo que metemos: 100");
                        cell.setCellValue("100");
                    }
                }
            }
            else {
                row3C = sheet2.createRow(y);
                for (int i = 0; i < 22; i++) {
                    if (i != 1) {
                        if(i == 7 || i == 11 || i == 12 || i == 13 || i == 14){
                            Cell cell = row3C.createCell(i);
                            cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                            System.out.println("Esto es lo que metemos: " + datos.get(c));
                            cell.setCellFormula("DOLLAR(" + datos.get(c) + ", 2)");
                            c++;
                        }
                        else {
                            Cell cell = row3C.createCell(i);
                            cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
                            System.out.println("Esto es lo que metemos: " + datos.get(c));
                            cell.setCellValue(datos.get(c));
                            c++;
                        }
                    }
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

    private void meterTitulos(List<String> titulos) {
        titulos.add("%DIV Sobre el Precio Actual");
        titulos.add("");
        titulos.add("%AC INV");
        titulos.add("Valor");
        titulos.add("Ticker");
        titulos.add("Nº de Acciones");
        titulos.add("Cotización Actual");
        titulos.add("Dinero Actual Disponible");
        titulos.add("Balance Sin Comisiones");
        titulos.add("Comisiones");
        titulos.add("Balance incluyendo comisiones");
        titulos.add("Precio Medio de Adquisición Sin Comisiones");
        titulos.add("Precio Medio de Adquisición Incluyendo Comisiones");
        titulos.add("Dinero Total Invertido");
        titulos.add("Dinero Total Invertido Incluyendo Comisiones");
        titulos.add("Balance (en porcentaje) Incluyendo Comisiones");
        titulos.add("Peso en la Cartera Invertido Con Comisiones");
        titulos.add("Peso en la Cartera Actual Disponible Con Comisiones");
        titulos.add("Balance en el Peso de la Cartera");
        titulos.add("Nombre en el Gráfico");
        titulos.add("Dividendo Estimado al Año");
        titulos.add("% en el Total de los Dividendos Recibidos");
    }

    private void meterDatos(List<String> datos2, List<String> datos22) {
        DecimalFormat formato2 = new DecimalFormat("#,##0.00");
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM CARTERA", null);
        cursor.moveToFirst();
        double dineroActualDisponible = 0;
        double balanceSinComisiones = 0;
        double comisiones = 0;
        double balanceIncluyendoComisiones = 0;
        double dineroTotalInvertido = 0;
        double dineroTotalInvertidoConComisiones = 0;
        double balanceTantoPorCientoConComisiones = 0;
        double dividendoEstimadoAnio = 0;
        for(int i=0; i<cursor.getCount(); i++){
            double dineroActualDisponibleBD = cursor.getDouble(cursor.getColumnIndex("dineroActualDisponible"));
            double balanceSinComisionesBD = cursor.getDouble(cursor.getColumnIndex("balanceSinComisiones"));
            double comisionesBD = cursor.getDouble(cursor.getColumnIndex("comisiones"));
            double balanceIncluyendoComisionesBD = cursor.getDouble(cursor.getColumnIndex("balanceIncluyendoComisiones"));
            double dineroTotalInvertidoBD = cursor.getDouble(cursor.getColumnIndex("dineroTotalInvertido"));
            double dineroTotalInvertidoConComisionesBD = cursor.getDouble(cursor.getColumnIndex("dineroTotalInvertidoConComisiones"));
            double dividendoEstimadoAnioBD = cursor.getDouble(cursor.getColumnIndex("dividendoEstimadoAnio"));
            dineroActualDisponible = dineroActualDisponible + dineroActualDisponibleBD;
            balanceSinComisiones = balanceSinComisiones + balanceSinComisionesBD;
            comisiones = comisiones + comisionesBD;
            balanceIncluyendoComisiones = balanceIncluyendoComisiones + balanceIncluyendoComisionesBD;
            dineroTotalInvertido = dineroTotalInvertido + dineroTotalInvertidoBD;
            dineroTotalInvertidoConComisiones = dineroTotalInvertidoConComisiones + dineroTotalInvertidoConComisionesBD;
            dividendoEstimadoAnio = dividendoEstimadoAnio + dividendoEstimadoAnioBD;
            for(int y=0; y<cursor.getColumnCount(); y++){
                if(y==2){
                    int idEmpresa = cursor.getInt(cursor.getColumnIndex("idEmpresa"));
                    datos2.add(MisFunciones.meterValorEmpresa(idEmpresa));
                }
                else
                    datos2.add(cursor.getString(y));
            }
            cursor.moveToNext();
        }
        datos22.add(Double.toString(dineroActualDisponible));
        datos22.add(Double.toString(balanceSinComisiones));
        datos22.add(Double.toString(comisiones));
        datos22.add(Double.toString(balanceIncluyendoComisiones));
        datos22.add(Double.toString(dineroTotalInvertido));
        datos22.add(Double.toString(dineroTotalInvertidoConComisiones));
        balanceTantoPorCientoConComisiones = (dineroActualDisponible - dineroTotalInvertidoConComisiones) / dineroTotalInvertidoConComisiones * 100;
        datos22.add(formato2.format(balanceTantoPorCientoConComisiones));
        datos22.add(Double.toString(dividendoEstimadoAnio));
    }

    private long calcularFilas() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper(this, "bbdd_compras", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM CARTERA", null);
        return cursor.getCount();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (getApplicationContext(), CarteraActivity.class);
        startActivityForResult(intent, 0);
    }
}