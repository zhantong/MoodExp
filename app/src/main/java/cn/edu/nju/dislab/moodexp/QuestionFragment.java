package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by zhantong on 2016/12/25.
 */

public class QuestionFragment extends Fragment {
    protected Answer mAnswer=new Answer();
    protected OnSubmitAnswerListener mCallback;
    protected boolean mIsComplete=true;
    protected View mView;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mIsComplete) {
            Button buttonNext = (Button) mView.findViewById(R.id.btn_next_question);
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onSubmitAnswer(getAnswer());
                    ((SurveyActivity) getActivity()).nextPage();
                }
            });
        }

        TextView textViewTitle=(TextView)mView.findViewById(R.id.txt_title);
        TextView textViewDescription=(TextView)mView.findViewById(R.id.txt_description);
        Question question=(Question)getArguments().getSerializable("data");
        String questionTitle=question.getTitle();
        int questionId=question.getId();
        mAnswer.setQuestionTitle(questionTitle);
        mAnswer.setQuestionId(questionId);
        if(questionTitle!=null){
            textViewTitle.setText(questionTitle);
            textViewTitle.setTag(questionId);
        }else{
            textViewTitle.setVisibility(View.GONE);
        }
        String description=question.getDescription();
        if(description!=null){
            textViewDescription.setText(description);
        }else{
            textViewDescription.setVisibility(View.GONE);
        }
    }
}
