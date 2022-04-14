package com.example.androidfirebasechat.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidfirebasechat.MemoryData;
import com.example.androidfirebasechat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
    private ImageView backBtn;
    private TextView name;
    private EditText messageET;
    private ImageView sendBtn;
    private CircleImageView profilePic;
    private String chatKey ;
    private final List<ChatList> chatLists = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private String getUserMobile = "";
    private RecyclerView chattingRecyclerView;
    private boolean LoadingFirstTime = true;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://androidfirebasechat-6ee81-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        backBtn = findViewById(R.id.backBtn);
        name = findViewById(R.id.name);
        messageET = findViewById(R.id.messageET);
        sendBtn = findViewById(R.id.sendBtn);
        profilePic = findViewById(R.id.profilePic);
        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        //get data from messages adapter class
        final String getName = getIntent().getStringExtra("name");
        final String getProfile = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");
        //get user mobile from memory
        getUserMobile = MemoryData.getData(Chat.this);
        name.setText(getName);

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));
        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);


        if(getProfile.length()!=0){
            Picasso.get().load(getProfile).into(profilePic);
        }
        else{
            profilePic.setImageResource(R.drawable.user_icon);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //generate chat key, by default is 1
                        if (chatKey.isEmpty()) {
                            chatKey = "1";
                            if (snapshot.hasChild("chat")) {
                                chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                            }
                        }
                        if (snapshot.hasChild("chat")){

                            if (snapshot.child("chat").child(chatKey).hasChild("messages")){
                                chatLists.clear();
                                for (DataSnapshot messageSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()){
                                    if (messageSnapshot.hasChild("msg")&& messageSnapshot.hasChild("mobile")){
                                        final String getTime;
                                        if (messageSnapshot.hasChild("time")){
                                            getTime = messageSnapshot.child("time").getValue(String.class);
                                        }
                                        else{
                                            getTime = "no time";
                                        }

                                        final String getMobile = messageSnapshot.child("mobile").getValue(String.class);
                                        final String getMsg = messageSnapshot.child("msg").getValue(String.class);

                                        final String msgTimestamp = messageSnapshot.getKey();

                                        ChatList chatList = new ChatList(getMobile, getName, getMsg, getTime);

                                        chatLists.add(chatList);
                                        if(LoadingFirstTime || Long.parseLong(msgTimestamp) > Long.parseLong(MemoryData.getLastMsgTS(Chat.this, chatKey))){
                                            LoadingFirstTime = false;
                                            MemoryData.saveLastMsgTS(msgTimestamp, chatKey, Chat.this);
                                            chatAdapter.updateChatList(chatLists);

                                            chattingRecyclerView.scrollToPosition(chatLists.size()-1);
                                        }
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        backBtn.setOnClickListener(view ->{
            finish();
        });
        sendBtn.setOnClickListener(view->{
            final String getTextMessage = messageET.getText().toString();

            //get current timestamps
            final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0,10);

            MemoryData.saveLastMsgTS(currentTimestamp, chatKey, Chat.this);
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());

            databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
            databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("msg").setValue(getTextMessage);
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("mobile").setValue(getUserMobile);
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("time").setValue(timeStamp);
            //clear text after send
            messageET.setText("");
        });
    }
}