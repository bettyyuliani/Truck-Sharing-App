package com.example.task82;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.task82.util.Util;
import com.example.task82.viewmodel.Chat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DriverChatsActivity extends AppCompatActivity {

    private RecyclerView chatListRecyclerView;
    private TextView emptychatsTV;
    private ProgressBar progressBar;
    private ChatListRecyclerViewAdapter chatListRecyclerViewAdapter;
    private List<Chat> chats;
    private List<String> usernames = new ArrayList<>();

    private DatabaseReference databaseReferenceChats, databaseReferenceUsers;
    private String username = "driver";

    private ChildEventListener childEventListener;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_chats);

        chatListRecyclerView = findViewById(R.id.chatsRecyclerView);
        emptychatsTV = findViewById(R.id.emptyChatsTV);
        chatListRecyclerViewAdapter = new ChatListRecyclerViewAdapter(this, chats);
//        progressBar = findViewById(R.id.progressBar);
        chats = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);
        chatListRecyclerView.setAdapter(chatListRecyclerViewAdapter);

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(Util.USERS);
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(Util.MESSAGES).child(username);

        query = databaseReferenceChats.orderByChild(Util.TIME_STAMP);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot, true, snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot, true, snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query.addChildEventListener(childEventListener);
//        progressBar.setVisibility(View.VISIBLE);
        emptychatsTV.setVisibility(View.VISIBLE);

    }

    private void updateList(DataSnapshot dataSnapshot, boolean isNew, String username)
    {
//        progressBar.setVisibility(View.GONE);
        emptychatsTV.setVisibility(View.GONE);
        final  String lastMessage, lastMessageTime, unreadCount;

        if(dataSnapshot.child(Util.LAST_MESSAGE).getValue()!=null)
            lastMessage = dataSnapshot.child(Util.LAST_MESSAGE).getValue().toString();
        else
            lastMessage = "";

        if(dataSnapshot.child(Util.LAST_MESSAGE_TIME).getValue()!=null)
            lastMessageTime = dataSnapshot.child(Util.LAST_MESSAGE_TIME).getValue().toString();
        else
            lastMessageTime="";

        unreadCount=dataSnapshot.child(Util.UNREAD_COUNT).getValue()==null?
                "0":dataSnapshot.child(Util.UNREAD_COUNT).getValue().toString();

        databaseReferenceUsers.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = dataSnapshot.child(Util.USERNAME).getValue() != null?
                        dataSnapshot.child(Util.USERNAME).getValue().toString(): "";
                Chat chat = new Chat(username, unreadCount, lastMessage, lastMessageTime);

                if(isNew) {
                    chats.add(chat);
                    usernames.add(username);
                }
                else {
                    int indexOfClickedUser = usernames.indexOf(username) ;
                    chats.set(indexOfClickedUser, chat);
                }
                chatListRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.createToast(getApplicationContext(), "Failed to fetch chats");
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        query.removeEventListener(childEventListener);
    }
}