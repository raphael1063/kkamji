package com.example.android.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.Activities.GameActivities.GameActivity;
import com.example.android.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

@OnClick({R.id.btn_game_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_game_start:
                startActivity(new Intent(getContext(), GameActivity.class));
                break;
        }
    }
}
