package com.example.task82;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task82.util.Util;
import com.example.task82.viewmodel.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatListViewHolder> {

    // declare variables
    private Context context;
    private List<Chat> chats;

    // constructor
    public ChatListRecyclerViewAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    // create layout for each view holder
    @NonNull
    @Override
    public ChatListRecyclerViewAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout, parent, false);
        return new ChatListViewHolder(view);
    }

    // modify the UI elements in each of the view holder
    @Override
    public void onBindViewHolder(@NonNull ChatListRecyclerViewAdapter.ChatListViewHolder holder, int position) {
        Chat chat = chats.get(position); // for each chat in the list

        // objects to reformat time in integer format to date format
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateTime = sfd.format(new Date(chat.getLastMessageTime()));
        String[] splitString = dateTime.split(" ");

        // retrieve timestamp
        String messageTime = splitString[1];
        // retrieve message sender
        String messageSender = chat.getUsername();

        // set text for each view according to its associated data
        holder.chatListTime.setText(messageTime);
        holder.chatListMessage.setText(chat.getLastMessage());
        holder.chatListName.setText(messageSender);

        // set on click listener for each view holder
        holder.chatListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra(Util.MESSAGE_SENDER, messageSender);
                context.startActivity(intent);
            }
        });
    }

    // return the number of data
    @Override
    public int getItemCount() {
        return chats.size();
    }

    // view holder or container for each item in the recycler view
    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        // view variables
        public LinearLayout chatListLayout;
        private TextView chatListName, chatListMessage, chatListTime;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            // obtain views
            chatListLayout = itemView.findViewById(R.id.chatListLayout);
            chatListName = itemView.findViewById(R.id.chatListName);
            chatListMessage = itemView.findViewById(R.id.chatListMessage);
            chatListTime = itemView.findViewById(R.id.chatListTime);
        }
    }
}
