package com.example.johanmorales.loginapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.johanmorales.loginapplication.Models.Servicio;
import com.example.johanmorales.loginapplication.R;

import java.util.ArrayList;

public class ServicesAdapter extends BaseAdapter {

    ArrayList<Servicio> servicesArray;
    Context context;

    public ServicesAdapter(ArrayList<Servicio> servicesArray, Context context){
        this.servicesArray = servicesArray;
        this.context = context;
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

        serviceViewHolder serviceViewHolder;

        if(convertView == null){

            convertView = LayoutInflater.from(context).inflate(R.layout.service_list_item, parent,false);

            serviceViewHolder = new serviceViewHolder();

            //Si la vista es nula se acude a view holder
            serviceViewHolder.paxName = convertView.findViewById(R.id.paxNameTextView);
            serviceViewHolder.numVuelo = convertView.findViewById(R.id.numVueloTextView);
            serviceViewHolder.serviceDate = convertView.findViewById(R.id.serviceDateTextView);
            serviceViewHolder.airline = convertView.findViewById(R.id.airlineTextView);

            convertView.setTag(serviceViewHolder);

        }else{

            serviceViewHolder = (ServicesAdapter.serviceViewHolder) convertView.getTag();
        }

        //set values on the layout
        serviceViewHolder.paxName.setText(servicesArray.get(position).getPaxName());
        serviceViewHolder.numVuelo.setText(servicesArray.get(position).getFlightId());
        serviceViewHolder.serviceDate.setText(servicesArray.get(position).getStartDate());
        serviceViewHolder.airline.setText(servicesArray.get(position).getAirline());

        return convertView;
    }

    //holder pattern
    static class serviceViewHolder{

        TextView paxName;
        TextView numVuelo;
        TextView serviceDate;
        TextView airline;
    }
}
