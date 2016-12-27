package cn.edu.nju.dislab.moodexp.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/25.
 */

public class SurveyActivity extends AppCompatActivity implements OnSubmitAnswerListener {
    private static final String TAG="SurveyActivity";
    private ViewPager mViewPager;
    private Map<Integer,Answer> answerMap;
    private Survey mSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Bundle bundle=getIntent().getExtras();
        if(bundle==null){
            return;
        }
        mSurvey=new Gson().fromJson(bundle.getString("survey"),Survey.class);
        Log.i(TAG,mSurvey.toString());

        List<QuestionFragment> questionFragments=new ArrayList<>();

        for(Question question:mSurvey.getQuestions()){
            if(question.getType().equals("Start")){
                StartFragment startFragment=new StartFragment();
                Bundle bundleToFragment = new Bundle();
                bundleToFragment.putSerializable("data", question);
                startFragment.setArguments(bundleToFragment);
                questionFragments.add(startFragment);
            }
        }
        for(Question question:mSurvey.getQuestions()){
            QuestionFragment questionFragment= QuestionFragmentFactory.get(question.getType());
            if(questionFragment!=null) {
                Bundle bundleToFragment = new Bundle();
                bundleToFragment.putSerializable("data", question);
                questionFragment.setArguments(bundleToFragment);
                questionFragments.add(questionFragment);
            }
        }
        boolean isFinishFragmentExists=false;
        for(Question question:mSurvey.getQuestions()){
            if(question.getType().equals("Finish")){
                isFinishFragmentExists=true;
                FinishFragment finishFragment=new FinishFragment();
                Bundle bundleToFragment = new Bundle();
                bundleToFragment.putSerializable("data", question);
                finishFragment.setArguments(bundleToFragment);
                questionFragments.add(finishFragment);
            }
        }
        if(!isFinishFragmentExists){
            questionFragments.get(questionFragments.size()-1).setIsLast(true);
        }
        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        QuestionFragmentAdapter questionFragmentAdapter=new QuestionFragmentAdapter(getSupportFragmentManager(),questionFragments);
        mViewPager.setAdapter(questionFragmentAdapter);
        answerMap=new HashMap<>();
    }
    public void nextPage(){
        if(mViewPager.getCurrentItem()==mViewPager.getAdapter().getCount()-1){
            onSurveyFinished();
        }else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void onSubmitAnswer(Answer answer) {
        answerMap.put(answer.getQuestionId(),answer);
    }
    public void onSurveyFinished(){
        Intent intent=new Intent();
        List<Answer> answers=new ArrayList<>(answerMap.values());
        SurveyAnswer surveyAnswer=new SurveyAnswer(mSurvey.getId(),mSurvey.getSession(),answers);
        intent.putExtra("answer",new Gson().toJson(surveyAnswer));
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
