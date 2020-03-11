package com.wonjin.android.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wonjin.android.Items.MeaningItem;
import com.wonjin.android.R;

import java.util.ArrayList;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.ViewHolder> {
    private ArrayList<MeaningItem> dataSet;

    private Context context;
    private static String TAG = "MeaningAdapter";

public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnMeaningDeleteListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

class ViewHolder extends RecyclerView.ViewHolder {
        EditText editMeaning;
        ImageButton btnDeleteMeaning;

        ViewHolder(View view) {
            super(view);
            editMeaning = view.findViewById(R.id.editMeaning);
            btnDeleteMeaning = view.findViewById(R.id.btnDeleteMeaning);

editMeaning.setOnFocusChangeListener((view1, gainFocus) -> {
                if (gainFocus) {
                    editMeaning.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            dataSet.set(getAdapterPosition(), new MeaningItem(editMeaning.getText().toString()));
//                            for(int index = 0 ; index < dataSet.size() ; index++){
////                                Log.e(TAG, "들어온 데이터 = " + dataSet.get(index).meaning);
//                            }

                        }
                    });
                } else {

                }

            });

            btnDeleteMeaning.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });

                   }

}

    public MeaningAdapter(ArrayList<MeaningItem> myDataSet) {
        dataSet = myDataSet;

    }

@NonNull
    @Override
    public MeaningAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_meaning, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.editMeaning.setText(dataSet.get(position).meaning);
        holder.btnDeleteMeaning.setOnClickListener(view -> {
            switch (view.getId()){
                case R.id.btnDeleteMeaning :
                    dataSet.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, dataSet.size());
                    break;
            }
        });

}

    @Override
    public int getItemCount() {
//        Log.e(TAG, "비어있는 meaningArray 의 size : " + meaningArray.size());
        return dataSet.size();
    }
}
