package com.example.belfu.homi.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.belfu.homi.Model.Expenses;
import com.example.belfu.homi.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by belfu on 29.03.2018.
 */

public class CustomAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    ArrayList<Expenses> expensesList;
    FirebaseUser fUser;
    public CustomAdapter(Activity activity, ArrayList<Expenses>expensesList, FirebaseUser fUser){
        this.expensesList=expensesList;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fUser=fUser;
    }
    @Override
    public int getCount(){return expensesList.size(); }

    @Override
    public Object getItem(int position){return expensesList.get(position);}

    @Override
    public long getItemId (int position) {return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View satir = null;
        Expenses mesaj = expensesList.get(position);

        if(mesaj.getGonderen().equals(fUser.getEmail())) {
            satir = layoutInflater.inflate(R.layout.custom_sag,null);
            TextView mail = (TextView) satir.findViewById(R.id.textViewBen);
            String[] k = fUser.getEmail().split("\\.");
            mail.setText(k[0]);
            TextView mesajj = (TextView) satir.findViewById(R.id.textViewMesajim);
            mesajj.setText(mesaj.getMesaj());
            /*TextView zaman = (TextView) satir.findViewById(R.id.textViewZamanim);
            zaman.setText(mesaj.getZaman());*/
            TextView cost = (TextView) satir.findViewById(R.id.textViewCost);
            cost.setText(mesaj.getCost()+"");

        }



        else {
            satir = layoutInflater.inflate(R.layout.custom_sol,null);
            TextView maili = (TextView) satir.findViewById(R.id.textViewGonderenKisi);
            String[] k = mesaj.getGonderen().split("\\.");
            maili.setText(k[0]);
            TextView mesaji = (TextView) satir.findViewById(R.id.textViewMesaji);
            mesaji.setText(mesaj.getMesaj());
            /*TextView zamani = (TextView) satir.findViewById(R.id.textViewZamani);
            zamani.setText(mesaj.getZaman());*/
            TextView costi = (TextView) satir.findViewById(R.id.textViewCosti);
            costi.setText(mesaj.getCost()+"");
        }
        return satir;
    }
}
