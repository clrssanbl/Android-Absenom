package com.example.myabsenom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private String email;
    private static final String USERS = "User";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setActionBarTitle("Profile Mahasiswa");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String UID = user.getUid();
        email = user.getEmail();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child(USERS);
        Log.v("USERID", userRef.getKey());
        final TextView edt_nama = findViewById(R.id.text_val_nama);
        final TextView edt_nim = findViewById(R.id.text_val_nim);
        final TextView edt_jur= findViewById(R.id.text_val_jurusan);
        final TextView edt_fak = findViewById(R.id.text_val_fakultas);
        final TextView edt_mail = findViewById(R.id.text_val_email);
        final TextView edt_jk = findViewById(R.id.text_val_jk);
        //untuk menampilkan data user yang sedang login
        userRef.addValueEventListener(new ValueEventListener() {
            String e_nama, e_nim, e_jur,e_fak,e_mail,e_jk;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyId: dataSnapshot.getChildren()){
                    if(keyId.child("email").getValue().equals(email)){
                        e_nama = keyId.child("nama").getValue(String.class);
                        e_nim = keyId.child("nim").getValue(String.class);
                        e_jur = keyId.child("jurusan").getValue(String.class);
                        e_fak = keyId.child("fakultas").getValue(String.class);
                        e_mail = keyId.child("email").getValue(String.class);
                        e_jk = keyId.child("jk").getValue(String.class);
                        break;
                    }
                }
                edt_nama.setText(e_nama);
                edt_nim.setText(e_nim);
                edt_jur.setText(e_jur);
                edt_fak.setText(e_fak);
                edt_mail.setText(e_mail);
                edt_jk.setText(e_jk);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                 }
        });

    }
    private void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent i = new Intent(this, HomeActivity.class);
                this.startActivity(i);
                return true;
            case R.id.action_logout:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.action_profile:
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
