package cn.edu.nju.dislab.moodexp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/25.
 */

public class MultiQuestionsFragment extends QuestionFragment {
    private static final String TAG="MultiQuestionsFragment";
    View mView;
    TextView mTextViewTitle;
    TextView mTextViewDescription;
    LinearLayout mContent;
    List<QuestionFragment> mQuestionFragments;



    private List<Question> questions;

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
                //((SurveyActivity) getActivity()).nextPage();
            }
        });

        mTextViewTitle=(TextView)mView.findViewById(R.id.txt_title);
        mTextViewDescription=(TextView)mView.findViewById(R.id.txt_description);
        mContent=(LinearLayout)mView.findViewById(R.id.questions_content);

        Context context=getActivity();

        Question question=(Question)getArguments().getSerializable("data");

        String title=question.getTitle();
        if(title!=null){
            mTextViewTitle.setText(title);
        }else{
            mTextViewTitle.setVisibility(View.GONE);
        }

        String description=question.getDescription();
        if(description!=null){
            mTextViewDescription.setText(description);
        }else{
            mTextViewDescription.setVisibility(View.GONE);
        }
        questions=question.getQuestions();
        if(questions!=null){
            FragmentManager fragmentManager= getChildFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            mQuestionFragments=new ArrayList<>();
            for(Question childQuestion:questions){
                Bundle bundleToFragment=new Bundle();
                switch (childQuestion.getType()){
                    case "RadioButtons":
                        RadioButtonsFragment fragment=new RadioButtonsFragment();
                        bundleToFragment.putSerializable("data",childQuestion);
                        bundleToFragment.putBoolean("isComplete",false);
                        fragment.setArguments(bundleToFragment);
                        fragmentTransaction.add(R.id.questions_content,fragment);
                        mQuestionFragments.add(fragment);
                        break;
                }
            }
            fragmentTransaction.commit();
        }
    }
}
