package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.util.Locale;

/**
 * Created by bpradmin on 11/7/2017.
 */
public class Enduser  extends ActionBarActivity implements ActionBar.TabListener{
    public Spinner spnType;
    SectionsPagerAdapter mSectionsPagerAdapter;
    public static Spinner autoType;
    public static EditText edtOwner,edtIdentification,edtSearch,edtPhone,edtNid,edtNiD,edtSearchKey,edtCitname;;
    public static Button btnRegQueue,btnRegItem,btnSearch;
    public TextView readyRegistered,yetRegistered,tvCitname;
    public LinearLayout readyContent,yetContent;
    public MenuItem menuItem;
    public ProgressDialog progressDialog;
    public Synchronizer synchronizer;
    public Validator validator;
    public Text tvqHeader;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enduser);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        synchronizer=new Synchronizer(Enduser.this);
        validator=new Validator();
        //load Losts when Activity starts
        //  loadLosts();
        //  Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.enduserpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                String msg = "";
                actionBar.setSelectedNavigationItem(position);
                switch (position) {
                    case 0:
                        msg = "Search Queue";
                        break;
                    case 1:
                        msg = "Regsiter On Queue";
                        //  Toast.makeText(MainActivity.this, "Position " + position + " =>" + msg, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enduser, menu);
        menuItem=(MenuItem) menu.findItem(R.id.search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.Profil){
            startActivity(new Intent(Enduser.this,ViewQueues.class));
        }
        if(id==R.id.Payment){
            startActivity(new Intent(Enduser.this,EnduserPayment.class));
        }
        if (id==R.id.search){
            startActivity(new Intent(Enduser.this,login.class));
            finish();
        }
        if (id==R.id.exit){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()){
            case 0:
                setContentView(R.layout.search_losts);
                btnSearch=(Button) findViewById(R.id.btnSearch);
                edtSearchKey=(EditText) findViewById(R.id.searchKey);
                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchLosts();
                    }
                });
                android.app.Fragment fragment;
                android.app.FragmentManager fm=getFragmentManager();
                android.app.FragmentTransaction ft=fm.beginTransaction();
                fragment=new index();
                ft.replace(R.id.fragContainer, fragment);
                ft.commit();
                break;
            case 1:
                setContentView(R.layout.queues);
                tvCitname=(TextView) findViewById(R.id.tvCitname);
                edtOwner=(EditText) findViewById(R.id.edtOwner);
                edtIdentification=(EditText) findViewById(R.id.edtIdentification);
                edtNid=(EditText) findViewById(R.id.edtNid);
                edtCitname=(EditText) findViewById(R.id.edtCitname);
                edtNiD=(EditText) findViewById(R.id.edtNiD);
                edtPhone=(EditText) findViewById(R.id.edtPhone);
                spnType=(Spinner) findViewById(R.id.spnType);
                btnRegQueue=(Button) findViewById(R.id.btnRegQueue);
                btnRegItem=(Button) findViewById(R.id.btnRegItem);
                readyRegistered=(TextView) findViewById(R.id.readyRegistered);
                yetRegistered=(TextView) findViewById(R.id.yetRegistered);
                readyContent=(LinearLayout) findViewById(R.id.readyContent);
                yetContent=(LinearLayout) findViewById(R.id.yetContent);
                edtNiD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                      if(hasFocus==false) {
                          if (edtNiD.getText().toString().trim().replace(" ","").length() == 16) {
                              if (!validator.nid(edtNiD.getText().toString().trim().replace(" ", ""))) {
                                  final AlertDialog alertDialog = new AlertDialog.Builder(Enduser.this).create();
                                  alertDialog.setMessage(getString(R.string.invalidnid));
                                  alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnok), new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          alertDialog.dismiss();
                                      }
                                  });
                                  alertDialog.show();
                              } else {//ID is Valid
                                  searchCitizen();
                              }
                          }else{ final AlertDialog alertDialog = new AlertDialog.Builder(Enduser.this).create();
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
                edtNid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                      if(hasFocus==false) {
                          if (edtNid.getText().toString().trim().replace(" ","").length() == 16) {
                              if (!validator.nid(edtNid.getText().toString().trim().replace(" ",""))) {
                                  final AlertDialog alertDialog = new AlertDialog.Builder(Enduser.this).create();
                                  alertDialog.setMessage(getString(R.string.invalidnid));
                                  alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnok), new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          alertDialog.dismiss();
                                      }
                                  });
                                  alertDialog.show();
                              }
                          }else{ final AlertDialog alertDialog = new AlertDialog.Builder(Enduser.this).create();
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

                edtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus==false){
                            if(edtPhone.getText().length()==10){
                                if(!validator.phone(edtPhone.getText().toString())){
                                final AlertDialog alertDialog=new AlertDialog.Builder(Enduser.this).create();
                                alertDialog.setMessage(getString(R.string.invalidphone));
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                                }
                            }else{
                                final AlertDialog alertDialog=new AlertDialog.Builder(Enduser.this).create();
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
                readyRegistered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        readyContent.setVisibility(View.VISIBLE);
                        yetContent.setVisibility(View.GONE);
                    }
                });
                yetRegistered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        readyContent.setVisibility(View.GONE);
                        yetContent.setVisibility(View.VISIBLE);
                    }
                });
                btnRegQueue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtOwner.getText().toString().trim().length()!=0 && edtNid.getText().toString().trim().length()!=0 && edtPhone.getText().toString().trim().length()!=0) {
                         if(validator.nid(edtNid.getText().toString().trim().replace(" ",""))){
                             if(validator.phone(edtPhone.getText().toString().trim())){
                            final AlertDialog alert = new AlertDialog.Builder(Enduser.this).create();
                            alert.setTitle(getString(R.string.mustpay));
                            alert.setMessage(getString(R.string.payacamount));
                            String url = synchronizer.getHost() + "/ajax/payments.php";
                            String data = "cate=registration";
                            RequestQueue queue = Volley.newRequestQueue(Enduser.this);
                            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject payments = response.getJSONArray("payments").getJSONObject(0);
                                                JSONArray paymodes = response.getJSONArray("paymodes");
                                                String msgData = getString(R.string.regamount) + payments.getString("pay_amount") + " RWF\n";
                                                msgData += getString(R.string.paidonac) + "\n\n";
                                                msgData += getString(R.string.acinfo) + "\n------------------------------------\n";
                                                for (int i = 0; i < paymodes.length(); i++) {
                                                    msgData += getString(R.string.actype) + paymodes.getJSONObject(i).getString("pmtd_name") + " \n";
                                                    msgData += getString(R.string.acname) + paymodes.getJSONObject(i).getString("pmtd_account_name") + " \n";
                                                    msgData += getString(R.string.acnbr) + paymodes.getJSONObject(i).getString("pmtd_account_number") + " \n\n";
                                                }
                                                alert.setMessage(msgData);
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO Auto-generated method stub
                                            progressDialog.dismiss();
                                            error.printStackTrace();
                                            Toast.makeText(Enduser.this, getString(R.string.servernotconnect) + "->" + error.getMessage().toString(), Toast.LENGTH_LONG).show();

                                        }
                                    });
                            queue.add(jsObjRequest);
                            alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addCitizens();
                                }
                            });
                            alert.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btncancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Enduser.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                            alert.show();
                             }else{
                                 Toast.makeText(Enduser.this,getString(R.string.invalidphone),Toast.LENGTH_SHORT).show();
                             }
                         }else{
                             Toast.makeText(Enduser.this,getString(R.string.invalidnid),Toast.LENGTH_SHORT).show();
                         }
                        }else{
                            Toast.makeText(Enduser.this,getString(R.string.nulls),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnRegItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addRegQueue();
                    }
                });
                setTypeSpinner();
                break;
            default:
        }
    }
    //Register Citizens
    public void addCitizens(){
        synchronizer=new Synchronizer(Enduser.this);
        if (edtOwner.getText().toString().trim().length()!=0 && edtNid.getText().toString().trim().length()!=0 && edtPhone.getText().toString().trim().length()!=0){
            synchronizer.addCitizens(edtOwner.getText().toString().replaceAll(" ", "%20"), edtNid.getText().toString().replaceAll(" ", "%20"), edtPhone.getText().toString().replaceAll(" ", "%20"));
            edtOwner.setText("");edtNid.setText(""); edtPhone.setText("");
        }else{
            Toast.makeText(Enduser.this,getString(R.string.nulls),Toast.LENGTH_SHORT).show();
        }
    }
    //SEARCH CITIZENS
    public void searchCitizen(){
        Synchronizer synchronizer=new Synchronizer(this);
        if (synchronizer.checkConnectionState()){
//      String uid=synchronizer.getSession();
            if (!validator.nid(edtNiD.getText().toString().trim().replace(" ", ""))){

                Toast.makeText(this, getString(R.string.invalidnid), Toast.LENGTH_LONG).show();

            }else {
                String url = synchronizer.getHost()+"/ajax/citizens.php";
                //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
                String data = "cate=search&key=" + edtNiD.getText().toString().replace(" ", "%20");
                //Show Progress
                progressDialog.setMessage(getString(R.string.fetchdata) + edtNiD.getText());
               progressDialog.show();
                RequestQueue queue = Volley.newRequestQueue(this);
                final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, "http://"+url + "?" + data, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    if(!response.getJSONArray("citizens").getJSONObject(0).isNull("cit_id")){

                                        edtCitname.setText(response.getJSONArray("citizens").getJSONObject(0).getString("cit_names"));
                                        edtCitname.setVisibility(View.VISIBLE);
                                        tvCitname.setVisibility(View.VISIBLE);
                                    }else{
                                        edtCitname.setText("");
                                        edtCitname.setVisibility(View.GONE);
                                        tvCitname.setVisibility(View.GONE);
                                        final AlertDialog alertDialog=new AlertDialog.Builder(Enduser.this).create();
                                        alertDialog.setTitle(getString(R.string.citnotexisheader));
                                        alertDialog.setMessage(getString(R.string.citnotexismsg));
                                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btnok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.dismiss();
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                }catch(JSONException ex){
                                    ex.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                progressDialog.dismiss();
                                error.printStackTrace();
                                Toast.makeText(Enduser.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG).show();
                            }
                        });
                queue.add(jsObjRequest);
            }
        }else{
            progressDialog.dismiss();
            Toast.makeText(this,getString(R.string.offline)+" "+synchronizer.checkConnectionState(),Toast.LENGTH_LONG).show();
        }
    }
