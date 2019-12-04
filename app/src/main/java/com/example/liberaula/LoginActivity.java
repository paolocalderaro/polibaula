package com.example.liberaula;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import listaAule.InizialeActivity;


public class LoginActivity extends AppCompatActivity {
    EditText emailID, password;
    Button BottoneAccedi;
    TextView BTRegistrati;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailID = findViewById(R.id.email);
        password = findViewById(R.id.password);
        BottoneAccedi = findViewById(R.id.accedi);
        BTRegistrati = findViewById(R.id.BackToRegistrati);


        //Queste righe servono a capire se l'utente cerca di loggarsi due volte per sbaglio
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null && mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                    Intent i = new Intent(LoginActivity.this, InizialeActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        };

        //qui impongo le condizioni che si devono verificare affinchè, dopo aver cliccato il bottone
        // accedi, l'app deve entrare, darmi errore, o dirmi se manca qualcosa (vari casi dell'if)
        BottoneAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()) {
                    emailID.setError("indirizzo Mail non valido, reinserire");
                    emailID.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("inserire la password");
                    password.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "i campi sono vuoti", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "errore, riprovare il Login", Toast.LENGTH_SHORT).show();
                            }else{
                                if(mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                                    Intent intToHome = new Intent(LoginActivity.this, InizialeActivity.class);
                                    startActivity(intToHome);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, "email non ancora verificata, verificare prima di accedere", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Errore!", Toast.LENGTH_SHORT).show();

                }


            }
            });

        //se clicco sul bottone "non sei ancora registrato?" mi porta alla schermata dove potro
        //registrarmi
        BTRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intSignUp);
            }
        });
        }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
