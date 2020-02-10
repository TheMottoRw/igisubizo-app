package com.example.bpradmin.igisubizo;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class Commissioner  extends ActionBarActivity implements ActionBar.TabListener, CitizensAdapter.ItemClickListener,KeyListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    public static Spinner autoType;
    public static EditText edtOwner,edtIdentification,edtSearch,edtPhone,edtNid,edtNiD;
    public static Button btnRegQueue,btnRegItem;
    public TextView readyRegistered,yetRegistered;
    public  Spinner spnType;
    public LinearLayout readyContent,yetContent;
    public MenuItem menuItem;
    public Synchronizer synchronizer;
    public Validator validator;

    CitizensAdapter adapter;
    public ProgressDialog progressDialog;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        synchronizer=new Synchronizer(this);
        validator=new Validator();
        if (synchronizer.checkSessionsExistance()==false){
            startActivity(new Intent(Commissioner.this,Enduser.class));
            finish();
        }
        //load Citizens when Activity starts
        //  loadCitizens();
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
        mViewPager = (ViewPager) findViewById(R.id.pager);
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
                        msg = "Add New";
                        break;
                    case 1:
                        msg = "Citizens";
                        //  Toast.makeText(Commissioner.this, "Position " + position + " =>" + msg, Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.menu_commissioner, menu);
        menuItem=(MenuItem) menu.findItem(R.id.search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.Profil) {
            Intent intent = new Intent(Commissioner.this,CommissionerProfil.class);
            startActivity(intent);
        }
        if (id == R.id.Payment) {
            Intent intent = new Intent(Commissioner.this,EnduserPayment.class);
            startActivity(intent);
        }
        if (id == R.id.accounts) {
            Intent intent = new Intent(Commissioner.this,AccountsIndex.class);
            startActivity(intent);
        }
        if (id==R.id.search){
            //   Toast.makeText(Commissioner.this,"Search Views",Toast.LENGTH_LONG).show();
            int tabPos=getSupportActionBar().getSelectedTab().getPosition();
            if (tabPos==1)
                edtSearch.setVisibility(View.VISIBLE);
        }
        if(id==R.id.balance){
            progressDialog=new ProgressDialog(Commissioner.this);
            progressDialog.setMessage(getString(R.string.loaddata));
            progressDialog.show();
            String uid = synchronizer.getSession();
            String url = synchronizer.getHost()+"/ajax/users.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/citizens.php";
            String data = "cate=loadbyid&id=" + uid;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                progressDialog.dismiss();
                                final AlertDialog alertDialog=new AlertDialog.Builder(Commissioner.this).create();
                                alertDialog.setTitle(getString(R.string.accbalance));
                                alertDialog.setMessage(getString(R.string.accamount)+" "+response.getJSONArray("user").getJSONObject(0).getString("usr_amount")+" RWF");
                                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                            }catch (JSONException ex){
                                ex.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast toast = Toast.makeText(Commissioner.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.action_lock) {
            Intent intent = new Intent(Commissioner.this,changepwd.class);
            startActivity(intent);
        }
        if(id==R.id.help){
            startActivity(new Intent(Commissioner.this,Issues.class));
        }
        if(id==R.id.logout){
            synchronizer.logout();
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
                setContentView(R.layout.queues);
                progressDialog=new ProgressDialog(Commissioner.this);
                progressDialog.setCancelable(false);
                autoType=(Spinner) findViewById(R.id.spnType);
                edtOwner=(EditText) findViewById(R.id.edtOwner);
                edtIdentification=(EditText) findViewById(R.id.edtIdentification);
                edtNid=(EditText) findViewById(R.id.edtNid);
                edtNiD=(EditText) findViewById(R.id.edtNiD);
                edtPhone=(EditText) findViewById(R.id.edtPhone);
                spnType=(Spinner) findViewById(R.id.spnType);
                btnRegQueue=(Button) findViewById(R.id.btnRegQueue);
                btnRegItem=(Button) findViewById(R.id.btnRegItem);
                readyRegistered=(TextView) findViewById(R.id.readyRegistered);
                yetRegistered=(TextView) findViewById(R.id.yetRegistered);
                readyContent=(LinearLayout) findViewById(R.id.readyContent);
                yetContent=(LinearLayout) findViewById(R.id.yetContent);
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
                        if (synchronizer.checkConnectionState() == true) {
                            if (edtOwner.getText().toString().trim().length() != 0 && edtNid.getText().toString().trim().length() != 0 || edtPhone.getText().toString().trim().length() != 0) {
                                if (validator.nid(edtNid.getText().toString().trim())) {
                                    if (validator.phone(edtPhone.getText().toString().trim())) {
                                        final AlertDialog alert = new AlertDialog.Builder(Commissioner.this).create();
                                        alert.setTitle(getString(R.string.mustpay));
                                        alert.setMessage(getString(R.string.payacamount));
                                        String url = synchronizer.getHost() + "/ajax/payments.php";
                                        String data = "cate=registration";
                                        RequestQueue queue = Volley.newRequestQueue(Commissioner.this);
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
                                                        Toast.makeText(Commissioner.this, getString(R.string.servernotconnect) + "->" + error.getMessage().toString(), Toast.LENGTH_LONG).show();

                                                    }
                                                });
                                        queue.add(jsObjRequest);
                                        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                addCitizens();
                                            }
                                        });
                                        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(Commissioner.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                                        alert.show();
                                    } else {
                                        Toast.makeText(Commissioner.this, getString(R.string.offline), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
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
            case 1:
                setContentView(R.layout.fragment_citizens);
             /*   edtSearch=(EditText) findViewById(R.id.edtsearch);
                edtSearch.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        searchCitizens();
                        return true;
                    }
                });
                */
                loadCitizens();
                break;
            default:
        }
    }
    //Register Citizens
    public void addCitizens(){
        synchronizer=new Synchronizer(Commissioner.this);
        if (!edtOwner.getText().toString().isEmpty()&&!edtNid.getText().toString().isEmpty()&&!edtPhone.getText().toString().isEmpty()){
            synchronizer.addCitizens(edtOwner.getText().toString().replaceAll(" ", "%20"), edtNid.getText().toString().replaceAll(" ", "%20"), edtPhone.getText().toString().replaceAll(" ", "%20"));
            edtOwner.setText("");edtNid.setText(""); edtPhone.setText("");
        }
    }
    public void loadCitizens(){
        Synchronizer synchronizer=new Synchronizer(Commissioner.this);
        if (synchronizer.checkConnectionState()) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loadcitizens));
            progressDialog.setCancelable(false);
            progressDialog.show();
            String uid = synchronizer.getSession();
            String url = synchronizer.getHost()+"/ajax/citizens.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/citizens.php";
            String data = "cate=load&commid=" + uid;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setCitizensView(response);
                            progressDialog.dismiss();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast toast = Toast.makeText(Commissioner.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void searchCitizens(){
        Synchronizer synchronizer=new Synchronizer(Commissioner.this);
        if (synchronizer.checkConnectionState()) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loadcitizens));
            //progressDialog.show();
            String uid=synchronizer.getSession();
            //String ky=edtSearch.getText().toString();
            String url = synchronizer.getHost()+"/ajax/citizens.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/citizens.php";
            String data = "cate=searchbykeys&commid="+uid+"&key=";// + ky;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setCitizensView(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            error.printStackTrace();
                            Toast toast = Toast.makeText(Commissioner.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void setCitizensView(JSONObject data){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvCitizens);
        recyclerView.setLayoutManager(new LinearLayoutManager(Commissioner.this));
        adapter = new CitizensAdapter(Commissioner.this, data);
        adapter.setClickListener(Commissioner.this);
        recyclerView.setAdapter(adapter);
    }
    public void setTypeSpinner(){
        Synchronizer synchronizer=new Synchronizer(Commissioner.this);
        String uid=synchronizer.getSession();
        //   Toast.makeText(this,"Connection State "+synchronizer.checkConnectionState(),Toast.LENGTH_SHORT).show();
        if (synchronizer.checkConnectionState() == true) {

            String url = synchronizer.getHost()+"/ajax/types.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/types.php";
            String data = "cate=load";
            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setTypesView(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            // TODO Auto-generated method stub
                            Toast toast = Toast.makeText(Commissioner.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        } else {
            Toast.makeText(Commissioner.this, getString(R.string.servernotconnect), Toast.LENGTH_SHORT).show();
        }
    }
    public void setTypesView(JSONObject obj){//Fill Up Loaded Type to Spinner
        try {
            JSONArray arr = obj.getJSONArray("types");
            String[] arrTypes=new String[arr.length()];
            for(int i=0;i<arr.length();i++){
                arrTypes[i]=arr.getJSONObject(i).getString("doctype");
            }
            ArrayAdapter arrayAdapter=new ArrayAdapter(this,R.layout.select_dialog_item_material,arrTypes);
            autoType.setAdapter(arrayAdapter);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    //ABOUTS QUEUES
    public void addRegQueue(){
        Synchronizer synchronizer=new Synchronizer(this);
        if (edtNiD.getText().toString().equals("") || edtIdentification.getText().toString().equals("")){
            Toast.makeText(Commissioner.this, getString(R.string.nulls), Toast.LENGTH_LONG).show();
        }else {
            if(validator.nid(edtNiD.getText().toString().trim().replaceAll(" ",""))){
                synchronizer.addQueue(edtNiD.getText().toString().replaceAll(" ", "%20"), edtIdentification.getText().toString().replaceAll(" ", "%20"), spnType.getSelectedItem().toString().replaceAll(" ", "%20"));
                edtNiD.setText("");edtIdentification.setText("");
            }else{
                Toast.makeText(Commissioner.this,getString(R.string.invalidnid),Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onItemClick(View view, int position) {
           final int pos=position;
            //    Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
            final AlertDialog alertDialog=new AlertDialog.Builder(Commissioner.this).create();
            RadioGroup radioGroup=new RadioGroup(this);
            RadioButton btnEdit=new RadioButton(this);
        btnEdit.setChecked(true);
            RadioButton btnView=new RadioButton(this);
        radioGroup.addView(btnView);
            radioGroup.addView(btnEdit);
        alertDialog.setView(radioGroup);
            alertDialog.setTitle(getString(R.string.chooseaction));
            alertDialog.show();
        try {
            Intent intent = new Intent(Commissioner.this, UpdateCitizens.class);
            intent.putExtra("citid", adapter.getItem(pos).getString("citizen_id"));
            startActivity(intent);
            Toast.makeText(Commissioner.this, "ID Clicked " + adapter.getItem(position).getString("citid").toString(), Toast.LENGTH_SHORT).show();
        }catch (JSONException ex){
            ex.printStackTrace();
        }
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        alertDialog.dismiss();
                        Intent intent = new Intent(Commissioner.this, UpdateCitizens.class);
                        intent.putExtra("citid", adapter.getItem(pos).getString("citizen_id"));
                        startActivity(intent);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    alertDialog.dismiss();
                    Intent intent = new Intent(Commissioner.this, UpdateCitizens.class);
                    intent.putExtra("citid", adapter.getItem(pos).getString("citizen_id"));
                    startActivity(intent);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getInputType() {
        return 0;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            AlertDialog dialog=new AlertDialog.Builder(Commissioner.this).create();
            dialog.setMessage("Are you Sure you want to Exit");
            dialog.show();
        }
        return false;
    }

    @Override
    public boolean onKeyOther(View view, Editable text, KeyEvent event) {

        return false;
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int states) {

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
                    return getString(R.string.registered).toUpperCase(l);
                case 1:
                    return getString(R.string.regnew).toUpperCase(l);
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
            View rootView = inflater.inflate(R.layout.fragment_addnew, container, false);
            return rootView;
        }
    }
}
