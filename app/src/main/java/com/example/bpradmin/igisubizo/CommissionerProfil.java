package com.example.bpradmin.igisubizo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bpradmin on 10/24/2017.
 */
public class CommissionerProfil  extends ActionBarActivity {
    public TextView names,email, phone, address,uname,cate,nid;
    public ProgressDialog progressDialog;
    public Synchronizer synchronizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commissioner_profil);
        names = (TextView) findViewById(R.id.names);
        nid= (TextView) findViewById(R.id.tvNid);
        email= (TextView) findViewById(R.id.tvEmail);
        phone = (TextView) findViewById(R.id.tvPhone);
        uname = (TextView) findViewById(R.id.tvUname);
        cate = (TextView) findViewById(R.id.tvCate);
        address = (TextView) findViewById(R.id.officeaddr);
        progressDialog=new ProgressDialog(this);
        synchronizer=new Synchronizer(this);
        if (synchronizer.checkSessionsExistance()==false){
            startActivity(new Intent(CommissionerProfil.this,SearchLosts.class));
            finish();
        }
        loadCommissionerInfo();
    }
    public void loadCommissionerInfo() {
        Synchronizer synchronizer=new Synchronizer(this);
        if (synchronizer.checkConnectionState()==true){
            String uid=synchronizer.getSession();
            String url=synchronizer.getHost()+"/ajax/users.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/postoffices.php";
            String data="cate=loadbyid&id="+uid;
            progressDialog.setMessage(getString(R.string.loaddata));
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://"+url+"?"+data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            setContent(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            error.printStackTrace();
                            progressDialog.dismiss();
                            Toast toast = Toast.makeText(CommissionerProfil.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.servernotconnect),Toast.LENGTH_SHORT).show();
        }
    }

    public void setContent(JSONObject jsonObjects) {
        try {
            JSONObject jsonObject=jsonObjects.getJSONArray("user").getJSONObject(0);
            //   Toast.makeText(CommissionerProfil.this, "Data "+jsonObject.toString(), Toast.LENGTH_LONG).show();
            names.setText(names.getText() + ": " + jsonObject.getString("names"));
            email.setText(email.getText() + ": " + jsonObject.getString("email"));
            phone.setText(phone.getText() + ": " + jsonObject.getString("phone"));
            nid.setText(nid.getText() + ": " + jsonObject.getString("nid"));
            cate.setText(cate.getText() + ": " + jsonObject.getString("category"));
            address.setText(address.getText() + " " + jsonObject.get("address"));
            uname.setText(uname.getText() + " " + jsonObject.get("username"));
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void redirect() {
        String sh = getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (sh.equals("0")) {
            intent.setClass(CommissionerProfil.this, Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                intent.setClass(CommissionerProfil.this, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(CommissionerProfil.this, Commissioner.class);
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
            finish();
        }

        if (id == R.id.action_lock) {
            finish();
        }
        if(id==R.id.logout){
            synchronizer.logout();
            finish();
        }
        if (id==R.id.exit){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
