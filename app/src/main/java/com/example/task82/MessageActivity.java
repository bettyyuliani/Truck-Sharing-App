package com.example.task82;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.task82.util.Util;
import com.example.task82.viewmodel.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private SwipeRefreshLayout messagesSwipeRefresh;
    private ImageView sendIcon;
    private EditText messageEditText;
    private DatabaseReference mRootReference;
    private FirebaseAuth firebaseAuth;
    private String loggedInUsername;
    private String driverUsername = "driver";
    private String receiverUsername;
    private MessagesRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Message> messages = new ArrayList<>();

    private int currentPage = 1;
    private static final int RECORD_PER_PAGE = 30;

    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendIcon = findViewById(R.id.sendIcon);
        messageEditText = findViewById(R.id.enterMessageEditText);


        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesSwipeRefresh = findViewById(R.id.messagesSwipeRefresh);

        // get the logged in user's username from shared preferences and assign it to the respective variable
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        loggedInUsername = prefs.getString(Util.LOGGEDIN_USER, "");

        if (loggedInUsername.equals("driver"))
        {
            Intent intent = getIntent();
            receiverUsername = intent.getStringExtra(Util.MESSAGE_SENDER);
        }
        else
        {
            receiverUsername = driverUsername;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();

        recyclerViewAdapter = new MessagesRecyclerViewAdapter(messages, this);

        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(recyclerViewAdapter);
        loadMessages();
        messagesRecyclerView.scrollToPosition(messages.size());
        messagesSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                loadMessages();
            }
        });


        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.connectionAvailable(getApplicationContext()))
                {
                    String message = messageEditText.getText().toString();
                    DatabaseReference userMessagePush = mRootReference.child(Util.MESSAGES).child(loggedInUsername).child(driverUsername).push();
                    String messageID = userMessagePush.getKey();
                    sendMessage(message, messageID);
                }
                else
                {
                    Util.createToast(getApplicationContext(), "No internet");
                }
            }
        });
    }

    private void loadMessages()
    {
        messages.clear();
        databaseReference = mRootReference.child(Util.MESSAGES).child(loggedInUsername).child(receiverUsername);

        Query messageQuery = databaseReference.limitToLast(currentPage * RECORD_PER_PAGE);
        if (childEventListener != null)
        {
            messageQuery.removeEventListener(childEventListener);
        }

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messages.add(message);
                recyclerViewAdapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messages.size()-1);
                messagesSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messagesSwipeRefresh.setRefreshing(false);
            }
        };

        messageQuery.addChildEventListener(childEventListener);
    }

    private void sendMessage(String message, String messageID)
    {
        try {
            if (!message.equals(""))
            {
                HashMap messageMap = new HashMap();
                HashMap chatsMap = new HashMap();
                messageMap.put(Util.MESSAGE, message);
                messageMap.put(Util.MESSAGE_ID, messageID);
                messageMap.put(Util.MESSAGE_SENDER, loggedInUsername);
                messageMap.put(Util.MESSAGE_TIME, ServerValue.TIMESTAMP);

                chatsMap.put(Util.MESSAGE, message);
                chatsMap.put(Util.MESSAGE_SENDER, loggedInUsername);
                chatsMap.put(Util.MESSAGE_TIME, ServerValue.TIMESTAMP);

                String currentUserRef = Util.MESSAGES + "/" + loggedInUsername + "/" + receiverUsername;
                String driverRef = Util.MESSAGES + "/" + receiverUsername + "/" + loggedInUsername;

                String chatCurrentUserRef = Util.CHATS + "/" + loggedInUsername;

                HashMap messageUserMap = new HashMap();
                messageUserMap.put(currentUserRef + "/" + messageID, messageMap);
                messageUserMap.put(driverRef + "/" + messageID, messageMap);

                messageEditText.setText("");

                if (!loggedInUsername.equals(driverUsername))
                {
                    HashMap chatMap = new HashMap();
                    chatMap.put(chatCurrentUserRef, chatsMap);
                    mRootReference.updateChildren(chatMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null)
                            {
                                Util.createToast(getApplicationContext(), "Failed to send message " + error.getMessage());
                            }
                        }
                    });
                }


                mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null)
                        {
                            Util.createToast(getApplicationContext(), "Failed to send message " + error.getMessage());
                        }
                    }
                });
            }
        }
        catch (Exception ex){
        Util.createToast(getApplicationContext(), "Failed to send message " + ex.getMessage());
        }
    }
}