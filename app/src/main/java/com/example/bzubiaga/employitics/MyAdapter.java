package com.example.bzubiaga.employitics;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by bzubiaga on 1/26/16.
 */
public class MyAdapter extends RecyclerView.ViewHolder {


    View mView;
    TextView tt1;
    Button like;
    TextView tt3;
    TextView tt4;
    TextView tt5;

    public MyAdapter(View itemView) {
        super(itemView);
        mView = itemView;
        tt1 = (TextView) itemView.findViewById(R.id.content);
        like = (Button) itemView.findViewById(R.id.like);
        tt3 = (TextView) itemView.findViewById(R.id.date);
        tt4 = (TextView) itemView.findViewById(R.id.name);
        tt5 = (TextView) itemView.findViewById(R.id.comments);
    }


}


