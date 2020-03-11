package com.wonjin.android.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.wonjin.android.Items.LogItem;
import com.wonjin.android.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.ViewHolder> {

    private ArrayList<LogItem> dataSet;
    String TAG = "TestResultAdapter";
    String meaning1, meaning2, meaning3, meaning4, meaning5;

static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.log_word)
        TextView logWord;
        @BindView(R.id.log_meanings_input)
        TextView logMeaningsInput;
        @BindView(R.id.log_index)
        TextView logIndex;
        @BindView(R.id.log_meanings_correct)
        TextView logMeaningsCorrect;
        @BindView(R.id.log_layout)
        ConstraintLayout logLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

public TestResultAdapter(ArrayList<LogItem> myDataset) {
        this.dataSet = myDataset;
    }

@NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new ViewHolder(view);
    }

@Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (dataSet.get(position).isCorrect()) {
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
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}