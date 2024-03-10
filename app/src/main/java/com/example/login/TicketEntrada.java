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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TicketEntrada extends AppCompatActivity {

    Spinner spinnerTarifa;
    EditText horaInE, fechaInE, Eplaca, Nombre, Apellido;
    BaseDatos dbHelper;
    ImageButton buscar, cerrarTE;
    Button btnITE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_entrada);

        Nombre = findViewById(R.id.Nombre);
        Apellido = findViewById(R.id.Apellido);
        buscar = findViewById(R.id.buscar);
        Eplaca = findViewById(R.id.Eplaca);
        dbHelper = new BaseDatos(this);
        btnITE = findViewById(R.id.btnITE);
        horaInE = findViewById(R.id.horaInE);
        fechaInE = findViewById(R.id.fechaInE);
        cerrarTE = findViewById(R.id.cerrarTE);
        spinnerTarifa = findViewById(R.id.spinnerTarifa);

        cerrarTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicketEntrada.this, InicioFac.class);
                startActivity(intent);
            }
        });


        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placa = Eplaca.getText().toString();
                obtenerInformacionCliente(placa);

            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        String fechaActual = dateFormat.format(calendar.getTime());
        String horaActual = timeFormat.format(calendar.getTime());

        fechaInE.setText(fechaActual);
        horaInE.setText(horaActual);

        spinnerTarifa = findViewById(R.id.spinnerTarifa);
        String[] TPM = {"60", "30"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TPM);
        spinnerTarifa.setAdapter(adapter);

        btnITE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placa = Eplaca.getText().toString();
                String fechaIngreso = fechaInE.getText().toString();
                String horaIngreso = horaInE.getText().toString();
                String nombre = Nombre.getText().toString();
                String apellido = Apellido.getText().toString();
                String tarifaPorMinuto = spinnerTarifa.getSelectedItem().toString();

                try (BaseDatos dbHelper = new BaseDatos(TicketEntrada.this)) {
                    dbHelper.insertarTicketEntrada(placa, fechaIngreso, horaIngreso,nombre, apellido, tarifaPorMinuto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("fechahora", "fecha y hora: " + fechaIngreso + horaIngreso + tarifaPorMinuto);
                Log.i("TarifaPorMinuto", "Valor de tarifa por minuto: " + tarifaPorMinuto);


                SharedPreferences sharedPreferences = getSharedPreferences("datos_ticket", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fechaEntrada", fechaIngreso);
                editor.putString("horaEntrada", horaIngreso);
                editor.putInt("tarifaPorMinuto", Integer.parseInt(tarifaPorMinuto));
                editor.apply();
                Intent intent = new Intent(TicketEntrada.this, TicketSalida.class);
                startActivity(intent);

                generarPDF();
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

                Nombre.setText(nombre);
                Apellido.setText(apellido);

                informacionCliente = "\nNombre: " + nombre + "\nApellido: " + apellido;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return informacionCliente;
    }

    public void generarPDF() {
        String placa = Eplaca.getText().toString();
        String nombre = Nombre.getText().toString();
        String apellido = Apellido.getText().toString();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String fechaActual = dateFormat.format(calendar.getTime());
        String horaActual = timeFormat.format(calendar.getTime());

        File file = new File(getExternalFilesDir(null), "ticket_entrada.pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Placa: " + placa));
            document.add(new Paragraph("Nombre: " + nombre));
            document.add(new Paragraph("Apellido: " + apellido));
            document.add(new Paragraph("Fecha de ingreso: " + fechaActual));
            document.add(new Paragraph("Hora de ingreso: " + horaActual));

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

    // Método para establecer InicioFac como la actividad de destino cuando se presiona el botón de retroceso
    private void onBackPressedToInicioFac() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TicketEntrada.this, InicioFac.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para evitar que el usuario regrese al TicketEntrada
            }
        }, 2000); // Retrasa la navegación a InicioFac por 2 segundos (ajusta según sea necesario)
    }

}
