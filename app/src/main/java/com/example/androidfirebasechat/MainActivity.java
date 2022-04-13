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
    private RecyclerView messagesRecyclerView;
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
                for(DataSnapshot dataSnapshot: snapshot.child("users").getChildren()){
                    final String getMobile = dataSnapshot.getKey();
                    if(!getMobile.equals(mobile)){
                        final String getName = dataSnapshot.child("name").getValue(String.class);
                        final String getProfilePic = dataSnapshot.child("profile_pic").getValue(String.class);

                        MessagesList messageList = new MessagesList(getName, getMobile, "",getProfilePic,0);
                        messagesLists.add(messageList);
                    }
                }
                messagesRecyclerView.setAdapter(new MessagesAdapter(messagesLists, MainActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}