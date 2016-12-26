package cn.edu.nju.dislab.moodexp;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/25.
 */

public class SurveyActivity extends AppCompatActivity implements OnSubmitAnswerListener{
    private static final String TAG="SurveyActivity";
    private ViewPager mViewPager;

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
    }
    public void nextPage(){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }

    @Override
    public void onSubmitAnswer(Answer answer) {
        JsonElement jsonElement= new Gson().toJsonTree(answer);
        Log.i(TAG,jsonElement.toString());
    }
}
