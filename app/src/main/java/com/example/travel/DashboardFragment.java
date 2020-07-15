package com.example.travel;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travel.Model.Data;
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

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income;
    private FloatingActionButton fab_expense;

    private TextView fab_income_txt;
    private TextView fab_expense_txt;
    private boolean isOpen = false;
    private Animation Fadeopen, Fadeclose;

    private TextView total_income;
    private TextView total_expense;

    private FirebaseAuth auth;
    private DatabaseReference inc_databaseReference;
    private DatabaseReference exp_databaseReference;
    private TextView textView;
    private RecyclerView recyclerView_inc;
    private RecyclerView recyclerView_exp;
    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        inc_databaseReference = FirebaseDatabase.getInstance().getReference().child("Income").child(uid);
        exp_databaseReference = FirebaseDatabase.getInstance().getReference().child("Expense").child(uid);

        inc_databaseReference.keepSynced(true);
        exp_databaseReference.keepSynced(true);

        fab_main = myview.findViewById(R.id.ft_main_btn);
        fab_income = myview.findViewById(R.id.ft_income_btn);
        fab_expense = myview.findViewById(R.id.ft_expense_btn);

        fab_income_txt = myview.findViewById(R.id.ft_income);
        fab_expense_txt = myview.findViewById(R.id.ft_expense);

        total_income=myview.findViewById(R.id.set_income);
        total_expense=myview.findViewById(R.id.set_expense);


        recyclerView_inc=myview.findViewById(R.id.dashboard_income);
        recyclerView_exp=myview.findViewById(R.id.dashboard_expense);

        Fadeopen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        Fadeclose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if (isOpen) {
                    fab_income.startAnimation(Fadeclose);
                    fab_expense.startAnimation(Fadeclose);

                    fab_income.setClickable(false);
                    fab_expense.setClickable(false);

                    fab_income_txt.startAnimation(Fadeclose);
                    fab_expense_txt.startAnimation(Fadeclose);

                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {
                    fab_income.startAnimation(Fadeopen);
                    fab_expense.startAnimation(Fadeopen);

                    fab_income.setClickable(true);
                    fab_expense.setClickable(true);

                    fab_income_txt.startAnimation(Fadeopen);
                    fab_expense_txt.startAnimation(Fadeopen);

                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }
        });
        inc_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalsm=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Data data=snapshot.getValue(Data.class);
                    totalsm+=data.getAmount();
                    String stotalinc=String.valueOf(totalsm);

                    total_income.setText("$ "+stotalinc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        exp_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalsm=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Data data=snapshot.getValue(Data.class);
                    totalsm+=data.getAmount();
                    String stotalexpense=String.valueOf(totalsm);

                    total_expense.setText("$ "+stotalexpense);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager linearLayoutManager_inc=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManager_inc.setStackFromEnd(true);
        linearLayoutManager_inc.setReverseLayout(true);
        recyclerView_inc.setHasFixedSize(true);
        recyclerView_inc.setLayoutManager(linearLayoutManager_inc);

        LinearLayoutManager linearLayoutManager_exp=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManager_exp.setStackFromEnd(true);
        linearLayoutManager_exp.setReverseLayout(true);
        recyclerView_exp.setHasFixedSize(true);
        recyclerView_exp.setLayoutManager(linearLayoutManager_exp);

        return myview;
    }

    private void ftanimation()
    {
        if (isOpen) {
            fab_income.startAnimation(Fadeclose);
            fab_expense.startAnimation(Fadeclose);

            fab_income.setClickable(false);
            fab_expense.setClickable(false);

            fab_income_txt.startAnimation(Fadeclose);
            fab_expense_txt.startAnimation(Fadeclose);

            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;
        } else {
            fab_income.startAnimation(Fadeopen);
            fab_expense.startAnimation(Fadeopen);

            fab_income.setClickable(true);
            fab_expense.setClickable(true);

            fab_income_txt.startAnimation(Fadeopen);
            fab_expense_txt.startAnimation(Fadeopen);

            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }
    private void addData() {
        fab_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income_insert();
            }
        });
        fab_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expense_insert();
            }
        });
    }

    public void income_insert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.insert_data, null);
        mydialog.setView(myview);
        mydialog.setCancelable(false);

        final AlertDialog dialog = mydialog.create();

        final EditText edt_amount = myview.findViewById(R.id.amount_edit);
        final EditText edt_type = myview.findViewById(R.id.type_edit);
        final EditText edt_note = myview.findViewById(R.id.note_edit);

        Button btn_save = myview.findViewById(R.id.save_btn);
        Button btn_cancel = myview.findViewById(R.id.cancel_btn);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = edt_type.getText().toString().trim();
                String amount = edt_amount.getText().toString().trim();
                String note = edt_note.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edt_type.setError("This is a required Field !");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    edt_amount.setError("This is a required Field !");
                    return;
                }
                int cnvt_amount = Integer.parseInt(amount);
                if (TextUtils.isEmpty(note)) {
                    edt_note.setError("This is a required Field !");
                    return;
                }
                String id = inc_databaseReference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(cnvt_amount, type, note, id, date);

                inc_databaseReference.child(id).setValue(data);

                Toast.makeText(getActivity(), "New Data inserted", Toast.LENGTH_SHORT).show();
                ftanimation();
                dialog.dismiss();

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftanimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void expense_insert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.insert_data, null);
        mydialog.setView(myview);
        mydialog.setCancelable(false);
        final AlertDialog dialog = mydialog.create();

        final EditText edt_amount = myview.findViewById(R.id.amount_edit);
        final EditText edt_type = myview.findViewById(R.id.type_edit);
        final EditText edt_note = myview.findViewById(R.id.note_edit);

        Button btn_save = myview.findViewById(R.id.save_btn);
        Button btn_cancel = myview.findViewById(R.id.cancel_btn);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = edt_type.getText().toString().trim();
                String amount = edt_amount.getText().toString().trim();
                String note = edt_note.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edt_type.setError("This is a required Field !");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    edt_amount.setError("This is a required Field !");
                    return;
                }
                int cnvt_amount = Integer.parseInt(amount);
                if (TextUtils.isEmpty(note)) {
                    edt_note.setError("This is a required Field !");
                    return;
                }
                String id = exp_databaseReference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(cnvt_amount, type, note, id, date);

                exp_databaseReference.child(id).setValue(data);

                Toast.makeText(getActivity(), "New Data inserted", Toast.LENGTH_SHORT).show();


                ftanimation();
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftanimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>().setQuery(inc_databaseReference,Data.class).build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Data, DashboardFragment.inc_viewholder>(options) {

            @Override
            public DashboardFragment.inc_viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboardinc,parent,false);
                return new DashboardFragment.inc_viewholder(view);
            }

            @Override
            protected void onBindViewHolder(DashboardFragment.inc_viewholder holder, int position,Data model) {
                holder.setincometype(model.getType());
                holder.setincomedate(model.getDate());
                holder.setincomeamount(model.getAmount());
            }
        };

        firebaseRecyclerAdapter.startListening();

        recyclerView_inc.setAdapter(firebaseRecyclerAdapter);

        FirebaseRecyclerOptions<Data> options1=
                new FirebaseRecyclerOptions.Builder<Data>().setQuery(exp_databaseReference,Data.class).build();
        FirebaseRecyclerAdapter expenseadapter= new FirebaseRecyclerAdapter<Data, DashboardFragment.exp_viewholder>(options1) {

            @Override
            public DashboardFragment.exp_viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboardexp,parent,false);
                return new DashboardFragment.exp_viewholder(view);
            }

            @Override
            protected void onBindViewHolder(DashboardFragment.exp_viewholder holder, int position,Data model1) {
                holder.setexptype(model1.getType());
                holder.setexpdate(model1.getDate());
                holder.setexpamount(model1.getAmount());
            }
        };

        expenseadapter.startListening();
        recyclerView_exp.setAdapter(expenseadapter);
    }


    public static class inc_viewholder extends RecyclerView.ViewHolder
    {
        View inc_view;
        public inc_viewholder( View itemView) {
            super(itemView);
            inc_view=itemView;
        }
        public void setincometype(String type)
        {
            TextView textView=inc_view.findViewById(R.id.typeinc);
            textView.setText(type);
        }

        public void setincomeamount(int amount)
        {
            TextView textView=inc_view.findViewById(R.id.amountinc);
            String amt=String.valueOf(amount);
            textView.setText("$ " +amt);
        }

        public void setincomedate(String date)
        {
            TextView textView=inc_view.findViewById(R.id.dateinc);
            textView.setText(date);
        }

    }

    public static class exp_viewholder extends RecyclerView.ViewHolder
    {
        View exp_view;
        public exp_viewholder( View itemView) {
            super(itemView);
            exp_view=itemView;
        }
        public void setexptype(String type)
        {
            TextView textView=exp_view.findViewById(R.id.typeexp);
            textView.setText(type);
        }

        public void setexpamount(int amount)
        {
            TextView textView=exp_view.findViewById(R.id.amountexp);
            String amt=String.valueOf(amount);
            textView.setText("$ "+amt);
        }

        public void setexpdate(String date)
        {
            TextView textView=exp_view.findViewById(R.id.dateexp);
            textView.setText(date);
        }

    }
}
