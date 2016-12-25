package cn.edu.nju.dislab.moodexp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/25.
 */

public class SurveyActivity extends AppCompatActivity implements OnSubmitAnswerListener{
    private static final String TAG="SurveyActivity";
    private ViewPager mViewPager;

    private List<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Bundle bundle=getIntent().getExtras();
        if(bundle==null){
            return;
        }
        Survey survey=new Gson().fromJson(bundle.getString("survey"),Survey.class);

        fragments=new ArrayList<>();

        for(Question question:survey.getQuestions()){
            Bundle bundleToFragment=new Bundle();
            switch (question.getType()){
                case "RadioButtons":
                    RadioButtonsFragment fragment=new RadioButtonsFragment();
                    bundleToFragment.putSerializable("data",question);
                    fragment.setArguments(bundleToFragment);
                    fragments.add(fragment);
                    Log.i(TAG,"added fragment");
                    break;
                case "MultiQuestions":
                    MultiQuestionsFragment multiQuestionsFragment=new MultiQuestionsFragment();
                    bundleToFragment.putSerializable("data",question);
                    multiQuestionsFragment.setArguments(bundleToFragment);
                    fragments.add(multiQuestionsFragment);
                    Log.i(TAG,"added MultiQuestions");
                    break;
            }
        }
        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        //FragmentAdapter fragmentAdapter=new FragmentAdapter(getSupportFragmentManager(),fragments);
        FragmentAdapter fragmentAdapter=new FragmentAdapter(getSupportFragmentManager(),fragments);
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
