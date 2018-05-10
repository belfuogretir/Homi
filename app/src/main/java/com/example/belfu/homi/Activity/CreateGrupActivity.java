package com.example.belfu.homi.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.belfu.homi.Adapter.PersonAdapter;
import com.example.belfu.homi.Model.Grup;
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

public class CreateGrupActivity extends AppCompatActivity {

    EditText etName;
    EditText etMail;
    Button btnSave;
    ImageButton ibAdd;
    ListView lv;
    FirebaseDatabase database;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    boolean t = true;
    boolean b = true;
    boolean d = true;
    boolean z = true;
    boolean k = false;

    final ArrayList<String> memberList = new ArrayList<>();
    final ArrayList<String> oneSignalList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:dd");
    String zaman = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_grup);

        etName = (EditText) findViewById(R.id.editText);
        etMail = (EditText) findViewById(R.id.editText3);
        btnSave = (Button) findViewById(R.id.button2);
        ibAdd = (ImageButton) findViewById(R.id.imageButton2);
        lv = (ListView) findViewById(R.id.lv);
        database = FirebaseDatabase.getInstance();

        final PersonAdapter adapter1 = new PersonAdapter(memberList,this);
        final DatabaseReference dbRef = database.getReference("users");
        final DatabaseReference dbRef1 = database.getReference("group");


        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(memberList != null){
                            for(int i=0; i<memberList.size(); i++){
                                if((etMail.getText().toString()).equals(memberList.get(i)))
                                    k= true;
                            }
                        }
                        if(k){
                            k = false;
                            Toast.makeText(getApplicationContext(), "Kişi zaten ekli", Toast.LENGTH_SHORT).show();
                            etMail.setText("");
                        }else{
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (!ds.getKey().equals("updatedTime")) {
                                    User user = ds.getValue(User.class);
                                    if (user.getEmail().equals(etMail.getText().toString())) {
                                        Toast.makeText(getApplicationContext(), "Eklendi", Toast.LENGTH_SHORT).show();
                                        memberList.add(etMail.getText().toString());
                                        b = false;
                                        etMail.setText("");
                                        break;
                                    } else {
                                        Log.wtf("else", "burda");
                                    }
                                    lv.setAdapter(adapter1);
                                }
                            }
                            if (b) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateGrupActivity.this);
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

     //buraya gelip onesignallisti doldurmuyor
        final DatabaseReference dbRefUsers=database.getReference("users");
        dbRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(!ds.getKey().equals("updatedTime")){
                        User user = ds.getValue(User.class);
                        for (int i = 0; i<memberList.size();i++) {
                            if (user.getEmail().equals(memberList.get(i))) {
                                oneSignalList.add(user.getOneSignalId());
                                String asd = firebaseUser.getEmail().split("\\.")[0];
                                asd += "-"+etName.getText().toString();
                                try {
                                    OneSignal.postNotification(new JSONObject("{'contents': {'en':"+firebaseUser.getEmail()+"sizi ekledi."+"}, 'include_player_ids': ['" + user.getOneSignalId() + "'],'data':{'id':"+asd+"}}"), null);
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etName.getText()==null || etName.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "geçerli bir grup adı giriniz", Toast.LENGTH_SHORT).show();
                }else if(memberList == null){
                    Toast.makeText(getApplicationContext(), "Kişi ekleyiniz", Toast.LENGTH_SHORT).show();
                }else{
                    String[] asd = firebaseUser.getEmail().split("\\.");
                    Log.wtf("save", "burda");
                    d= true;
                    z= true;
                    String l = etName.getText().toString();
                    final String name = asd[0]+"-"+l;

                    dbRef1.child("updatedTime").setValue(zaman);

                    dbRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(z){
                                if(t){
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getKey().equals(name + "")) {
                                            Log.wtf("asfsd", "burda");
                                            Toast.makeText(getApplicationContext(), "Grup adı mevcut", Toast.LENGTH_SHORT).show();
                                            d = false;
                                            break;
                                        }
                                    }
                                }
                                if (d) {
                                    Log.wtf("if d", "burda");
                                    t = false;
                                    dbRef1.child(name).setValue(new Grup(etName.getText().toString(), "",""));
                                    DatabaseReference dbRef3 = database.getReference("group/" + name + "/expenses");
                                    SimpleDateFormat sdff = new SimpleDateFormat("MMyyyy");
                                    String currentDateandTime = sdff.format(new Date());
                                    dbRef3.child(currentDateandTime).setValue("");
                                    Log.wtf("date",currentDateandTime+"");
                                    DatabaseReference dbRef2 = database.getReference("group/" + name + "/members");
                                    for (int i = 0; i < memberList.size(); i++) {
                                        dbRef2.getRef().push().setValue(memberList.get(i));
                                    }
                                    dbRef2.getRef().push().setValue(firebaseUser.getEmail());
                                    dbRefUsers.child("updatedTime").setValue(zaman);
                                    try {
                                        for(int i=0;i<oneSignalList.size(); i++)
                                            OneSignal.postNotification(new JSONObject("{'contents': {'en':"+firebaseUser.getEmail()+"sizi ekledi."+"}, 'include_player_ids': ['" + oneSignalList.get(i) + "'],'data':{'id':"+name+"}}"), null);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);

                                }
                                z=false;
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                }

            }
        });
    }
}
