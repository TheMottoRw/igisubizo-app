package com.example.bpradmin.igisubizo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class addnew extends Fragment{
    Synchronizer synchronizer;
    public addnew() {
        // Required empty public constructor
synchronizer=new Synchronizer(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addnew, container, false);
        return view;
    }
}

