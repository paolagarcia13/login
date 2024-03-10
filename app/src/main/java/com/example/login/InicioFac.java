package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InicioFac extends AppCompatActivity {
    Button btnTE,btnTS,btnRegCl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_fac);

        btnRegCl=findViewById(R.id.btnRegCl);
        btnRegCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioFac.this, RegistroCliente.class);
                startActivity(intent); // Agregar esta línea para iniciar la actividad
            }
        });
        btnTS=findViewById(R.id.btnTS);
        btnTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioFac.this, TicketSalida.class);
                startActivity(intent); // Agregar esta línea para iniciar la actividad
            }
        });
        btnTE=findViewById(R.id.btnTE);
        btnTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioFac.this, TicketEntrada.class);
                startActivity(intent);
            }
        });

    }
}