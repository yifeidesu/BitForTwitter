package com.robyn.bitty1;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by yifei on 7/18/2017.
 */

public class SearchTimelineFragment extends Fragment {


    public static SearchTimelineFragment newInstance() {

        Bundle args = new Bundle();

        SearchTimelineFragment fragment = new SearchTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
