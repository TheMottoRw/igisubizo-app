package com.example.bpradmin.igisubizo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

//    private List<String> mData = Collections.emptyList();
private JSONObject mData = null;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    public int currPosition;
    public View currView;
    Context ctx;
Synchronizer synchronizer;
    // data is passed into the constructor
   /* public MyRecyclerViewAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }
    */
    public MyRecyclerViewAdapter(Context context, JSONObject data) {
        ctx=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        synchronizer=new Synchronizer(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            //String animal=mData.get(position);
            //holder.myTextView.setText(animal);
            JSONArray jsonArray=mData.getJSONArray("losts");
            JSONObject jsonData=jsonArray.getJSONObject(position);
            String lostid=jsonData.getString("lost_id");
            String owner = jsonData.getString("owner");
            String doctype =jsonData.getString("doctype");
            String identifier = jsonData.getString("identifier");
            String regdate = jsonData.getString("regdate");
            holder.tvPayamount.setText(jsonData.getString("pay_amount"));
            holder.tvPayid.setText(jsonData.getString("pay_id"));
            holder.lostid.setText(lostid);
             holder.tvLostOwner.setText(owner);
            holder.tvLostType.setText(doctype);
            holder.tvLostidentRetr.setText(identifier);
            holder.tvGotDate.setText(regdate);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        int retData=0;
        try {
         retData= mData.getJSONArray("losts").length();
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return retData;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvPayid,tvPayamount,tvLostOwner,tvLostDtStr,tvLostidentRetr,tvLostType,tvGotDate,lostid;
        public Button tvOwnerFound;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvPayid=(TextView) itemView.findViewById(R.id.tvpayid);
            tvPayamount = (TextView) itemView.findViewById(R.id.tvpayamount);
            lostid=(TextView) itemView.findViewById(R.id.lostid);
            tvLostOwner = (TextView) itemView.findViewById(R.id.tvLostOwner);
            tvLostidentRetr = (TextView) itemView.findViewById(R.id.tvLostidentRetr);
            tvLostType = (TextView) itemView.findViewById(R.id.tvLostType);
            tvGotDate = (TextView) itemView.findViewById(R.id.tvGotDate);
            tvOwnerFound = (Button) itemView.findViewById(R.id.tvOwnerFound);
tvOwnerFound.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //show amount to be paid,to take losts
        final AlertDialog alert=new AlertDialog.Builder(ctx).create();
        alert.setTitle(ctx.getString(R.string.mustpaylosts));
        alert.setMessage(ctx.getString(R.string.lostsamount) + " " + tvPayamount.getText() + " RWF");
        alert.setButton(DialogInterface.BUTTON_POSITIVE,ctx.getString(R.string.btnok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Synchronizer synchronizer = new Synchronizer(ctx);
                synchronizer.setFoundLost(itemView,lostid.getText().toString(), tvPayid.getText().toString(), tvPayamount.getText().toString());
                dialog.dismiss();
            }
        });
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, ctx.getString(R.string.btncancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
       // ctx.startActivity(new Intent(ctx,MainActivity.class));
    }
});
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public JSONObject getItem(int id) {
        JSONObject i=null;
        try {
            i = mData.getJSONArray("losts").getJSONObject(id);
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return i;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}