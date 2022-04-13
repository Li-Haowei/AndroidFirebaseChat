package com.example.androidfirebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    private Button btnRegister;
    private EditText etName;
    private EditText etEmail;
    private EditText etNumber;
    private String name;
    private String email;
    private String number;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://console.firebase.google.com/project/androidfirebasechat-6ee81/database/androidfirebasechat-6ee81-default-rtdb/data/~2F");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);

        btnRegister.setOnClickListener(view -> {
            name = etName.getText().toString();
            email = etEmail.getText().toString();
            number = etNumber.getText().toString();
            if(name.length()==0||email.length()==0||number.length()==0){
                Toast.makeText(this,"All fields required",Toast.LENGTH_SHORT).show();
            }else{
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("users").hasChild(number)){
                            Toast.makeText(Register.this,"Mobile already exists",Toast.LENGTH_SHORT).show();
                        }else {
                            databaseReference.child("users").child(number).child("email").setValue(email);
                            databaseReference.child("users").child(number).child("name").setValue(name);

                            Toast.makeText(Register.this,"Success",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Register.this, MainActivity.class);
                            intent.putExtra("mobile", number);
                            intent.putExtra("email", email);
                            intent.putExtra("name", name);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}