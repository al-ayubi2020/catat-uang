package com.example.pencatattransaksi;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.pencatattransaksi.Model.Data;
import com.example.pencatattransaksi.Model.Data_set;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class    DashboardFragment extends Fragment {

    FloatingActionButton fab_main_btn, fab_income_btn, fab_expense_btn, fab_logout_btn, fab_info_btn, target_pemasukan, target_pengeluaran;
    TextView fab_income_txt, fab_expense_txt, totalIncomeResult, totalExpenseResult, totalResult, month, set_income, set_expense, selisih_income, selisih_expense;
    boolean isOpen = false;
    Animation FadeOpen, FadeClose;
    FirebaseAuth mAuth;

    FirebaseRecyclerAdapter adapter_income, adapter_expense;

    DatabaseReference mIncomeDatabase, mExpenseDatabase, mIncomeDatabaseDate, mExpenseDatabaseDate, mIncomeTarget, mExpenseTarget, mIncomeTargetChild, mExpenseTargetChild, mIncomeDelete, mExpenseDelete;

    RecyclerView mRecyclerIncome, mRecyclerExpense;

    String mydate, bulan;

    Button btnDeleteDataAll;

    Uri imageuri;


    int income = 0;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        AnimatedPieView mAnimatedPieView = myview.findViewById(R.id.chart);

        selisih_income = myview.findViewById(R.id.selisih_income_result_dashboard);
        selisih_expense = myview.findViewById(R.id.selisih_expense_result_dashboard);
        set_income = myview.findViewById(R.id.terget_income_result_dashboard);
        set_expense = myview.findViewById(R.id.terget_expense_result_dashboard);

        target_pemasukan = myview.findViewById(R.id.atur_pemasukan);
        target_pengeluaran = myview.findViewById(R.id.atur_pengeluaran);

        mIncomeTarget = FirebaseDatabase.getInstance().getReference().child("Target Income").child(uid);
        mExpenseTarget = FirebaseDatabase.getInstance().getReference().child("Target Expense").child(uid);

        mIncomeTargetChild = FirebaseDatabase.getInstance().getReference().child("Target Income").child(uid).child("Target");
        mExpenseTargetChild = FirebaseDatabase.getInstance().getReference().child("Target Expense").child(uid).child("Target");

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        bulan = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date());

        month = myview.findViewById(R.id.month);

        mydate = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());

        mIncomeDatabaseDate = FirebaseDatabase.getInstance().getReference().child("IncomeDataDate").child(uid).child(mydate).child(bulan);
        mExpenseDatabaseDate = FirebaseDatabase.getInstance().getReference().child("ExpenseDataDate").child(uid).child(mydate).child(bulan);

        mIncomeDelete = FirebaseDatabase.getInstance().getReference().child("IncomeDataDate").child(uid);
        mExpenseDelete = FirebaseDatabase.getInstance().getReference().child("ExpenseDataDate").child(uid);

        totalIncomeResult = myview.findViewById(R.id.income_result_dashboard);
        totalExpenseResult = myview.findViewById(R.id.expense_result_dashboard);
        totalResult = myview.findViewById(R.id.total_result_dashboard);

        mRecyclerIncome = myview.findViewById(R.id.recyclerview_income_dashboard);
        mRecyclerExpense = myview.findViewById(R.id.recyclerview_expense_dashboard);

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_button);
        fab_income_btn = myview.findViewById(R.id.income_ft_button);
        fab_expense_btn = myview.findViewById(R.id.expense_ft_btn);

        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        fab_logout_btn = myview.findViewById(R.id.fb_logout_button);
        fab_info_btn = myview.findViewById(R.id.fb_main_info_button);

        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);

        btnDeleteDataAll = myview.findViewById(R.id.btn_reset);

        btnDeleteDataAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetdata();

            }
        });


        fab_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), InfoActivity.class));
            }
        });

        target_pemasukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settargetincome();

            }
        });

        target_pengeluaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settargetexpense();

            }
        });

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if (isOpen){

                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_expense_btn.setClickable(false);
                    fab_income_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);

                    isOpen = false;
                }

                else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_expense_btn.setClickable(true);
                    fab_income_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);

                    isOpen = true;
                }

            }
        });

        fab_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        mIncomeDatabaseDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int[] totalincome = {0};
                final int[] totaltarget = {0};

                for (DataSnapshot mysnap:snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    totalincome[0] +=data.getAmount();
                    totaltarget[0] +=data.getAmount();

                    String stResult = String.valueOf(totalincome[0]);

                    totalIncomeResult.setText(stResult);
                }

                mIncomeTarget.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        int param = 0;

                        for (DataSnapshot mysnap:snapshot.getChildren()){

                            Data_set data = mysnap.getValue(Data_set.class);

                            param += data.getAmount();

                            if (param > 0){
                                totaltarget[0] -= data.getAmount();

                                String sttarget = String.valueOf(totaltarget[0]);

                                selisih_income.setText(sttarget);
                            }

                            else {
                                selisih_income.setText("0");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabaseDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int[] totalexpense = {0};
                final int[] totaltarget = {0};

                for (DataSnapshot mysnap:snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    totalexpense[0] +=data.getAmount();
                    totaltarget[0] +=data.getAmount();

                    String stResult = String.valueOf(totalexpense[0]);

                    totalExpenseResult.setText(stResult);
                }

                mExpenseTarget.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int param = 0;

                        for (DataSnapshot mysnap:snapshot.getChildren()){

                            Data_set data = mysnap.getValue(Data_set.class);

                            param += data.getAmount();

                            if (param > 0){
                                totaltarget[0] -= data.getAmount();

                                String sttarget = String.valueOf(totaltarget[0]);

                                selisih_expense.setText(sttarget);
                            }

                            else {
                                selisih_expense.setText("0");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mIncomeTarget.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totaltarget = 0;

                for (DataSnapshot mysnap:snapshot.getChildren()){

                    Data_set data = mysnap.getValue(Data_set.class);

                    totaltarget += data.getAmount();

                    String sttarget = String.valueOf(totaltarget);

                    set_income.setText(sttarget);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseTarget.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                int totaltarget = 0;

                for (DataSnapshot mysnap:snapshot.getChildren()){

                    Data_set data = mysnap.getValue(Data_set.class);

                    totaltarget += data.getAmount();

                    String sttarget = String.valueOf(totaltarget);

                    set_expense.setText(sttarget);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int[] totalincome = {0};

                for (DataSnapshot mysnap:snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    totalincome[0] +=data.getAmount();

                }

                mExpenseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot mysnap:snapshot.getChildren()){
                            Data data = mysnap.getValue(Data.class);

                            totalincome[0] -=data.getAmount();

                            String stResult = String.valueOf(totalincome[0]);

                            totalResult.setText(stResult);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mIncomeDatabaseDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int[] totalincome = {0};
                final int[] totalexpense = {0};
                final int[] totalsaldo = {0};

                for (DataSnapshot mysnap:snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    totalincome[0] +=data.getAmount();
                    totalsaldo[0] +=data.getAmount();

                }

                mExpenseDatabaseDate.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot mysnap:snapshot.getChildren()){
                            Data data = mysnap.getValue(Data.class);

                            totalexpense[0] +=data.getAmount();
                            totalsaldo[0] +=data.getAmount();

                            AnimatedPieViewConfig config = new AnimatedPieViewConfig();
                            config.startAngle(-90)
                                    .addData(new SimplePieInfo(totalincome[0], Color.parseColor("#2196F3"), "Pemasukan"))
                                    .addData(new SimplePieInfo(totalexpense[0], Color.parseColor("#0F4B7A"), "Pengeluaran"))
                                    .drawText(true)
                                    .strokeMode(false)
                                    .duration(2000)
                                    .textSize(30)
                                    .canTouch(true).pieRadius(300);

                            mAnimatedPieView.applyConfig(config);
                            mAnimatedPieView.start();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> firebaseRecyclerOptions_income = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mIncomeDatabase, Data.class).build();
        FirebaseRecyclerOptions<Data> firebaseRecyclerOptions_expense = new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase, Data.class).build();

        adapter_expense = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(firebaseRecyclerOptions_expense) {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent,false));
            }

            @Override
            protected void onBindViewHolder(ExpenseViewHolder expenseViewHolder, int i, Data data) {

                expenseViewHolder.setExpenseAmmount(data.getAmount());
                expenseViewHolder.setExpenseType(data.getType());
                expenseViewHolder.setExpenseDate(data.getDate());

            }
        };

        adapter_income = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(firebaseRecyclerOptions_income) {

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false));
            }

            @Override
            protected void onBindViewHolder(IncomeViewHolder incomeViewHolder, int i, Data data) {

                incomeViewHolder.setIncomeAmmount(data.getAmount());
                incomeViewHolder.setIncomeType(data.getType());
                incomeViewHolder.setIncomeDate(data.getDate());

            }
        };

        mRecyclerExpense.setAdapter(adapter_expense);
        mRecyclerIncome.setAdapter(adapter_income);
        adapter_income.startListening();
        adapter_expense.startListening();

        month.setText(bulan);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter_expense != null & adapter_income != null){
            adapter_income.stopListening();
            adapter_expense.stopListening();
        }
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type){
            TextView mtype = mIncomeView.findViewById(R.id.type_income_dashboard_card);
            mtype.setText(type);
        }

        public void setIncomeAmmount(int ammount){
            TextView mammount = mIncomeView.findViewById(R.id.ammount_income_dashboard_card);
            String strAmmount = String.valueOf(ammount);
            mammount.setText(strAmmount);
        }

        public void setIncomeDate(String date){
            TextView mDate = mIncomeView.findViewById(R.id.date_income_dashboard_card);
            mDate.setText(date);
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type){
            TextView mtype = mExpenseView.findViewById(R.id.expense_type_dashboard_card);
            mtype.setText(type);
        }

        public void setExpenseAmmount(int ammount){
            TextView mammount = mExpenseView.findViewById(R.id.expense_ammount_dashboard_card);
            String strAmmount = String.valueOf(ammount);
            mammount.setText(strAmmount);
        }

        public void setExpenseDate(String date){
            TextView mDate = mExpenseView.findViewById(R.id.expense_date_dashboard_card);
            mDate.setText(date);
        }
    }

    private void addData(){

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                incomeDataInsert();

            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expenseDataInsert();

            }
        });
    }

    public void resetdata (){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.reset_popup, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        Button btnCancel = myview.findViewById(R.id.btnbatal_resetset);
        Button btnDelete = myview.findViewById(R.id.btnhapus_resetset);
        EditText verivikasi = myview.findViewById(R.id.verivikasi);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String setverif = verivikasi.getText().toString();
                if (!setverif.equals("NIKEPHOROS")){

                    verivikasi.setError("Tulisan salah!");
                    return;

                }

                mIncomeDatabase.removeValue();
                mExpenseDatabase.removeValue();
                mIncomeDelete.removeValue();
                mExpenseDelete.removeValue();
                mIncomeTargetChild.removeValue();
                mExpenseTargetChild.removeValue();

                dialog.dismiss();

                getFragmentManager().beginTransaction().detach(DashboardFragment.this).attach(DashboardFragment.this).commit();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void settargetincome (){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.set_target, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        EditText settarget = myview.findViewById(R.id.ammount_set_update);

        Button btnSave = myview.findViewById(R.id.btntetapkan_set);
        Button btnCancel = myview.findViewById(R.id.btnbatal_set);
        Button btnDelete = myview.findViewById(R.id.btnhapus_set);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String set = settarget.getText().toString().trim();

                if (set.isEmpty()){
                    settarget.setError("Tidak Boleh Kosong");
                    return;
                }

                int numset = Integer.parseInt(set);

                Data_set data = new Data_set(numset);

                mIncomeTargetChild.setValue(data);

                dialog.dismiss();

                getFragmentManager().beginTransaction().detach(DashboardFragment.this).attach(DashboardFragment.this).commit();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeTargetChild.removeValue();
                dialog.dismiss();
                getFragmentManager().beginTransaction().detach(DashboardFragment.this).attach(DashboardFragment.this).commit();
            }
        });

        dialog.show();

    }

    public void settargetexpense (){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.set_target, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        EditText settarget = myview.findViewById(R.id.ammount_set_update);

        Button btnSave = myview.findViewById(R.id.btntetapkan_set);
        Button btnCancel = myview.findViewById(R.id.btnbatal_set);
        Button btnDelete = myview.findViewById(R.id.btnhapus_set);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String set = settarget.getText().toString().trim();

                if (set.isEmpty()){
                    settarget.setError("Tidak Boleh Kosong");
                    return;
                }

                int numset = Integer.parseInt(set);

                Data_set data = new Data_set(numset);

                mExpenseTargetChild.setValue(data);

                dialog.dismiss();

                getFragmentManager().beginTransaction().detach(DashboardFragment.this).attach(DashboardFragment.this).commit();

            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseTargetChild.removeValue();
                dialog.dismiss();
                getFragmentManager().beginTransaction().detach(DashboardFragment.this).attach(DashboardFragment.this).commit();
            }
        });

        dialog.show();

    }


    public void incomeDataInsert(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        EditText editAmmount = myview.findViewById(R.id.ammount_edit);
        EditText editType = myview.findViewById(R.id.type_edit);
        EditText editNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = editType.getText().toString().trim();
                String ammount = editAmmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (type.isEmpty()){
                    editType.setError("Tidak Boleh Kosong");
                    return;
                }

                if (ammount.isEmpty()){
                    editAmmount.setError("Tidak Boleh Kosong");
                    return;
                }

                int ourammountint = Integer.parseInt(ammount);

                if (note.isEmpty()){
                    editNote.setError("Tidak Boleh Kosong");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(ourammountint, type, note, id, mDate);

                mIncomeDatabase.child(id).setValue(data);

                mIncomeDatabaseDate.child(id).setValue(data);

                dialog.dismiss();

                getFragmentManager().beginTransaction().detach(DashboardFragment.this).attach(DashboardFragment.this).commit();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        EditText editAmmount = myview.findViewById(R.id.ammount_edit);
        EditText editType = myview.findViewById(R.id.type_edit);
        EditText editNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = editType.getText().toString().trim();
                String ammount = editAmmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (type.isEmpty()){
                    editType.setError("Tidak Boleh Kosong");
                    return;
                }

                if (ammount.isEmpty()){
                    editAmmount.setError("Tidak Boleh Kosong");
                    return;
                }

                int ourammountint = Integer.parseInt(ammount);

                if (note.isEmpty()){
                    editNote.setError("Tidak Boleh Kosong");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(ourammountint, type, note, id, mDate);

                mExpenseDatabase.child(id).setValue(data);

                mExpenseDatabaseDate.child(id).setValue(data);

                dialog.dismiss();

                getFragmentManager().beginTransaction().detach(DashboardFragment.this).attach(DashboardFragment.this).commit();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}