package com.example.bpradmin.igisubizo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bpradmin on 11/2/2017.
 */
public class Queues extends Fragment {
    Synchronizer synchronizer;
    public EditText edtOwner,edtPhone,edtIdentifier;
    public Spinner spnType;
    public Button btnRegQueue;
Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_index, container, false);
        context=container.getContext();
        /*btnRegOnQueue=(Button) view.findViewById(R.id.btnRegOnQueue);
        btnRegOnQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((SearchLosts) getActivity(), Queues.class));
            }
        });*/
        return view;
    }

    public void addRegQueue(){
        if (edtOwner.getText().toString().equals("") || edtIdentifier.getText().toString().equals("") || edtPhone.getText().toString().equals("")){
            Toast.makeText(context, getString(R.string.nulls), Toast.LENGTH_LONG).show();
        }else {
                    synchronizer.addQueue(edtOwner.getText().toString().replaceAll(" ", "%20"), edtIdentifier.getText().toString().replaceAll(" ", "%20"), spnType.getSelectedItem().toString().replaceAll(" ", "%20"));
            edtOwner.setText("");edtIdentifier.setText("");edtPhone.setText("");
        }
    }
    public void setTypeSpinner(){
        Synchronizer synchronizer=new Synchronizer(context);
       String url = synchronizer.getHost()+"/ajax/types.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/types.php";
            String data = "cate=load";
            RequestQueue queue = Volley.newRequestQueue(context);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setTypesView(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            // TODO Auto-generated method stub
                            Toast toast = Toast.makeText(context,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
    }
    public void setTypesView(JSONObject obj){//Fill Up Loaded Type to Spinner
        try {
            JSONArray arr = obj.getJSONArray("types");
            String[] arrTypes=new String[arr.length()];
            for(int i=0;i<arr.length();i++){
                arrTypes[i]=arr.getJSONObject(i).getString("doctype");
            }
            ArrayAdapter arrayAdapter=new ArrayAdapter(context,R.layout.select_dialog_item_material,arrTypes);
            spnType.setAdapter(arrayAdapter);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
  //      getMenuInflater().inflate(R.menu.menu_enduser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    */
}