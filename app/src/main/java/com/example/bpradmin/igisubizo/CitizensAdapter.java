package com.example.bpradmin.igisubizo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bpradmin on 7/6/2018.
 */
public class CitizensAdapter extends RecyclerView.Adapter<CitizensAdapter.ViewHolder> {

    //    private List<String> mData = Collections.emptyList();
    private JSONObject mData = null;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context ctx;

    // data is passed into the constructor
   /* public CitizensAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }
    */
    public CitizensAdapter(Context context, JSONObject data) {
        ctx=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.citizen_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            //String animal=mData.get(position);
            //holder.myTextView.setText(animal);
            JSONArray jsonArray=mData.getJSONArray("citizens");
            JSONObject jsonData=jsonArray.getJSONObject(position);
            String citid=jsonData.getString("cit_id");
            String cit_names = jsonData.getString("cit_names");
            String cit_nid =jsonData.getString("cit_nid");
            String regdate = jsonData.getString("regdate");

            holder.citid.setText(citid);
            holder.tvOwner.setText(cit_names);
            holder.tvNidNbr.setText(cit_nid);
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
            retData= mData.getJSONArray("citizens").length();
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return retData;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView myTextView,tvOwner,tvLostDtStr,tvNidNbr,tvType,tvGotDate,citid;
        public Button btnEdit,btnView;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.tvAnimalName);
            citid=(TextView) itemView.findViewById(R.id.citid);
            tvOwner = (TextView) itemView.findViewById(R.id.tvOwner);
            tvNidNbr = (TextView) itemView.findViewById(R.id.tvNidNbr);
            tvGotDate = (TextView) itemView.findViewById(R.id.tvGotDate);
            btnView=(Button) itemView.findViewById(R.id.btnViewDocs);
            btnEdit=(Button) itemView.findViewById(R.id.btnEdit);
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ctx,ViewQueues.class);
                    intent.putExtra("nid",tvNidNbr.getText().toString());
                    ctx.startActivity(intent);
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ctx,UpdateCitizens.class);
                    intent.putExtra("citid",citid.getText().toString());
                ctx.startActivity(intent);
                }
            });
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
            i = mData.getJSONArray("citizens").getJSONObject(id);
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