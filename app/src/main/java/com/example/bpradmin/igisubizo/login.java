package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by BPRAdmin on 10/6/2017.
 */
public class login extends ActionBarActivity {
    Synchronizer synchronizer;public EditText uname,password;Button connect;
    public TextView resetLink;public ProgressDialog progressDialog;
    public LinearLayout containerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        synchronizer = new Synchronizer(this);
        uname = (EditText) findViewById(R.id.edtUname);
        password = (EditText) findViewById(R.id.edtPwd);
        resetLink = (TextView) findViewById(R.id.resetLink);
        connect = (Button) findViewById(R.id.btnLogin);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        progressDialog = new ProgressDialog(this);
        resetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPwd();
            }
        });
        progressDialog.setMessage("Connecting to Server");
        progressDialog.setCancelable(false);
//set up configurations form
        containerLayout = (LinearLayout) findViewById(R.id.lnLayout);
        containerLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(login.this).create();
                alertDialog.setTitle("Configuration Panel");
                final EditText edtPasscode=new EditText(login.this);
                edtPasscode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                alertDialog.setView(edtPasscode);
               alertDialog.setMessage("Please Enter PassCode to Continue");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        if (edtPasscode.getText().toString().equals("Admin@bfg.Passcode")) {
                            Toast.makeText(login.this, "Configure Host", Toast.LENGTH_SHORT).show();
                            final AlertDialog alertDialog1 = new AlertDialog.Builder(login.this).create();
                            alertDialog1.setTitle("Configuration Panel");
                            String hosts = getSharedPreferences("host", MODE_PRIVATE).getString("url","bfg.rw/igisubizo");
                            final EditText edtText = new EditText(login.this);
                           edtText.setText(hosts);
                            alertDialog1.setView(edtText);
                            alertDialog1.setMessage("Setup Host Configuration");
                            alertDialog1.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String host = edtText.getText().toString();
                                    SharedPreferences.Editor hosts = getSharedPreferences("host", MODE_PRIVATE).edit();
                                    hosts.putString("url", host);
                                    hosts.commit();
                                    alertDialog1.dismiss();
                                    Toast.makeText(login.this, "Hosts Configured Success", Toast.LENGTH_LONG).show();
                                }
                            });
                            alertDialog1.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog1.dismiss();
                                }
                            });
                            alertDialog1.show();
                        } else {
                            Toast.makeText(login.this,"Passcode Incorrect",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }
    public void login(){
        if (!uname.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            loginRequest(uname.getText().toString().replaceAll(" ", "%20"), password.getText().toString());
        uname.setText("");password.setText("");
               }else{
        Toast.makeText(this,getString(R.string.nulls),Toast.LENGTH_LONG).show();
        }
}
    public void loginRequest(String uname,String pwd){
        if (synchronizer.checkConnectionState()==true) {
            String url =synchronizer.getHost()+"/ajax/applogin.php";
            // String url = "192.168.173.1:12/RUT/etracking/ajax/applogin.php";
            String data = "uname=" + uname.trim() + "&password=" + pwd;
            httpLoginRequest(url, data);
        }else{
            Toast.makeText(login.this, getString(R.string.offline), Toast.LENGTH_LONG).show();
        }
    }
    private void httpLoginRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
// Request a json response from the provided URL.
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://"+url+"?"+data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        doLogin(response);
                        // Toast.makeText(ctx, "Data "+jsonResponse, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(login.this,"Internet Connection Error", Toast.LENGTH_LONG);
                        toast.show();
                        progressDialog.dismiss();
                    }
                });
        queue.add(jsObjRequest);

    }
    public void doLogin(JSONObject response){
        try {
            if (!response.getString("cate").equals("fail")) {
                //registering new Shared Preference
                SharedPreferences sharedPreferences = getSharedPreferences("igisubizologgedID", MODE_PRIVATE);
                SharedPreferences.Editor sharedPreferencesCate= getSharedPreferences("igisubizologgedCate", MODE_PRIVATE).edit();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("uid", response.getString("sessid"));
                sharedPreferencesCate.putString("category",response.getString("cate")).commit();
                edit.commit();
                finish();
                Intent intent=new Intent();
                if(response.getString("cate").equals("postoffice")) {
                    intent = intent.setClass(login.this, MainActivity.class);
                }else if(response.getString("cate").equals("commissioner")){
                    intent=intent.setClass(login.this,Commissioner.class);
                }
                startActivity(intent);
                Toast.makeText(login.this, getString(R.string.logsccs), Toast.LENGTH_LONG).show();
            }
            if (response.getString("cate").equals("fail")) {
                progressDialog.dismiss();
                Toast.makeText(login.this, getString(R.string.wrongunameorpwd), Toast.LENGTH_LONG).show();
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void resetPwd(){
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("http://"+synchronizer.getHost()+"/reset");
               //Uri uri = Uri.parse("192.168.173.1:12/RUT/etracking/reset.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        t.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.index_main, menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
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
            startActivity(new Intent(login.this,Language.class));
        }
return super.onOptionsItemSelected(item);
    }
}
