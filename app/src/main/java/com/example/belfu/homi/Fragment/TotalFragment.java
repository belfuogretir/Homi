package com.example.belfu.homi.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.belfu.homi.Activity.GrupDetailActivity;
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
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by belfu on 30.03.2018.
 */

public class TotalFragment extends android.support.v4.app.Fragment {

    Button btnSave,btngrup;
    TextView tv, tvT, tvP;
    EditText editText, editTextC;
    ListView lv;
    FirebaseDatabase database;
    LinearLayout linearL;
    ImageView iv, ivp;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String oda;
    final SimpleDateFormat sdff = new SimpleDateFormat("MMyyyy");
    final String currentDateandTime = sdff.format(new Date());
    final ArrayList<String> memberList = new ArrayList<>();
    final ArrayList<String> oneSignalList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_total, container, false);

        btngrup = v.findViewById(R.id.button3);
        linearL = v.findViewById(R.id.linearL);
        tvT = v.findViewById(R.id.textView5);
        tvP = v.findViewById(R.id.textView4);
        editText = v.findViewById(R.id.editText4);
        editTextC = v.findViewById(R.id.editText5);
        lv = v.findViewById(R.id.listView);
        btnSave = v.findViewById(R.id.button5);
        iv = v.findViewById(R.id.imageView1);
        ivp = v.findViewById(R.id.imageView2);
        database = FirebaseDatabase.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:dd");
        final String zaman = sdf.format(new Date());
        Log.wtf("date", currentDateandTime + "");


        oda = MainActivity.secilenGrupStatic;
        final String[][] k = {oda.split("\\-")};
        btngrup.setText(k[0][1]);

        DatabaseReference dbRef2 = database.getReference("group/"+oda+"/members");

        dbRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if (!ds.getValue().equals(firebaseUser.getEmail())){
                        memberList.add(ds.getValue()+"");
                    }
                }
                for(int i=1;i<=memberList.size();i++){
                    final ImageView imageview = new ImageView(getContext());
                    imageview.setBackgroundResource(R.color.Progress2);
                    linearL.addView(imageview);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRefUsers=database.getReference("users");
                dbRefUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            if(!ds.getKey().equals("updatedTime")) {
                                User user = ds.getValue(User.class);
                                for (int i = 0; i < memberList.size(); i++) {
                                    if (user.getEmail().equals(memberList.get(i))) {
                                        try {
                                                OneSignal.postNotification(new JSONObject("{'contents': {'en':"+editText.getText().toString()+"}, 'include_player_ids': ['" + user.getOneSignalId() + "'],'data':{'id':"+oda+"}}"), null);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                DatabaseReference dbRef = database.getReference("group/" + oda + "/expenses/" + currentDateandTime);
                String gonderen = firebaseUser.getEmail();
                String mesaj = editText.getText().toString();
                int cost = Integer.parseInt(editTextC.getText().toString());
                dbRef.getRef().push().setValue(new Expenses(gonderen, mesaj, zaman, cost));
                editText.setText("");
                editTextC.setText("");
            }
        });

        btngrup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GrupDetailActivity.class);
               // intent.putExtra("Oda",oda);
                getActivity().startActivity(intent);
            }
        });




        final ArrayList<Expenses> expensesList = new ArrayList<>();
        final CustomAdapter adapter = new CustomAdapter(getActivity(), expensesList, firebaseUser);
        DatabaseReference dbRef = database.getReference("group/" + oda + "/expenses/" + currentDateandTime);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expensesList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        if (ds2.getValue().equals("deneme000001")) {
                            ds.getRef().removeValue();
                        }
                    }
                    if (ds.getKey().equals("name"))
                        continue;
                    expensesList.add(ds.getValue(Expenses.class));
                }
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setMessage("silmek istediğinize emin misiniz?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int k) {
                                Delete(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int l) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = mBuilder.create();
                alertDialog.setTitle("Exit?");
                alertDialog.show();
                return true;
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


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int[] totalP = {0};
                final int[] total = {0};
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Expenses expenses = ds.getValue(Expenses.class);
                    total[0] += expenses.getCost();
                    if (expenses.getGonderen().equals(firebaseUser.getEmail())) {
                        totalP[0] += expenses.getCost();
                    }
                }
                tvP.setText(totalP[0] + "");
                tvT.setText(total[0] + "");
                float a = (float) ((double) totalP[0] / (double) total[0]);
                float b = 1 - a;

                iv.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, b));

                ivp.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, a));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    private void Delete(final int i) {
        DatabaseReference dbRef1 = database.getReference("group/" + oda + "/expenses/" + currentDateandTime);

        dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (i == count) {
                        Expenses expenses = ds.getValue(Expenses.class);
                        if (expenses.getGonderen().equals(firebaseUser.getEmail())) {
                            ds.getRef().removeValue();
                        }
                    }
                    count++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
