package com.example.belfu.homi.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.belfu.homi.R;

import java.util.ArrayList;

/**
 * Created by belfu on 16.04.2018.
 */

public class GrupAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public GrupAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            gridView = inflater.inflate(R.layout.grup_list_view_item, null);

            TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);

            ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

            textView.setText(list.get(position));

            int a = position%4;

                if (a == 0){
                    imageView.setImageResource(R.drawable.house1);
                }if (a == 1){
                    imageView.setImageResource(R.drawable.house2);
                }if (a == 2){
                    imageView.setImageResource(R.drawable.house3);
                }if (a == 3){
                    imageView.setImageResource(R.drawable.house4);
                }



        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

}