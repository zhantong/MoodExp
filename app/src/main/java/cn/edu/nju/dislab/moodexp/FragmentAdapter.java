package cn.edu.nju.dislab.moodexp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by zhantong on 2016/12/25.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    private List<QuestionFragment> mQuestionFragments;

    public FragmentAdapter(FragmentManager fm, List<QuestionFragment> questionFragments) {
        super(fm);
        mQuestionFragments=questionFragments;
    }


    @Override
    public Fragment getItem(int position) {
        return mQuestionFragments.get(position);
    }

    @Override
    public int getCount() {
        return mQuestionFragments.size();
    }
}
