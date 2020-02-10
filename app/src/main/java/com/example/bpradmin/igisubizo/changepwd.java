package com.example.bpradmin.igisubizo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by BPRAdmin on 10/6/2017.
 */
public class changepwd extends ActionBarActivity {
    Synchronizer synchronizer;
    public EditText edtOld,edtNewPwd,edtConfNewPwd;
    public Button btnUpdPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
        synchronizer=new Synchronizer(this);
        edtOld=(EditText) findViewById(R.id.edtPwdOld);
        edtNewPwd=(EditText) findViewById(R.id.edtPwdNew);
        edtConfNewPwd=(EditText) findViewById(R.id.edtPwdConf);
        btnUpdPwd=(Button) findViewById(R.id.btnUpdwd);
        btnUpdPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwd();
            }
        });
        if (synchronizer.checkSessionsExistance()==false){
            startActivity(new Intent("com.example.bpradmin.etrack.login"));
            finish();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);

    }
    public void changePwd(){
        if (edtOld.getText().toString().equals("") || edtNewPwd.getText().toString().equals("") || edtConfNewPwd.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.passwordboxmust), Toast.LENGTH_LONG).show();
        }else {
            if (edtNewPwd.getText().toString().equals(edtConfNewPwd.getText().toString())) {
                if (synchronizer.checkSessionsExistance()) {
                    synchronizer.changePwd(edtOld.getText().toString().replaceAll(" ", "%20"), edtNewPwd.getText().toString().replaceAll(" ", "%20"));
            edtOld.setText("");edtNewPwd.setText("");edtConfNewPwd.setText("");
                } else {
                    Toast.makeText(this, "No Session Exist", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.pwddontmatch), Toast.LENGTH_LONG).show();
            }
        }
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
        if(id==16908332) {
            finish();
        }
        if(id==R.id.Profil) {
            startActivity(new Intent(changepwd.this,PostProfil.class));
            finish();
        }
        if(id==R.id.logout){
            synchronizer.logout();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void redirect() {
        String sh = getSharedPreferences("igisubizologgedCate", Context.MODE_PRIVATE).getString("category", "0");
        Intent intent = new Intent();
        if (sh.equals("0")) {
            intent.setClass(changepwd.this,Enduser.class);
        } else {
            if (sh.equals("postoffice")) {
                    intent.setClass(changepwd.this, MainActivity.class);
            }
            if (sh.equals("commissioner")) {
                intent.setClass(changepwd.this, Commissioner.class);
            }
            startActivity(intent);
        }
    }
}
