package com.example.belfu.homi.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.belfu.homi.Adapter.GrupAdapter;
import com.example.belfu.homi.Adapter.PersonAdapter;
import com.example.belfu.homi.Fragment.TotalFragment;
import com.example.belfu.homi.Model.Expenses;
import com.example.belfu.homi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseDatabase database;
    GridView gridView;
  //  ListView listView;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<String> groupList1;
    public static String secilenGrupStatic = null;
    ArrayList<String> groupList = new ArrayList<>();
    Boolean a= true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton iBtn = (ImageButton) findViewById(R.id.imageButton);
    //  listView = (ListView) findViewById(R.id.listView);
        gridView = (GridView) findViewById(R.id.gridView1);
        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, groupList);
        groupList1 = new ArrayList<>();
        iBtn.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();

        final GrupAdapter adapter = new GrupAdapter(groupList,this);



        final DatabaseReference dbRef = database.getReference("group");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList.clear();
                groupList1.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        for (DataSnapshot ds3 : ds2.getChildren()) {
                            if (ds3.getValue().equals(firebaseUser.getEmail())) {
                                String[] k = ds.getKey().split("\\-");
                                groupList.add(k[1]);
                                groupList1.add(ds.getKey());
                            }
                        }

                    }
                  //  listView.setAdapter(adapter);
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setMessage("gruptan cıkmak istediğinize emin misiniz").setCancelable(false)
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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String secilenGrup = groupList1.get(i);
                SimpleDateFormat sdff = new SimpleDateFormat("MMyyyy");
                final String currentDateandTime = sdff.format(new Date());
                Log.wtf("date",currentDateandTime+"");
                final DatabaseReference dbRef = database.getReference("group/"+secilenGrup+"/expenses");
                final DatabaseReference dbRef1 = database.getReference("group/"+secilenGrup+"/expenses/"+currentDateandTime);
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            if (ds.getKey().equals(currentDateandTime)){
                                Log.wtf("simdiki zaman","burda");
                                a= false;
                                break;
                            }
                        }
                            if(a){
                                Log.wtf("yeni zaman","burda");
                                dbRef.child(currentDateandTime).setValue("");
                                dbRef1.getRef().push().setValue(new Expenses("deneme000001","","",0));
                                a = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("GrupKey",secilenGrup);
                secilenGrupStatic = secilenGrup;
                TotalFragment myFragment = new TotalFragment();
                myFragment.setArguments(bundle);

                Intent intent = new Intent(getApplicationContext(),ExpenseActivity.class);
                startActivity(intent);
            }
        });

    }

    private void Delete(int i) {
        DatabaseReference dbRef1 = database.getReference("group/"+groupList1.get(i)).child("members");

        
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageButton:
                startActivity(new Intent(MainActivity.this, CreateGrupActivity.class));
                break;
        }
    }
}