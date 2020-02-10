package com.example.bpradmin.igisubizo;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class index extends Fragment {
    public Button btnRegOnQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_index, container, false);
        /*btnRegOnQueue=(Button) view.findViewById(R.id.btnRegOnQueue);
        btnRegOnQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((SearchLosts) getActivity(), Queues.class));
            }
        });*/
        return view;
    }

}