//SEARCH LOSTS
public void searchLosts(){
    Synchronizer synchronizer=new Synchronizer(this);
    if (synchronizer.checkConnectionState()){
//      String uid=synchronizer.getSession();
        if (edtSearchKey.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.searchboxmust), Toast.LENGTH_LONG).show();
        }else {
            String url = synchronizer.getHost()+"/ajax/losts.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
            String data = "cate=search&key=" + edtSearchKey.getText().toString().replace(" ", "%20");
            //Show Progress
            progressDialog.setMessage(getString(R.string.fetchdata) + edtSearchKey.getText());
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, "http://"+url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setLostsFragView(response);
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(Enduser.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG).show();
                        }
                    });
            queue.add(jsObjRequest);
        }
    }else{
        progressDialog.dismiss();
        Toast.makeText(this,getString(R.string.offline)+" "+synchronizer.checkConnectionState(),Toast.LENGTH_LONG).show();
    }
}
    public void setLostsFragView(JSONObject obj){
        try{
            if (obj.isNull("losts")==false){
                JSONObject jsonObject=obj.getJSONArray("losts").getJSONObject(0);
                //  Toast.makeText(SearchLosts.this,"Data Load\n"+jsonObject.getString("representer"),Toast.LENGTH_LONG).show();
                android.app.Fragment fragment=new found();
                Bundle bundle=new Bundle();
                bundle.putString("name", jsonObject.getString("name"));
                bundle.putString("representer", jsonObject.getString("representer").toString());
                bundle.putString("owner",jsonObject.getString("owner"));
                bundle.putString("payamount",jsonObject.getString("pay_amount"));
                bundle.putString("identifier",jsonObject.getString("identifier"));
                bundle.putString("doctype", jsonObject.getString("doctype"));
                bundle.putString("province", jsonObject.getString("province_name"));
                bundle.putString("district", jsonObject.getString("district_name"));
                bundle.putString("sector", jsonObject.getString("sector_name"));
                bundle.putString("cell", jsonObject.getString("cell"));
                bundle.putString("phone", jsonObject.getString("phone"));
                bundle.putString("address", jsonObject.getString("address"));
                fragment.setArguments(bundle);
                android.app.FragmentManager fm=getFragmentManager();
                android.app.FragmentTransaction ft=fm.beginTransaction();
                ft.replace(R.id.fragContainer, fragment);
                ft.commit();
                // Toast.makeText(SearchLosts.this,"Data \n"+obj.getJSONArray("losts").getJSONObject(0).getString("owner"),Toast.LENGTH_LONG).show();
            }else{
                android.app.Fragment fragment=new notfound();
                android.app.FragmentManager fm=getFragmentManager();
                android.app.FragmentTransaction ft=fm.beginTransaction();
                ft.replace(R.id.fragContainer, fragment);
                ft.commit();
                //Toast.makeText(this, "Null " + obj.toString(), Toast.LENGTH_SHORT).show();

            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }

    }
    //END
//ABOUTS QUEUES
    public void addRegQueue(){
        Synchronizer synchronizer=new Synchronizer(this);
        if (edtNiD.getText().toString().equals("") || edtIdentification.getText().toString().equals("")){
            Toast.makeText(Enduser.this, getString(R.string.nulls), Toast.LENGTH_LONG).show();
        }else {
            if(validator.nid(edtNiD.getText().toString().trim().replaceAll(" ",""))){
            synchronizer.addQueue(edtNiD.getText().toString().replaceAll(" ", "%20"), edtIdentification.getText().toString().replaceAll(" ", "%20"), spnType.getSelectedItem().toString().replaceAll(" ", "%20"));
            edtNiD.setText("");edtIdentification.setText("");
        }else{
            Toast.makeText(Enduser.this,getString(R.string.invalidnid),Toast.LENGTH_SHORT).show();
        }
        }
    }
    public void setTypeSpinner(){
        final Synchronizer synchronizer=new Synchronizer(Enduser.this);
        String url = synchronizer.getHost()+"/ajax/types.php";
        // String url = "192.168.173.1:12/RUT/etracking/ajax/types.php";
        String data = "cate=load";
        RequestQueue queue = Volley.newRequestQueue(Enduser.this);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,"http://"+url + "?" + data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setTypesView(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // TODO Auto-generated method stub
                        Toast toast = Toast.makeText(Enduser.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        queue.add(jsObjRequest);
    }
    public void setTypesView(JSONObject obj){//Fill Up Loaded Type to Spinner
        try {
            JSONArray arr = obj.getJSONArray("types");
            String[] arrTypes=new String[arr.length()];
            for(int i=0;i<arr.length();i++){
                arrTypes[i]=arr.getJSONObject(i).getString("doctype");
            }
            ArrayAdapter arrayAdapter=new ArrayAdapter(Enduser.this,R.layout.select_dialog_item_material,arrTypes);
            spnType.setAdapter(arrayAdapter);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    //END
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.btnSearch).toUpperCase(l);
                case 1:
                    return getString(R.string.btnreg).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.search_losts, container, false);
            return rootView;
        }
    }
}