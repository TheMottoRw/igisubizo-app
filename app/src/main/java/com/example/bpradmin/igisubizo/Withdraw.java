package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
public class Withdraw extends ActionBarActivity {
    Synchronizer synchronizer;
    public Intent intent;
    public TextView tvAccname,tvAccNumber,tvUpdateHeader;
    public EditText edtAmount,edtAccname,edtAccnbr,edtAccompany,edtBalance;
    public Button btnWithdrawMoney;
    public Spinner spnPaymodes;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw);
        synchronizer=new Synchronizer(this);
        tvUpdateHeader=(TextView) findViewById(R.id.updateHeader);
        tvAccname=(TextView) findViewById(R.id.tvAccname);
        tvAccNumber=(TextView) findViewById(R.id.tvAccnbr);
        edtAccompany=(EditText) findViewById(R.id.edtAcccompany);
        edtAccname=(EditText) findViewById(R.id.edtAccname);
        edtAccnbr=(EditText) findViewById(R.id.edtAccnbr);
        edtAmount=(EditText) findViewById(R.id.edtAmount);
        edtBalance=(EditText) findViewById(R.id.edtBalance);

        btnWithdrawMoney=(Button) findViewById(R.id.btnWithdrawMoney);
        btnWithdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtAmount.getText().equals("")){
                    if(Integer.parseInt(edtAmount.getText().toString())<=Integer.parseInt(edtBalance.getText().toString())){
                final AlertDialog alertDialog=new AlertDialog.Builder(Withdraw.this).create();
                alertDialog.setTitle(getString(R.string.approvewithdrawHeader));
                alertDialog.setMessage(getString(R.string.approvewithdrawwarning));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btncontinue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        final AlertDialog alertDialog1 = new AlertDialog.Builder(Withdraw.this).create();
                        alertDialog1.setTitle(getString(R.string.approvewth));
                        alertDialog1.setMessage(getString(R.string.undone));
                        alertDialog1.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnapprove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog1.dismiss();
                                addWithdraw();
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

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,  getString(R.string.btncancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                    }else{
                        Toast.makeText(Withdraw.this,getString(R.string.balancelesser),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Withdraw.this,getString(R.string.nulls),Toast.LENGTH_SHORT).show();
                }
            }
        });
       progressDialog=new ProgressDialog(Withdraw.this);
        progressDialog.setCancelable(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        loadUserInfo();
    }

        public void addWithdraw(){
    if (!edtAmount.getText().toString().isEmpty() && !edtAccname.getText().toString().isEmpty() && !edtAccnbr.getText().toString().isEmpty()) {
    if(Integer.parseInt(edtAmount.getText().toString())>0) {
        synchronizer.addWithdraw(edtAmount.getText().toString().replaceAll(" ", "%20"));
        edtAmount.setText("");
    }else{
        Toast.makeText(Withdraw.this,getString(R.string.zeroamount),Toast.LENGTH_SHORT).show();
    }
    }else{
        Toast.makeText(Withdraw.this,getString(R.string.nulls),Toast.LENGTH_SHORT).show();
    }
    }
    public void loadUserInfo(){
        String uid=synchronizer.getSession(),url="";
        //   Toast.makeText(this,"Connection State "+synchronizer.checkConnectionState(),Toast.LENGTH_SHORT).show();
       if (synchronizer.checkConnectionState() == true) {
        if(synchronizer.getSessionCategory().equals("commissioner")){
            url= synchronizer.getHost()+"/ajax/users.php";
        }else if(synchronizer.getSessionCategory().equals("postoffice")){
            url= synchronizer.getHost()+"/ajax/postoffices.php";
        }
            //String url = "192.168.173.1:12/RUT/etracking/ajax/paymodes.php";
            String data = "cate=loadbyid&id="+uid;
            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setWithdrawViews(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            // TODO Auto-generated method stub
                            Toast toast = Toast.makeText(Withdraw.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        } else {
            Toast.makeText(Withdraw.this, getString(R.string.servernotconnect), Toast.LENGTH_SHORT).show();
        }
    }

    public void setWithdrawViews(JSONObject obj){//Fill Up Loaded Type to Spinner
        try {
            JSONArray arr = obj.has("user")?obj.getJSONArray("user"):obj.has("postoffice")?obj.getJSONArray("postoffice"):new JSONArray();
            JSONObject arrObj=arr.getJSONObject(0);
            edtAccompany.setText(arrObj.isNull("wth_company_name")?"":arrObj.getString("wth_company_name"));
            edtAccname.setText(arrObj.isNull("wthac_name")?"":arrObj.getString("wthac_name"));
              edtAccnbr.setText(arrObj.isNull("wthac_number")?"":arrObj.getString("wthac_number"));
            edtBalance.setText(arr.getJSONObject(0).isNull("usr_amount")==true?"0":arr.getJSONObject(0).getString("usr_amount"));
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_enduser, menu);
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
        if (id == R.id.Profil) {
            String cate=synchronizer.getSessionCategory();
            Intent intent = new Intent(Withdraw.this,ViewQueues.class);
            startActivity(intent);finish();
        }

        if (id==R.id.exit){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
