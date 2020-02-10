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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Issues extends ActionBarActivity implements IssuesAdapter.ItemClickListener {
public Button btnNewIssue;
    public Synchronizer synchronizer;
    public ProgressDialog progressDialog;
    public  IssuesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        synchronizer=new Synchronizer(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        progressDialog=new ProgressDialog(this);
        btnNewIssue=(Button) findViewById(R.id.btnNewIssue);
        btnNewIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("issid","0");
                intent.setClass(Issues.this, IssuesChat.class);
                startActivity(intent);
            }
        });
        //loading whole issues for support
        loadIssues();
    }
    protected void onResume(){
        loadIssues();
    }
    public void loadIssues(){
        synchronizer=new Synchronizer(Issues.this);
        if (synchronizer.checkConnectionState()) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loaddata));
            progressDialog.setCancelable(false);
            progressDialog.show();
            String uid = synchronizer.getSession();
            String category="";
            switch (synchronizer.getSessionCategory()){
                case "commissioner":
                    category="Commissioner";break;
                case "postoffice":
                    category="Postoffice";break;
                default:
                    category="invalid";
            }
            String url = synchronizer.getHost()+"/ajax/issues.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
            String data = "cate=load&ownerid=" + uid+"&ownertype="+category;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setIssuesView(response);
                            progressDialog.dismiss();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast toast = Toast.makeText(Issues.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void setIssuesView(JSONObject data){
        try{
           // Toast.makeText(Issues.this, "Size " + String.valueOf(data.getJSONArray("issues").length()), Toast.LENGTH_LONG).show();
            if(data.getJSONArray("issues").length()!=0){
              RecyclerView rvIssues=(RecyclerView) findViewById(R.id.rvIssues);
                rvIssues.setLayoutManager(new LinearLayoutManager(Issues.this));
                adapter = new IssuesAdapter(Issues.this,data);
                adapter.setClickListener(Issues.this);
                rvIssues.setAdapter(adapter);
                rvIssues.setVisibility(View.VISIBLE);
            }else{
                RecyclerView rvIssues=(RecyclerView) findViewById(R.id.rvIssues);
                rvIssues.setVisibility(View.GONE);
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if(id==16908332) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
                Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, IssuesChat.class);
            intent.putExtra("issid", adapter.getItem(position).getString("iss_id"));
            startActivity(intent);
        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }
}
