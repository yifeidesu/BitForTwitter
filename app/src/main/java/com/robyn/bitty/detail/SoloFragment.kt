package com.robyn.bitty.detail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robyn.bitty.R
import com.robyn.bitty.databinding.SoloFgBinding
import com.twitter.sdk.android.core.models.Tweet

class SoloFragment : Fragment(), SoloContract.View {

    override lateinit var mPresenter: SoloContract.Presenter
    lateinit var mBinding: SoloFgBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding =
                DataBindingUtil.inflate(inflater, R.layout.solo_fg, container, false)

        val view = mBinding.root

        mPresenter.load()

        return view
    }

    /**
     * Set binding data
     *
     * @param tweet is loaded by the presenter's data source
     */
    override fun load(tweet: Tweet) {
        mBinding.tweet = tweet
    }

    companion object {
        fun newInstance(): SoloFragment {
            return SoloFragment()
        }
    }
}