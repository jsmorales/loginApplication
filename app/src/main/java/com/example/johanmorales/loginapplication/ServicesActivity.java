package com.example.johanmorales.loginapplication;

import android.app.Service;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServicesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = ServicesActivity.class.getSimpleName();
    private static final int MY_SOCKET_TIMEOUT_MS = 20000;

    public Resultado resultado;

    public View servicesProgress;
    public View emptyTextView;
    public ListView servicesListView;
    public TextView counterTextView;
    public ImageView refreshDataImageView;
    public View formServices;

    public Switch switchUrlSite;

    public SearchView servicesSearchView;

    public ArrayList<Servicio> serviceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        emptyTextView = findViewById(R.id.emptyTextView);

        servicesProgress = findViewById(R.id.services_progress);

        servicesListView = findViewById(R.id.servicesListView);

        counterTextView = findViewById(R.id.counterTextView);

        refreshDataImageView = findViewById(R.id.refreshDataImageView);

        servicesSearchView = findViewById(R.id.servicesSearchView);
        servicesSearchView.setQueryHint("Buscar...");

        servicesSearchView.setOnQueryTextListener(this);

        formServices = findViewById(R.id.formServices);

        switchUrlSite = findViewById(R.id.switchUrlSite);

        //CharSequence query = servicesSearchView.getQuery();

        //obtener el resultado del login
        resultado = (Resultado) getIntent().getExtras().get("resultado");

        Log.d(TAG, "El token: "+resultado.getToken());


        getServicesSwitch();

        refreshDataImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getServicesSwitch();
            }
        });

        switchUrlSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getServicesSwitch();
            }
        });
    }

    public void getServicesSwitch(){

        String urlApiTest = "https://prmsai-backend-test.azurewebsites.net/api/services?token="+resultado.getToken();
        String urlApi = "https://sillaruedassai.azurewebsites.net/api/services?token="+resultado.getToken();

        if(switchUrlSite.isChecked()){
            getServices(urlApi);
        }else{
            getServices(urlApiTest);
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

    public void getServices(String urlApi){

        servicesProgress.setVisibility(View.VISIBLE);
        formServices.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(ServicesActivity.this);

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

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("serviceList",serviceListView);
        outState.putString("pruebaSaved", "Prueba de saved state");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if( (savedInstanceState == null) ) {

            getServicesSwitch();

        }else{

            Log.d(TAG, "List services array: esta en savedInstanceState");

            Log.d(TAG,savedInstanceState.getString("pruebaSaved"));

            Log.d(TAG,savedInstanceState.getParcelableArrayList("serviceList").toString());

            setAdapterList(savedInstanceState.<Servicio>getParcelableArrayList("serviceList"));
        }
    }*/
}
