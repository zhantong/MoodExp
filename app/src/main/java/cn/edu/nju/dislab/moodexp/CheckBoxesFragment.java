package cn.edu.nju.dislab.moodexp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhantong on 2016/12/26.
 */

public class CheckBoxesFragment extends QuestionFragment {
    List<CheckBox> mCheckBoxes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView=inflater.inflate(R.layout.checkboxes,container,false);

        Bundle arguments=getArguments();
        if(arguments!=null&&arguments.containsKey("isComplete")&&(!arguments.getBoolean("isComplete"))){
            mIsComplete=false;
            mView=childView;
        }else {
            mView = inflater.inflate(R.layout.fragment_question, container, false);
            ScrollView scrollViewContent = (ScrollView) mView.findViewById(R.id.content);
            scrollViewContent.addView(childView);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayout linearLayoutChoices=(LinearLayout)mView.findViewById(R.id.linearLayout_choices);
        Context context=getActivity();
        Question question=(Question)getArguments().getSerializable("data");
        List<Choice> choices=question.getChoices();
        if(choices!=null){
            mCheckBoxes=new ArrayList<>();
            for(Choice choice:choices){
                CheckBox checkBox=new CheckBox(context);
                checkBox.setText(choice.getDescription());
                checkBox.setTag(choice.getId());
                checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayoutChoices.addView(checkBox);
                mCheckBoxes.add(checkBox);
            }
        }else{
            linearLayoutChoices.setVisibility(View.GONE);
        }
    }
    @Override
    public Answer getAnswer(){

        List<CheckBoxAnswer> checkBoxAnswers=new ArrayList<>();
        for(CheckBox checkBox:mCheckBoxes){
            if(checkBox.isChecked()){
                checkBoxAnswers.add(new CheckBoxAnswer((int)checkBox.getTag(),checkBox.getText().toString()));
            }
        }
        mAnswer.setAnswer(new Gson().toJson(checkBoxAnswers));
        return mAnswer;
    }
    private class CheckBoxAnswer implements Serializable{
        @Expose
        private int id;
        @Expose
        private String answer;

        public CheckBoxAnswer(int id,String answer){
            this.id=id;
            this.answer=answer;
        }
    }
}
