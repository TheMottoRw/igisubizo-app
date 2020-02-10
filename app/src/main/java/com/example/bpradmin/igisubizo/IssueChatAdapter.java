package com.example.bpradmin.igisubizo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bpradmin on 7/6/2018.
 */
public class IssueChatAdapter extends RecyclerView.Adapter<IssueChatAdapter.ViewHolder> {

    //    private List<String> mData = Collections.emptyList();
    private JSONObject mData = null;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context ctx;
    public Synchronizer synchronizer;

    // data is passed into the constructor
   /* public IssueChatAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }
    */
    public IssueChatAdapter(Context context, JSONObject data) {
        ctx=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        synchronizer=new Synchronizer(ctx);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.issue_sent_row, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONArray jsonArray=mData.getJSONArray("issues_chat");
            JSONObject jsonData=jsonArray.getJSONObject(position);
            if(jsonData.getString("isc_from_type").equals(synchronizer.getSessionCategory().toLowerCase())) {
holder.viewContainer.setBackground(ctx.getDrawable(R.drawable.sent_message));
                }else{
                holder.viewContainer.setBackground(ctx.getDrawable(R.drawable.received_message));
            }
            String iscId = jsonData.getString("isc_id");
            String iscMessage = jsonData.getString("isc_message");
            String regdate = jsonData.getString("regdate");
            String iscStatus = jsonData.getString("isc_status");

            holder.iscid.setText(iscId);
            holder.tvIscMSg.setText(iscMessage);
            holder.tvIscDate.setText(regdate);
            holder.tvIscStatus.setText(iscStatus);

        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        int retData=0;
        try {
            retData= mData.getJSONArray("issues_chat").length();
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return retData;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView myTextView,tvIscMSg,tvIscDate,tvIscStatus,iscid;
        public RelativeLayout viewContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            viewContainer=(RelativeLayout) itemView.findViewById(R.id.viewContainer);
            iscid=(TextView) itemView.findViewById(R.id.iscid);
            tvIscMSg= (TextView) itemView.findViewById(R.id.tvIscMSg);
            tvIscDate = (TextView) itemView.findViewById(R.id.tvIscDate);
            tvIscDate = (TextView) itemView.findViewById(R.id.tvIscStatus);
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
            i = mData.getJSONArray("issues_chat").getJSONObject(id);
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