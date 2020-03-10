package com.example.android.Adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.buildware.widget.indeterm.IndeterminateCheckBox;
import com.example.android.Items.ChapterSelectItem;
import com.example.android.R;
import java.util.ArrayList;
import java.util.List;

public class SelectChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VOCABOOK = 0;
    public static final int CHAPTER = 1;
    private Context context;
    private List<ChapterSelectItem> data;
    private String TAG = "SelectChapterAdapter";

    public SelectChapterAdapter(List<ChapterSelectItem> data) {
        this.data = data;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        IndeterminateCheckBox checkVocabookTitle;
        CheckBox checkChapterTitle;
        ImageView btnExpandToggle;
        TextView checkVocabookNumOfWords;
        ConstraintLayout checkVocabookLayout, checkChapterLayout;
        ChapterSelectItem ChapterItem;

        ViewHolder(View itemView) {
            super(itemView);
            checkVocabookLayout = itemView.findViewById(R.id.vocabook_layout);
            checkVocabookTitle = itemView.findViewById(R.id.check_vocabook_title);
            checkVocabookNumOfWords = itemView.findViewById(R.id.check_num_of_words_vocabook);
            checkChapterLayout = itemView.findViewById(R.id.check_chapter_layout);
            checkChapterTitle = itemView.findViewById(R.id.check_chapter_title);
            btnExpandToggle = itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (type) {
            case VOCABOOK:
                if (inflater != null) {
                    view = inflater.inflate(R.layout.item_check_vocabook, parent, false);
                }
                return new ViewHolder(view);
            case CHAPTER:
                if (inflater != null) {
                    view = inflater.inflate(R.layout.item_check_chapter, parent, false);
                }
                return new ViewHolder(view);
        }
        return null;
    }

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ChapterSelectItem item = data.get(position);
        final ViewHolder itemController = (ViewHolder) holder;
        switch (item.type) {
            case VOCABOOK:
                String numOfwords = "(" + item.getNumOfWords() +")";
                itemController.ChapterItem = item;
                itemController.checkVocabookTitle.setText(item.title);
                itemController.checkVocabookNumOfWords.setText(numOfwords);
                if (item.invisibleChapters == null) { // invisibleChapters 에 자식아이템이 없을 때
                    itemController.btnExpandToggle.setImageResource(R.drawable.circle_minus);
                } else {// invisibleChapters 에 자식아이템이 있을 때
                    itemController.btnExpandToggle.setImageResource(R.drawable.circle_plus);
                }
                if (data.get(position).getItemChecked() == null) {
                    itemController.checkVocabookTitle.setState(null);
                } else if (data.get(position).getItemChecked()) {
                    itemController.checkVocabookTitle.setState(true);
                } else {
                    itemController.checkVocabookTitle.setState(false);
                }

                itemController.checkVocabookLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChapters == null) { // 열려있는 vocabook 를 닫을 때
                            item.invisibleChapters = new ArrayList<ChapterSelectItem>();
                            int count = 0;
                            int pos = data.indexOf(item);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHAPTER) {
                                //Log.e(TAG, "pos : " + pos + " , count :  " + count + " // 닫기 전 " +data.get(pos).title+ "의 Boolean 상태 : " + data.get(pos).isItemChecked());
                                item.invisibleChapters.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btnExpandToggle.setImageResource(R.drawable.circle_plus);
//                            for(int index = 0 ; index < item.invisibleChapters.size() ; index++){
//                                Log.e(TAG, "가려진 챕터 [" + item.invisibleChapters.get(index).title + "] 불린 상태 : " + item.invisibleChapters.get(index).isItemChecked());
//                            }
                        } else {  // 닫혀있는 vocabook 를 열 때
                            int pos = data.indexOf(item);
                            int index = pos + 1;
                            for (ChapterSelectItem i : item.invisibleChapters) {
                                data.add(index, i);
                                if (data.get(pos).getItemChecked() == null) {
                                    //Log.e(TAG, pos + "번 단어장 [" + data.get(pos).title + "] 의 체크 상태" + data.get(pos).isItemChecked());
                                } else if (data.get(pos).getItemChecked()) {
                                    //Log.e(TAG, pos + "번 단어장 [" + data.get(pos).title + "] 의 체크 상태" + data.get(pos).isItemChecked());
                                    data.get(index).setItemChecked(true);
                                } else {
                                    //Log.e(TAG, pos + "번 단어장 [" + data.get(pos).title + "] 의 체크 상태" + data.get(pos).isItemChecked());
                                    data.get(index).setItemChecked(false);
                                }
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);

itemController.btnExpandToggle.setImageResource(R.drawable.circle_minus);
                            item.invisibleChapters = null;
                        }
                        notifyDataSetChanged();
                    }
                });
                itemController.checkVocabookTitle.setOnStateChangedListener((checkBox, state) -> {
                    final int pos = data.indexOf(item);
                    Log.e(TAG, "클릭된 단어장 : " + item.title);
                    if (state == null) {
                        data.get(pos).setItemChecked(null);
                    } else if (state) {
                        data.get(pos).setItemChecked(true);
                        for (int dataIndex = pos + 1; dataIndex < data.size() && data.get(dataIndex).type == CHAPTER; dataIndex++) {
                            data.get(dataIndex).setItemChecked(true);
                        }
                        if (item.invisibleChapters != null) {  //vocabook 이 체크되면 닫혀있는 vocabook 를 열고 모든 chapter 를 체크한다.
                            int index = pos + 1;
                            for (ChapterSelectItem i : item.invisibleChapters) {
                                data.add(index, i);
                                if (data.get(pos).getItemChecked()) {
                                    data.get(index).setItemChecked(true);
                                } else {
                                    data.get(index).setItemChecked(false);
                                }
                                index++;
                            }

                            Handler handler = new Handler();
                            final int finalIndex = index;
                            final Runnable r = () -> notifyItemRangeInserted(pos + 1, finalIndex - pos - 1);
                            handler.post(r);
                            itemController.btnExpandToggle.setImageResource(R.drawable.circle_minus);
                            item.invisibleChapters = null;
                        }
                    } else {
                        data.get(pos).setItemChecked(false);
                        for (int index = pos + 1; index < data.size() && data.get(index).type == CHAPTER; index++) {
                            data.get(index).setItemChecked(false);
                        }
                    }
                    //UI 처리 문제가 발생해 Handler 처리
                    Handler handler = new Handler();
                    final Runnable r = this::notifyDataSetChanged;
                    handler.post(r);
                });

                break;
            case CHAPTER:
                //Log.e(TAG, "data 의 size : " + data.size());
                //Log.e(TAG, "position : " + position);
                String chapterTitle = data.get(position).getTitle() + " (" + data.get(position).getNumOfWords() + ")";
                itemController.checkChapterTitle.setText(chapterTitle);
                itemController.checkChapterTitle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        data.get(position).setItemChecked(true);
                    } else {
                        data.get(position).setItemChecked(false);
                    }
                });
                if (data.get(position).getItemChecked() == null) {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                } else if (data.get(position).getItemChecked()) {
                    itemController.checkChapterTitle.setChecked(true);
                } else {
                    itemController.checkChapterTitle.setChecked(false);
                }
                itemController.checkChapterTitle.setOnClickListener(v -> {

                    int pos = data.indexOf(item);
                    int firstChapterIndex = -1;
                    //Log.e(TAG, "클릭된 챕터 : " + item.title);
                    for (int dataIndex = pos; dataIndex > 0 && data.get(dataIndex).type == CHAPTER; dataIndex--) {
                        firstChapterIndex = dataIndex;
                    }
                    //Log.e(TAG, "클릭된 챕터를 포함한 단어장의 첫번째 챕터 : " + data.get(firstChapterIndex).title);
                    int chapterTotal = 0;
                    for (int dataIndex = firstChapterIndex; dataIndex < data.size() && data.get(dataIndex).type == CHAPTER; dataIndex++) {
                        //Log.e(TAG, dataIndex + "번째 Chapter (" + data.get(dataIndex).title + ") 의 체크 상태 : " + data.get(dataIndex).isItemChecked());
                        chapterTotal++;
                    }
                    //Log.e(TAG, "chapterTotal : " + chapterTotal);
                    int checkTrue = chapterTotal;
                    for (int dataIndex = firstChapterIndex; dataIndex < data.size() && data.get(dataIndex).type == CHAPTER; dataIndex++) {
                        if (data.get(dataIndex).getItemChecked()) {
                            checkTrue--;
                        }
                    }
                    //Log.e(TAG, "checkTrue : " + checkTrue);
                    if (checkTrue == chapterTotal) {
                        //Log.e(TAG, "전부 false 일 때 [" + data.get(firstChapterIndex - 1).title + "] 을 false 로 변경");
                        //전부 false 일 때
                        data.get(firstChapterIndex - 1).setItemChecked(false);
                    } else if (checkTrue == 0) {
                        //Log.e(TAG, "전부 true 일 때 [" + data.get(firstChapterIndex - 1).title + "] 을 true 로 변경");
                        //전부 true 일 때
                        data.get(firstChapterIndex - 1).setItemChecked(true);
                    } else {
                        //Log.e(TAG, "일부만 true 일 때 [" + data.get(firstChapterIndex - 1).title + "] 을 indeterminate 로 변경");
                        // 일부만 true 일 때
                        data.get(firstChapterIndex - 1).setItemChecked(null);
                    }
                    notifyDataSetChanged();
                });

break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

@Override
    public int getItemCount() {
        return data.size();
    }

}
