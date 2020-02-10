package com.example.bpradmin.igisubizo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PaymentHistory extends ActionBarActivity {
public DatePicker calView,calViewTo;
    public Button btnDownload;
    public ArrayList dateRange;
    public TextView selDate;
    public Synchronizer synchronizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo);
        synchronizer=new Synchronizer(PaymentHistory.this);
        calView=(DatePicker) findViewById(R.id.calView);
        calViewTo=(DatePicker) findViewById(R.id.calViewTo);
        btnDownload=(Button) findViewById(R.id.btnDownload);
        selDate=(TextView) findViewById(R.id.seldate);
        calView.setMaxDate(new Date().getTime());
        calViewTo.setMaxDate(new Date().getTime());
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                    String from = calView.getYear() + "-" + (calView.getMonth()+1) + "-" + calView.getDayOfMonth();
                    String to =calViewTo.getYear()+"-"+(calViewTo.getMonth()+1)+"-"+calViewTo.getDayOfMonth();
                    if(sdf.parse(to).before(sdf.parse(from))){
                        Toast.makeText(PaymentHistory.this,"To Date can not be Previous of From",Toast.LENGTH_LONG).show();
                    }else{
                        //date are Correct Pass Data to Server
                        String uid=synchronizer.getSession(),payertype=synchronizer.getSessionCategory();
                        switch (payertype){
                            case"postoffice":
                                payertype="Postoffice";
                                break;
                            case"commissioner":
                                payertype="Commissioner";
                                break;
                            default:
                                payertype="invalid";
                        }
                        downloadReport("http://"+synchronizer.getHost()+"/ajax/payhist?cate=report&payertype="+payertype+"&payerid="+uid+"&start="+from+"&end="+to);
                             }
                }catch (ParseException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    public void downloadReport(final String url){
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {

                Uri uri = Uri.parse(url);
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
        //getMenuInflater().inflate(R.menu.payment_history, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
