package com.example.bpradmin.igisubizo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.w3c.dom.Text;


public class IssuesChat extends ActionBarActivity {
public EditText edtIssTitle,edtIscMsg;
    public Button btnSend;
    public TextView issTitle,issid;
    public Synchronizer synchronizer;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issues_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        issid=(TextView) findViewById(R.id.issid);
        edtIssTitle=(EditText) findViewById(R.id.edtIssTitle);
        edtIscMsg=(EditText) findViewById(R.id.edtIscMsg);
        issTitle=(TextView) findViewById(R.id.issTitle);
        btnSend=(Button) findViewById(R.id.btnAddIsc);
        synchronizer=new Synchronizer(this);
        progressDialog=new ProgressDialog(this);
        Bundle intent=getIntent().getExtras();
        if(!intent.getString("issid").equals("0")){//issid exists
        //search for Issues Chats and title
            issid.setText(intent.getString("issid"));
        }else{
            issTitle.setVisibility(View.GONE);
            edtIssTitle.setVisibility(View.VISIBLE);
        }
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIssueChat();
            }
        });
    }
    public void addIssueChat(){
        if(getIntent().getExtras().getString("issid").equals("0")){
            synchronizer.addIssue(edtIssTitle.getText().toString().replace(" ", "%20"), edtIscMsg.getText().toString().replace(" ", "%20"));
        edtIssTitle.setText("");edtIssTitle.setVisibility(View.GONE);edtIscMsg.setText("");
            loadIssuesChat();
        }else{//load new Issues

        }
    }
public void loadIssuesChat(){
    synchronizer=new Synchronizer(IssuesChat.this);
    if (synchronizer.checkConnectionState()) {
        //progressDialog.show();
        String uid=synchronizer.getSession();
        String ky=issid.getText().toString();
        String url = synchronizer.getHost()+"/ajax/issueschat.php";
        //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
        String data = "cate=load&issid="+issid;

        RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setIssuesChatView(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                        Toast toast = Toast.makeText(IssuesChat.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
    }else{
        Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
    }
}
    public void setIssuesChatView(JSONObject obj){
        try{
           JSONObject jsonObject=obj.getJSONArray("issues_chat").getJSONObject(0);
            
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
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
}
