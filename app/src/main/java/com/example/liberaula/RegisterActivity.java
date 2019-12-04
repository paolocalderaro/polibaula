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

public class RegisterActivity extends AppCompatActivity {
     EditText emailID, password;
     Button BottoneRegistrati;
     TextView tvAccedi;
     FirebaseAuth mFirebaseAuth;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth=FirebaseAuth.getInstance();
        emailID = findViewById(R.id.email);
        password=findViewById(R.id.password);
        BottoneRegistrati=findViewById(R.id.registrati);
        tvAccedi = findViewById(R.id.textView);

        //se clicco il bottone registrati, analizzo i vari casi (esattamente come nel login)
        BottoneRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailID.getText().toString();
                String pwd=password.getText().toString();
                if(email.isEmpty() && !email.endsWith("@studenti.poliba.it")){
                emailID.setError("indirizzo Mail non valido, reinserire");
                emailID.requestFocus();
                }
                else if (pwd.isEmpty()){
                    password.setError("inserire la password");
                    password.requestFocus();
                }
                else if (email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"i campi sono vuoti",Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && pwd.isEmpty()&& !email.endsWith("@studenti.poliba.it"))){
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"registrato non correttamente, riprova",Toast.LENGTH_SHORT).show();
                            }else{
                                mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this,"registrato correttamente,verifica la tua mail per accedere",Toast.LENGTH_LONG).show();
                                            Intent goToLogin=new Intent(RegisterActivity.this,LoginActivity.class);
                                            startActivity(goToLogin);
                                        }else{
                                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this,"si è verificato un errore!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //se clicco sul testo "già registrato?" mi porta nella schermata di login
        tvAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
