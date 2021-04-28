package com.example.myapplication;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Sentencias.CREAR_TABLA_COMPRA);
        db.execSQL(Sentencias.CREAR_TABLA_EMPRESAS);
        db.execSQL(Sentencias.CREAR_TABLA_DIVIDENDOS_ESTIMADOS);
        db.execSQL(Sentencias.CREAR_TABLA_DIVIDENDOS_RECIBIDOS);
        db.execSQL(Sentencias.CREAR_TABLA_COTIZACIONES);
        db.execSQL(Sentencias.CREAR_TABLA_CARTERA);
        db.execSQL(Sentencias.CREAR_TABLA_TOTALES_CARTERA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
