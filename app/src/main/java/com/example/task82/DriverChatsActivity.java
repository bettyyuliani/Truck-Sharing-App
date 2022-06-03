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

/**
 * this activity is only visible to the driver
 * displays all the chats that have been sent by different users
 */
public class DriverChatsActivity extends AppCompatActivity {

    // adapter variable
    private ChatListRecyclerViewAdapter chatListRecyclerViewAdapter;

    // data variables
    private List<Chat> chats =  new ArrayList<>();

    // view variables
    private RecyclerView chatListRecyclerView;
    private TextView emptychatsTV;

    // variables for firebase
    private DatabaseReference databaseReferenceChats, databaseReferenceUsers;
    private ChildEventListener childEventListener;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_chats);

        // obtain views
        chatListRecyclerView = findViewById(R.id.chatsRecyclerView);
        emptychatsTV = findViewById(R.id.emptyChatsTV);

        //create and set a new adapter to link recycler view to the associated data
        chatListRecyclerViewAdapter = new ChatListRecyclerViewAdapter(this, chats);
        chatListRecyclerView.setAdapter(chatListRecyclerViewAdapter);

        // manages layout of the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()); // create a linear layout manager
        linearLayoutManager.setReverseLayout(true); // reverse layout order
        linearLayoutManager.setStackFromEnd(true); // display views from the end of the data
        chatListRecyclerView.setLayoutManager(linearLayoutManager); // set layout manager

        // obtain firebase nodes
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(Util.USERS); // get user nodes
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(Util.CHATS); // get chat nodes

        // sort chats by message time
        query = databaseReferenceChats.orderByChild(Util.MESSAGE_TIME);

        // create new listener for child nodes
        childEventListener = new ChildEventListener() {
            // if a new child node is added
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot, snapshot.getKey());
            }

            // if data in child node is changed
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot, snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // TODO: if child node is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // TODO: if child node is moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: if task is cancelled
            }
        };

        query.addChildEventListener(childEventListener); // add the event listener to the new chat query
        emptychatsTV.setVisibility(View.VISIBLE); // hide 'no chats' text view

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

    /**
     * update and refresh list of chats
     * @param dataSnapshot new data/node in the firebase
     * @param username username of message sender
     */
    private void updateList(DataSnapshot dataSnapshot, String username)
    {
        emptychatsTV.setVisibility(View.GONE); // hide 'no chats' text view
        final String lastMessage; // last message sent by sender
        final long lastMessageTime; // time stamp of last message sent

        // checks if the message value of the newly added node exists
        if(dataSnapshot.child(Util.MESSAGE).getValue()!=null) {
            lastMessage = dataSnapshot.child(Util.MESSAGE).getValue().toString();
        }
        else {
            lastMessage = "";
        }

        // checks if the time stamp value of the newly added node exists
        if(dataSnapshot.child(Util.MESSAGE_TIME).getValue()!=null) {
            lastMessageTime = (long) dataSnapshot.child(Util.MESSAGE_TIME).getValue();
        }
        else {
            lastMessageTime= 0;
        }

        // listener for the changes in child node
        databaseReferenceUsers.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // if username exists, return the value, otherwise return ""
                String username = dataSnapshot.child(Util.MESSAGE_SENDER).getValue() != null?
                        dataSnapshot.child(Util.MESSAGE_SENDER).getValue().toString(): "";
                // create new chat object
                Chat chat = new Chat(username, lastMessage, lastMessageTime);

                // add the new chat into the chats list
                chats.add(chat);
                // notify recycler view for changes in data in order to refresh views
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