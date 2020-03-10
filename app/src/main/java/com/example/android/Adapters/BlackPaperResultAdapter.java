package com.example.android.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.Items.BlackPaperItem;
import com.example.android.Items.LogItem;
import com.example.android.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlackPaperResultAdapter extends RecyclerView.Adapter<BlackPaperResultAdapter.ViewHolder> {

private ArrayList<BlackPaperItem> dataMemorized;
    private ArrayList<LogItem> dataResult;
    String TAG = "BlackPaperResultAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.result_word)
        TextView resultWord;
        @BindView(R.id.result_index)
        TextView resultIndex;
        @BindView(R.id.result_meanings_correct)
        TextView resultMeaningsCorrect;
        @BindView(R.id.correctRate)
        TextView correctRate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

public BlackPaperResultAdapter(ArrayList<BlackPaperItem> dataMemorized, ArrayList<LogItem> dataResult) {
        this.dataMemorized = dataMemorized;
        this.dataResult = dataResult;
    }

@NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);

        return new ViewHolder(view);
    }

@Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String index = (position + 1) + ". ";
        holder.resultIndex.setText(index);
        holder.resultWord.setText(dataMemorized.get(position).getWord());
        holder.resultMeaningsCorrect.setText(meanings(position));
        double correctRate = (int)((double)dataMemorized.get(position).correctCount/ (double)dataMemorized.get(position).testTime * 100.0);
        String rate = correctRate + "%";
        holder.correctRate.setText(rate);
        Log.e(TAG, position + ". correctCount : " + dataMemorized.get(position).correctCount);
        Log.e(TAG, position + ". testTime : " + dataMemorized.get(position).testTime);
    }

    @Override
    public int getItemCount() {
        return dataMemorized.size();
    }

    private String meanings(int position) {
        String meaning1;
        if (dataMemorized.get(position).getMeaning1() == null) {
            meaning1 = "";
        } else {
            meaning1 = dataMemorized.get(position).getMeaning1();
        }
        String meaning2;
        if (dataMemorized.get(position).getMeaning2() == null) {
            meaning2 = "";
        } else {
            meaning2 = ", " + dataMemorized.get(position).getMeaning2();
        }
        String meaning3;
        if (dataMemorized.get(position).getMeaning3() == null) {
            meaning3 = "";
        } else {
            meaning3 = ", " + dataMemorized.get(position).getMeaning3();
        }
        String meaning4;
        if (dataMemorized.get(position).getMeaning4() == null) {
            meaning4 = "";
        } else {
            meaning4 = ", " + dataMemorized.get(position).getMeaning4();
        }
        String meaning5;
        if (dataMemorized.get(position).getMeaning5() == null) {
            meaning5 = "";
        } else {
            meaning5 = ", " + dataMemorized.get(position).getMeaning5();
        }

        return meaning1 + meaning2 + meaning3 + meaning4 + meaning5;
    }

}