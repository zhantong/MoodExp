package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by zhantong on 2016/12/25.
 */

public class QuestionFragment extends Fragment {
    private Answer mAnswer;
    protected OnSubmitAnswerListener mCallback;
    public void setAnswer(Answer answer){
        mAnswer=answer;
    }
    public Answer getAnswer(){
        return mAnswer;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mCallback=(OnSubmitAnswerListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement OnSubmitAnswerListener");
        }
    }
}
