package com.example.belfu.homi.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.belfu.homi.Activity.MainActivity;
import com.example.belfu.homi.Adapter.CustomAdapter;
import com.example.belfu.homi.Model.Expenses;
import com.example.belfu.homi.Model.User;
import com.example.belfu.homi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

/**
 * Created by belfu on 30.03.2018.
 */

public class PersonFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemSelectedListener{

    String oda;
    FirebaseDatabase database;
    ListView lv;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Button btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_person, container, false);

        TextView tv = v.findViewById(R.id.textView9);
        lv = v.findViewById(R.id.listVievArsiv);
        btn = v.findViewById(R.id.button4);
        database = FirebaseDatabase.getInstance();
        final ArrayList<String> dateList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, dateList);
        final Spinner spinner = v.findViewById(R.id.spinner);
        final ArrayList<Expenses> expensesList = new ArrayList<>();
        final CustomAdapter adapter1 = new CustomAdapter(getActivity(), expensesList, firebaseUser);

        oda = MainActivity.secilenGrupStatic;
        final String[][] k = {oda.split("\\-")};
        tv.setText(k[0][1]);

        DatabaseReference dbRef = database.getReference("group/"+oda+"/expenses");


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dateList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    dateList.add(ds.getKey());
                    spinner.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final DatabaseReference dbRef1 = database.getReference("group/"+oda+"/expenses/"+spinner.getItemAtPosition(position));
                dbRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        expensesList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getKey().equals("name"))
                                continue;
                            expensesList.add(ds.getValue(Expenses.class));
                        }
                        lv.setAdapter(adapter1);
                        adapter1.notifyDataSetChanged();

                        final int[] totalP = {0};
                        final int[] total = {0};
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Expenses expenses = ds.getValue(Expenses.class);
                            total[0] += expenses.getCost();
                            if (expenses.getGonderen().equals(firebaseUser.getEmail())) {
                                totalP[0] += expenses.getCost();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.setTitle("Ayrıntılar");
                View view1 = getActivity().getLayoutInflater().inflate(R.layout.dialog_expensedetail, null);
                dialog.setContentView(view1);


                String[] k = expensesList.get(i).getGonderen().split("\\.");

                TextView name = view1.findViewById(R.id.textView3);
                TextView note = view1.findViewById(R.id.textView6);
                TextView cost = view1.findViewById(R.id.textView7);
                TextView time = view1.findViewById(R.id.textView8);

                name.setText("Kişi:  " + k[0]);
                note.setText("Not:  " + expensesList.get(i).getMesaj().toString());
                cost.setText("Tutar:  " + expensesList.get(i).getCost() + "");
                time.setText("Zaman:  " + expensesList.get(i).getZaman());

                dialog.show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = (String) spinner.getItemAtPosition(spinner.getSelectedItemPosition());
                final DatabaseReference dbRef1 = database.getReference("group/"+oda+"/expenses/"+a);
                dbRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int[] totalP = {0};
                        int[] total = {0};
                        int[] kisibasi = {0};
                        int[] borc = {0};
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Expenses expenses = ds.getValue(Expenses.class);
                            total[0] += expenses.getCost();
                            if (expenses.getGonderen().equals(firebaseUser.getEmail())) {
                                totalP[0] += expenses.getCost();
                            }
                        }
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setCancelable(true);
                        dialog.setTitle("Ayrıntılar");
                        View view1 = getActivity().getLayoutInflater().inflate(R.layout.dialog_endof_month, null);
                        dialog.setContentView(view1);

                        TextView tvP =view1.findViewById(R.id.textView11);
                        TextView tvT =view1.findViewById(R.id.textView12);
                        TextView tvB =view1.findViewById(R.id.textView10);
                        tvP.setText("Harcamalarım: "+totalP[0] + "");
                        tvT.setText("Total:  "+total[0] + "");

                        kisibasi[0] = total[0]/2;
                        borc[0] = kisibasi[0]-totalP[0];
                        Log.wtf("borc",borc[0]+"");
                        if(borc[0]<0){
                            tvB.setText("Borç: "+"alacak "+-1*borc[0]);
                        }else{
                            tvB.setText("Borç: "+"verecek "+borc[0]);
                        }

                        dialog.show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
