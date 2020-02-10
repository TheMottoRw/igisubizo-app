package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, MyRecyclerViewAdapter.ItemClickListener {
    SectionsPagerAdapter mSectionsPagerAdapter;
    public static Spinner autoType;
    public static EditText edtOwner,edtIdentification,edtSearch;
    public static Button btnRegLosts;
    public boolean searchVisible=false;
    public Synchronizer synchronizer;
    MyRecyclerViewAdapter adapter;
    public ProgressDialog progressDialog;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        synchronizer=new Synchronizer(MainActivity.this);
        if (synchronizer.checkSessionsExistance()==false){
            startActivity(new Intent(MainActivity.this,Enduser.class));
            finish();
        }else{
           // redirect();
        }
         synchronizer.synchLosts();
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
                        msg = "New Lost";
                        break;
                    case 1:
                        msg = "Losts Registered";
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_lock) {
            Intent intent = new Intent(MainActivity.this,changepwd.class);
            startActivity(intent);
        }
        if (id == R.id.Profil) {
            Intent intent = new Intent(MainActivity.this,PostProfil.class);
            startActivity(intent);
        }
        if (id==R.id.search){
            //   Toast.makeText(MainActivity.this,"Search Views",Toast.LENGTH_LONG).show();
               int tabPos=getSupportActionBar().getSelectedTab().getPosition();
            if (tabPos==1) {
                if(searchVisible==false) {
                 searchVisible=true;
                    edtSearch.setVisibility(View.VISIBLE);
                }else{
                    searchVisible=false;
                    edtSearch.setVisibility(View.GONE);
                }
            }
            }
        if (id == R.id.Payment) {
            Intent intent = new Intent(MainActivity.this,EnduserPayment.class);
            startActivity(intent);
        }
        if (id == R.id.accounts) {
            Intent intent = new Intent(MainActivity.this,AccountsIndex.class);
            startActivity(intent);
        }
        if(id==R.id.balance){
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.loaddata));
            progressDialog.show();
            String uid = synchronizer.getSession();
            String url = synchronizer.getHost()+"/ajax/postoffices.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/citizens.php";
            String data = "cate=loadbyid&id=" + uid;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                progressDialog.dismiss();
                                final AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle(getString(R.string.accbalance));
                                alertDialog.setMessage(getString(R.string.accamount)+" "+response.getJSONArray("postoffice").getJSONObject(0).getString("usr_amount")+" RWF");
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
                            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }

        if(id==R.id.help){
            startActivity(new Intent(MainActivity.this,Issues.class));
        }
        if(id==R.id.logout){
            SharedPreferences sharedPreferences=this.getSharedPreferences("igisubizologgedID", MODE_PRIVATE);
            this.getSharedPreferences("igisubizologgedCate",MODE_PRIVATE).edit().clear().commit();
            SharedPreferences.Editor edit=sharedPreferences.edit().clear();
            edit.commit();
            Intent intent = new Intent(MainActivity.this,Enduser.class);
          startActivity(intent);
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
               setContentView(R.layout.fragment_addnew);
                autoType=(Spinner) findViewById(R.id.autoType);
                edtOwner=(EditText) findViewById(R.id.edtOwner);
                edtIdentification=(EditText) findViewById(R.id.edtIdentification);
                btnRegLosts=(Button) findViewById(R.id.btnAdd);
                btnRegLosts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addLosts();
                    }
                });
                setTypeSpinner();
                break;
            case 1:
               setContentView(R.layout.fragment_lost);
                edtSearch=(EditText) findViewById(R.id.edtsearch);
                edtSearch.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        searchLosts();
                        return true;
                    }
                });
                loadLosts();
                break;
            default:
        }
    }
    public void addLosts(){
        synchronizer=new Synchronizer(MainActivity.this);
       if (!edtOwner.getText().toString().isEmpty()&&!edtIdentification.getText().toString().isEmpty()){
         synchronizer.addLosts(edtOwner.getText().toString().replaceAll(" ", "%20"), edtIdentification.getText().toString().replaceAll(" ","%20"), autoType.getSelectedItem().toString().replaceAll(" ", "%20"));
    edtOwner.setText(""); edtIdentification.setText("");autoType.setSelection(0);
    }
    }
    public void loadLosts(){
        synchronizer=new Synchronizer(MainActivity.this);
        if (synchronizer.checkConnectionState()) {
            progressDialog=new ProgressDialog(this);
                    progressDialog.setMessage(getString(R.string.loadlosts));
            progressDialog.setCancelable(false);
            progressDialog.show();
            String uid = synchronizer.getSession();
             String url = synchronizer.getHost()+"/ajax/losts.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
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
                            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
           Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void searchLosts(){
        synchronizer=new Synchronizer(MainActivity.this);
        if (synchronizer.checkConnectionState()) {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loadlosts));
            progressDialog.setCancelable(false);
            //progressDialog.show();
            String uid=synchronizer.getSession();
            String ky=edtSearch.getText().toString();
            String url = synchronizer.getHost()+"/ajax/losts.php";
            //String url = "192.168.173.1:12/RUT/etracking/ajax/losts.php";
            String data = "cate=searchbykeys&postoffid="+uid+"&key=" + ky;

            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, " http://" + url + "?" + data, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setLostsView(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            error.printStackTrace();
                            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
            queue.add(jsObjRequest);
        }else{
            Toast.makeText(this,getString(R.string.offline),Toast.LENGTH_SHORT).show();
        }
    }
    public void setLostsView(JSONObject data){
        try{
        if(data.getJSONArray("losts").length()>0){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvLosts);
            recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new MyRecyclerViewAdapter(MainActivity.this, data);
        adapter.setClickListener(MainActivity.this);
        recyclerView.setAdapter(adapter);
        }else{
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvLosts);
            recyclerView.setVisibility(View.GONE);
        }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }
    public void setTypeSpinner(){
        synchronizer=new Synchronizer(MainActivity.this);
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
                                Toast toast = Toast.makeText(MainActivity.this,getString(R.string.servernotconnect), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                queue.add(jsObjRequest);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.servernotconnect), Toast.LENGTH_SHORT).show();
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
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
        //    Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UpdateLosts.class);
            intent.putExtra("lostid", adapter.getItem(position).getString("lost_id"));
            startActivity(intent);
        }catch(JSONException ex){
            ex.printStackTrace();
        }
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
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
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