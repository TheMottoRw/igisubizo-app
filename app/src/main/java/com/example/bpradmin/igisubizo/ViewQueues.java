package com.example.bpradmin.igisubizo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewQueues extends ActionBarActivity implements QueueAdapter.ItemClickListener {
    QueueAdapter adapter;
    public ProgressDialog progressDialog;public static EditText edtSearch;
    public static Button btnSearchItems;
    public Synchronizer synchronizer;
    public RecyclerView recyclerView;
    public LinearLayout indexViewLayout;
    public Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_queues);
        validator=new Validator();
        edtSearch=(EditText) findViewById(R.id.searchKey);
        btnSearchItems=(Button) findViewById(R.id.btnSearch);
        btnSearchItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            searchQueues(edtSearch.getText().toString());
            }
        });
        indexViewLayout=(LinearLayout) findViewById(R.id.indexViewLayout);
        recyclerView=(RecyclerView) findViewById(R.id.rvQueues);
        synchronizer=new Synchronizer(this);
        if(!synchronizer.getSession().equals("0")){
            Intent intent=getIntent();
            String nid=intent.getStringExtra("nid");
            edtSearch.setText(nid);
            searchQueues(nid);
        }
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.logo);
    }
    public void loadQueues(){
        Synchronizer synchronizer=new Synchronizer(ViewQueues.this);
        if (synchronizer.checkConnectionState()) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.loaddata));
            progressDialog.show();
            String uid = synchronizer.getSession();
            String url = synchronizer.getHost()+"/ajax/queue.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/queue.php";
            String data = "cate=loadbypostoffice&id=" + uid;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setQueuesView(response);
                            progressDialog.dismiss();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast toast = Toast.makeText(ViewQueues.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void searchQueues(String key){
        Synchronizer synchronizer=new Synchronizer(ViewQueues.this);
        if (synchronizer.checkConnectionState()) {
            if(key.trim().length()!=0){
                if(validator.nid(key)){
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.loaddata));
                //progressDialog.show();
                String uid = synchronizer.getSession();
                String url = synchronizer.getHost() + "/ajax/queue.php";
                //String url = "192.168.173.1:12/RUT/etracking/ajax/queue.php";
                String data = "cate=search&key=" + key;

                RequestQueue queue = Volley.newRequestQueue(this);
                final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                        setQueuesView(response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                error.printStackTrace();
                                Toast toast = Toast.makeText(ViewQueues.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                queue.add(jsObjRequest);
                }else{
                    Toast.makeText(ViewQueues.this,getString(R.string.invalidnid),Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,getString(R.string.nulls),Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void setQueuesView(JSONObject data){
        try{
        if(data.getJSONArray("queues").length()!=0){
            indexViewLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewQueues.this));
        adapter = new QueueAdapter(ViewQueues.this, data);
        adapter.setClickListener(ViewQueues.this);
        recyclerView.setAdapter(adapter);
        }else{
            indexViewLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        }catch (JSONException ex){
            ex.printStackTrace();
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
                Intent intent = new Intent(ViewQueues.this, EnduserPayment.class);
                startActivity(intent);
                finish();
            }
        }else{
            if (id == R.id.action_lock) {
                Intent intent = new Intent(ViewQueues.this,changepwd.class);
                startActivity(intent);
            }
            if (id == R.id.Profil) {
                Intent intent = new Intent(ViewQueues.this,CommissionerProfil.class);
                startActivity(intent);
            }
            if (id==R.id.search){
                //   Toast.makeText(ViewQueues.this,"Search Views",Toast.LENGTH_LONG).show();
                int tabPos=getSupportActionBar().getSelectedTab().getPosition();
                if (tabPos==1)
                    edtSearch.setVisibility(View.VISIBLE);
            }
            if(id==R.id.logout){
                synchronizer.logout();
                finish();
            }
        }
        if (id==R.id.exit){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
