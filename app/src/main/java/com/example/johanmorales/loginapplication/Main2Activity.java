package com.example.johanmorales.loginapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.johanmorales.loginapplication.Fragments.IncomeRecentsFragment;
import com.example.johanmorales.loginapplication.Models.Employee;
import com.example.johanmorales.loginapplication.Models.RecentLog;
import com.example.johanmorales.loginapplication.Models.Respuesta;
import com.example.johanmorales.loginapplication.Models.Resultado;
import com.example.johanmorales.loginapplication.utils.ConnectivityReceiver;
import com.example.johanmorales.loginapplication.utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = Main2Activity.class.getSimpleName();
    private static final int MY_SOCKET_TIMEOUT_MS = 20000;
    private static final String INCOME_RECENTS_FRAGMENT_TAG = "income_recents_fragment_tag";
    public TextView userNameTextViewToolBar;
    public TextView positionTextViewToolBar;
    public EditText socialNumberTextInput;
    public View consulta_progress;
    public View consultaForm;
    public View resultadoLayout;

    public Respuesta respuesta;
    public Resultado resultado;
    public Employee empleado;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("SAI - Monitor");
        toolbar.setNavigationContentDescription("SAI - Monitor");

        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkConnection();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        consulta_progress = findViewById(R.id.consulta_progress);
        consultaForm = findViewById(R.id.consultaForm);
        resultadoLayout = findViewById(R.id.resultadoLayout);

        View hView =  navigationView.getHeaderView(0);

        userNameTextViewToolBar = hView.findViewById(R.id.userNameTextViewToolBar);
        positionTextViewToolBar = hView.findViewById(R.id.positionTextViewToolBar);


        socialNumberTextInput = findViewById(R.id.socialNumberTextInput);

        respuesta = (Respuesta) getIntent().getExtras().get("respuesta");


        if(respuesta != null){

            Log.d(TAG,"respuesta sigue: "+respuesta.getMessage());

            //Toast.makeText(this, respuesta.getMessage(), Toast.LENGTH_LONG).show();

            resultado = respuesta.getResult();

            empleado = resultado.getEmployee();

            Log.d(TAG,"token: "+resultado.getToken());

            CharSequence name = empleado.getFirstName()+" "+empleado.getLastName();

            Log.d(TAG,"empleado: "+userNameTextViewToolBar.getText());

            userNameTextViewToolBar.setText(name);
            positionTextViewToolBar.setText(empleado.getPosition());
        }

        //-------------------------------------------------------------------------------

        Button consultaButton = findViewById(R.id.consultaButton);

        consultaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String socialNumber = socialNumberTextInput.getText().toString();

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(socialNumber)) {

                    socialNumberTextInput.setError(getString(R.string.error_field_required));
                    socialNumberTextInput.requestFocus();

                }else{

                    consultar(socialNumber);
                }

            }
        });


        //-------------------------------------------------------------------------------
    }

    public void checkConnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    public void showSnack(boolean isConnected){

        String message;
        int color;

        if(isConnected){
            message = "Conectado a internet!";
            color = Color.WHITE;
        }else{
            message = "Conexion a internet perdida!";
            color = Color.RED;
        }

        Snackbar snack = Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);
        View sbView = snack.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snack.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    public void consultar(String socialNumber){

        consulta_progress.setVisibility(View.VISIBLE);
        consultaForm.setVisibility(View.GONE);
        resultadoLayout.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(Main2Activity.this);

        String urlApi = "https://control-llegada-backend-test.azurewebsites.net/api/checkArriving";

        JSONObject req = new JSONObject();

        try {
            req.put("socialNumber",socialNumber);
            req.put("filterUbication","CASINO");
            req.put("token",resultado.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, urlApi, req, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        //executeResponseJsonApp(response);

                        consulta_progress.setVisibility(View.GONE);
                        consultaForm.setVisibility(View.VISIBLE);
                        resultadoLayout.setVisibility(View.VISIBLE);

                        socialNumberTextInput.setText("");
                        socialNumberTextInput.clearFocus();

                        JSONObject res = response;

                        Log.d(TAG, "Status de la respuesta: " + res.toString());

                        TextView nameVerificationTextView = findViewById(R.id.nameVerificationTextView);
                        TextView validationTextView = findViewById(R.id.validationTextView);
                        TextView detailTextView = findViewById(R.id.detailTextView);
                        TextView turnTextView = findViewById(R.id.turnTextView);

                        try {

                            if(res.getBoolean("success")) {

                                JSONObject result = response.getJSONObject("result");

                                nameVerificationTextView.setText(result.getString("name"));

                                validationTextView.setText(result.getBoolean("validation") ? "Puede Ingresar" : "No puede Ingresar");

                                detailTextView.setText(result.getString("detail"));

                                turnTextView.setText(result.getString("turn"));

                            }else{
                                Toast.makeText(Main2Activity.this, res.getString("message"), Toast.LENGTH_SHORT).show();

                                nameVerificationTextView.setText("-");

                                validationTextView.setText("-");

                                detailTextView.setText("-");

                                turnTextView.setText("-");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                        Log.d(TAG, error.toString());

                        consulta_progress.setVisibility(View.GONE);

                        //despliegue del error en un toast
                        Toast.makeText(Main2Activity.this,error.toString(),Toast.LENGTH_LONG).show();

                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void closeSession(){

        respuesta = null;

        Intent intentCloseSession = new Intent(this, LoginActivity.class);

        //se deberia tener una instancia de LoginActivity para poder dejar null la respuesta

        startActivity(intentCloseSession);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            closeSession();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        int id = item.getItemId();

        if (id == R.id.nav_services) {
            // Handle the action

            Intent servicesIntent = new Intent(Main2Activity.this, ServicesActivity.class);
            servicesIntent.putExtra("resultado", resultado);
            startActivity(servicesIntent);

        } else if (id == R.id.nav_recent_income) {

            Log.d(TAG,"Recientes en un fragment!");

            //call incomeRecentsFragment

            consultarLogsRecientes();
            /**
             *
            //fragment manager, gestiona los elementos fragment
            FragmentManager fragmentManager = getSupportFragmentManager(); //Support porque extiende de AppCompatibility
            //ahora se crea un fragment transaction para poner el fragment sobre la actividad que se quiere
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //se le da la instracciona al fragment masnager que se quiere iuniciar una trasaccion
            //va contenedor en este caso el layout que contiene el contenido principal, luego el fragment, y un tag en este caso cualquier variable tipo string que identifique el fragment
            IncomeRecentsFragment incomeRecentsFragment = new IncomeRecentsFragment();
            fragmentTransaction.add(R.id.contentMain, incomeRecentsFragment, INCOME_RECENTS_FRAGMENT_TAG);
            //se realiza la transacción
            fragmentTransaction.commit(); */

        } /*else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void consultarLogsRecientes(){

        consulta_progress.setVisibility(View.VISIBLE);
        consultaForm.setVisibility(View.GONE);
        resultadoLayout.setVisibility(View.GONE);

        final ArrayList<RecentLog> arrRecentLogs = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(Main2Activity.this);

        String urlApi = "https://control-llegada-backend-test.azurewebsites.net/api/recentLogs";

        JSONObject req = new JSONObject();

        try {
            req.put("token",resultado.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlApi, req, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        //executeResponseJsonApp(response);

                        consulta_progress.setVisibility(View.GONE);
                        consultaForm.setVisibility(View.VISIBLE);
                        resultadoLayout.setVisibility(View.VISIBLE);

                        JSONObject res = response;

                        Log.d(TAG, "Status de la respuesta recentLogs: " + res.toString());

                        try {

                            JSONArray resArray = res.getJSONArray("result");

                            for(Integer i = 0; i < resArray.length(); i++){

                                JSONObject logJson;

                                RecentLog recentLog = new RecentLog();

                                logJson = (JSONObject) resArray.get(i);

                                recentLog.setName(logJson.getString("name"));
                                recentLog.setArrivingControlLogId(logJson.getInt("arrivingControlLogId"));
                                recentLog.setArrivingTime(logJson.getString("arrivingTime"));
                                recentLog.setArrivingAgent(logJson.getString("arrivingAgent"));
                                recentLog.setAgentTurn(logJson.getString("agentTurn"));
                                recentLog.setAbleToEnter(logJson.getBoolean("isAbleToEnter"));
                                recentLog.setDetail(logJson.getString("detail"));

                                Log.d(TAG, recentLog.getName());

                                arrRecentLogs.add(recentLog);
                            }


                            Bundle bundle = new Bundle();

                            bundle.putParcelableArrayList("recent_logs",arrRecentLogs);

                            //fragment manager, gestiona los elementos fragment
                            FragmentManager fragmentManager = getSupportFragmentManager(); //Support porque extiende de AppCompatibility
                            //ahora se crea un fragment transaction para poner el fragment sobre la actividad que se quiere
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //se le da la instracciona al fragment masnager que se quiere iuniciar una trasaccion
                            //va contenedor en este caso el layout que contiene el contenido principal, luego el fragment, y un tag en este caso cualquier variable tipo string que identifique el fragment
                            IncomeRecentsFragment incomeRecentsFragment = new IncomeRecentsFragment();
                            //se setea los argumentos para el fragment en este caso un bundle con el array list de recientes
                            incomeRecentsFragment.setArguments(bundle);
                            //se añade la transaccion
                            fragmentTransaction.add(R.id.contentMain, incomeRecentsFragment, INCOME_RECENTS_FRAGMENT_TAG);
                            //se realiza la transacción
                            fragmentTransaction.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        /*TextView nameVerificationTextView = findViewById(R.id.nameVerificationTextView);
                        TextView validationTextView = findViewById(R.id.validationTextView);
                        TextView detailTextView = findViewById(R.id.detailTextView);
                        TextView turnTextView = findViewById(R.id.turnTextView);

                        try {

                            if(res.getBoolean("success")) {

                                JSONObject result = response.getJSONObject("result");

                                nameVerificationTextView.setText(result.getString("name"));

                                validationTextView.setText(result.getBoolean("validation") ? "Puede Ingresar" : "No puede Ingresar");

                                detailTextView.setText(result.getString("detail"));

                                turnTextView.setText(result.getString("turn"));

                            }else{
                                Toast.makeText(Main2Activity.this, res.getString("message"), Toast.LENGTH_SHORT).show();

                                nameVerificationTextView.setText("-");

                                validationTextView.setText("-");

                                detailTextView.setText("-");

                                turnTextView.setText("-");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                        Log.d(TAG, error.toString());

                        consulta_progress.setVisibility(View.GONE);

                        //despliegue del error en un toast
                        Toast.makeText(Main2Activity.this,error.toString(),Toast.LENGTH_LONG).show();

                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


}
