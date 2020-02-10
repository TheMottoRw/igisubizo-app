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
public class UpdateCitizens extends ActionBarActivity {
    Synchronizer synchronizer;
    public Intent intent;
    public TextView citizenid;
    public EditText edtOwner,edtIdentification,edtPhone;
    public Button btnUpd;
    public Spinner spnType;
    public ProgressDialog progressDialog;
    public Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_citizens);
        citizenid=(TextView) findViewById(R.id.citid);
        edtOwner=(EditText) findViewById(R.id.edtOwner);
        edtIdentification=(EditText) findViewById(R.id.edtNid);
        edtPhone=(EditText) findViewById(R.id.edtPhone);
        btnUpd=(Button) findViewById(R.id.btnUpd);
        btnUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCitizens();
            }
        });
        synchronizer=new Synchronizer(this);
        validator=new Validator();
        progressDialog=new ProgressDialog(UpdateCitizens.this);
        progressDialog.setCancelable(false);
        httpRequestCitizenToUpdate();//request data to fill form of update
        if (synchronizer.checkSessionsExistance()==false){
            startActivity(new Intent("com.example.bpradmin.etrack.login"));
            finish();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
    }
    public void httpRequestCitizenToUpdate(){
        intent=getIntent();
        String citizenid=intent.getStringExtra("citid");
        String url=synchronizer.getHost()+"/ajax/citizens.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/citizens.php";
        String data="cate=loadbyid&id="+citizenid;
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
                        Toast toast = Toast.makeText(UpdateCitizens.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
    }
    public void fillUpdateForm(JSONObject objectDat){
        try {
            JSONObject objectData=objectDat.getJSONArray("citizens").getJSONObject(0);
            citizenid.setText(objectData.getString("cit_id"));
            edtOwner.setText(objectData.getString("cit_names"));
            edtIdentification.setText(objectData.getString("cit_nid"));
            edtPhone.setText(objectData.getString("cit_phone"));
            //    Toast.makeText(UpdateCitizens.this,"Array Size "+objectDat.getJSONObject("typ").getJSONArray("types").length(),Toast.LENGTH_LONG).show();
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
                dt+="=>"+arrTypes[i];
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

    public void updateCitizens(){
        Synchronizer synchronizer=new Synchronizer(UpdateCitizens.this);
        if (!edtOwner.getText().toString().trim().isEmpty()&&!edtIdentification.getText().toString().trim().isEmpty()){
        if(validator.nid(edtIdentification.getText().toString())){
            if(validator.phone(edtPhone.getText().toString())){
            synchronizer.updCitizens(citizenid.getText().toString(), edtOwner.getText().toString().replaceAll(" ", "%20"), edtIdentification.getText().toString().replaceAll(" ", "%20"), edtPhone.getText().toString().replaceAll(" ", "%20"));
            }else {
                Toast.makeText(UpdateCitizens.this,getString(R.string.invalidphone),Toast.LENGTH_SHORT).show();
            }
            }else {
            Toast.makeText(UpdateCitizens.this,getString(R.string.invalidnid),Toast.LENGTH_SHORT).show();
        }
        }else {
            Toast.makeText(UpdateCitizens.this,getString(R.string.nulls),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_commissioner, menu);
        return true;
    }
    public void redirect() {
        String sh = getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (sh.equals("0")) {
            intent.setClass(UpdateCitizens.this, Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                intent.setClass(UpdateCitizens.this, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(UpdateCitizens.this, Commissioner.class);
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
        if (id == R.id.Profil) {
            Intent intent = new Intent(UpdateCitizens.this,UpdateCitizens.class);
            startActivity(intent);finish();
        }

        if (id == R.id.action_lock) {
            Intent intent = new Intent("com.example.bpradmin.etrack.changepwd");
            startActivity(intent);finish();
        }
        if(id==R.id.logout){
            synchronizer.logout();
        }
        return super.onOptionsItemSelected(item);
    }
}
