package com.example.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class BaseDatos extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "parkingapp";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USUARIOS = "CREATE TABLE usuarios (id INTEGER PRIMARY KEY,cedula TEXT, nombre TEXT, apellido TEXT, correo TEXT,celular TEXT, rol TEXT)";
    private static final String CREATE_TABLE_CLIENTES = "CREATE TABLE clientes (id INTEGER PRIMARY KEY,placa TEXT, tipo TEXT, nombre TEXT, apellido TEXT,celular TEXT)";
    private static final String CREATE_TABLE_TICKETENTRADA = "CREATE TABLE tickE (id INTEGER PRIMARY KEY, placa TEXT, fechaIngreso TEXT, horaIngreso TEXT, tarifaPorMinuto TEXT, nombre TEXT, apellido TEXT)";
    private static final String CREATE_TABLE_TICKETSALIDA = "CREATE TABLE tickS (id INTEGER PRIMARY KEY, placa TEXT, fechaActual TEXT, horaActual TEXT, tarifaPorMinuto TEXT, nombre TEXT, apellido TEXT)";

    private static final String CREATE_TABLE_ADMINISTRADOR = "CREATE TABLE administrador (id INTEGER PRIMARY KEY,cedula INTEGER, nombre TEXT, apellido TEXT, correo TEXT,celular TEXT, rol TEXT)";
    private static final String CREATE_TABLE_ROL = "CREATE TABLE rol (id INTEGER PRIMARY KEY, admin TEXT, user TEXT)";

    public BaseDatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(CREATE_TABLE_CLIENTES);
        db.execSQL(CREATE_TABLE_TICKETENTRADA);
        db.execSQL(CREATE_TABLE_TICKETSALIDA);
        db.execSQL(CREATE_TABLE_ADMINISTRADOR);
        db.execSQL(CREATE_TABLE_ROL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertarAdministrador(String cedula, String nombre, String apellido, String correo, String celular,String rol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cedula", cedula);
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("correo", correo);
        values.put("celular", celular);
        values.put("rol", rol);
        db.insert("usuarios", null, values);
        db.close();
    }
    public void insertarUsuario(String cedula, String nombre, String apellido, String correo, String celular, String rol) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cedula", cedula);
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("correo", correo);
        values.put("celular", celular);
        values.put("rol", rol);
        db.insert("usuarios", null, values);
        db.close();
    }
    public void insertarCliente(String placa, String tipo, String nombre, String apellido, String celular) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("placa", placa);
        values.put("tipo", tipo);
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("celular", celular);
        db.insert("clientes", null, values);
        db.close();
    }
    public void insertarTicketEntrada(String placa, String fechaIngreso, String horaIngreso,String nombre, String apellido, String tarifaPorMinuto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("placa", placa);
        values.put("fechaIngreso", fechaIngreso);
        values.put("horaIngreso", horaIngreso);
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("tarifaPorMinuto", tarifaPorMinuto);
        db.insert("tickE", null, values);
        db.close();
    }
    public void insertarTicketSalida(String placa, String fechaActual, String horaActual,String nombre, String apellido, String tarifaPorMinuto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("placa", placa);
        values.put("fechaActual", fechaActual);
        values.put("horaActual", horaActual);
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("tarifaPorMinuto", tarifaPorMinuto);
        db.insert("tickS", null, values);
        db.close();
    }
}

