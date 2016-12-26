package cn.edu.nju.dislab.moodexp.survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/25.
 */

public class MultiQuestionsFragment extends QuestionFragment {
    List<QuestionFragment> mQuestionFragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_multi_questions,container,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button buttonNext = (Button) mView.findViewById(R.id.btn_next_question);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mQuestionFragments!=null){
                    for(QuestionFragment questionFragment:mQuestionFragments){
                        mCallback.onSubmitAnswer(questionFragment.getAnswer());
                    }
                }
                ((SurveyActivity) getActivity()).nextPage();
            }
        });

        Question question=(Question)getArguments().getSerializable("data");

        List<Question> questions=question.getQuestions();
        if(questions!=null){
            FragmentManager fragmentManager= getChildFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            mQuestionFragments=new ArrayList<>();
            for(Question childQuestion:questions){
                QuestionFragment questionFragment= QuestionFragmentFactory.get(childQuestion.getType());
                if(questionFragment!=null){
                    Bundle bundleToFragment=new Bundle();
                    bundleToFragment.putSerializable("data",childQuestion);
                    bundleToFragment.putBoolean("isComplete",false);
                    questionFragment.setArguments(bundleToFragment);
                    fragmentTransaction.add(R.id.questions_content,questionFragment);
                    mQuestionFragments.add(questionFragment);
                }
            }
            fragmentTransaction.commit();
        }
    }
}
