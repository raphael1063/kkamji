package com.wonjin.android.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wonjin.android.Items.WordItem;
import com.wonjin.android.Methods;
import com.wonjin.android.R;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    private ArrayList<WordItem> dataSet;
    private WordAdapter.WordItemClickListener mListener = null;
    private Context context;
    private static String TAG = "WordAdapter";

public interface WordItemClickListener {
        // 아이템 전체 부분 클릭
        void onItemClicked(int position);

        void onDeleteButtonClicked(int position);
    }

public void setOnClickListener(WordItemClickListener listener) {
        this.mListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textWord, textMeanings;
        ImageView wordImg;
        ImageButton btnDeleteWord;

        ViewHolder(View view) {
            super(view);
            textWord = view.findViewById(R.id.textWord);
            textMeanings = view.findViewById(R.id.textMeanings);
            wordImg = view.findViewById(R.id.wordImg);
            btnDeleteWord = view.findViewById(R.id.btnDeleteWord);
            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener.onItemClicked(position);
                    }
                }

            });

            btnDeleteWord.setOnClickListener(v -> {
                int position = getAdapterPosition();
                dataSet.remove(position);
                notifyItemRemoved(position);
            });

        }
    }

    public WordAdapter(ArrayList<WordItem> myDataSet) {
        dataSet = myDataSet;

    }

@NonNull
    @Override
    public WordAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_word, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textWord.setText(dataSet.get(position).word);
        String meaning1, meaning2, meaning3, meaning4, meaning5;
        Log.e(TAG, "meanings : " + dataSet.get(position).meaning1 + " " + dataSet.get(position).meaning2 + " " + dataSet.get(position).meaning3 + " " + dataSet.get(position).meaning4 + " " + dataSet.get(position).meaning5);
        if (dataSet.get(position).meaning1 == null) {
            meaning1 = "";
        } else {
            meaning1 = dataSet.get(position).meaning1;
        }
        if (dataSet.get(position).meaning2 == null) {
            Log.e(TAG, "meaning2 = null");
            meaning2 = "";
        } else {
            meaning2 = dataSet.get(position).meaning2;
            Log.e(TAG, "meaning2 != null // dataSet.get(position).meaning2 : " + dataSet.get(position).meaning2);
        }
        if (dataSet.get(position).meaning3 == null) {
            meaning3 = "";
        } else {
            meaning3 = dataSet.get(position).meaning3;
        }
        if (dataSet.get(position).meaning4 == null) {
            meaning4 = "";
        } else {
            meaning4 = dataSet.get(position).meaning4;
        }
        if (dataSet.get(position).meaning5 == null) {
            meaning5 = "";
        } else {
            meaning5 = dataSet.get(position).meaning5;
        }

        Log.e(TAG, "word : " + dataSet.get(position).getWord());
        Log.e(TAG, "meaning1 : " + dataSet.get(position).getMeaning1());
        Log.e(TAG, "meaning2 : " + dataSet.get(position).getMeaning2());
        Log.e(TAG, "meaning3 : " + dataSet.get(position).getMeaning3());
        Log.e(TAG, "meaning4 : " + dataSet.get(position).getMeaning4());
        Log.e(TAG, "meaning5 : " + dataSet.get(position).getMeaning5());
        Log.e(TAG, "wordImage : " + dataSet.get(position).wordImageURI);

        holder.textMeanings.setText(meaning1 + " " + meaning2 + " " + meaning3 + " " + meaning4 + " " + meaning5);
        String wordImageFileName = null;
        if (dataSet.get(position).wordImageURI.startsWith("http")) {
            wordImageFileName = Methods.getWordFileNameFromUri(dataSet.get(position).wordImageURI);
            Log.e(TAG, position + "번 째 단어 이미지 파일명 = " + wordImageFileName);
        }
        if (dataSet.get(position).getWordImageURI().startsWith("/storage/emulated")) { // 새 단어 추가
            Log.e(TAG, "새 단어 추가");
            holder.wordImg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(dataSet.get(position).getWordImageURI())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.wordImg);
        } else if (dataSet.get(position).wordImageURI.isEmpty() || wordImageFileName == null) {
            Log.e(TAG, "이미지 비어있음");
            holder.wordImg.setVisibility(View.GONE);

        } else {
            Log.e(TAG, "서버에서 이미지 불러옴");
            holder.wordImg.setVisibility(View.VISIBLE);
            if (!dataSet.get(position).getWordImageURI().equals("null")) {
                String wordImageUri = Methods.getWordImageFromServer(dataSet.get(position).getWordImageURI());
                Log.e(TAG, "wordImageUri : " + wordImageUri);
                Glide.with(context)
                        .load(wordImageUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .override(200, 200)
                        .into(holder.wordImg);
            }

        }

//클릭 이벤트
        if (mListener != null) {
            final int pos = position;
            holder.itemView.setOnClickListener(view -> mListener.onItemClicked(pos));
            holder.btnDeleteWord.setOnClickListener(view -> mListener.onDeleteButtonClicked(pos));

        }

//        //TODO 뷰홀더 추가
    }

    @Override
    public int getItemCount() {
//        Log.e(TAG, "비어있는 meaningArray 의 size : " + meaningArray.size());
        return dataSet.size();
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataSet.size());

    }
}
