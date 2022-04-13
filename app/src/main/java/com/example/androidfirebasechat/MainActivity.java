package com.example.androidfirebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

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
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }
}