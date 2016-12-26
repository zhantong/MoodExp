package cn.edu.nju.dislab.moodexp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhantong on 2016/12/25.
 */

public class SurveyActivity extends AppCompatActivity implements OnSubmitAnswerListener{
    private static final String TAG="SurveyActivity";
    private ViewPager mViewPager;
    private Map<Integer,Answer> answerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Bundle bundle=getIntent().getExtras();
        if(bundle==null){
            return;
        }
        Survey survey=new Gson().fromJson(bundle.getString("survey"),Survey.class);

        List<QuestionFragment> questionFragments=new ArrayList<>();

        for(Question question:survey.getQuestions()){
            QuestionFragment questionFragment=QuestionFragmentFactory.get(question.getType());
            if(questionFragment!=null) {
                Bundle bundleToFragment = new Bundle();
                bundleToFragment.putSerializable("data", question);
                questionFragment.setArguments(bundleToFragment);
                questionFragments.add(questionFragment);
            }
        }
        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        FragmentAdapter fragmentAdapter=new FragmentAdapter(getSupportFragmentManager(),questionFragments);
        mViewPager.setAdapter(fragmentAdapter);

        answerMap=new HashMap<>();
    }
    public void nextPage(){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }

    @Override
    public void onSubmitAnswer(Answer answer) {
        answerMap.put(answer.getQuestionId(),answer);
    }
    public void onSurveyFinished(){
        Intent intent=new Intent();
        intent.putExtra("answers",new Gson().toJson(answerMap));
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
