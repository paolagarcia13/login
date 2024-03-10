package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistroCliente extends AppCompatActivity {

    Button btnRegCl;
    EditText Eplaca,ETipo,ENombre,EApellido,ECel;
    ImageButton btnBuscar;
    BaseDatos dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cliente);

        btnRegCl = findViewById(R.id.btnRegCl);
        Eplaca = findViewById(R.id.Eplaca);
        ETipo = findViewById(R.id.ETipo);
        ENombre = findViewById(R.id.ENombre);
        EApellido = findViewById(R.id.EApellido);
        ECel = findViewById(R.id.ECel);
        btnBuscar = findViewById(R.id.btnBuscar);
        dbHelper = new BaseDatos(this);

        btnRegCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placa = Eplaca.getText().toString();
                String tipo = ETipo.getText().toString();
                String nombre = ENombre.getText().toString();
                String apellido = EApellido.getText().toString();
                String celular = ECel.getText().toString();

                try (BaseDatos dbHelper = new BaseDatos(RegistroCliente.this)) {
                    dbHelper.insertarCliente(placa, tipo, nombre, apellido, celular);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RegistroCliente.this, TicketEntrada.class);
                startActivity(intent);
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placa = Eplaca.getText().toString();
                obtenerInformacionCliente(placa);
            }
        });
    }

    public String obtenerInformacionCliente(String placa) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String informacionCliente = "";

        String consultaSQL = "SELECT * FROM clientes WHERE placa = ?";

        Cursor cursor = db.rawQuery(consultaSQL, new String[]{placa});

        if (cursor != null && cursor.moveToFirst()) {
            String tipo = cursor.getString(cursor.getColumnIndex("tipo"));
            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            String apellido = cursor.getString(cursor.getColumnIndex("apellido"));
            String celular = cursor.getString(cursor.getColumnIndex("celular"));

            informacionCliente = "\nNombre: " + nombre + "\nApellido: " + apellido + "\nTipo: " + tipo + "\nCelular: " + celular;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return informacionCliente;
    }

}
