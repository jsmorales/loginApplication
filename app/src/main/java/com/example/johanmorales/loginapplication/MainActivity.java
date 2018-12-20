package com.example.johanmorales.loginapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.johanmorales.loginapplication.Models.Employee;
import com.example.johanmorales.loginapplication.Models.Respuesta;
import com.example.johanmorales.loginapplication.Models.Resultado;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.userNameTextView);

        //ArrayList<Hour> hoursListView = getIntent().getParcelableArrayListExtra("hours");
        Respuesta respuesta = (Respuesta) getIntent().getExtras().get("respuesta");


        if(respuesta != null){

            Log.d(TAG,"respuesta sigue: "+respuesta.toString());

            Toast.makeText(this, respuesta.getMessage(), Toast.LENGTH_LONG).show();

            Resultado resultado = respuesta.getResult();
            Employee empleado = resultado.getEmployee();

            //Log.d(TAG,"token: "+resultado.getToken());

            userNameTextView.setText(empleado.getFirstName()+" "+empleado.getLastName());
        }
    }
}
