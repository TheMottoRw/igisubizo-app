package com.example.bpradmin.igisubizo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by bpradmin on 11/4/2017.
 */
public class SearchResult extends Activity{
    ProgressDialog progressDialog;
    MyRecyclerViewAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
    public void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String qy=intent.getStringExtra(SearchManager.QUERY);
            //SEARCH DATA

        }
    }
    public void loadLosts(){
        Synchronizer synchronizer=new Synchronizer(SearchResult.this);
        if (synchronizer.checkConnectionState()) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loadlosts));
            progressDialog.show();
            String uid = synchronizer.getSession();
           String url = synchronizer.getHost()+"/ajax/losts.php";
           // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
            String data = "cate=loadbypostoffice&id=" + uid;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setLostsView(response);
                            progressDialog.dismiss();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast toast = Toast.makeText(SearchResult.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void setLostsView(JSONObject data){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvLosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResult.this));
        adapter = new MyRecyclerViewAdapter(SearchResult.this, data);
       // adapter.setClickListener(SearchResult.this);
        recyclerView.setAdapter(adapter);
    }
}
