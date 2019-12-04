package listaAule;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.liberaula.SpecAula;

import com.example.liberaula.R ;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext ;
    private List<SpecAula> mData ;
    RequestOptions option;


    public RecyclerViewAdapter(Context mContext, List<SpecAula> mData) {
        this.mContext = mContext;
        this.mData = mData;
        // Request option for Glide
        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);

    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.aula_row_item,parent,false) ;
        final MyViewHolder viewHolder = new MyViewHolder(view) ;
        viewHolder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, AulaActivity.class);
                i.putExtra("nome_aula",mData.get(viewHolder.getAdapterPosition()).getNome());
                i.putExtra("descrizione_aula",mData.get(viewHolder.getAdapterPosition()).getDescrizione());
                i.putExtra("coda_aula",mData.get(viewHolder.getAdapterPosition()).getCoda());
                i.putExtra("locazione_aula",mData.get(viewHolder.getAdapterPosition()).getLocazione());
                i.putExtra("capienza_aula",mData.get(viewHolder.getAdapterPosition()).getCapienza());
                i.putExtra("contatore_aula",mData.get(viewHolder.getAdapterPosition()).getContatore());
                i.putExtra("accesso_aula",mData.get(viewHolder.getAdapterPosition()).isAccesso());
                //i.putExtra("id_aula",mData.get(viewHolder.getAdapterPosition()).getIdNfc());
                i.putExtra("image_url_aula",mData.get(viewHolder.getAdapterPosition()).getImage_url());

                mContext.startActivity(i);

            }
        });




        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_nome.setText(mData.get(position).getNome());
        holder.tv_contatore.setText((int)mData.get(position).getContatore());
        holder.tv_coda.setText((int)mData.get(position).getCoda());
        holder.tv_locazione.setText(mData.get(position).getLocazione());

        // Load Image from the internet and set it into Imageview using Glide

        Glide.with(mContext).load(mData.get(position).getImage_url()).apply(option).into(holder.img_thumbnail);



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_nome ;
        TextView tv_contatore ;
        TextView tv_coda;
        TextView tv_locazione;
        ImageView img_thumbnail;
        LinearLayout view_container;


        public MyViewHolder(View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.container);
            tv_nome = itemView.findViewById(R.id.nome_aula);
            tv_locazione = itemView.findViewById(R.id.locazione);
            tv_contatore = itemView.findViewById(R.id.contatore);
            tv_coda = itemView.findViewById(R.id.coda);
            img_thumbnail = itemView.findViewById(R.id.thumbnail);

        }
    }

}
