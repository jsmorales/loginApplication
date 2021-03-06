package com.example.johanmorales.loginapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.johanmorales.loginapplication.Adapters.ServicesAdapter;
import com.example.johanmorales.loginapplication.Models.Resultado;
import com.example.johanmorales.loginapplication.Models.Servicio;
import com.example.johanmorales.loginapplication.Services.MyServiceSocketIO;
import com.example.johanmorales.loginapplication.utils.FormatDateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ServicesActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        ServiceConnection,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = ServicesActivity.class.getSimpleName();
    private static final int MY_SOCKET_TIMEOUT_MS = 20000;
    private static final int ID_NOT_MESSAGE = 234560;
    public static final String urlApiTestClean = "https://prmsai-backend-test.azurewebsites.net";
    public static final String urlApiClean = "https://sillaruedassai.azurewebsites.net";
    private static boolean activityVisible;

    private Socket socket;

    public Resultado resultado;

    public View servicesProgress;
    public View emptyTextView;
    public ListView servicesListView;
    public TextView counterTextView;
    public View formServices;
    public TextView loggerTextView;
    public ImageView backButtonTool;

    public Switch switchUrlSite;

    public SearchView servicesSearchView;

    public ArrayList<Servicio> serviceListView;

    public Intent socketServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        //setting the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_1);
        toolbar.setTitle("SAI - Servicios PRM");
        toolbar.setTitleMarginStart(155);
        setSupportActionBar(toolbar);
        //change title SupportActionBar
        //getSupportActionBar().setTitle("Servicios - SAI");

        //binding objects
        emptyTextView = findViewById(R.id.emptyTextView);
        servicesProgress = findViewById(R.id.services_progress);
        servicesListView = findViewById(R.id.servicesListView);
        counterTextView = findViewById(R.id.counterTextView);
        servicesSearchView = findViewById(R.id.servicesSearchView);
        servicesSearchView.setQueryHint("Buscar...");
        servicesSearchView.setOnQueryTextListener(this);
        formServices = findViewById(R.id.formServices);
        switchUrlSite = findViewById(R.id.switchUrlSite);
        loggerTextView = findViewById(R.id.loggerTextView);
        backButtonTool = findViewById(R.id.backButtonTool);

        //obtener el resultado del login
        resultado = (Resultado) getIntent().getExtras().get("resultado");

        //service intent
        socketServiceIntent = new Intent(this, MyServiceSocketIO.class);

        //-----------------------------------------------------------------------------------------
        Log.d(TAG, "El token: "+resultado.getToken());

        //switchUrlSite.setChecked(true);

        getServicesSwitch();

        switchUrlSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getServicesSwitch();
            }
        });

        backButtonTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //-----------------------------------------------------------------------------------------
    }

    public void getServicesSwitch(){

        String dateStart = FormatDateUtil.getActualDatePlusFive(" 00:00:00");
        String dateEnd = FormatDateUtil.getActualDatePlusFive(" 24:00:00");

        //Log.d(TAG, "Comienza en: "+dateStart+" termina en: "+dateEnd);

        String dates = "&airline=-&dateStart="+dateStart.replaceAll(" ","%20")+"&dateEnd="+dateEnd.replaceAll(" ","%20");

        String urlApiTest = "https://prmsai-backend-test.azurewebsites.net/api/services?token="+resultado.getToken()+dates;
        String urlApi = "https://sillaruedassai.azurewebsites.net/api/services?token="+resultado.getToken()+dates;

        //Log.d(TAG,urlApiTest);

        if(switchUrlSite.isChecked()){
            getServices(urlApi,dateStart,dateEnd);
            getConnectionSocket(urlApiClean);
        }else{
            getServices(urlApiTest,dateStart,dateEnd);
            getConnectionSocket(urlApiTestClean);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;

        ArrayList<Servicio> arrayListFiltered = new ArrayList<>();

        if( (serviceListView != null) && (text != "") ) {

            //adapter.filter(text);
            /**
             * if (filter.isMatched(object, text)) {
             *     filterList.add(object);
             *  }
             * */
            Log.d(TAG, "Buscando: " + text);

            for (Servicio service : serviceListView) {

                //Log.d(TAG, "Nombre: " + service.getPaxName());
                //Log.d(TAG, "Contiene: " + service.getPaxName().contains(text));

                if (service.getPaxName().contains(text) || service.getFlightId().contains(text) || service.getStatus().contains(text)) {
                    arrayListFiltered.add(service);
                } else {
                    continue;
                }
            }

            setAdapterList(arrayListFiltered);
        }

        return false;
    }

    public void getInitServiceSocket(final String url){

        //inicializar el servicio
        //Intent socketServiceIntent = new Intent(this, MyServiceSocketIO.class);

        socketServiceIntent.putExtra("url",url);
        socketServiceIntent.putExtra("resultado",resultado);

        //bindService(socketServiceIntent,this, Context.BIND_AUTO_CREATE);

        startService(socketServiceIntent);
    }

    public void getConnectionSocket(final String url){


        checkSocketStatus();

        try {

            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.transports = new String[] {"websocket"};

            socket = IO.socket(url, opts);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "Socket conectado a: "+url);

                    logerText("Socket conectado a: "+url);


                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Log.d(TAG, "Socket desconectado de: "+url);

                    logerText("Socket desconectado a: "+url);

                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "Error conectando a socket. ");

                    //logerText("Error conectando a socket. ");

                }
            }).on("service_created", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Servicio creado.");

                    logerText("[Servicio Creado]");

                }
            }).on("service_updated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Servicio actualizado.");

                    JSONObject obj = (JSONObject)args[0];

                    try {

                        logerText("[Servicio Actualizado] - "+obj.getString("paxName"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).on("service_assignation_updated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Servicio asignado.");

                    JSONObject obj = (JSONObject)args[0];

                    try {

                        logerText("[Servicio Asignado] - "+obj.getString("serviceId"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).on("service_deleted", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Servicio eliminado.");

                    JSONObject obj = (JSONObject)args[0];

                    try {

                        logerText("[Servicio Eliminado] - "+obj.getString("serviceId"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }/**/
    }

    public void logerText(final String message){

        //este metodo hace que retome el hilo principal para que se pueda
        //modificar la interfaz
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                loggerTextView.append(message+"\n");
            }
        });

    }

    public void notifyMessage(String title, String message){

        if(!isActivityVisible()) {

            Random rand = new Random();

            Intent notifyIntent = new Intent(this, this.getClass());
            // Set the Activity to start in a new, empty task
            //https://stackoverflow.com/questions/12408719/resume-activity-in-android
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //put the extras (android lg k10 2017)
            notifyIntent.putExtra("resultado", resultado);
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            //stackBuilder.addNextIntentWithParentStack(notifyIntent);

            // Create the PendingIntent
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            //startActivityIfNeeded(notifyIntent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "logginAppChannel")
                    .setSmallIcon(R.drawable.ic_menu_send)
                    .setContentTitle("SAI-Monitor - " + title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setVibrate(new long[]{NotificationCompat.DEFAULT_VIBRATE})
                    .setShowWhen(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true);

            //
            mBuilder.setContentIntent(notifyPendingIntent);


            //for android 8.0 o higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "logginAppChannel";
                String description = "loginapp";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("logginAppChannel", name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManagerCompat.notify(rand.nextInt(ID_NOT_MESSAGE) + 1, mBuilder.build());

        }
    }

    public void getServices(String urlApi, String dateStart, String dateEnd){

        servicesProgress.setVisibility(View.VISIBLE);
        formServices.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(ServicesActivity.this);

        JSONObject req = new JSONObject();

        try {
            req.put("token",resultado.getToken());
            req.put("dateStart",dateStart);
            req.put("dateEnd",dateEnd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlApi, req, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        servicesProgress.setVisibility(View.GONE);
                        formServices.setVisibility(View.VISIBLE);

                        JSONObject res = response;
                        Servicio servicio;

                        serviceListView = new ArrayList<>();

                        Log.d(TAG, "Respuesta services: " + res.toString());

                        try {

                            JSONArray result = res.getJSONArray("result");

                            Log.d(TAG, "En 0: "+result.get(0));

                            for(int i = 0; i < result.length(); i++){

                                JSONObject val = (JSONObject) result.get(i);

                                servicio = new Servicio();

                                servicio.setPaxName(val.getString("paxName"));
                                servicio.setFlightId(val.getString("flightId"));
                                servicio.setStartDate(val.getString("startDate"));
                                servicio.setAirline(val.getString("airline"));
                                servicio.setStatus(val.getString("status"));
                                servicio.setFlightDateTime(val.getString("flightDateTime"));
                                servicio.setId(val.getString("id"));

                                Log.d(TAG, servicio.getPaxName());

                                serviceListView.add(servicio);
                            }

                            setAdapterList(serviceListView);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                        Log.d(TAG, error.toString());

                        servicesProgress.setVisibility(View.GONE);

                        //despliegue del error en un toast
                        Toast.makeText(ServicesActivity.this,error.toString(),Toast.LENGTH_LONG).show();

                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    public void setAdapterList(ArrayList<Servicio> serviceList){

        ServicesAdapter servicesAdapter = new ServicesAdapter(serviceList,ServicesActivity.this, resultado.getToken(), this);

        servicesListView.setAdapter(servicesAdapter);
        servicesListView.setEmptyView(emptyTextView);

        counterTextView.setText(serviceList.size()+" registros.");

    }

    public void checkSocketStatus(){

        if(socket == null){
            Log.d(TAG, "Socket no inicializado!");
        }else{
            Log.d(TAG, "Socket ya ha iniciado!");
            socket.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //logerText("onDestroy");

        Log.d(TAG, "Ejecutando onDestroy");

        checkSocketStatus();

        stopService(socketServiceIntent);
    }

    //validates if the activity is visible

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public void initServiceSocketIO(){

        if(switchUrlSite.isChecked()){
            getInitServiceSocket(urlApiClean);
        }else {
            getInitServiceSocket(urlApiTestClean);
        }
    }

    public void activityResumed() {

        activityVisible = true;

        //initServiceSocketIO();
        Log.d(TAG, "Ejecutando activityResumed");
        stopService(socketServiceIntent);
    }

    public void activityPaused() {

        activityVisible = false;

        initServiceSocketIO();

    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityPaused();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        //MyServiceSocketIO.MyBinder binder = (MyServiceSocketIO.MyBinder) service;

        Log.d(TAG, "Service socket corriendo!!");

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        Log.d(TAG, "Service socket no esta corriendo!!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.servicesmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_refresh){

            getServicesSwitch();

        }else if(id == R.id.action_clear_log){

            loggerTextView.setText("");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

}
