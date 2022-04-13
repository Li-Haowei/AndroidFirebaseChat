package com.example.androidfirebasechat.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
    private ImageView backBtn;
    private TextView name;
    private EditText messageET;
    private ImageView sendBtn;
    private CircleImageView profilePic;
    private String chatKey ;
    private String getUserMobile = "";
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

        //get data from messages adapter class
        final String getname = getIntent().getStringExtra("name");
        final String getProfile = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");
        //get user mobile from memory
        getUserMobile = MemoryData.getData(Chat.this);
        name.setText(getname);
        if(getProfile.length()!=0){
            Picasso.get().load(getProfile).into(profilePic);
        }
        else{
            profilePic.setImageResource(R.drawable.user_icon);
        }

        try {
            if (chatKey.isEmpty()) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //generate chat key, by default is 1
                        chatKey= "1";
                        if(snapshot.hasChild("chat")){
                            chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() +1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }catch (Exception e){

        }
        backBtn.setOnClickListener(view ->{
            finish();
        });
        sendBtn.setOnClickListener(view->{
            final String getTextMessage = messageET.getText().toString();

            //get current timestamps
            final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0,10);

            MemoryData.saveLastMsgTS(currentTimestamp, chatKey, Chat.this);

            databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
            databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("msg").setValue(getTextMessage);
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("mobile").setValue(getUserMobile);
            //clear text after send
            messageET.setText("");
        });
    }
}