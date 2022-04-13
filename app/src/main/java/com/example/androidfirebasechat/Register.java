package com.example.androidfirebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://androidfirebasechat-6ee81-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //show progress dialog for user to wait
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");

        btnRegister = findViewById(R.id.btnRegister);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);

        //check if user already locked in

        if(!MemoryData.getData(this).isEmpty()){
            Intent intent = new Intent(Register.this, MainActivity.class);
            intent.putExtra("mobile", MemoryData.getData(this));
            intent.putExtra("email", MemoryData.getName(this));
            intent.putExtra("name", "");
            startActivity(intent);
            finish();
        }


        btnRegister.setOnClickListener(view -> {
            progressDialog.show();
            name = etName.getText().toString();
            email = etEmail.getText().toString();
            number = etNumber.getText().toString();
            if(name.length()==0||email.length()==0||number.length()==0){
                Toast.makeText(this,"All fields required",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else{
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("users").hasChild(number)){
                            Toast.makeText(Register.this,"Mobile already exists",Toast.LENGTH_SHORT).show();
                        }else {
                            databaseReference.child("users").child(number).child("email").setValue(email);
                            databaseReference.child("users").child(number).child("name").setValue(name);
                            databaseReference.child("users").child(number).child("profile_pic").setValue("");
                            //save mobile number into local memory
                            MemoryData.saveData(number, Register.this);
                            //save name into local memory
                            MemoryData.saveName(name, Register.this);
                            //
                            Toast.makeText(Register.this,"Success",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Register.this, MainActivity.class);
                            intent.putExtra("mobile", number);
                            intent.putExtra("email", email);
                            intent.putExtra("name", name);
                            startActivity(intent);
                            finish();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();

                    }
                });
            }
        });
    }
}