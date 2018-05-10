package com.example.belfu.homi.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.belfu.homi.Adapter.PersonAdapter;
import com.example.belfu.homi.Model.User;
import com.example.belfu.homi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GrupDetailActivity extends AppCompatActivity {

    String oda;
    TextView tv;
    ListView lv;
    ImageButton ib;
    Button btn;
    FirebaseDatabase database;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    boolean z= false;
    boolean b = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_detail);


        database = FirebaseDatabase.getInstance();
        final ArrayList<String> memberList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, memberList);
        final PersonAdapter adapter1 = new PersonAdapter(memberList,this);
        lv = (ListView) findViewById(R.id.listviewkisiler);
        ib = (ImageButton) findViewById(R.id.imageButton4);
        tv = (TextView) findViewById(R.id.textView2);
        btn = (Button) findViewById(R.id.button6);
        oda = MainActivity.secilenGrupStatic;
        final String[][] k = {oda.split("\\-")};
        tv.setText(k[0][1]);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:dd");
        final String zaman = sdf.format(new Date());
        final DatabaseReference dbRef2 = database.getReference("users");


        final DatabaseReference dbRef = database.getReference("group/"+oda+"/members");
        //dbRef.child("updatedTime").setValue(zaman);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberList.clear();
                String[] asd = firebaseUser.getEmail().split("\\.");
                if(asd[0].equals(k[0][0])){
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        memberList.add(ds.getValue().toString());
                        lv.setAdapter(adapter1);
                    }
                }else{
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        memberList.add(ds.getValue().toString());
                        lv.setAdapter(adapter);
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(GrupDetailActivity.this);
                mBuilder.setMessage("gruptan cıkmak istediğinize emin misiniz").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int k) {
                                Delete();

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
            }
        });

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(GrupDetailActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_addperson, null);
                final EditText etMail = (EditText) mView.findViewById(R.id.editText2);
                Button dialogButton = (Button) mView.findViewById(R.id.button7);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(int i=0; i<memberList.size(); i++){
                                        if((etMail.getText().toString()).equals(memberList.get(i)))
                                            z= true;
                                }
                                if(z){
                                    z = false;
                                    Toast.makeText(getApplicationContext(), "Kişi zaten ekli", Toast.LENGTH_SHORT).show();
                                }else{
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        if (user.getEmail().equals(etMail.getText().toString())) {
                                            Toast.makeText(getApplicationContext(), "Eklendi", Toast.LENGTH_SHORT).show();
                                            dbRef.getRef().push().setValue(etMail.getText().toString());
                                            memberList.add(etMail.getText().toString());
                                            b = false;
                                            mView.setVisibility(View.GONE);
                                            break;
                                        } else {
                                            Log.wtf("else", "burda");
                                        }
                                        lv.setAdapter(adapter1);
                                    }
                                    if (b) {
                                        Log.wtf("alert2", "burda");
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GrupDetailActivity.this);
                                        mBuilder.setMessage("Davet et?").setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int k) {
                                                        Intent share = new Intent(Intent.ACTION_SEND);
                                                        share.setType("text/plain");
                                                        share.putExtra(Intent.EXTRA_SUBJECT, "Homi");
                                                        share.putExtra(Intent.EXTRA_TEXT, "Homi\n\nFor Android\nhttps://play.google.com/store/apps/details?deneme\n\n");
                                                        startActivity(Intent.createChooser(share, "Share Homi with your friends"));
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int l) {
                                                        dialogInterface.cancel();
                                                    }
                                                });
                                        AlertDialog alertDialog = mBuilder.create();
                                        alertDialog.setTitle("Kullanıcı Bulunamadı");
                                        alertDialog.show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private void Delete() {
        DatabaseReference dbRef1 = database.getReference("group/"+oda+"/members");

        dbRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue().equals(firebaseUser.getEmail())) {
                        ds.getRef().removeValue();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        startActivity(new Intent(GrupDetailActivity.this, MainActivity.class));
    }
}
