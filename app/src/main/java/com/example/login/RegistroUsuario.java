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
import android.widget.Spinner;
import android.widget.TextView;

public class RegistroUsuario extends AppCompatActivity {

    Button btnRegUsu, btnReAdm;
    EditText Ecedula, Enombre, Eapellido, Ecorreo, Ecelular;
    ImageButton bntBuscar;
    TextView textResultado;
    BaseDatos dbHelper;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        btnRegUsu = findViewById(R.id.btnRegUsu);
        btnReAdm = findViewById(R.id.btnReAdm);
        Ecedula = findViewById(R.id.Ecedula);
        Enombre = findViewById(R.id.Enombre);
        Eapellido = findViewById(R.id.Eapellido);
        Ecorreo = findViewById(R.id.Ecorreo);
        Ecelular = findViewById(R.id.Ecelular);
        bntBuscar = findViewById(R.id.btnBuscar);
        dbHelper = new BaseDatos(this);

        btnRegUsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = Ecedula.getText().toString();
                String nombre = Enombre.getText().toString();
                String apellido = Eapellido.getText().toString();
                String correo = Ecorreo.getText().toString();
                String rol = spinner.toString();
                String celular = Ecelular.toString();
                try (BaseDatos dbHelper = new BaseDatos(RegistroUsuario.this)) {
                    dbHelper.insertarUsuario(cedula, nombre, apellido, correo, celular, rol);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RegistroUsuario.this, AdminInicio.class);
                startActivity(intent);
            }
        });
        btnReAdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = Ecedula.getText().toString();
                String nombre = Enombre.getText().toString();
                String apellido = Eapellido.getText().toString();
                String correo = Ecorreo.getText().toString();
                String rol = spinner.toString();
                String celular = Ecelular.toString();
                try (BaseDatos dbHelper = new BaseDatos(RegistroUsuario.this)) {
                    dbHelper.insertarAdministrador(cedula, nombre, apellido, correo, celular,rol);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RegistroUsuario.this, AdminInicio.class);
                startActivity(intent);
            }
        });
        bntBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = Ecedula.getText().toString();
                 obtenerInformacionUsuario(cedula);
            }
        });
    }

    public String obtenerInformacionUsuario(String cedula) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String informacionUsuario = "";

        String consultaSQL = "SELECT * FROM clientes WHERE cedula = ?";

        Cursor cursor = db.rawQuery(consultaSQL, new String[]{cedula});

        if (cursor != null && cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            String apellido = cursor.getString(cursor.getColumnIndex("apellido"));

            Enombre.setText(nombre);
            Eapellido.setText(apellido);

            informacionUsuario = "\nNombre: " + nombre + "\nApellido: " + apellido;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return informacionUsuario;
    }

}
