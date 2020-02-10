package com.example.bpradmin.igisubizo;

/**
 * Created by bpradmin on 7/9/2018.
 */
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

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    //    private List<String> mData = Collections.emptyList();
    private JSONObject mData = null;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    public int currPosition;
    public View currView;
    Context ctx;
    Synchronizer synchronizer;
    // data is passed into the constructor
   /* public QueueAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }
    */
    public QueueAdapter(Context context, JSONObject data) {
        ctx=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        synchronizer=new Synchronizer(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.queue_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            //String animal=mData.get(position);
            //holder.myTextView.setText(animal);
            JSONArray jsonArray=mData.getJSONArray("queues");
            JSONObject jsonData=jsonArray.getJSONObject(position);
            String queueid=jsonData.getString("queue_id");
            String owner = jsonData.getString("cit_names");
            String doctype =jsonData.getString("doctype");
            String identifier = jsonData.getString("queue_identifier");
            String regdate = jsonData.getString("regdate");
            holder.queueid.setText(queueid);
            holder.tvQueueOwner.setText(owner);
            holder.tvQueueType.setText(doctype);
            holder.tvQueueidentRetr.setText(identifier);
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
            retData= mData.getJSONArray("queues").length();
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return retData;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvQueueOwner,tvQueueidentRetr,tvQueueType,tvGotDate,queueid;
        public Button btnEdit;

        public ViewHolder(final View itemView) {
            super(itemView);
            queueid=(TextView) itemView.findViewById(R.id.queueid);
            tvQueueOwner = (TextView) itemView.findViewById(R.id.tvQueueOwner);
            tvQueueidentRetr = (TextView) itemView.findViewById(R.id.tvQueueidentRetr);
            tvQueueType = (TextView) itemView.findViewById(R.id.tvQueueType);
            tvGotDate = (TextView) itemView.findViewById(R.id.tvGotDate);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
           btnEdit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent=new Intent(ctx,UpdateQueue.class);
                   intent.putExtra("queueid",queueid.getText().toString());
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
            i = mData.getJSONArray("queues").getJSONObject(id);
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