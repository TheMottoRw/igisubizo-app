package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AccountsIndex extends ActionBarActivity {
public ListView lstOptions;
    public Button btnUpdateWithdrawAccount,btnWithdraw;
    public  Synchronizer synchronizer;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_index);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        progressDialog=new ProgressDialog(AccountsIndex.this);
        lstOptions=(ListView) findViewById(R.id.lstOptions);
        btnUpdateWithdrawAccount=(Button) findViewById(R.id.btnUpdWithdrawAccount);
        btnWithdraw=(Button) findViewById(R.id.btnWithdrawMoney);
        synchronizer=new Synchronizer(this);
        btnUpdateWithdrawAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProfileInfo();
        }
        });
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountsIndex.this, Withdraw.class));
            }
        });
        setAccountsOptions();
    }
    public void loadProfileInfo() {
        String url="";
        if (synchronizer.checkConnectionState()==true){
            String uid=synchronizer.getSession();
        if(synchronizer.getSessionCategory().equals("commissioner")){
            url=synchronizer.getHost()+"/ajax/users.php";
        }else if(synchronizer.getSessionCategory().equals("postoffice")){
            url=synchronizer.getHost()+"/ajax/postoffices.php";
        }else{
            Toast.makeText(AccountsIndex.this,synchronizer.getSessionCategory(),Toast.LENGTH_SHORT).show();
        }
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
                            Toast toast = Toast.makeText(AccountsIndex.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
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
        final JSONObject jsonObject=jsonObjects.has("user")?jsonObjects.getJSONArray("user").getJSONObject(0):jsonObjects.has("postoffice")?jsonObjects.getJSONArray("postoffice").getJSONObject(0):new JSONObject();
            final LinearLayout linearLayout=new LinearLayout(AccountsIndex.this);
            Context context=getApplicationContext();
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            if (jsonObject.isNull("wthac_id")) {
                final AlertDialog alertDialog = new AlertDialog.Builder(AccountsIndex.this).create();
                alertDialog.setMessage(getString(R.string.addAccMsg));
                alertDialog.setTitle(getString(R.string.withdrawAcc));
                final TextView tvaccname = new TextView(AccountsIndex.this), tvaccnbr = new TextView(AccountsIndex.this), tvacccompany = new TextView(AccountsIndex.this);
                final EditText accname = new EditText(AccountsIndex.this), accnbr = new EditText(AccountsIndex.this);
                final Spinner spnWthcompany=new Spinner(AccountsIndex.this);
                tvaccname.setText(getString(R.string.acname));
                tvaccnbr.setText(getString(R.string.acnbr));
                tvacccompany.setText(getString(R.string.actype));
                setWithdrawCompanysView(spnWthcompany, jsonObjects, "0");
                linearLayout.addView(tvacccompany);
                linearLayout.addView(spnWthcompany);
                linearLayout.addView(tvaccname);
                linearLayout.addView(accname);
                linearLayout.addView(tvaccnbr);
                linearLayout.addView(accnbr);
                alertDialog.setView(linearLayout);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.addnew_btnsave), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        //Save information
updateAccounts("0",accname.getText().toString().replace(" ", "%20"),accnbr.getText().toString().replace(" ", "%20"),spnWthcompany.getSelectedItem().toString().replace(" ", "%20"),"register");
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
                final AlertDialog alertDialog = new AlertDialog.Builder(AccountsIndex.this).create();
                alertDialog.setMessage(getString(R.string.updateAccMsg));
                alertDialog.setTitle(getString(R.string.withdrawAcc));
                final TextView tvaccname = new TextView(AccountsIndex.this), tvaccnbr = new TextView(AccountsIndex.this), tvacccompany = new TextView(AccountsIndex.this);
                final EditText accname = new EditText(AccountsIndex.this), accnbr = new EditText(AccountsIndex.this);
                final Spinner spnWthcompany=new Spinner(AccountsIndex.this);
                tvaccname.setText(getString(R.string.acname));
                tvaccnbr.setText(getString(R.string.acnbr));
                tvacccompany.setText(getString(R.string.actype));
                try{
                    accname.setText(jsonObject.getString("wthac_name"));
                    accnbr.setText(jsonObject.getString("wthac_number"));
                   setWithdrawCompanysView(spnWthcompany,jsonObjects,jsonObject.getString("wthac_company"));
                }catch (JSONException ex){
                    ex.printStackTrace();
                }
                linearLayout.addView(tvacccompany);
                linearLayout.addView(spnWthcompany);
                linearLayout.addView(tvaccname);
                linearLayout.addView(accname);
                linearLayout.addView(tvaccnbr);
                linearLayout.addView(accnbr);
                alertDialog.setView(linearLayout);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnupdate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            //Update information
                            try {
                                updateAccounts(jsonObject.getString("wthac_id"), accname.getText().toString().replace(" ", "%20"), accnbr.getText().toString().replace(" ", "%20"),spnWthcompany.getSelectedItem().toString().replace(" ", "%20"), "update");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btncancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
public void updateAccounts(String wthacid,String accname,String accnbr,String company,String cate){
    if(accname.length()!=0 && accnbr.length()!=0 && company.length()!=0){
    String uid=synchronizer.getSession(),category="";
    if(synchronizer.getSessionCategory().equals("commissioner")){
        category="Commissioner";
    }else if(synchronizer.getSessionCategory().equals("postoffice")){
        category="Postoffice";
    }
    String url=synchronizer.getHost()+"/ajax/withdrawacc.php";
    //String url = "192.168.173.1:12/RUT/etracking/ajax/postoffices.php";
    String data="cate="+cate+"&id="+wthacid+"&ownerid="+uid+"&ownertype="+category+"&company="+company+"&accname="+accname+"&accnumber="+accnbr;
    progressDialog.setMessage(getString(R.string.loaddata));
    progressDialog.show();
    RequestQueue queue = Volley.newRequestQueue(this);
    final StringRequest stringRequest= new StringRequest
            (Request.Method.GET, " http://"+url+"?"+data, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                   if (response.equals("ok")){
                       Toast.makeText(AccountsIndex.this,getString(R.string.succsacchange),Toast.LENGTH_SHORT).show();
                   }
                    if(response.equals("fail")){
                        Toast.makeText(AccountsIndex.this,getString(R.string.failacchange),Toast.LENGTH_SHORT).show();
                    }
                    if(response.equals("exist")){
                        Toast.makeText(AccountsIndex.this,getString(R.string.existacchange),Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    error.printStackTrace();
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(AccountsIndex.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
    queue.add(stringRequest);
    }else{
        Toast.makeText(AccountsIndex.this,getString(R.string.nulls),Toast.LENGTH_SHORT).show();
    }
}
    public  void setAccountsOptions(){
    ArrayList rowsData=new ArrayList();
    rowsData.add(getString(R.string.accinfo));
    if(synchronizer.getSessionCategory().equals("commissioner")) {
        rowsData.add(getString(R.string.abanditse));
    }else if(synchronizer.getSessionCategory().equals("postoffice")){
        rowsData.add(getString(R.string.totlosts));
    }
    rowsData.add(getString(R.string.payhist));
    rowsData.add(getString(R.string.withdrawrep));
    rowsData.add(getString(R.string.acsummary));
    ArrayAdapter rowDt=new ArrayAdapter(AccountsIndex.this,R.layout.listview_row,rowsData);
lstOptions.setAdapter(rowDt);
    lstOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent("");
            switch (position){
                case 0:
                    if(synchronizer.getSessionCategory().equals("commissioner")){
                        intent.setClass(AccountsIndex.this,CommissionerProfil.class);
                        startActivity(intent);
                    }else if(synchronizer.getSessionCategory().equals("postoffice")){
                        intent.setClass(AccountsIndex.this,PostProfil.class);
                        startActivity(intent);
                    }
                    break;
                case 1:
                    if(synchronizer.getSessionCategory().equals("commissioner")) {
                        intent.setClass(AccountsIndex.this, CitizensReport.class);
                    }else if(synchronizer.getSessionCategory().equals("postoffice")){
                        intent.setClass(AccountsIndex.this,LostsReport.class);
                    }
                    startActivity(intent);
                    break;
                case 2:
                    intent.setClass(AccountsIndex.this,PaymentHistory.class);
                    startActivity(intent);
                    break;
                case 3:
                    intent.setClass(AccountsIndex.this,WithdrawHistory.class);
                    startActivity(intent);
                    break;
                case 4:
                    intent.setClass(AccountsIndex.this,AccountSummary.class);
                    startActivity(intent);
                    break;

            }
        }
    });
}
    public void setWithdrawCompanysView(Spinner spnWithdrawCompany,JSONObject obj,String accompany){//Fill Up Loaded WithdrawCompany to Spinner
         try {
            JSONArray arr = obj.getJSONArray("wthcompany");
            String[] arrWithdrawCompanys=new String[arr.length()],arrWithdrawCompanysId=new String[arr.length()];
            for(int i=0;i<arr.length();i++){
                arrWithdrawCompanys[i]=arr.getJSONObject(i).getString("wthc_accronym");
                arrWithdrawCompanysId[i]=arr.getJSONObject(i).getString("wthc_id");
            }
            ArrayAdapter arrayAdapter=new ArrayAdapter(AccountsIndex.this,R.layout.select_dialog_item_material,arrWithdrawCompanys);
            spnWithdrawCompany.setAdapter(arrayAdapter);
            int i=0;
            while (i<arrWithdrawCompanys.length) {
                if (arrWithdrawCompanysId[i].equals(accompany)) {
                    spnWithdrawCompany.setSelection(i);
                }
                i++;
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        final ActionBar actionBar = getSupportActionBar();
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
