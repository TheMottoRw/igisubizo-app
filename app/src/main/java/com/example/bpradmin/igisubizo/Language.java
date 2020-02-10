package com.example.bpradmin.igisubizo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Locale;

/**
 * Created by BPRAdmin on 10/10/2017.
 */
public class Language extends AppCompatActivity{
    public Switch langs;
    public Button lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language); final ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        final Synchronizer synchronizer=new Synchronizer(Language.this);
Button btnLang=(Button) findViewById(R.id.btnLang);
        btnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                redirect();
            }
        });
        langs=(Switch) findViewById(R.id.langs);
            setLang(false);
        langs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setLang(langs.isChecked());
            }
        });
    }
    public void setLang(boolean val){
        if (val) {
            Configuration config = new Configuration();
            config.locale = Locale.ENGLISH;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
           // Toast.makeText(Language.this, "Lang to Load Eng", Toast.LENGTH_LONG).show();
        } else {
            Locale locale = new Locale("rw");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
         //   Toast.makeText(Language.this, "Lang to Load Rw", Toast.LENGTH_LONG).show();
        }
    }
    public void redirect() {
        String sh = getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (sh.equals("0")) {
            intent.setClass(Language.this, Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                intent.setClass(Language.this, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(Language.this, Commissioner.class);
            }
        }
        startActivity(intent);
       // Toast.makeText(Language.this,sh,Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.action_lock){
          //Intent intent=new Intent("com.example.bpradmin.etrackclient.queue");
            //startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
