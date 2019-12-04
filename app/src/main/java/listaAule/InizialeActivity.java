package listaAule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.liberaula.NfcActivity;
import com.example.liberaula.R;
import com.example.liberaula.SpecAula;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class InizialeActivity extends AppCompatActivity {


    private List<SpecAula> lstAula ;
    private RecyclerView recyclerView ;
    FloatingActionButton btnNfc;
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniziale);

        lstAula = new ArrayList<>() ;
        ref = FirebaseDatabase.getInstance().getReference().child("aule");
        ref.keepSynced(true);

        recyclerView = findViewById(R.id.recyclerviewid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        btnNfc=findViewById(R.id.NFCfloat);
        btnNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intNfc = new Intent(InizialeActivity.this, NfcActivity.class);
                startActivity(intNfc);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<SpecAula, AulaViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<SpecAula, AulaViewHolder>
                (SpecAula.class,R.layout.aula_row_item,AulaViewHolder.class,ref){
            @Override
            protected void populateViewHolder(final AulaViewHolder viewHolder, final SpecAula model, int position){
                viewHolder.setNome(model.getNome());
                viewHolder.setDesc(model.getDescrizione());
                viewHolder.setImage(getApplicationContext(),model.getImage_url());
                viewHolder.setLocazione(model.getLocazione());
                viewHolder.setContatore(model.getContatore());
                viewHolder.setCoda(model.getCoda());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(InizialeActivity.this, AulaActivity.class);
                        i.putExtra("nome_aula",model.getNome());
                        i.putExtra("descrizione_aula",model.getDescrizione());
                        i.putExtra("coda_aula",model.getCoda());
                        i.putExtra("locazione_aula",model.getLocazione());
                        i.putExtra("capienza_aula",model.getCapienza());
                        i.putExtra("contatore_aula",model.getContatore());
                        i.putExtra("accesso_aula",model.isAccesso());
                        i.putExtra("id_nfc",model.getIdNfc());
                        i.putExtra("image_url_aula",model.getImage_url());

                        startActivity(i);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AulaViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public AulaViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public  void setNome(String nome){
            TextView nome_aula = mView.findViewById(R.id.nome_aula);
            nome_aula.setText(nome);
        }
        public void setDesc(String desc){
            TextView desc_aula = mView.findViewById(R.id.descrizione);
            desc_aula.setText(desc);
        }
        public void  setImage(Context ctx,String image){
            RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);

            ImageView imm_aula = mView.findViewById(R.id.thumbnail);
            Picasso.get().load(image).into(imm_aula);
            Glide.with(ctx).load(image).apply(requestOptions).into(imm_aula);
        }
        public void setLocazione(String locazione){
            TextView locazione_aula = mView.findViewById(R.id.locazione);
            locazione_aula.setText(locazione);
        }
        public void  setContatore(long contatore){
            TextView cont_aula = mView.findViewById(R.id.contatore);
            int count= (int)contatore;
            cont_aula.setText(""+(count));
        }
        public void setCoda(long coda){
            TextView coda_aula = mView.findViewById(R.id.coda);
            coda_aula.setText("prenotati: "+(coda));
        }



    }










}
