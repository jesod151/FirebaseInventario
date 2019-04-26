package com.example.firebaseconn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText txtUser, txtPass;
    private Button btnLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUser = findViewById(R.id.txtUsuario);
        txtPass = findViewById(R.id.txtPassword);

        btnLog = findViewById(R.id.btnLogIn);
        btnLog.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtUser.getText().toString().equals("") && !txtPass.getText().toString().equals("")){
                    mAuth.signInWithEmailAndPassword(txtUser.getText().toString(), txtPass.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //signed
                    goMenuActivity();
                    Toast.makeText(getApplicationContext(), "Conectado correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    //signed out
                    Toast.makeText(getApplicationContext(), "Desconectado", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void goMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuth.signOut();
        Toast.makeText(getApplicationContext(), "Desconectado", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
