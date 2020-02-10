package com.example.bpradmin.igisubizo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.ViewHolder>{

    //    private List<String> mData = Collections.emptyList();
    private JSONObject mData = null;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context ctx;
    public Synchronizer synchronizer;

    // data is passed into the constructor
    public IssuesAdapter(Context context, JSONObject data) {
        ctx=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
            synchronizer=new Synchronizer(ctx);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.issue_view_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONArray jsonArray=mData.getJSONArray("issues");
            JSONObject jsonData=jsonArray.getJSONObject(position);
            String issId = jsonData.getString("iss_id");
            String issTitle = jsonData.getString("iss_title");
            String lastMsg = jsonData.getString("last_msg");
            String regdate = jsonData.getString("regdate");
            String issStatus = jsonData.getString("iss_status");

            holder.issid.setText(issId);
            holder.tvIssTitle.setText(issTitle);
            holder.tvLastmsg.setText(lastMsg);
            holder.tvIssDate.setText(regdate);
            holder.tvIssStatus.setText(issStatus);

        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        int retData=0;
        try {
            retData= mData.getJSONArray("issues").length();
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return retData;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvIssTitle,tvLastmsg,tvIssDate,tvIssStatus,issid;
        public RelativeLayout viewContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            viewContainer=(RelativeLayout) itemView.findViewById(R.id.viewContainer);
            issid=(TextView) itemView.findViewById(R.id.issid);
            tvIssTitle= (TextView) itemView.findViewById(R.id.tvIssTitle);
            tvLastmsg= (TextView) itemView.findViewById(R.id.tvLastmsg);
            tvIssDate = (TextView) itemView.findViewById(R.id.tvIssDate);
            tvIssStatus = (TextView) itemView.findViewById(R.id.tvIsstatus);
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
            i = mData.getJSONArray("issues").getJSONObject(id);
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