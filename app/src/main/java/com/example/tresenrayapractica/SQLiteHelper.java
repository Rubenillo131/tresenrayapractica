package com.example.tresenrayapractica;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Registros.db";

    public static final String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS jugadores(_id integer PRIMARY KEY, jugador1 text,jugador2 text, dificultad text, resultado text);";
    public static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS resultados(_id integer PRIMARY KEY, usuario text,partidas text, dificultad integer, puntos integer);";

    public static final String DELETE_TABLE1 = "DROP TABLE IF EXISTS jugadores";
    public static final String DELETE_TABLE2 = "DROP TABLE IF EXISTS resultados";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE1);
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE1);
        db.execSQL(DELETE_TABLE2);
    }
}
