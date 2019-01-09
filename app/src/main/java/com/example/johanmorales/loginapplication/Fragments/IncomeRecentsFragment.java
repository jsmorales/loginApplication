package com.example.johanmorales.loginapplication.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.johanmorales.loginapplication.Adapters.RecentsAdapter;
import com.example.johanmorales.loginapplication.Models.RecentLog;
import com.example.johanmorales.loginapplication.R;

import java.util.ArrayList;


public class IncomeRecentsFragment extends Fragment {

    private static final String TAG = IncomeRecentsFragment.class.getSimpleName();
    ArrayList<RecentLog> recents;
    RecyclerView recyclerView;

    public IncomeRecentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //para obtener los arguments
        Bundle bundle = getArguments();

        recents = bundle.getParcelableArrayList("recent_logs");

        Toast.makeText(getContext(), "Primer recent: "+recents.get(0).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //se instancia el layout en donde va a aparecer
        View view = inflater.inflate(R.layout.fragment_income_recents, container, false);

        //se instancia el recyclerView en donde se va a mostrar la lista
        recyclerView = view.findViewById(R.id.recentListRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecentsAdapter recentsAdapter = new RecentsAdapter(recents,getContext());

        recyclerView.setAdapter(recentsAdapter);

        return view;
    }

}
