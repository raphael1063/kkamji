package com.example.android.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.VocabookItemMoveCallback;
import com.example.android.Items.VocabookItem;
import com.example.android.Methods;
import com.example.android.R;
import com.example.android.Retrofit.RetrofitClient;
import com.example.android.StartDragListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabookAdapter extends RecyclerView.Adapter<VocabookAdapter.ViewHolder> implements VocabookItemMoveCallback.ItemTouchHelperContract {
    private ArrayList<VocabookItem> dataSet;
    private static String TAG = "VocabookAdapter";
    private Context context;

    private final StartDragListener mStartDragListener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout vocabookLayout;
        TextView vocabookTitle, vocabookNumberOfWords;
        ImageView vocabookImage;
        ImageButton btnDragVocabook;

        ViewHolder(View view) {
            super(view);
            this.vocabookTitle = view.findViewById(R.id.text_vocabook_title);
            this.vocabookImage = view.findViewById(R.id.image_vocabook_cover);
            this.vocabookNumberOfWords = view.findViewById(R.id.text_vocabook_number_of_words);
            this.vocabookLayout = view.findViewById(R.id.vocabook_layout);
            this.btnDragVocabook = view.findViewById(R.id.btn_drag_vocabook);

        }
    }

    public VocabookAdapter(Context mContext, ArrayList<VocabookItem> dataSet, StartDragListener startDragListener) {
        this.dataSet = dataSet;
        context = mContext;
        mStartDragListener = startDragListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabook, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

@Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int test = 0;
        if (!dataSet.get(position).isSelected()) {
            holder.vocabookLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));
        } else {
            holder.vocabookLayout.setBackgroundColor(Color.argb(100, 200, 200, 200));
        }

        holder.vocabookTitle.setText(dataSet.get(position).getVocabookTitle());
        Log.e(TAG, "dataSet.get(position).getVocabookCoverImage()" + dataSet.get(position).getVocabookCoverImage());
        if (dataSet.get(position).getVocabookCoverImage().equals("default_cover_image")) { // 이미지가 없는 경우
            holder.vocabookImage.setImageResource(R.drawable.ic_book);
        } else if (dataSet.get(position).getVocabookCoverImage().startsWith("/storage/emulated")) { // 새 단어장이 추가된 경우
            Log.e(TAG, "새 단어장 추가");
            Glide.with(context)
                    .load(dataSet.get(position).getVocabookCoverImage())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.vocabookImage);
        } else { // 서버에서 단어장 커버 이미지를 불러오는 경우
            Log.e(TAG, "서버에서 이미지 불러옴");
            String coverImageUri = Methods.getVocabookImageFromServer(dataSet.get(position).getVocabookCoverImage());
            Log.e(TAG, "커버이미지 uri : " + coverImageUri);
            Glide.with(context)
                    .load(coverImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.vocabookImage);
        }
        String numOfWords = "(" + dataSet.get(position).getNumOfWords() + ")";
        Log.e(TAG, "dataSet.get(position).getNumOfWords() : " + dataSet.get(position).getNumOfWords());
        holder.vocabookNumberOfWords.setText(numOfWords);

        holder.btnDragVocabook.setOnTouchListener((v, event) -> {
            if (event.getAction() ==
                    MotionEvent.ACTION_DOWN) {
                mStartDragListener.requestDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(dataSet, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(dataSet, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
//        for(int idx = 0 ; idx < dataSet.size() ; idx++){
//            Log.e(TAG, "vocabookTitle : " + dataSet.get(idx).getVocabookTitle() + " / position : " + idx);
//        }
        for(int idx = 0 ; idx < dataSet.size() ; idx++){
            dataSet.get(idx).setVocabookNum(idx);
            Log.e(TAG, "vocabookTitle : " + dataSet.get(idx).getVocabookTitle() + " / position : " + dataSet.get(idx).getVocabookNum());
        }
        SharedPreferences settings = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        for(int idx = 0 ; idx < dataSet.size() ; idx++) {
            Call<ResponseBody> res = RetrofitClient.getInstance().getService().update_vocabook_index(userEmail, dataSet.get(idx).getVocabookTitle(), dataSet.get(idx).getVocabookNum());
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            String updateIndexResponse = response.body().string();
                            Log.e(TAG, "updateIndexResponse : " + updateIndexResponse);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "updateIndexResponse fail : " + t.getMessage());
                }
            });
        }
    }

    //드래그할 아이템의 배경색을 회색으로 바꾼다.
    @Override
    public void onRowSelected(ViewHolder holder) {
        holder.vocabookLayout.setBackgroundColor(Color.argb(100,107,178,255));
    }

    //드래그한 아이템을 놓았을 때 배경색을 회색으로 바꾼다.
    @Override
    public void onRowClear(ViewHolder holder) {
        holder.vocabookLayout.setBackgroundColor(Color.WHITE);

    }

}