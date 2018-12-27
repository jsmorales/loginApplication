package com.example.johanmorales.loginapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.johanmorales.loginapplication.Models.Servicio;
import com.example.johanmorales.loginapplication.R;
import com.example.johanmorales.loginapplication.ServicesActivity;
import com.example.johanmorales.loginapplication.utils.FormatDateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServicesAdapter extends BaseAdapter {

    public static final String TAG = ServicesAdapter.class.getSimpleName();
    ArrayList<Servicio> servicesArray;
    Context context;
    String token;
    ServicesActivity servAct;

    public ServicesAdapter(ArrayList<Servicio> servicesArray, Context context, String token, ServicesActivity servAct){
        this.servicesArray = servicesArray;
        this.context = context;
        this.token = token;
        this.servAct = servAct;
    }

    @Override
    public int getCount() {
        if(servicesArray == null)
            return 0;
        return servicesArray.size();
    }

    @Override
    public Object getItem(int position) {
        return servicesArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Integer  pos = position;

        serviceViewHolder serviceViewHolder;

        if(convertView == null){

            convertView = LayoutInflater.from(context).inflate(R.layout.service_list_item, parent,false);

            serviceViewHolder = new serviceViewHolder();

            //Si la vista es nula se acude a view holder
            serviceViewHolder.paxName = convertView.findViewById(R.id.paxNameTextView);
            serviceViewHolder.numVuelo = convertView.findViewById(R.id.numVueloTextView);
            serviceViewHolder.serviceDate = convertView.findViewById(R.id.serviceDateTextView);
            serviceViewHolder.airline = convertView.findViewById(R.id.airlineTextView);
            serviceViewHolder.statusTextView = convertView.findViewById(R.id.statusTextView);
            serviceViewHolder.serviceFlightDateTextView = convertView.findViewById(R.id.serviceFlightDateTextView);

            convertView.setTag(serviceViewHolder);

        }else{

            serviceViewHolder = (ServicesAdapter.serviceViewHolder) convertView.getTag();
        }

        //set values on the layout
        serviceViewHolder.paxName.setText(servicesArray.get(position).getPaxName());
        serviceViewHolder.numVuelo.setText(servicesArray.get(position).getFlightId());
        serviceViewHolder.airline.setText(servicesArray.get(position).getAirline());
        serviceViewHolder.statusTextView.setText(servicesArray.get(position).getStatus());


        serviceViewHolder.serviceDate.setText(FormatDateUtil.getDateFormatted(servicesArray.get(position).getStartDate()));
        serviceViewHolder.serviceFlightDateTextView.setText(FormatDateUtil.getDateFormatted(servicesArray.get(position).getFlightDateTime()));

        //set click listener
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"Click a: "+servicesArray.get(pos).getPaxName());

            }
        });


        //set click listener on delete button

        Button btnDelete = convertView.findViewById(R.id.deleteServiceButton);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(context);
                builder.setTitle("Eliminar Servicio")
                        .setMessage("Eliminar servicio: "+servicesArray.get(pos).getPaxName()+"?")
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                Toast.makeText(context, "Intentando eliminar.", Toast.LENGTH_SHORT).show();

                                deleteService(servicesArray.get(pos).getId());
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_delete)
                        .show();

            }
        });

        return convertView;
    }

    //holder pattern
    static class serviceViewHolder{

        TextView paxName;
        TextView numVuelo;
        TextView serviceDate;
        TextView airline;
        TextView statusTextView;
        TextView serviceFlightDateTextView;
    }

    public void deleteService(String socialNumber){

        String urlApi = "https://prmsai-backend-test.azurewebsites.net/api/services/"+socialNumber+"?token="+token;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, urlApi, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        JSONObject res = response;

                        Log.d(TAG,res.toString());

                        //ServicesActivity serv = new ServicesActivity();
                        //serv.getServicesSwitch();

                        try {

                            if(res.getBoolean("success")) {

                                Toast.makeText(context, res.getString("message"), Toast.LENGTH_LONG).show();

                                servAct.getServicesSwitch();
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

                        //despliegue del error en un toast
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}
