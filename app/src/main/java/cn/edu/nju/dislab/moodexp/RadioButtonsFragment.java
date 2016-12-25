package cn.edu.nju.dislab.moodexp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;


import java.util.List;

/**
 * Created by zhantong on 2016/12/25.
 */

public class RadioButtonsFragment extends QuestionFragment {
    private static final String TAG="RadioButtonsFragment";
    private boolean mIsComplete=true;
    View mView;
    TextView mTextViewTitle;
    TextView mTextViewDescription;
    RadioGroup mRadioGroupChoices;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments=getArguments();
        if(arguments!=null&&arguments.containsKey("isComplete")&&(!arguments.getBoolean("isComplete"))){
            mIsComplete=false;
            mView=inflater.inflate(R.layout.radioboxes,container,false);
            return mView;
        }
        mView=inflater.inflate(R.layout.fragment_question,container,false);
        ScrollView scrollViewContent=(ScrollView)mView.findViewById(R.id.content);
        View childView=inflater.inflate(R.layout.radioboxes,container,false);
        scrollViewContent.addView(childView);
        return mView;
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

        mTextViewTitle=(TextView)mView.findViewById(R.id.txt_title);
        mTextViewDescription=(TextView)mView.findViewById(R.id.txt_description);
        mRadioGroupChoices=(RadioGroup)mView.findViewById(R.id.radio_group_choices);

        Context context=getActivity();

        Question question=(Question)getArguments().getSerializable("data");

        final String title=question.getTitle();
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

        List<String> choices=question.getChoices();
        if(choices!=null){
            for(String choice:choices){
                RadioButton radioButton=new RadioButton(context);
                radioButton.setText(choice);
                radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mRadioGroupChoices.addView(radioButton);
            }
            mRadioGroupChoices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton= (RadioButton)mView.findViewById(checkedId);
                    Answer answer=new Answer();
                    answer.setTitle(title);
                    answer.setAnswer(radioButton.getText().toString());
                    setAnswer(answer);
                }
            });
        }else{
            mRadioGroupChoices.setVisibility(View.GONE);
        }
    }
}
