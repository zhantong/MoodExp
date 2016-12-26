package cn.edu.nju.dislab.moodexp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;


import java.util.List;

/**
 * Created by zhantong on 2016/12/25.
 */

public class RadioButtonsFragment extends QuestionFragment {
    RadioGroup mRadioGroupChoices;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView=inflater.inflate(R.layout.radiobuttons,container,false);

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


        mRadioGroupChoices=(RadioGroup)mView.findViewById(R.id.radio_group_choices);

        Context context=getActivity();

        Question question=(Question)getArguments().getSerializable("data");


        List<Choice> choices=question.getChoices();
        if(choices!=null){
            for(Choice choice:choices){
                RadioButton radioButton=new RadioButton(context);
                radioButton.setText(choice.getDescription());
                radioButton.setTag(choice.getId());
                radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mRadioGroupChoices.addView(radioButton);
            }
            mRadioGroupChoices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton= (RadioButton)mView.findViewById(checkedId);
                    mAnswer.setAnswer(radioButton.getText().toString());
                    mAnswer.setId((int)radioButton.getTag());
                }
            });
        }else{
            mRadioGroupChoices.setVisibility(View.GONE);
        }
    }
}
