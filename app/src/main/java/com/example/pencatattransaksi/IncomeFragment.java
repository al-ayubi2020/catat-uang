package com.example.pencatattransaksi;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pencatattransaksi.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class IncomeFragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference mIncomeDatabase, mIncomeDatabaseDate, mIncomeDatabaseYear;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;
    EditText edtAmmount, edtType, edtNote;
    Button btnUpdate, btnDelete;
    TextView totalIncome, year;

    String type, note, post_key, mydate, bulan;
    int ammount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        bulan = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date());

        mydate = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());

        mIncomeDatabaseDate = FirebaseDatabase.getInstance().getReference().child("IncomeDataDate").child(uid).child(mydate).child(bulan);

        mIncomeDatabaseYear = FirebaseDatabase.getInstance().getReference().child("IncomeDataDate").child(uid).child(mydate);

        recyclerView = myview.findViewById(R.id.recyclerview_income);

        totalIncome = myview.findViewById(R.id.income_result_income);

        year = myview.findViewById(R.id.year);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabaseYear.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalincome = 0;

                for (DataSnapshot mysnap:snapshot.getChildren()){

                    for (DataSnapshot snap:mysnap.getChildren()){

                        Data data = snap.getValue(Data.class);

                        totalincome+=data.getAmount();

                        String stResult = String.valueOf(totalincome);

                        totalIncome.setText(stResult);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mIncomeDatabase, Data.class).build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(firebaseRecyclerOptions) {

            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false));
            }

            protected void onBindViewHolder(MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();

                        type = model.getType();
                        note = model.getNote();
                        ammount = model.getAmount();

                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.startListening();

        year.setText(mydate);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        void setNote(String note) {

            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        void setAmmount(int ammount) {
            TextView mAmmount = mView.findViewById(R.id.ammount_txt_income);
            String stammount = String.valueOf(ammount);
            mAmmount.setText(stammount);
        }

    }

    public void updateDataItem(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmmount = myview.findViewById(R.id.ammount_edit_update);
        edtType = myview.findViewById(R.id.type_edit_update);
        edtNote = myview.findViewById(R.id.note_edit_update);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());

        btnUpdate = myview.findViewById(R.id.btnSave_update);
        btnDelete = myview.findViewById(R.id.btnDelete_update);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String mdammount = String.valueOf(ammount);
                mdammount = edtAmmount.getText().toString().trim();

                int myAmmount = Integer.parseInt(mdammount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmmount,type,note,post_key,mDate);

                mIncomeDatabase.child(post_key).setValue(data);
                mIncomeDatabaseDate.child(post_key).setValue(data);


                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIncomeDatabase.child(post_key).removeValue();
                mIncomeDatabaseDate.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null){
            adapter.stopListening();
        }
    }
}