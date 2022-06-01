package com.example.task82;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task82.util.Util;
import com.example.task82.viewmodel.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessageViewHolder> {

    //declare variables
    private ArrayList<Message> messages;
    private Context context;
    private FirebaseAuth firebaseAuth;

    public MessagesRecyclerViewAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
       return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        // get the logged in user's username from shared preferences and assign it to the respective variable
        SharedPreferences prefs = context.getSharedPreferences(Util.SHARED_PREF_DATA, context.MODE_PRIVATE);
        String loggedInUsername = prefs.getString(Util.LOGGEDIN_USER, "");
        Log.i("astaga", loggedInUsername);

        String messageSender = message.getMessage_sender();

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateTime = sfd.format(new Date(message.getMessage_time()));

        String[] splitString = dateTime.split(" ");
        String messageTime = splitString[1];

        Util.createToast(context,loggedInUsername + messageSender);

        if (loggedInUsername.equals(messageSender)) {
            holder.sentLayout.setVisibility(View.VISIBLE);
            holder.receivedLayout.setVisibility(View.GONE);
            holder.sentTextView.setText(message.getMessage());
            holder.sentTimeTextView.setText(messageTime);
        }
        else
        {
            holder.receivedLayout.setVisibility(View.VISIBLE);
            holder.sentLayout.setVisibility(View.GONE);
            holder.receivedTextView.setText(message.getMessage());
            holder.receivedTimeTextView.setText(messageTime);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout sentLayout, receivedLayout;
        private TextView sentTextView, receivedTextView, sentTimeTextView, receivedTimeTextView;
        private ConstraintLayout messageLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sentLayout = itemView.findViewById(R.id.sentLayout);
            receivedLayout = itemView.findViewById(R.id.receivedLayout);
            sentTextView = itemView.findViewById(R.id.sentTextView);
            receivedTextView = itemView.findViewById(R.id.receivedTextView);
            sentTimeTextView = itemView.findViewById(R.id.sentTimeTextView);
            receivedTimeTextView = itemView.findViewById(R.id.receivedTimeTextView);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
