package com.wonjin.android.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wonjin.android.Items.HomeItem;
import com.wonjin.android.Methods;
import com.wonjin.android.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Context context;

    private ArrayList<HomeItem> dataSet;
    String TAG = "HomeAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_vocabook_image)
        ImageView homeVocabookImage;
        @BindView(R.id.home_vocabook_title)
        TextView homeVocabookTitle;
        @BindView(R.id.home_chapter_title)
        TextView homeChapterTitle;
        @BindView(R.id.home_chapter_num)
        TextView homeChapterNum;
        @BindView(R.id.home_date)
        TextView homeDate;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    public HomeAdapter(ArrayList<HomeItem> myDataset) {
        dataSet = myDataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.homeVocabookTitle.setText(dataSet.get(position).getVocabookTitle());
        holder.homeChapterTitle.setText(dataSet.get(position).getChapterTitle());
        if(dataSet.get(position).getStudyDate() == null){
            holder.homeDate.setText("학습 기록 없음");
            holder.homeDate.setTextColor(Color.RED);
        } else {
            holder.homeDate.setText(Methods.getStudyDaysPassed(Methods.currentDate(), dataSet.get(position).getStudyDate()));
            holder.homeDate.setTextColor(Color.BLACK);
        }

        holder.homeChapterNum.setText("(" + dataSet.get(position).getChapterWordCount() + ")");
        if (dataSet.get(position).getVocabookImage() == null || dataSet.get(position).getVocabookImage().equals("default_cover_image")) { // 이미지가 없는 경우
            holder.homeVocabookImage.setImageResource(R.drawable.ic_book);
        } else { // 서버에서 단어장 커버 이미지를 불러오는 경우
            Log.e(TAG, "서버에서 이미지 불러옴");
            String coverImageUri = Methods.getVocabookImageFromServer(dataSet.get(position).getVocabookImage());
            Log.e(TAG, "커버이미지 uri : " + coverImageUri);
            Glide.with(context)
                    .load(coverImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.homeVocabookImage);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}