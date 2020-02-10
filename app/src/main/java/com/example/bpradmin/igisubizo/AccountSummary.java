package com.example.bpradmin.igisubizo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bpradmin on 10/24/2017.
 */
    public class AccountSummary  extends ActionBarActivity {
    public ListView lstSummary;
    public ProgressDialog progressDialog;
    public Button btnDownload;
    public Synchronizer synchronizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        synchronizer=new Synchronizer(this);
        if (synchronizer.checkSessionsExistance()==false){
            startActivity(new Intent(AccountSummary.this,SearchLosts.class));
            finish();
        }
        lstSummary=(ListView) findViewById(R.id.lstSummary);
        loadUserInfo();
    }
    public void loadUserInfo() {
        String url="";
        if (synchronizer.checkConnectionState()==true){
            String uid=synchronizer.getSession();
            if(synchronizer.getSessionCategory().equals("commissioner")) {
                url = synchronizer.getHost() + "/ajax/users.php";
            }else if(synchronizer.getSessionCategory().equals("postoffice")){
                url=synchronizer.getHost()+"/ajax/postoffices.php";
            }
            //String url = "192.168.173.1:12/RUT/etracking/ajax/users.php";
            String data="cate=loadbyid&id="+uid;
            progressDialog=new ProgressDialog(AccountSummary.this);
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
                            Toast toast = Toast.makeText(AccountSummary.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
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
            //   Toast.makeText(AccountSummary.this, "Data "+jsonObject.toString(), Toast.LENGTH_LONG).show();
            ArrayList arrayList=new ArrayList();
            if(synchronizer.getSessionCategory().equals("commissioner")) {
                JSONObject jsonObject=jsonObjects.getJSONArray("user").getJSONObject(0);
                arrayList.add(getString(R.string.registeredon)+" " + jsonObject.getString("usr_regdate").substring(0, 10));
                arrayList.add(getString(R.string.citizens)+" " + jsonObject.getString("usr_registered")+" "+(Integer.parseInt(jsonObject.getString("usr_registered"))<2?" Person":"People"));
                arrayList.add(getString(R.string.income)+" " + (jsonObject.isNull("total_income")?"0":jsonObject.getString("total_income")) + " RWF");
                arrayList.add(getString(R.string.withdrawn)+" " + String.valueOf(Integer.parseInt(jsonObject.getString("total_income")) - Integer.parseInt(jsonObject.getString("usr_amount"))) + " RWF");
                arrayList.add(getString(R.string.accbalance)+" " + jsonObject.getString("usr_amount") + " RWF");
                arrayList.add(getString(R.string.regpaymsubm));
                arrayList.add(getString(R.string.paid)+" " + (jsonObject.getString("paid_amount") == null ? '0' : jsonObject.getString("paid_amount")) + " rwf");
                arrayList.add(getString(R.string.remain)+" " + jsonObject.getString("usr_remain_amount") + " RWF");
            }else if(synchronizer.getSessionCategory().equals("postoffice")){
                JSONObject jsonObject=jsonObjects.getJSONArray("postoffice").getJSONObject(0);
                arrayList.add(getString(R.string.registeredon)+" " + jsonObject.getString("post_regdate").substring(0, 10));
                arrayList.add(getString(R.string.totlosts) + " " + (jsonObject.isNull("totlosts") ? "0" : jsonObject.getString("totlosts")));
                arrayList.add(getString(R.string.totgiven)+" " + (jsonObject.isNull("total_given") ? "0" : jsonObject.getString("total_given")));
                arrayList.add(getString(R.string.totremain)+" " + (jsonObject.isNull("total_remain") ? "0" : jsonObject.getString("total_remain")));
                arrayList.add(getString(R.string.income)+" " + (jsonObject.isNull("total_income")?"0":jsonObject.getString("total_income"))+" RWF");
                arrayList.add(getString(R.string.withdrawn)+" " + String.valueOf(Integer.parseInt(jsonObject.getString("total_income")) - Integer.parseInt(jsonObject.getString("usr_amount"))) + " RWF");
                arrayList.add(getString(R.string.accbalance)+" " + jsonObject.getString("usr_amount") + " RWF");
                arrayList.add(getString(R.string.lostspaymsubm));
                arrayList.add(getString(R.string.paid)+" " + (jsonObject.isNull("paid_amount")? '0' : jsonObject.getString("paid_amount")) + " RWF");
                arrayList.add(getString(R.string.remain)+" " + jsonObject.getString("usr_remain_amount") + " RWF");
            }
            ArrayAdapter adapter=new ArrayAdapter(AccountSummary.this,R.layout.listview_row,arrayList);
            lstSummary.setAdapter(adapter);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void redirect() {
        String sh = getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (sh.equals("0")) {
            intent.setClass(AccountSummary.this, Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                intent.setClass(AccountSummary.this, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(AccountSummary.this, Commissioner.class);
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
