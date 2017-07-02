package com.example.rahul.otpnotification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

/**
 * Created by rahul on 2/7/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> dataSource;

    public MessageAdapter( ArrayList<Message> dataSource ){
        this.dataSource = dataSource;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //get the context and the layout id
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.message_layout;

        //create a layoutinflater object and inflate the previous layout to get the view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem ,parent, false);

        //get our viewholder
        MessageViewHolder messageViewHolder = new MessageViewHolder( view );

        Log.d(TAG, "onCreateViewHolder: created the viewholder" );

        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        if ( this.dataSource != null )
            holder.bind( this.dataSource.get(position) );
    }

    @Override
    public int getItemCount() {

        if ( this.dataSource != null ) {
            Log.d("Adapter", "getItemCount: " + this.dataSource.size());
            return this.dataSource.size();
        }
        return 0;
    }

    public void dataChanged( ArrayList<Message> data ){
        this.dataSource = data;
        Log.d("Adapter", "dataChanged: " + this.dataSource.size());
        notifyDataSetChanged();
        this.notifyDataSetChanged();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView OTP;
        private TextView Sender;

        public MessageViewHolder(View itemView) {
            super(itemView);

            OTP = ( TextView) itemView.findViewById(R.id.otp);
            Sender = ( TextView)itemView.findViewById(R.id.sender);
        }

        public void bind(Message message){
            String otp = message.getOtp();
            String sender = message.getSender();

            OTP.setText(otp);
            Sender.setText(sender);
        }
    }


}
