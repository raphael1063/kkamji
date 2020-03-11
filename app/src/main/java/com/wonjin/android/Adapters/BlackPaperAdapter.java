package com.wonjin.android.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.wonjin.android.Activities.StudyActivities.BlackPaperActivity;
import com.wonjin.android.Items.LogItem;
import com.wonjin.android.R;
import java.util.ArrayList;

public class BlackPaperAdapter extends RecyclerView.Adapter<BlackPaperAdapter.ViewHolder>{
    private ArrayList<LogItem> dataSet;
    private String TAG = "BlackPaperAdapter";
    String meaning1, meaning2, meaning3, meaning4, meaning5;
    private BlackPaperActivity blackPaperActivity;

static class ViewHolder extends RecyclerView.ViewHolder {

        TextView logIndex, logWord, logMeaningsInput, logMeaningsCorrect;
        ConstraintLayout logLayout;
        View screenView;

        ViewHolder(View view) {
            super(view);
            this.logIndex = view.findViewById(R.id.log_index);
            this.logWord = view.findViewById(R.id.log_word);
            this.logMeaningsInput = view.findViewById(R.id.log_meanings_input);
            this.logMeaningsCorrect = view.findViewById(R.id.log_meanings_correct);
            this.logLayout = view.findViewById(R.id.log_layout);
        }
    }

public BlackPaperAdapter(ArrayList<LogItem> myDataset, Activity activity) {
        this.dataSet = myDataset;
        this.blackPaperActivity = (BlackPaperActivity) activity;
    }

@NonNull
    @Override
    public BlackPaperAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new ViewHolder(view);
    }

@Override
    public void onBindViewHolder(@NonNull BlackPaperAdapter.ViewHolder holder, int position) {
        String currentWord = blackPaperActivity.getCurrentWord();
        Log.e(TAG, "currentWord : " + currentWord);
        if(dataSet.get(position).isCorrect()){
            holder.logLayout.setBackgroundColor(Color.argb(100, 194, 255, 194)); // 맞췄을 때
            holder.logMeaningsInput.setTextColor(Color.argb(100, 0, 130, 0));
        } else {
            holder.logLayout.setBackgroundColor(Color.argb(100, 255, 194, 194)); // 틀렸을 때
            holder.logMeaningsInput.setTextColor(Color.argb(100, 130, 0, 0));
        }
        String index = dataSet.get(position).getIndex() + ". ";
        holder.logIndex.setText(index);
        holder.logWord.setText(dataSet.get(position).getWord());
        holder.logMeaningsInput.setText(dataSet.get(position).getMeaningsInput());
        holder.logMeaningsCorrect.setText(dataSet.get(position).getMeaningsCorrect());
        for(int idx = 0 ; idx < dataSet.size() ; idx ++ ){
            if(dataSet.get(position).getWord().equals(currentWord)){
                holder.logMeaningsInput.setVisibility(View.GONE);
                holder.logMeaningsCorrect.setVisibility(View.GONE);
            } else {
                holder.logMeaningsInput.setVisibility(View.VISIBLE);
                holder.logMeaningsCorrect.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}