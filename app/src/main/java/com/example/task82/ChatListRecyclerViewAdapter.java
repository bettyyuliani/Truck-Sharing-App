package com.example.task82;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task82.viewmodel.Chat;

import java.util.List;

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatListViewHolder> {
    private Context context;
    private List<Chat> chats;

    public ChatListRecyclerViewAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatListRecyclerViewAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout, parent, false);
        return new ChatListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatListRecyclerViewAdapter.ChatListViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.chatListTime.setText(chat.getLastMessageTime());
        holder.chatListMessage.setText(chat.getLastMessage());
        holder.chatListUnreadCount.setText(chat.getUnreadCount());
        holder.chatListName.setText(chat.getUsername());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout chatListLayout;
        private TextView chatListName, chatListMessage, chatListTime, chatListUnreadCount;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            chatListLayout = itemView.findViewById(R.id.chatListLayout);
            chatListName = itemView.findViewById(R.id.chatListName);
            chatListMessage = itemView.findViewById(R.id.chatListMessage);
            chatListTime = itemView.findViewById(R.id.chatListTime);
            chatListUnreadCount = itemView.findViewById(R.id.chatListUnreadCount);
        }
    }
}
