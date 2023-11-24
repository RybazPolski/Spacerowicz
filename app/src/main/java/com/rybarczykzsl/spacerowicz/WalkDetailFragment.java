package com.rybarczykzsl.spacerowicz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class WalkDetailFragment extends Fragment {

    private long walkId = 0;
    public void setWalk(long id){ this.walkId = id; }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("walkId",walkId);
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            walkId = savedInstanceState.getLong("walkId");
        }else{
         // ...?
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walk_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if(view != null) {
            TextView title = (TextView) view.findViewById(R.id.tvMainTitle);
            TextView desc = (TextView) view.findViewById(R.id.tvMainDesc);
            Walk walk = Walk.walks[(int) walkId];
            title.setText(walk.getName());
            desc.setText(walk.getDesc());
        }
    }
}