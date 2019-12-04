package com.example.liberaula;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<SpecAula> Aule= new ArrayList<>();
    public FirebaseDatabaseHelper() {
        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("liberaula-5c9ec");
    }
    public interface DataStatus{
        void DataIsLoaded(List<SpecAula> aule, List<String> chiavi);
        void DataIsUpdated();
        void DataIsInserted();
        void DataIsDeleted();

        }
        public void leggiAula(final DataStatus dataStatus){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Aule.clear();
                List<String> chiavi= new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    chiavi.add(keyNode.getKey());
                    SpecAula aula=keyNode.getValue(SpecAula.class);
                    Aule.add(aula);
                }
                dataStatus.DataIsLoaded(Aule,chiavi);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
