package com.example.bpradmin.igisubizo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
 * Created by bpradmin on 10/22/2017.
 */
public class UpdateQueue extends ActionBarActivity {
    Synchronizer synchronizer;
    public Intent intent;
    public TextView queueid;
    public EditText edtOwner,edtIdentification;
    public Button btnUpd;
    public Spinner spnType;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_queue);
        queueid=(TextView) findViewById(R.id.queueid);
        edtOwner=(EditText) findViewById(R.id.edtOwner);
        edtIdentification=(EditText) findViewById(R.id.edtIdentification);
        spnType=(Spinner) findViewById(R.id.updautoType);
        btnUpd=(Button) findViewById(R.id.btnUpd);
        btnUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQueues();
            }
        });
        synchronizer=new Synchronizer(this);
        progressDialog=new ProgressDialog(UpdateQueue.this);
        httpRequestQueueToUpdate();//request data to fill form of update
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
    }
    public void httpRequestQueueToUpdate(){
        intent=getIntent();
        String queueid=intent.getStringExtra("queueid");
        String url=synchronizer.getHost()+"/ajax/queue.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/queues.php";
        String data="cate=loadbyid&id="+queueid;
        progressDialog.setMessage(getString(R.string.loaddata));
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://"+url+"?"+data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        fillUpdateForm(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast toast = Toast.makeText(UpdateQueue.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
    }
    public void fillUpdateForm(JSONObject objectDat){
        try {
            JSONObject objectData=objectDat.getJSONArray("queues").getJSONObject(0);
            queueid.setText(objectData.getString("queue_id"));
            edtOwner.setText(objectData.getString("cit_nid"));
            edtIdentification.setText(objectData.getString("queue_identifier"));
            //    Toast.makeText(UpdateQueue.this,"Array Size "+objectDat.getJSONObject("typ").getJSONArray("types").length(),Toast.LENGTH_LONG).show();
            setTypesView(objectDat,objectData.getString("doctype"));
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    public void setTypesView(JSONObject obj,String doctype){//Fill Up Loaded Type to Spinner
        try {
            JSONArray arr = obj.getJSONArray("types");
            String[] arrTypes=new String[arr.length()];
            String dt="";
            for(int i=0;i<arr.length();i++){
                arrTypes[i]=arr.getJSONObject(i).getString("doctype");
            }
            // Toast.makeText(this,"Array "+dt,Toast.LENGTH_LONG).show();
            ArrayAdapter arrayAdapter=new ArrayAdapter(this,R.layout.select_dialog_item_material,arrTypes);
            spnType.setAdapter(arrayAdapter);
            int i=0;
            while (i<arrTypes.length) {
                if (arrTypes[i].equals(doctype)) {
                    spnType.setSelection(i);
                }
                i++;
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void updateQueues(){
        Synchronizer synchronizer=new Synchronizer(UpdateQueue.this);
        if (!edtOwner.getText().toString().isEmpty()&&!edtIdentification.getText().toString().isEmpty()){
            synchronizer.updQueues(queueid.getText().toString(), edtOwner.getText().toString().replaceAll(" ", "%20"), edtIdentification.getText().toString().replaceAll(" ", "%20"), spnType.getSelectedItem().toString().replaceAll(" ", "%20"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*if(synchronizer.getSession().equals("0")) {
            getMenuInflater().inflate(R.menu.menu_enduser, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_commissioner,menu);
        }
        */
        return true;
    }
    public void redirect() {
        String sh = getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (sh.equals("0")) {
            intent.setClass(UpdateQueue.this, Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                intent.setClass(UpdateQueue.this, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(UpdateQueue.this, Commissioner.class);
            }
            startActivity(intent);
        }
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
        if(synchronizer.getSession().equals("0")) {
            if (id == R.id.Payment) {
                String cate = synchronizer.getSessionCategory();
                Intent intent = new Intent(UpdateQueue.this, EnduserPayment.class);
                startActivity(intent);
                finish();
            }
        }else {
            if (id == R.id.action_lock) {
                Intent intent = new Intent(UpdateQueue.this, changepwd.class);
                startActivity(intent);
                finish();
            }
            if (id == R.id.Profil) {
                Intent intent = new Intent(UpdateQueue.this, PostProfil.class);
                startActivity(intent);
                finish();
            }
            if (id == R.id.logout) {
                synchronizer.logout();
                finish();
            }

            if (id == R.id.action_lock) {
                Intent intent = new Intent("com.example.bpradmin.etrack.changepwd");
                startActivity(intent);
                finish();
            }
            if (id == R.id.logout) {
                synchronizer.logout();
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}