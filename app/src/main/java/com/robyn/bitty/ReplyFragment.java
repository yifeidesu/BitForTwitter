package com.robyn.bitty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by yifei on 7/24/2017.
 */

public class ReplyFragment extends Fragment {

    // TODO: 7/25/2017 bind views here

    public static ReplyFragment newInstance() {

        Bundle args = new Bundle();

        ReplyFragment fragment = new ReplyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(getActivity()); // can butterknife bind in fg?

        return inflater.inflate(R.layout.fragment_reply, container, false);


    }
}
