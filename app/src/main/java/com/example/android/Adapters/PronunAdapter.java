package com.example.android.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.Items.PronunItem;
import com.example.android.R;

import java.util.ArrayList;

public class PronunAdapter extends RecyclerView.Adapter<PronunAdapter.ViewHolder> {
    private ArrayList<PronunItem> dataSet;
    String TAG = "PronunAdapter";
    String selected, vocabookTitle, chapterTitle, word, meaning1, meaning2, meaning3, meaning4, meaning5, wordImage;

public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView wordPronun, meaningPronun;
        public ConstraintLayout pronunItemLayout;

        public ViewHolder(View view) {
            super(view);
            wordPronun = view.findViewById(R.id.wordPronun);
            meaningPronun = view.findViewById(R.id.meaningPronun);
            pronunItemLayout = view.findViewById(R.id.pronunItemLayout);
        }
    }

public PronunAdapter(ArrayList<PronunItem> myDataset) {
        dataSet = myDataset;
    }

@Override
    public PronunAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pronun, parent, false);
        return new ViewHolder(view);
    }

@Override
    public void onBindViewHolder(PronunAdapter.ViewHolder holder, int position) {

        holder.wordPronun.setText(dataSet.get(position).getWord());
        holder.meaningPronun.setText(meanings(position));
        Log.e(TAG, "correct = " + dataSet.get(position).correct);
        if (dataSet.get(position).correct == null) {
            holder.pronunItemLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));
        } else if (dataSet.get(position).correct) {
            holder.pronunItemLayout.setBackgroundColor(Color.argb(100, 192, 255, 149));
        } else {
            holder.pronunItemLayout.setBackgroundColor(Color.argb(100, 255, 175, 175));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public String meanings(int index) {
        if (dataSet.get(index).getMeaning1() == null) {
            meaning1 = "";
        } else {
            meaning1 = dataSet.get(index).getMeaning1();
        }
        if (dataSet.get(index).getMeaning2() == null) {
            meaning2 = "";
        } else {
            meaning2 = ", " + dataSet.get(index).getMeaning2();
        }
        if (dataSet.get(index).getMeaning3() == null) {
            meaning3 = "";
        } else {
            meaning3 = ", " + dataSet.get(index).getMeaning3();
        }
        if (dataSet.get(index).getMeaning4() == null) {
            meaning4 = "";
        } else {
            meaning4 = ", " + dataSet.get(index).getMeaning4();
        }
        if (dataSet.get(index).getMeaning5() == null) {
            meaning5 = "";
        } else {
            meaning5 = ", " + dataSet.get(index).getMeaning5();
        }

        return meaning1 + meaning2 + meaning3 + meaning4 + meaning5;
    }
}