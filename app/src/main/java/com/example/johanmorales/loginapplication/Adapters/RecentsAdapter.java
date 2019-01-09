package com.example.johanmorales.loginapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.johanmorales.loginapplication.Models.RecentLog;
import com.example.johanmorales.loginapplication.R;
import com.example.johanmorales.loginapplication.utils.FormatDateUtil;

import java.util.ArrayList;

//5. se extiende la clase de --> extends RecyclerView.Adapter<Nombredelaclase.MyViewHolder>

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.MyViewHolder> {

    private static final String TAG = RecentsAdapter.class.getSimpleName();
    //7.se crea un ArrayList de tipo de objeto que se le va a pasar a la lista y una variable tipo Context
    ArrayList<RecentLog> recentLogsArrayList;
    Context context;

    //9. se crea el constructor del adapter
    public RecentsAdapter(ArrayList<RecentLog> recentLogsArrayList, Context context){

        this.recentLogsArrayList = recentLogsArrayList;
        this.context = context;
    }

    //6. se implementan los metodos con alt + enter
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_income_recents_item, viewGroup, false);

        MyViewHolder recentsViewHolder = new MyViewHolder(view);

        return recentsViewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {



        //10. se hace el bind de datos con la layout elemento por elemento
        RecentLog recentObjt = recentLogsArrayList.get(i); //se obtiene el objeto del ArrayList en esa posicion

        Log.d(TAG,String.valueOf(i));
        Log.d(TAG,recentObjt.getName());
        Log.d(TAG,String.valueOf(recentObjt.isAbleToEnter()));

        myViewHolder.nameTextView.setText(recentObjt.getName()); //se setea el textView segun el valor
        myViewHolder.arrivingAgentTextView.setText(recentObjt.getArrivingAgent());
        myViewHolder.arrivingTimeTextView.setText(FormatDateUtil.getDateFormatted(recentObjt.getArrivingTime()));
        myViewHolder.agentTurnTextView.setText(FormatDateUtil.getDateFormatted(recentObjt.getAgentTurn()));
        myViewHolder.detailTextView.setText(recentObjt.getDetail().equals("") ? "-" : recentObjt.getDetail());

        myViewHolder.recentLogContentItem.setBackgroundResource(recentObjt.isAbleToEnter() ? R.color.recentLogSuccess : R.color.recentLogFail);
    }

    @Override
    public int getItemCount() {
        //8. se regresa el size del ArrayList
        return recentLogsArrayList.size();
    }

    //1. primero se crea la clase viewHolder que extiende de RecyclerView.ViewHolder
    static public class MyViewHolder extends RecyclerView.ViewHolder{

        //3. se añaden los elementos del layout item
        public TextView nameTextView;
        public TextView arrivingAgentTextView;
        public TextView agentTurnTextView;
        public TextView arrivingTimeTextView;
        public TextView detailTextView;
        public LinearLayout recentLogContentItem;

        //2. se autocompleta el constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //4. se hace el bind de los elementos del layout item desde la view itemView
            nameTextView = itemView.findViewById(R.id.nameTextView);
            arrivingAgentTextView = itemView.findViewById(R.id.arrivingAgentTextView);
            agentTurnTextView = itemView.findViewById(R.id.agentTurnTextView);
            arrivingTimeTextView = itemView.findViewById(R.id.arrivingTimeTextView);
            detailTextView = itemView.findViewById(R.id.detailTextView);
            recentLogContentItem = itemView.findViewById(R.id.recentLogContentItem);
        }
    }
}
