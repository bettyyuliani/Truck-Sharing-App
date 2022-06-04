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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    // data variables
    private String loggedInUsername;
    private String driverUsername = "driver";
    private String receiverUsername;
    private ArrayList<Message> messages = new ArrayList<>();

    // view variables
    private RecyclerView messagesRecyclerView;
    private SwipeRefreshLayout messagesSwipeRefresh;
    private ImageView sendIcon;
    private EditText messageEditText;

    // firebase variables
    private DatabaseReference mRootReference;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    // recycler view adapter
    private MessagesRecyclerViewAdapter recyclerViewAdapter;

    // other variables (for swipe refresh)
    private int currentPage = 1;
    private static final int RECORD_PER_PAGE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // obtain views
        sendIcon = findViewById(R.id.sendIcon);
        messageEditText = findViewById(R.id.enterMessageEditText);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesSwipeRefresh = findViewById(R.id.messagesSwipeRefresh);

        // get the logged in user's username from shared preferences and assign it to the respective variable
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        loggedInUsername = prefs.getString(Util.LOGGEDIN_USER, "");

        // if the logged in user is a driver
        if (loggedInUsername.equals("driver"))
        {
            Intent intent = getIntent();
            receiverUsername = intent.getStringExtra(Util.MESSAGE_SENDER);
        }
        // if the logged in user is the Truck Sharing App user
        else
        {
            receiverUsername = driverUsername;
        }

        // get root node of the firebase
        mRootReference = FirebaseDatabase.getInstance().getReference();

        //create and set a new adapter to link recycler view to the associated data
        recyclerViewAdapter = new MessagesRecyclerViewAdapter(messages, this);
        messagesRecyclerView.setAdapter(recyclerViewAdapter);

        //create and set a layout manager to manage the layout of the view
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // display messages
        loadMessages();

        // adjust the display of messages
        messagesRecyclerView.scrollToPosition(messages.size());
        messagesSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                loadMessages();
            }
        });

        // on click listener for send icon
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if connection is available
                if (Util.connectionAvailable(getApplicationContext()))
                {
                    String message = messageEditText.getText().toString(); // get message input by the user
                    DatabaseReference userMessagePush = mRootReference.child(Util.MESSAGES).child(loggedInUsername).child(driverUsername).push(); // get reference to the node location
                    String messageID = userMessagePush.getKey(); // get the last token pointed by the child node
                    sendMessage(message, messageID); // in firebase: create new node for sender and receiver
                }
                // if no connection was found
                else
                {
                    Util.createToast(getApplicationContext(), "No internet");
                }
            }
        });
    }

    // create menu on tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.truck_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // on click listener for selected options from the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.homeMenu:
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                return true;
            case R.id.accountMenu:
                Intent accountIntent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(accountIntent);
                return true;
            case R.id.myordersMenu:
                Intent ordersIntent = new Intent(getApplicationContext(), OrdersActivity.class);
                startActivity(ordersIntent);
                return true;
            case R.id.textDriverMenu:
                return true;
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
     * display messages
     */
    private void loadMessages()
    {
        messages.clear();
        databaseReference = mRootReference.child(Util.MESSAGES).child(loggedInUsername).child(receiverUsername); // obtain child node

        Query messageQuery = databaseReference.limitToLast(currentPage * RECORD_PER_PAGE); // create a message query where it obtains the 30 last messages

        // if child event listener exists
        if (childEventListener != null)
        {
            messageQuery.removeEventListener(childEventListener); // removes listener
        }

        // create new listener
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class); // get message from the node
                messages.add(message); // bind message to the messages list
                recyclerViewAdapter.notifyDataSetChanged(); // allows recycler view to refresh its views
                messagesRecyclerView.scrollToPosition(messages.size()-1); // scroll to the current message
                messagesSwipeRefresh.setRefreshing(false); // stop swipe refresh from refreshing
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // TODO: if data in child node is changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // TODO: if child node is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // TODO: if child node is moved
            }

            // if message sending is cancelled
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messagesSwipeRefresh.setRefreshing(false);
            }
        };

        // add the event listener to the new message query
        messageQuery.addChildEventListener(childEventListener);
    }

    /**
     * send message from sender to receiver
     * in the database, a copy of the node is created as the message is shared between two users
     * @param message message string to be sent
     * @param messageID unique ID to identify message
     */
    private void sendMessage(String message, String messageID)
    {
        try {
            if (!message.equals(""))
            {
                HashMap messageMap = new HashMap(); // create a message hashmap to represent the message node
                HashMap chatsMap = new HashMap(); // create a chat hashmap to represent the message node

                // load in details about the message
                messageMap.put(Util.MESSAGE, message);
                messageMap.put(Util.MESSAGE_ID, messageID);
                messageMap.put(Util.MESSAGE_SENDER, loggedInUsername);
                messageMap.put(Util.MESSAGE_TIME, ServerValue.TIMESTAMP);

                // load in details about the chats between two users
                chatsMap.put(Util.MESSAGE, message);
                chatsMap.put(Util.MESSAGE_SENDER, loggedInUsername);
                chatsMap.put(Util.MESSAGE_TIME, ServerValue.TIMESTAMP);

                // create copy of two message nodes between two users
                String currentUserRef = Util.MESSAGES + "/" + loggedInUsername + "/" + receiverUsername;
                String receiverRef = Util.MESSAGES + "/" + receiverUsername + "/" + loggedInUsername;

                // create a new chat node
                String chatCurrentUserRef = Util.CHATS + "/" + loggedInUsername;

                HashMap messageUserMap = new HashMap(); // create a message user hashmap to represent the messages of the user node
                messageUserMap.put(currentUserRef + "/" + messageID, messageMap); // node for sender
                messageUserMap.put(receiverRef + "/" + messageID, messageMap); // node for receiver

                // clear the message box
                messageEditText.setText("");

                // only create a new chat node for user
                if (!loggedInUsername.equals(driverUsername))
                {
                    // create new chat node
                    HashMap chatMap = new HashMap();
                    chatMap.put(chatCurrentUserRef, chatsMap);

                    // update child nodes of chat node
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

                // update child nodes of message node
                mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null)
                        {
                            Util.createToast(getApplicationContext(), "Failed to send message " + error.getMessage());
                        }
                    }
                });

                // send notification to the receiver that a new message has been sent
                Util.sendNotification(getApplicationContext(), "New message received", message, receiverUsername);
            }
        }

        // if message fails to be sent
        catch (Exception ex){
            Util.createToast(getApplicationContext(), "Failed to send message " + ex.getMessage());
        }
    }
}