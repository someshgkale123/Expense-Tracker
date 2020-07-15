package com.example.travel;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.travel.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth auth;
    private DatabaseReference firebaseDatabase;

    private TextView expensetotalsum;
    private RecyclerView recyclerView;

    private EditText edit_amount;
    private EditText edit_type;
    private EditText edit_note;

    private Button update_btn;
    private Button delete_btn;

    private String type;
    private String note;
    private int amount;
    private String postkey;


    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
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
        View view= inflater.inflate(R.layout.fragment_expense, container, false);

        auth=FirebaseAuth.getInstance();

        FirebaseUser user=auth.getCurrentUser();
        String uid=user.getUid();

        firebaseDatabase= FirebaseDatabase.getInstance().getReference().child("Expense").child(uid);
        expensetotalsum=view.findViewById(R.id.total_expense);
        recyclerView=view.findViewById(R.id.expense_recycler);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalexpense=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Data data=snapshot.getValue(Data.class);
                    totalexpense+=data.getAmount();
                    String stotalexpense=String.valueOf(totalexpense);

                    expensetotalsum.setText("                     $ "+stotalexpense);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>().setQuery(firebaseDatabase,Data.class).build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Data, ExpenseFragment.viewholder>(options)
        {
            @Override
            public ExpenseFragment.viewholder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recyclerdata,parent,false);
                return new ExpenseFragment.viewholder(view);
            }


            @Override
            protected void onBindViewHolder(ExpenseFragment.viewholder holder, final int position, final Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();
                        postkey=getRef(position).getKey();
                        updateData();
                    }
                });
            }

        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class viewholder extends RecyclerView.ViewHolder
    {
        View mview;
        public viewholder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        private void setType(String type)
        {
            TextView textView=mview.findViewById(R.id.expense_type);
            textView.setText(type);
        }
        private void setNote(String note)
        {
            TextView textView=mview.findViewById(R.id.expense_note);
            textView.setText(note);
        }
        private void setDate(String date)
        {
            TextView textView=mview.findViewById(R.id.expense_date);
            textView.setText(date);
        }

        private void setAmount(int amount)
        {
            TextView textView=mview.findViewById(R.id.expense_amount);
            String samount=String.valueOf(amount);
            textView.setText("$ "+samount);
        }
    }

    private void updateData()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.alert_update_data,null);
        builder.setView(view);

        edit_amount=view.findViewById(R.id.amount_edit);
        edit_type=view.findViewById(R.id.type_edit);
        edit_note=view.findViewById(R.id.note_edit);

        update_btn=view.findViewById(R.id.update_btn);
        delete_btn=view.findViewById(R.id.delete_btn);
        edit_type.setText(type);
        edit_type.setSelection(type.length());

        edit_note.setText(note);
        edit_note.setSelection(note.length());

        edit_amount.setText(String.valueOf(amount));
        edit_amount.setSelection(String.valueOf(amount).length());


        final AlertDialog dialog=builder.create();
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=edit_type.getText().toString().trim();
                note=edit_note.getText().toString().trim();

                String amt=String.valueOf(amount);
                amt=edit_amount.getText().toString().trim();

                int newamt=Integer.parseInt(amt);

                String date= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(newamt,type,note,postkey,date);
                firebaseDatabase.child(postkey).setValue(data);
                dialog.dismiss();
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.child(postkey).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}