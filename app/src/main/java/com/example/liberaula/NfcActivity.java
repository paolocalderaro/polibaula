package com.example.liberaula;

import androidx.appcompat.app.AppCompatActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String;
import nfc_reader.NdefMessageParser;
import nfc_reader.record.ParsedNdefRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.MutableData;

import java.util.List;


public class NfcActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    TextView text;
    TextView testo;
    PendingIntent pendingIntent;
    //private FirebaseDatabase mFirebaseDatabase;
    //private DatabaseReference myRef;
    private String res = "id3245";
    public String output = "id3245";
    private DatabaseReference myRef2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        RelativeLayout layout=findViewById(R.id.layout);

        /*per l'animazione:
        AnimationDrawable animationDrawable=(AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(700);
        animationDrawable.setExitFadeDuration(700);
        animationDrawable.start();
        */


        //per nfc
        text = findViewById(R.id.text);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "hai bisogno di abilitare l'NFC!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    //Il seguente metodo si avvia ogni volta che viene letto un tag NFC, e ogni volta che viene letto,
    // viene riconosciuta l'aula in cui si entra e viene incrementato nel db il contatore che
    //traccia l'affollamento
        @Override
        protected void onNewIntent(Intent intent) {
        //in res avremo l'id del tag letto
        setIntent(intent);
        res = resolveIntent(intent);
        text.setText("res" + res + "res");

        //nella transazione seguente andiamo a cercare nel db l'id letto, vedere se l'aula è piena
        myRef2 = FirebaseDatabase.getInstance().getReference().child("aule").child(res);
        myRef2.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                testo= findViewById(R.id.counter);
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
                            testo.setText("l'aula "+aulaLettaT.nome+" è piena");
                            //animazione colorata:
                            RelativeLayout layout=findViewById(R.id.layout);
                            Drawable gradient=getResources().getDrawable(R.drawable.gradient_rosso);
                            layout.setBackground(gradient);

                        }
                    });
                    return Transaction.success(mutableData);
                }
                else if(aulaLettaT.contatore+aulaLettaT.coda<aulaLettaT.capienza) {
                    aulaLettaT.contatore = aulaLettaT.contatore + 1;
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            int postiDisp= (int) ((int) aulaLettaT.capienza-(aulaLettaT.contatore+aulaLettaT.coda));
                            testo.setText("prego, entrare nell'aula. ci sono ancora " +postiDisp+" posti disponibili");
                            //animazione colorata:
                            RelativeLayout layout=findViewById(R.id.layout);
                            Drawable gradient=getResources().getDrawable(R.drawable.gradient_verde);
                            layout.setBackground(gradient);

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



        private String resolveIntent (Intent intent){
            String action = intent.getAction();

            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] msgs;

           /* if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {

            */
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
                //}

                return displayMsgs(msgs);
                //output=displayMsgs(msgs);
            }
            return "id324t";
        }
        private String displayMsgs (NdefMessage[]msgs){
            if (msgs == null || msgs.length == 0)
                return null;

            StringBuilder builder = new StringBuilder();
            List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
            final int size = records.size();

            for (int i = 0; i < size; i++) {
                ParsedNdefRecord record = records.get(i);
                String str = record.str();
                builder.append(str);
            }
            output = builder.toString();
            return output;

        }

        //NFC i seguenti metodi restituisce l'id del tag
        private String dumpTagData (Tag tag){
            StringBuilder sb = new StringBuilder();
            byte[] id = tag.getId();
            return toHex(id);
        }
        private String toHex ( byte[] bytes){
            StringBuilder sb = new StringBuilder();
            for (int i = bytes.length - 1; i >= 0; --i) {
                int b = bytes[i] & 0xff;
                if (b < 0x10)
                    sb.append('0');
                sb.append(Integer.toHexString(b));
                if (i > 0) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }




    }























