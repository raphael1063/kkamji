package com.example.android.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.ChapterItemMoveCallback;
import com.example.android.Items.ChapterItem;
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

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> implements ChapterItemMoveCallback.ItemTouchHelperContract {
    private ArrayList<ChapterItem> dataSet;
    private static String TAG = "ChapterAdapter";
    private ChapterAdapter.ChapterItemClickListener mListener = null;
    private Context context;

    private final StartDragListener mStartDragListener;

    public interface ChapterItemClickListener {
        // 아이템 전체 부분 클릭
        void onItemClicked(int position);
    }

public void setOnClickListener(ChapterItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        ConstraintLayout chapterLayout;
        TextView chapterTitle, chapterNumberOfWords, textRecentStudyDate;
        ImageButton btnDragChapter;

        ViewHolder(View view) {
            super(view);
            this.chapterTitle = view.findViewById(R.id.text_chapter_title);
            this.chapterNumberOfWords = view.findViewById(R.id.text_chapter_num_of_words);
            this.textRecentStudyDate = view.findViewById(R.id.text_recent_study_date);
            this.chapterLayout = view.findViewById(R.id.chapter_layout);
            this.btnDragChapter = view.findViewById(R.id.btn_drag_chapter);
            context = view.getContext();

            view.setOnCreateContextMenuListener(this);

view.setOnClickListener(view1 -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener.onItemClicked(position);
                    }
                }

            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem Delete = contextMenu.add(Menu.NONE, 1002, 2, "단어장 삭제");
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = item -> {
            final EditText folderNameSet = new EditText(context);
            if (item.getItemId() == 1002) { //단어장 삭제
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);
                builderDelete.setTitle("단어장 삭제");
                builderDelete.setMessage("정말로 [" + dataSet.get(getAdapterPosition()).getChapterTitle() + "] 단어장을 삭제하시겠습니까?");
                builderDelete.setNegativeButton("취소", (dialogInterface, i) -> Toast.makeText(context, "단어장삭제 취소", Toast.LENGTH_SHORT).show());
                builderDelete.setPositiveButton("삭제", (dialogInterface, i) -> {
                    Toast.makeText(context, "단어장삭제", Toast.LENGTH_SHORT).show();
                    deleteChapter(getAdapterPosition());
                    //해당위치의 폴더 삭제.
                    dataSet.remove(getAdapterPosition());

//어댑터에 해당위치의 아이템이 삭제되었음을 알림.
                    notifyItemRemoved(getAdapterPosition());
                    //어댑터에 아이템의 범위가 변경되었음을 알림.
                    notifyItemRangeChanged(getAdapterPosition(), dataSet.size());

                });
                builderDelete.show();
            }
            return true;
        };

}

    public void deleteChapter(int position) {
        SharedPreferences settings = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        Call<ResponseBody> res = RetrofitClient.getInstance().getService().delete_chapter(userEmail, dataSet.get(position).getVocabookTitle(), dataSet.get(position).getChapterNum(), dataSet.get(position).getChapterTitle());
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String deleteResult = response.body().string();
                    Log.e(TAG, "deleteResult :  " + deleteResult);
                } catch (IOException e) {
                    Log.e(TAG, "Retrofit Error : " + e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "failed to load user info : " + t.getMessage());
            }
        });
    }

    public ChapterAdapter(Context mContext, ArrayList<ChapterItem> dataSet, StartDragListener startDragListener) {
        this.dataSet = dataSet;
        context = mContext;
        mStartDragListener = startDragListener;
    }

    @NonNull
    @Override
    public ChapterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        context = parent.getContext();
        return new ChapterAdapter.ViewHolder(view);
    }

@Override
    public void onBindViewHolder(ChapterAdapter.ViewHolder holder, int position) {

        if (!dataSet.get(position).isSelected()) {
            holder.chapterLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));
        } else {
            holder.chapterLayout.setBackgroundColor(Color.argb(100, 200, 200, 200));
        }

        holder.chapterTitle.setText(dataSet.get(position).getChapterTitle());
        String numOfWords = "(" + dataSet.get(position).getNumOfWords() + ")";
        holder.chapterNumberOfWords.setText(numOfWords);
        String date = Methods.getStudyDaysPassed(Methods.currentDate(), dataSet.get(position).getRecentStudyDate());
        if(date.equals("기록 없음")){
            holder.textRecentStudyDate.setTextColor(Color.RED);
        }
        holder.textRecentStudyDate.setText(date);

holder.btnDragChapter.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
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

        for (int idx = 0; idx < dataSet.size(); idx++) {
            dataSet.get(idx).setChapterNum(idx);
            Log.e(TAG, "chapterTitle : " + dataSet.get(idx).getChapterTitle() + " / position : " + dataSet.get(idx).getChapterNum());
        }
        SharedPreferences settings = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String userEmail = settings.getString("loginEmail", "");
        for (int idx = 0; idx < dataSet.size(); idx++) {
            Call<ResponseBody> res = RetrofitClient.getInstance().getService().update_chapter_index(userEmail, dataSet.get(idx).getVocabookTitle(), dataSet.get(idx).getChapterTitle(), dataSet.get(idx).getChapterNum());
            res.enqueue(new Callback<ResponseBody>() {//TODO
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
    public void onRowSelected(ChapterAdapter.ViewHolder holder) {
        holder.chapterLayout.setBackgroundColor(Color.argb(100, 107, 178, 255));
    }

    //드래그한 아이템을 놓았을 때 배경색을 회색으로 바꾼다.
    @Override
    public void onRowClear(ChapterAdapter.ViewHolder holder) {
        holder.chapterLayout.setBackgroundColor(Color.WHITE);

    }
}

