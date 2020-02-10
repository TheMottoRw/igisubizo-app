package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
public class EnduserPayment extends ActionBarActivity {
    Synchronizer synchronizer;
    public Intent intent;
    public TextView tvAccname,tvAccNumber,tvNiD,tvUpdateHeader,tvRemainAmount;
    public EditText edtNiD,edtAmount,edtSendername,edtSenderphone,edtRemainAmount;
    public Button btnUpd;
    public Spinner spnPaymodes;
    public ProgressDialog progressDialog;
    public Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enduser_payment);
        setPaymodesSpinner();
        validator=new Validator();
        tvUpdateHeader=(TextView) findViewById(R.id.updateHeader);
        tvRemainAmount=(TextView) findViewById(R.id.tvRemainAmount);
        tvNiD=(TextView) findViewById(R.id.tvNiD);
        tvAccname=(TextView) findViewById(R.id.tvAccname);
        tvAccNumber=(TextView) findViewById(R.id.tvAccnbr);
        edtNiD=(EditText) findViewById(R.id.edtNiD);
        edtSendername=(EditText) findViewById(R.id.edtSendername);
        edtSenderphone=(EditText) findViewById(R.id.edtSenderphone);
        edtAmount=(EditText) findViewById(R.id.edtAmount);
        edtRemainAmount=(EditText) findViewById(R.id.edtRemainAmount);
        spnPaymodes=(Spinner) findViewById(R.id.spnPaymode);
        edtNiD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false && edtNiD.getText().toString().trim().replace(" ", "").length()==16){
                    if(!validator.nid(edtNiD.getText().toString().trim().replace(" ",""))){
                        final AlertDialog alertDialog=new AlertDialog.Builder(EnduserPayment.this).create();
                        alertDialog.setMessage(getString(R.string.invalidnid));
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
            }
        });
        edtSenderphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false && edtSenderphone.getText().length()!=0){
                    if (!validator.phone(edtSenderphone.getText().toString())){
                        final AlertDialog alertDialog=new AlertDialog.Builder(EnduserPayment.this).create();
                        alertDialog.setMessage(getString(R.string.invalidphone));
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
            }
        });
        spnPaymodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchPaymodes(spnPaymodes.getSelectedItem().toString().replace(" ","%20"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnUpd=(Button) findViewById(R.id.btnUpd);
        btnUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtAmount.getText().length()!=0) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(EnduserPayment.this).create();
                    alertDialog.setTitle(getString(R.string.approvepaymentHeader));
                    alertDialog.setMessage(getString(R.string.approvepaymentwarning));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btncontinue), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            final AlertDialog alertDialog1 = new AlertDialog.Builder(EnduserPayment.this).create();
                            alertDialog1.setTitle(getString(R.string.approvepay));
                            alertDialog1.setMessage(getString(R.string.undone));
                            alertDialog1.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnapprove), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog1.dismiss();
                                    if (!synchronizer.getSession().equals("0")) {
                                        if (Integer.parseInt(edtRemainAmount.getText().toString()) >= Integer.parseInt(edtAmount.getText().toString())) {
                                            addPayment();
                                            edtAmount.setText("");
                                        } else {
                                            Toast.makeText(EnduserPayment.this, getString(R.string.remainlesser), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        addPayment();
                                    }
                                }
                            });
                            alertDialog1.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btncancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog1.dismiss();
                                }
                            });
                            alertDialog1.show();
                        }
                    });

                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btncancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else{
                    Toast.makeText(EnduserPayment.this,getString(R.string.nulls),Toast.LENGTH_LONG).show();
                }
            }
        });
        synchronizer=new Synchronizer(this);
        if(!synchronizer.getSession().equals("0")){
            tvUpdateHeader.setText(getString(R.string.headerpaymentsubm));
            if(synchronizer.getSessionCategory().equals("commissioner")){
                loadUserInfo();
            }else if(synchronizer.getSessionCategory().equals("postoffice")){
                loadPostInfo();
            }
        }
        progressDialog=new ProgressDialog(EnduserPayment.this);
        progressDialog.setCancelable(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        //HIde NID Textbox for Commissioner
        if(!synchronizer.getSession().equals("0")){
            tvNiD.setVisibility(View.GONE);
            edtNiD.setVisibility(View.GONE);
            edtRemainAmount.setVisibility(View.VISIBLE);
            tvRemainAmount.setVisibility(View.VISIBLE);
        }
    }
    public void loadUserInfo(){
        String uid=synchronizer.getSession();
        //   Toast.makeText(this,"Connection State "+synchronizer.checkConnectionState(),Toast.LENGTH_SHORT).show();
               if (synchronizer.checkConnectionState() == true) {
        String url = synchronizer.getHost()+"/ajax/users.php";
        //String url = "192.168.173.1:12/RUT/etracking/ajax/paymodes.php";
        String data = "cate=loadbyid&id="+uid;
        RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(!response.isNull("user")){
                        try{
                            edtRemainAmount.setText(response.getJSONArray("user").getJSONObject(0).isNull("usr_remain_amount")==true?"0":response.getJSONArray("user").getJSONObject(0).getString("usr_remain_amount"));
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        }else{
                            Toast.makeText(EnduserPayment.this,"Users not found",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(EnduserPayment.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
             } else {
               Toast.makeText(EnduserPayment.this, getString(R.string.servernotconnect), Toast.LENGTH_SHORT).show();
         }
    }
    public void loadPostInfo(){
        String uid=synchronizer.getSession();
        //   Toast.makeText(this,"Connection State "+synchronizer.checkConnectionState(),Toast.LENGTH_SHORT).show();
               if (synchronizer.checkConnectionState() == true) {
        String url = synchronizer.getHost()+"/ajax/postoffices.php";
        //String url = "192.168.173.1:12/RUT/etracking/ajax/paymodes.php";
        String data = "cate=loadbyid&id="+uid;
        RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(!response.isNull("postoffice")){
                            try{
                                edtRemainAmount.setText(response.getJSONArray("postoffice").getJSONObject(0).isNull("usr_remain_amount")==true?"0":response.getJSONArray("postoffice").getJSONObject(0).getString("usr_remain_amount"));
                            }catch(JSONException ex){
                                ex.printStackTrace();
                            }
                        }else{
                            Toast.makeText(EnduserPayment.this,"Postoffice not found",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(EnduserPayment.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
             } else {
               Toast.makeText(EnduserPayment.this, getString(R.string.servernotconnect), Toast.LENGTH_SHORT).show();
         }
    }
    public void addPayment(){
        Synchronizer synchronizer=new Synchronizer(EnduserPayment.this);
        if(synchronizer.getSessionCategory().equals("commissioner") || synchronizer.getSessionCategory().equals("postoffice")) {
            if (!edtAmount.getText().toString().isEmpty() && !edtSendername.getText().toString().isEmpty() && !edtSenderphone.getText().toString().isEmpty()) {
             if(Integer.parseInt(edtAmount.getText().toString())>0){
                 if(validator.phone(edtSenderphone.getText().toString().trim().replaceAll(" ",""))){
                synchronizer.addPayment(edtNiD.getText().toString().replaceAll(" ", "%20"), edtSendername.getText().toString().replaceAll(" ", "%20"), edtSenderphone.getText().toString().replaceAll(" ", "%20"), edtAmount.getText().toString().replaceAll(" ", "%20"), spnPaymodes.getSelectedItem().toString().replaceAll(" ", "%20"));
                edtNiD.setText("");edtSendername.setText("");edtSenderphone.setText("");edtAmount.setText("");edtRemainAmount.setText("");
                 }else{
                     Toast.makeText(EnduserPayment.this,getString(R.string.invalidphone),Toast.LENGTH_SHORT).show();
                 }
                 }else{
                 Toast.makeText(EnduserPayment.this,getString(R.string.invalidamount),Toast.LENGTH_SHORT).show();
             }
            }
        }else{
            if (!edtNiD.getText().toString().isEmpty() && !edtAmount.getText().toString().isEmpty() && !edtSendername.getText().toString().isEmpty() && !edtSenderphone.getText().toString().isEmpty()) {
            if(validator.nid(edtNiD.getText().toString().trim().replaceAll(" ",""))){
                if(validator.phone(edtSenderphone.getText().toString().trim().replaceAll(" ",""))) {
                    if (Integer.parseInt(edtAmount.getText().toString()) > 0) {
                        synchronizer.addPayment(edtNiD.getText().toString().replaceAll(" ", "%20"), edtSendername.getText().toString().replaceAll(" ", "%20"), edtSenderphone.getText().toString().replaceAll(" ", "%20"), edtAmount.getText().toString().replaceAll(" ", "%20"), spnPaymodes.getSelectedItem().toString().replaceAll(" ", "%20"));
                        edtNiD.setText("");
                        edtSendername.setText("");
                        edtSenderphone.setText("");
                        edtAmount.setText("");
                        edtRemainAmount.setText("");
                    } else {
                        Toast.makeText(EnduserPayment.this, getString(R.string.invalidamount), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EnduserPayment.this,getString(R.string.invalidphone),Toast.LENGTH_SHORT).show();
                }
                }else{
                Toast.makeText(EnduserPayment.this,getString(R.string.invalidnid),Toast.LENGTH_SHORT).show();
            }
            }
        }
    }
    public void setPaymodesSpinner(){
        Synchronizer synchronizer=new Synchronizer(EnduserPayment.this);
        String uid=synchronizer.getSession();
        //   Toast.makeText(this,"Connection State "+synchronizer.checkConnectionState(),Toast.LENGTH_SHORT).show();
        if (synchronizer.checkConnectionState() == true) {
            String url = synchronizer.getHost()+"/ajax/paymodes.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/paymodes.php";
            String data = "cate=load";
            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setPaymodesView(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            // TODO Auto-generated method stub
                            Toast toast = Toast.makeText(EnduserPayment.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        } else {
            Toast.makeText(EnduserPayment.this, getString(R.string.servernotconnect), Toast.LENGTH_SHORT).show();
        }
    }
    public void setPaymodesView(JSONObject obj){//Fill Up Loaded Type to Spinner
        try {
            JSONArray arr = obj.getJSONArray("paymodes");
            String[] arrPaymodes=new String[arr.length()];
            for(int i=0;i<arr.length();i++){
                arrPaymodes[i]=arr.getJSONObject(i).getString("pmtd_name");
            }
            ArrayAdapter arrayAdapter=new ArrayAdapter(this,R.layout.select_dialog_item_material,arrPaymodes);
            spnPaymodes.setAdapter(arrayAdapter);
            //Setup paymodes account info

         //   tvAccname.setText(getString(R.string.acname)+":"+arr.getJSONObject(0).getString("pmtd_account_name"));
         //   tvAccNumber.setText(getString(R.string.acnbr) + ":" + arr.getJSONObject(0).getString("pmtd_account_number"));
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
public void searchPaymodes(String key){
    Synchronizer synchronizer=new Synchronizer(EnduserPayment.this);
    String uid=synchronizer.getSession();
    //   Toast.makeText(this,"Connection State "+synchronizer.checkConnectionState(),Toast.LENGTH_SHORT).show();
    if (synchronizer.checkConnectionState() == true) {
        String url = synchronizer.getHost()+"/ajax/paymodes.php";
        //String url = "192.168.173.1:12/RUT/etracking/ajax/paymodes.php";
        String data = "cate=search&key="+key;
        RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            tvAccname.setVisibility(View.VISIBLE);tvAccNumber.setVisibility(View.VISIBLE);
                            tvAccname.setText(getString(R.string.acname) + "\n" + response.getJSONArray("paymodes").getJSONObject(0).getString("pmtd_account_name"));
                            tvAccNumber.setText(getString(R.string.acnbr)+"\n"+response.getJSONArray("paymodes").getJSONObject(0).getString("pmtd_account_number"));
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(EnduserPayment.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
    } else {
        Toast.makeText(EnduserPayment.this, getString(R.string.servernotconnect), Toast.LENGTH_SHORT).show();
    }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_enduser, menu);
        return true;
    }
    public void redirect() {
        String sh = getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (sh.equals("0")) {
            intent.setClass(EnduserPayment.this, Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                intent.setClass(EnduserPayment.this, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(EnduserPayment.this, Commissioner.class);
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
            String cate=synchronizer.getSessionCategory();
            Intent intent = new Intent(EnduserPayment.this,ViewQueues.class);
            startActivity(intent);finish();
        }

        if (id==R.id.exit){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
