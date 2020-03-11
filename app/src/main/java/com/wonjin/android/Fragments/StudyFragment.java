package com.wonjin.android.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.wonjin.android.Activities.StudyActivities.MyVocaBookActivity;
import com.wonjin.android.Activities.StudyActivities.SelectChapterActivity;
import com.wonjin.android.Activities.StudyActivities.SelectModeActivity;
import com.wonjin.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyFragment extends Fragment {

@BindView(R.id.btn_my_vocabook)
    View btnMyVocabook;
    @BindView(R.id.lottie_my_vocabook)
    LottieAnimationView lottieMyVocabook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        ButterKnife.bind(this, view);

return view;
    }

@OnClick({R.id.btn_my_vocabook, R.id.btn_test, R.id.btn_learning, R.id.btn_pronun_practice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_my_vocabook:
                Intent openVocabookTab = new Intent(getContext(), MyVocaBookActivity.class);
                startActivity(openVocabookTab);
                break;
            case R.id.btn_test:
                Intent openSelectChapterToTest = new Intent(getContext(), SelectChapterActivity.class);
                openSelectChapterToTest.putExtra("mode", "test");
                startActivity(openSelectChapterToTest);
                break;
            case R.id.btn_learning:
                Intent openLearningTab = new Intent(getContext(), SelectModeActivity.class);
                startActivity(openLearningTab);
                break;
            case R.id.btn_pronun_practice:
                Intent openSelectChapterToPronun = new Intent(getContext(), SelectChapterActivity.class);
                openSelectChapterToPronun.putExtra("mode", "pronun");
                startActivity(openSelectChapterToPronun);
                break;
        }
    }
}
