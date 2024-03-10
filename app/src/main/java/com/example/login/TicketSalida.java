package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TicketSalida extends AppCompatActivity {

    EditText horaInS, fechaInS, EplacaS, NombreS, ApellidoS, ValorT;
    BaseDatos dbHelper;
    ImageButton buscarS, cerrarTS;
    Button btnITS;
    String horaEntradaTicket;
    String fechaEntradaTicket;

    int tarifaPorMinuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_salida);

        NombreS = findViewById(R.id.NombreS);
        ApellidoS = findViewById(R.id.ApellidoS);
        buscarS = findViewById(R.id.buscarS);
        dbHelper = new BaseDatos(this);
        btnITS = findViewById(R.id.btnITS);
        EplacaS = findViewById(R.id.EplacaS);
        fechaInS = findViewById(R.id.fechaInS);
        horaInS = findViewById(R.id.horaInS);
        ValorT = findViewById(R.id.ValorT);
        cerrarTS = findViewById(R.id.cerrarTS);

        SharedPreferences sharedPreferences = getSharedPreferences("datos_ticket", Context.MODE_PRIVATE);
        fechaEntradaTicket = sharedPreferences.getString("fechaEntrada", null);
        horaEntradaTicket = sharedPreferences.getString("horaEntrada", null);
        tarifaPorMinuto = sharedPreferences.getInt("tarifaPorMinuto", 0);

        cerrarTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicketSalida.this, InicioFac.class);
                startActivity(intent);
            }
        });

        buscarS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placa = EplacaS.getText().toString();
                obtenerInformacionCliente(placa);
                double valorTotal = calcularValorTotal();
                ValorT.setText(String.valueOf(valorTotal));
            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        String fechaActual = dateFormat.format(calendar.getTime());
        String horaActual = timeFormat.format(calendar.getTime());

        fechaInS.setText(fechaActual);
        horaInS.setText(horaActual);

        btnITS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double valorTotal = calcularValorTotal();
                generarPDF(valorTotal);
            }
        });
    }

    public String obtenerInformacionCliente(String placa) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String informacionCliente = "";

        String consultaSQL = "SELECT * FROM clientes WHERE placa = ?";

        try (Cursor cursor = db.rawQuery(consultaSQL, new String[]{placa})) {
            if (cursor != null && cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                String apellido = cursor.getString(cursor.getColumnIndex("apellido"));


                NombreS.setText(nombre);
                ApellidoS.setText(apellido);
                informacionCliente = "\nNombre: " + nombre + "\nApellido: " + apellido;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return informacionCliente;
    }


    public void generarPDF(double valorTotal) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String fechaActual = dateFormat.format(calendar.getTime());
        String horaActual = timeFormat.format(calendar.getTime());
        String fechaIngreso = fechaEntradaTicket;
        String horaIngreso = horaEntradaTicket;
        String placa = EplacaS.getText().toString();
        String nombre = NombreS.getText().toString();
        String apellido = ApellidoS.getText().toString();
        String tarifa = String.valueOf(tarifaPorMinuto); // Convierte la tarifa por minuto a String

        dbHelper.insertarTicketSalida(placa, fechaActual, horaActual, nombre, apellido, tarifa);


        File file = new File(getExternalFilesDir(null), "ticket_salida.pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Placa: " + placa));
            document.add(new Paragraph("Nombre: " + nombre));
            document.add(new Paragraph("Apellido: " + apellido));
            document.add(new Paragraph("Fecha de entrada: " + fechaIngreso));
            document.add(new Paragraph("Hora de entrada: " + horaIngreso));
            document.add(new Paragraph("Fecha de salida: " + fechaActual));
            document.add(new Paragraph("Hora de salida: " + horaActual));
            document.add(new Paragraph("Valor Total: " + valorTotal));

            document.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            onBackPressedToInicioFac();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onBackPressedToInicioFac() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TicketSalida.this, InicioFac.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private double calcularValorTotal() {
        Log.i("calcularValorTotal", "Fecha de entrada recibida: " + fechaEntradaTicket + " " + horaEntradaTicket + " " + tarifaPorMinuto);

        if (fechaEntradaTicket == null || horaEntradaTicket == null) {
            return 0.0;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String fechaActual = dateFormat.format(calendar.getTime());
        String horaActual = timeFormat.format(calendar.getTime());
        String fechaIngreso = fechaEntradaTicket;
        String horaIngreso = horaEntradaTicket;

        Log.i("calcularValorTotal", "Fecha de entrada recibida: " + fechaIngreso + " " + horaIngreso + " " + tarifaPorMinuto);

        try {
            Date fechaEntradaDate = dateFormat.parse(fechaIngreso);
            Date horaEntradaDate = timeFormat.parse(horaIngreso);
            Date fechaSalidaDate = dateFormat.parse(fechaActual);
            Date horaSalidaDate = timeFormat.parse(horaActual);

            long tiempoEntradaMillis = fechaEntradaDate.getTime() + horaEntradaDate.getTime();
            long tiempoSalidaMillis = fechaSalidaDate.getTime() + horaSalidaDate.getTime();
            long diferenciaTiempoMillis = tiempoSalidaMillis - tiempoEntradaMillis;

            double diferenciaMinutos = (double) diferenciaTiempoMillis / (1000 * 60);
            double valorTotal = diferenciaMinutos * tarifaPorMinuto;

            ValorT.setText(String.valueOf(valorTotal));

            return valorTotal;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0.0;
        }
    }



}
