package com.example.androidfirebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.androidfirebasechat.messages.MessagesAdapter;
import com.example.androidfirebasechat.messages.MessagesList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private  final List<MessagesList>  messagesLists = new ArrayList<>();
    private String mobile;
    private String email;
    private String name;
    private String chatKey = "";
    private int unseenMessages = 0;
    private String lastMessage = "";
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private boolean dataSet = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://androidfirebasechat-6ee81-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        //get intent data from Register.class activity
        final CircleImageView userProfilePic = findViewById(R.id.userProfilePic);

        mobile = getIntent().getStringExtra("mobile");
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        Log.d("creation",""+mobile);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set adapter to recycleview
        messagesAdapter = new MessagesAdapter(messagesLists, MainActivity.this);
        messagesRecyclerView.setAdapter(messagesAdapter);
        //show progress dialog for user to wait
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        //get profile pic from firebase database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profilePicUrl = snapshot.child("users").child(mobile).child("profile_pic").getValue(String.class);
                //loads image into imageview, or in this case, a CircleImageView
                if(profilePicUrl!=null) if(!profilePicUrl.isEmpty()) Picasso.get().load(profilePicUrl).into(userProfilePic);
                Log.d("creation",""+profilePicUrl);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesLists.clear();
                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";
                for(DataSnapshot dataSnapshot: snapshot.child("users").getChildren()){

                    final String getMobile = dataSnapshot.getKey();

                    dataSet = false;
                    if(!getMobile.equals(mobile)){
                        final String getName = dataSnapshot.child("name").getValue(String.class);
                        final String getProfilePic = dataSnapshot.child("profile_pic").getValue(String.class);


                        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCounts = (int)snapshot.getChildrenCount();
                                if(getChatCounts>0){
                                    for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;

                                        if(dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2")
                                        && dataSnapshot1.hasChild("messages")){
                                            final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                            final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                            if((getUserOne.equals(getMobile)&&getUserTwo.equals(mobile))||
                                                    (getUserOne.equals(mobile)||getUserTwo.equals(getMobile))){
                                                for(DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()){
                                                    final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
                                                    final long getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(MainActivity.this, getKey));

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    if(getMessageKey>getLastSeenMessage){
                                                        unseenMessages++;
                                                    }

                                                }
                                            }
                                        }

                                    }
                                }
                                if(dataSet){
                                    dataSet = true;
                                    MessagesList messageList = new MessagesList(getName, getMobile, lastMessage,getProfilePic,unseenMessages, chatKey);
                                    messagesLists.add(messageList);
                                    messagesAdapter.updateData(messagesLists);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}