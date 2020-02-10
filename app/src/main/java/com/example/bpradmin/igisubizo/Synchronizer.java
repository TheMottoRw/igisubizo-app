package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.View;
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
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by bpradmin on 10/15/2017.
 */
public class Synchronizer{
    public Context ctx=null;public String strResponse="";public JSONObject jsonResponse=null;
    public ProgressDialog progressDialog;
    View view;
  //  MyRecyclerViewAdapter adapter;
    public Synchronizer(Context context){
        this.ctx=context;
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(ctx.getString(R.string.connectserver));
    }

public  String getHost(){
    SharedPreferences sharedPreferences=ctx.getSharedPreferences("host", Context.MODE_PRIVATE);
    String url=sharedPreferences.getString("url","bfg.rw/igisubizo");
    return url;
}
    public void logout(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("igisubizologgedID", ctx.MODE_PRIVATE);
        ctx.getSharedPreferences("igisubizologgedID", ctx.MODE_PRIVATE).edit().clear().commit();
        SharedPreferences.Editor edit = sharedPreferences.edit().clear();
        edit.commit();
        Intent intent = new Intent(ctx, login.class);
        ctx.startActivity(intent);
    }
    public void redirect() {
        String sh = ctx.getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (checkSessionsExistance() == false) {
            intent.setClass(ctx, Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                intent.setClass(ctx, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(ctx, Commissioner.class);
            }
            ctx.startActivity(intent);
        }
    }
    //Payments Informations
    public void addPayment(String nid,String sendername,String phone,String amount,String paymode){
        String uid=getSession(),payerid="0",payertype="";
        if(getSessionCategory().equals("commissioner")){
            payerid=uid;
            payertype="Commissioner";
        }else if(getSessionCategory().equals("postoffice")){
            payerid=uid;
            payertype="Postoffice";
        }
        String url =this.getHost()+"/ajax/payhist.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
        String data = "cate=register&nid="+nid+"&sendername="+sendername+"&senderphone="+phone+"&amount="+amount+"&paymode="+paymode+"&payerid="+payerid+"&payertype="+payertype;
        httpAddPaymentsRequest(url, data);
     //  Toast.makeText(ctx,"Commid "+commid,Toast.LENGTH_LONG).show();
    }
    private void httpAddPaymentsRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doAddPayments(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,  ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
    }
    public void doAddPayments(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,ctx.getString(R.string.succsaddpayments), Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failaddpayments), Toast.LENGTH_LONG).show();
        }
        if (response.equals("exist")){
            Toast.makeText(ctx,ctx.getString(R.string.addedpaymentsexist), Toast.LENGTH_LONG).show();
        }
        if (response.equals("notexist")){
            Toast.makeText(ctx,ctx.getString(R.string.citnotexisheader), Toast.LENGTH_LONG).show();
        }
    }
    //Withdraws Informations
    public void addWithdraw(String amount){
        String uid=getSession(),withdrawertype="";
        if(getSessionCategory().equals("commissioner")){
            withdrawertype="Commissioner";
        }else if(getSessionCategory().equals("postoffice")){
            withdrawertype="Postoffice";
        }
        String url =this.getHost()+"/ajax/withdrawhist.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
        String data = "cate=register&amount="+amount+"&withdrawerid="+uid+"&withdrawertype="+withdrawertype;
        httpAddWithdrawsRequest(url, data);
        //  Toast.makeText(ctx,"Commid "+commid,Toast.LENGTH_LONG).show();
    }
    private void httpAddWithdrawsRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doAddWithdraws(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,  ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
    }
    public void doAddWithdraws(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,ctx.getString(R.string.succsaddwithdraws), Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failaddwithdraws), Toast.LENGTH_LONG).show();
        }
        if (response.equals("exist")){
            Toast.makeText(ctx,ctx.getString(R.string.addedwithdrawsexist), Toast.LENGTH_LONG).show();
        }
    }
    //Citizens Informations
    public void addCitizens(String name,String nid,String phone){
        String uid=getSession(),commid="0";
           if(getSessionCategory().equals("commissioner")){
                commid=uid;
            }
            String url =this.getHost()+"/ajax/citizens.php";
            // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
            String data = "cate=register&name="+name+"&nid="+nid+"&phone="+phone+"&commid="+commid;
        httpAddCitizensRequest(url, data);
        //Toast.makeText(ctx,"Add Losts Called",Toast.LENGTH_LONG).show();
    }
    private void httpAddCitizensRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doAddCitizens(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,  ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
    }
    public void doAddCitizens(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,ctx.getString(R.string.succsaddcitizens), Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failaddcitizens), Toast.LENGTH_LONG).show();
        }
        if (response.equals("exist")){
            Toast.makeText(ctx,ctx.getString(R.string.addedcitizensexist), Toast.LENGTH_LONG).show();
        }
    }
    public void updCitizens(String citid,String owner,String identification,String phone){
        String uid=getSession();
        if (!uid.equals("0")){
            String url =this.getHost()+"/ajax/citizens.php";
            // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
            String data = "cate=update&id="+citid+"&owner="+owner+"&nid="+identification+"&phone="+phone+"&comments=none";
            httpUpdCitizensRequest(url, data);
        }else {
            Toast.makeText(ctx,"No Session Exists "+uid,Toast.LENGTH_LONG).show();
        }
        //  Toast.makeText(ctx,"Update Losts Called",Toast.LENGTH_LONG).show();
    }
    private void httpUpdCitizensRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doUpdCitizens(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);

    }
    public void doUpdCitizens(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,ctx.getString(R.string.succsupdcitizens), Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failupdcitizens), Toast.LENGTH_LONG).show();
        }
        if (response.equals("exist")){
          final  AlertDialog alertDialog=new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle(ctx.getString(R.string.uniqueinfoHeader));
            alertDialog.setMessage(ctx.getString(R.string.uniqueinfoMsg));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, ctx.getString(R.string.btnok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
            Toast.makeText(ctx,ctx.getString(R.string.failupdcitizens), Toast.LENGTH_LONG).show();
        }
    }
    public void loadCitizens(){
        String uid=getSession();
        String url=this.getHost()+"/ajax/citizens.php";
        //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
        String data="cate=load&commid="+uid;
        loadLostsData(url, data);
    }
    public void loadCitizensData(String url,String data){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://"+url+"?"+data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setLoadedCitizens(response);
                        // Toast.makeText(ctx, "Data "+jsonResponse, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(ctx, ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
    }
    public void setLoadedCitizens(JSONObject data){
        try {

            JSONArray jsonArray=data.getJSONArray("citizens");
            int i=0;
           /* while (i<jsonArray.length()){
        Toast.makeText(ctx, "Data " + jsonArray.getJSONObject(i).getString("identifier"), Toast.LENGTH_LONG).show();
                i++;
            }
*/
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void addLosts(String owner,String identification,String doctype){
        String uid=getSession();
       if (!uid.equals("0")){
        String url =this.getHost()+"/ajax/losts.php";
       // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
           String data = "cate=register&postoffid="+uid+"&owner="+owner+"&identifier="+identification+"&type="+doctype+"&comments=none";
            httpAddLostsRequest(url, data);
        }else {
            Toast.makeText(ctx,"No Session Exists "+uid,Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(ctx,"Add Losts Called",Toast.LENGTH_LONG).show();
    }
    private void httpAddLostsRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doAddLosts(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,  ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
}
    public void doAddLosts(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,ctx.getString(R.string.succsaddlosts), Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failaddlosts), Toast.LENGTH_LONG).show();
        }
        if (response.equals("exist")){
            Toast.makeText(ctx,ctx.getString(R.string.addedlostsexist), Toast.LENGTH_LONG).show();
        }
    }
    public void addQueue(String nid,String identification,String doctype){
            String url =this.getHost()+"/ajax/queue.php",commid="0";
        String uid=getSession();
        if(getSessionCategory().equals("commissioner")){
            commid=uid;
        };
             //String url = "192.168.173.1:12/RUT/etracking/ajax/queue.php";
            String data = "cate=register&nid="+nid+"&identifier="+identification+"&type="+doctype+"&commid="+commid;
            httpAddQueueRequest(url, data);
    }
    private void httpAddQueueRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doAddQueue(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
    }
    public void doAddQueue(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,R.string.succsaddqueue,Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx, R.string.failaddqueue,Toast.LENGTH_LONG).show();
        }
        if (response.equals("exist")){
            Toast.makeText(ctx, R.string.addedqueueexist, Toast.LENGTH_LONG);
            }
        if(response.equals("maxitems")){
            final AlertDialog alertDialog=new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle(ctx.getString(R.string.maxitemsheader));
            alertDialog.setMessage(ctx.getString(R.string.maxitemssmsg));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        if(response.equals("notexist")){
            final AlertDialog alertDialog=new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle(ctx.getString(R.string.citnotexisheader));
            alertDialog.setMessage(ctx.getString(R.string.citnotexismsg));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }
    public void updQueues(String queueid,String nid,String identification,String doctype){
        String uid=getSession();
        String url =this.getHost()+"/ajax/queue.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/queues.php";
        String data = "cate=update&id="+queueid+"&nid="+nid+"&identifier="+identification+"&type="+doctype+"&comments=none";
        httpUpdQueuesRequest(url, data);

        //  Toast.makeText(ctx,"Update Queues Called",Toast.LENGTH_LONG).show();
    }
    private void httpUpdQueuesRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doUpdQueues(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);

    }
    public void doUpdQueues(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,ctx.getString(R.string.succsupdqueues), Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failupdqueues), Toast.LENGTH_LONG).show();
        }
        if (response.equals("exist")) {
            Toast.makeText(ctx,R.string.addedqueueexist,Toast.LENGTH_LONG).show();
        }
        if(response.equals("maxitems")){
            final AlertDialog alertDialog=new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle(ctx.getString(R.string.maxitemsheader));
            alertDialog.setMessage(ctx.getString(R.string.maxitemssmsg));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
        if(response.equals("notexist")){
            final AlertDialog alertDialog=new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle(ctx.getString(R.string.citnotexisheader));
            alertDialog.setMessage(ctx.getString(R.string.citnotexismsg));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }
    public void updLosts(String lostid,String owner,String identification,String doctype){
        String uid=getSession();
        if (!uid.equals("0")){
            String url =this.getHost()+"/ajax/losts.php";
           // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
            String data = "cate=update&id="+lostid+"&owner="+owner+"&identifier="+identification+"&type="+doctype+"&comments=none";
            httpAddLostsRequest(url, data);
        }else {
            Toast.makeText(ctx,"No Session Exists "+uid,Toast.LENGTH_LONG).show();
        }
      //  Toast.makeText(ctx,"Update Losts Called",Toast.LENGTH_LONG).show();
    }
    private void httpUpdLostsRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doUpdLosts(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);

    }
    public void doUpdLosts(String response){
        if (response.equals("ok")) {
            Toast.makeText(ctx,ctx.getString(R.string.succsupdlosts), Toast.LENGTH_LONG).show();
        }
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failupdlosts), Toast.LENGTH_LONG).show();
        }
    }
    public void setFoundLost(final View currView,String lostid,String payid,String payamount){
         String url =this.getHost()+"/ajax/losts.php";
       //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
        String data ="cate=found&id="+lostid+"&payid="+payid+"&amount="+payamount;
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                     //   Toast.makeText(ctx,"Found Called to Set",Toast.LENGTH_SHORT).show();
                        if (response.equals("ok")){
                            currView.setVisibility(View.GONE);
                            Toast.makeText(ctx,ctx.getString(R.string.loststaken),Toast.LENGTH_SHORT).show();
                        }
                        if (response.equals("fail")){
                            Toast.makeText(ctx,ctx.getString(R.string.failtotake),Toast.LENGTH_SHORT).show();
                        }
                        if (response.equals("ready")){
                            Toast.makeText(ctx,ctx.getString(R.string.allreadytaken),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
    }
    public void loadLosts(){
        String uid=getSession();
        String url=this.getHost()+"/ajax/losts.php";
       //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
        String data="cate=loadbypostoffice&id="+uid;
        loadLostsData(url, data);
    }
    public void loadLostsData(String url,String data){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://"+url+"?"+data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setLoadedLosts(response);
                     // Toast.makeText(ctx, "Data "+jsonResponse, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(ctx, ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                    toast.show();
                    }
                });
        queue.add(jsObjRequest);
    }
    public void setLoadedLosts(JSONObject data){
        try {

        JSONArray jsonArray=data.getJSONArray("losts");
            int i=0;
           /* while (i<jsonArray.length()){
        Toast.makeText(ctx, "Data " + jsonArray.getJSONObject(i).getString("identifier"), Toast.LENGTH_LONG).show();
                i++;
            }
*/
        }catch (JSONException ex){
        ex.printStackTrace();
    }
    }
    //Issues Informations
    public void addIssue(String title,String message){
        String uid=getSession(),ownerid="0",ownertype="";
        if(getSessionCategory().equals("commissioner")){
            ownerid=uid;
            ownertype="Commissioner";
        }else if(getSessionCategory().equals("postoffice")){
            ownerid=uid;
            ownertype="Postoffice";
        }
        String url =this.getHost()+"/ajax/issues.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
        String data = "cate=register&ownerid="+uid+"&ownertype="+ownertype+"&title="+title+"&message="+message;
        httpAddIssuesRequest(url, data);
        //  Toast.makeText(ctx,"Commid "+commid,Toast.LENGTH_LONG).show();
    }
    private void httpAddIssuesRequest(String url,String data) {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doAddIssues(response);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx,  ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);
    }
    public void doAddIssues(String response){
        if (response.equals("fail")){
            Toast.makeText(ctx,ctx.getString(R.string.failaddissue), Toast.LENGTH_LONG).show();
        }
    }
    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
    public void downloadFile(String fileUrl) {  //this is the downloader method
        try{
            File apkStorage=null,outputFile=null;
        URL url = new URL(getHost()+"/"+fileUrl);//Create Download URl
            HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
        c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
        c.connect();//connect the URL Connection

        //If Connection response is not OK then show Logs
        if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Toast.makeText(ctx, "Server returned HTTP " + c.getResponseCode()
                    + " " + c.getResponseMessage(),Toast.LENGTH_SHORT).show();

        }


        //Get File if SD card is present
        if (isSDCardPresent()) {

            apkStorage = new File(
                    Environment.getExternalStorageDirectory() + "/Igisubizo");
        } else
            Toast.makeText(ctx, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

        //If File is not present create directory
        if (!apkStorage.exists()) {
            apkStorage.mkdir();
            Toast.makeText(ctx, "Directory Created.",Toast.LENGTH_SHORT).show();
        }
        outputFile = new File(apkStorage, "archive.pdf");//Create Output file in Main File

        //Create New File if not present
        if (!outputFile.exists()) {
            outputFile.createNewFile();
            Toast.makeText(ctx, "File Created",Toast.LENGTH_SHORT).show();
        }

        FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

        InputStream is = c.getInputStream();//Get InputStream for connection

        byte[] buffer = new byte[1024];//Set buffer type
        int len1 = 0;//init length
        while ((len1 = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);//Write new file
        }

        //Close all connection after doing task
        fos.close();
        is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkSessionsExistance(){
        boolean feed=false;
        SharedPreferences sharedPreferences=ctx.getSharedPreferences("igisubizologgedID",ctx.MODE_PRIVATE);
        //reading from Shared Preference
        String uid=sharedPreferences.getString("uid","0");
        if (!uid.equals("0")){
        feed=true;
        }
        return feed;
    }
    public String getSession(){
        String uid="0";
        SharedPreferences sharedPreferences=ctx.getSharedPreferences("igisubizologgedID", ctx.MODE_PRIVATE);
        //reading from Shared Preference
        uid=sharedPreferences.getString("uid","0");
        return uid;
    }
    public String getSessionCategory(){
        String feed="";
        SharedPreferences sharedPreferences=ctx.getSharedPreferences("igisubizologgedCate",ctx.MODE_PRIVATE);
        //reading from Shared Preference
        String cate=sharedPreferences.getString("category","0");
        if (!cate.equals("0")){
            feed=cate;
        }
        return feed;
    }
   // etracking etrack=new etracking(ctx);
    public boolean synchLosts(){
     if(checkConnectionState()) {
 /*  ArrayList nonSentLosts = etrack.getNonSentLocalLosts();
         for (int i = 0; i < nonSentLosts.size(); i++) {
             //Requests to Synchronize not sent Losts


             //after synchronized update localDb
         }
         */
     }
   return true;
    }

    public boolean checkConnectionState(){
        boolean feed=false;
        ConnectivityManager cm=(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        feed=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        //feed=activeNetwork.isConnected();
        return feed;
    }

    public void changePwd(String old,String newPwd){
        String uid=getSession(),url="";
        if(getSessionCategory().equals("postoffice")){
        url=this.getHost()+"/ajax/postoffices.php";
        }else if(getSessionCategory().equals("commissioner")){
            url=this.getHost()+"/ajax/users.php";
        }
        //String url = "192.168.173.1:12/RUT/etracking/ajax/postoffices.php";
        String data="cate=changepwd&id="+uid+"&old="+old+"&new="+newPwd;
        progressDialog.setMessage(ctx.getString(R.string.connectserver));
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                       if (response.equals("ok")){
                    Toast.makeText(ctx,ctx.getString(R.string.chpwdsuccs),Toast.LENGTH_SHORT).show();
                       }
                        if (response.equals("fail")){
                            Toast.makeText(ctx,ctx.getString(R.string.failchpwd),Toast.LENGTH_SHORT).show();
                        }
                        if (response.equals("notexist")){
                            Toast.makeText(ctx,ctx.getString(R.string.fakeac),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast toast = Toast.makeText(ctx, ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        queue.add(stringRequest);

    }
    private void httpStringRequest(String url,String data) {
         RequestQueue queue = Volley.newRequestQueue(ctx);
// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + url + "?" + data,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            strResponse = response;
                            printResponse(strResponse);
                       //     doLogin(strResponse);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast toast = Toast.makeText(ctx,ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            queue.add(stringRequest);

    }
    public void printResponse(String response){
        Toast toast=null;
        if (!response.equals("fail")) {
            toast = Toast.makeText(ctx, "Response\n" + response, Toast.LENGTH_SHORT);
        }else {
            toast = Toast.makeText(ctx, "Else Response\n" + response, Toast.LENGTH_SHORT);

        }
        toast.show();
    }
    //END STRING REQUEST
    private void httpJsonRequest(String url,String data) {
        //JSON REQUEST AND RETRIEVE
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, " http://"+url+"?"+data, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            jsonResponse=response;
                            Toast.makeText(ctx, "Response: \n" + response.getString("name").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(ctx,ctx.getString(R.string.servernotconnect), Toast.LENGTH_LONG);

                    }
                });
        queue.add(jsObjRequest);
    }
  }

