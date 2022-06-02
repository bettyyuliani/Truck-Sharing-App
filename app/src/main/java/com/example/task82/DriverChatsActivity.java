package com.example.task82;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        chats = new ArrayList<>();
        chatListRecyclerViewAdapter = new ChatListRecyclerViewAdapter(this, chats);
//        progressBar = findViewById(R.id.progressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);
        chatListRecyclerView.setAdapter(chatListRecyclerViewAdapter);

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(Util.USERS);
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(Util.CHATS);

        query = databaseReferenceChats.orderByChild(Util.MESSAGE_TIME);
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

    // create menu on tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // on click listener for selected options from the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutMenu:
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateList(DataSnapshot dataSnapshot, boolean isNew, String username)
    {
//        progressBar.setVisibility(View.GONE);
        emptychatsTV.setVisibility(View.GONE);
        final  String lastMessage;
        final long lastMessageTime;

        if(dataSnapshot.child(Util.MESSAGE).getValue()!=null)
            lastMessage = dataSnapshot.child(Util.MESSAGE).getValue().toString();
        else
            lastMessage = "";

        if(dataSnapshot.child(Util.MESSAGE_TIME).getValue()!=null)
            lastMessageTime = (long) dataSnapshot.child(Util.MESSAGE_TIME).getValue();
        else
            lastMessageTime= 0;


        databaseReferenceUsers.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = dataSnapshot.child(Util.MESSAGE_SENDER).getValue() != null?
                        dataSnapshot.child(Util.MESSAGE_SENDER).getValue().toString(): "";
                Chat chat = new Chat(username, lastMessage, lastMessageTime);

                chats.add(chat);
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