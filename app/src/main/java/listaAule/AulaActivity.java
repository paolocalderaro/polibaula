package listaAule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.example.liberaula.R;

import com.example.liberaula.SpecAula;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import android.widget.Toast;


public class AulaActivity extends AppCompatActivity {

    DatabaseReference myRef,myRef2;
    TextView timerScritta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aule);

        // ini views

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingtoolbar_id);
        collapsingToolbarLayout.setTitleEnabled(true);

        TextView tv_nome = findViewById(R.id.aa_nome_aula);
        final TextView tv_coda = findViewById(R.id.aa_coda);

        TextView tv_locazione = findViewById(R.id.aa_locazione) ;
        TextView tv_descrizione = findViewById(R.id.aa_descrizione);
        final TextView tv_contatore  = findViewById(R.id.aa_contatore) ;
        ImageView img = findViewById(R.id.aa_thumbnail);



        // hide the default actionbar
        //getSupportActionBar().hide();

        // Receive data

        String nome  = getIntent().getExtras().getString("nome_aula");
        String descrizione = getIntent().getExtras().getString("descrizione_aula");
        long coda = getIntent().getExtras().getLong("coda_aula") ;
        final String id=getIntent().getExtras().getString("id_nfc");
        String locazione = getIntent().getExtras().getString("locazione_aula");
        long capienza = getIntent().getExtras().getLong("capienza_aula") ;
        long contatore = getIntent().getExtras().getLong("contatore_aula") ;
        boolean accesso = getIntent().getExtras().getBoolean("accesso_aula");
        String image_url = getIntent().getExtras().getString("image_url_aula") ;


        final CheckBox prenotato=findViewById(R.id.prenotato);
        final Button prenota=findViewById(R.id.prenota);
        final boolean[] firstclick = {true};
        prenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView timerScritta=findViewById(R.id.timer);

                final CountDownTimer timer=new CountDownTimer(5000,1000) {
                    @Override
                    public void onTick(long millis) {
                        timerScritta.setText(""+millis/1000+"s ");
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(AulaActivity.this, "fine prenotazione", Toast.LENGTH_SHORT).show();

                        //resetto la variabile che consente di clickare sul bottone
                        firstclick[0]=true;

                        prenotato.setChecked(false);
                        //transazione per decrementare la coda
                        myRef = FirebaseDatabase.getInstance().getReference().child("aule").child(id);
                        myRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                final SpecAula aulaLettaT = mutableData.getValue(SpecAula.class);
                                if(aulaLettaT==null){
                                    return Transaction.success(mutableData);
                                }
                                else if(aulaLettaT.contatore+aulaLettaT.coda<aulaLettaT.capienza)
                                {
                                    aulaLettaT.coda=aulaLettaT.coda-1;
                                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                                        @Override
                                        public void run() {
                                            timerScritta.setText("");
                                            tv_coda.setText("prenotati: "+aulaLettaT.coda);


                                        }
                                    });
                                    mutableData.setValue(aulaLettaT);
                                    return Transaction.success(mutableData);
                                }
                                return Transaction.success(mutableData);

                            }
                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                                // Transaction completed
                                // Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                            }
                        });


                    }
                };
                if(firstclick[0]){
                    myRef2 = FirebaseDatabase.getInstance().getReference().child("aule").child(id);
                    myRef2.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            final SpecAula aulaLettaT = mutableData.getValue(SpecAula.class);
                            if(aulaLettaT==null){
                                return Transaction.success(mutableData);
                            }
                            else if((aulaLettaT.contatore+aulaLettaT.coda)>=aulaLettaT.capienza)
                            {
                                // il seguente looper serve ad aggiornare il testo nella activity da un thread esterno
                                new Handler(Looper.getMainLooper()).post(new Runnable(){
                                    @Override
                                    public void run() {
                                        Toast.makeText(AulaActivity.this, "l'aula "+aulaLettaT.nome+" è piena, non è possibile prenotarsi", Toast.LENGTH_SHORT).show();
                                        prenotato.setChecked(false);

                                    }
                                });
                                return Transaction.success(mutableData);
                            }
                            else if(aulaLettaT.contatore+aulaLettaT.coda<aulaLettaT.capienza) {
                                aulaLettaT.coda = aulaLettaT.coda + 1;
                                new Handler(Looper.getMainLooper()).post(new Runnable(){
                                    @Override
                                    public void run() {
                                        Toast.makeText(AulaActivity.this, "hai prenotato un posto in "+aulaLettaT.nome+". Affrettati! la tua prenotazione durerà circa 5 minuti.", Toast.LENGTH_SHORT).show();
                                        prenotato.setChecked(true);
                                        timer.start();
                                        tv_coda.setText("prenotati: "+aulaLettaT.coda);


                                    }
                                });
                                firstclick[0] =false;
                                mutableData.setValue(aulaLettaT);
                                return Transaction.success(mutableData);
                            }

                            return Transaction.success(mutableData);

                        }
                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                               DataSnapshot dataSnapshot) {
                            // Transaction completed
                            // Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                        }
                    });}else if(!firstclick[0]){
                    Toast.makeText(AulaActivity.this, "hai già prenotato un posto in questa aula.", Toast.LENGTH_SHORT).show();
                }

            }
        });



        // setting values to each view

        tv_nome.setText(nome);
        tv_locazione.setText(locazione);
        tv_descrizione.setText(descrizione);
        tv_contatore.setText(""+(contatore));
        tv_coda.setText("prenotati: "+(coda));


        collapsingToolbarLayout.setTitle(nome);


        RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);


        // set image using Glide
        Glide.with(this).load(image_url).apply(requestOptions).into(img);





    }


}
