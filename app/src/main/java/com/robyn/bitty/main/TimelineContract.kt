package com.robyn.bitty.main

import com.robyn.bitty.BasePresenter
import com.robyn.bitty.BaseView

/**
 * Created by yifei on 11/27/2017.
 */

interface TimelineContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}