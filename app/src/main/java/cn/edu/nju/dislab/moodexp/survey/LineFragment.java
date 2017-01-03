package cn.edu.nju.dislab.moodexp.survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/26.
 */

public class LineFragment extends QuestionFragment {
    EditText mEditTextAnswer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.line, container, false);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("isComplete") && (!arguments.getBoolean("isComplete"))) {
            mIsComplete = false;
            mView = childView;
        } else {
            mView = inflater.inflate(R.layout.fragment_question, container, false);
            ScrollView scrollViewContent = (ScrollView) mView.findViewById(R.id.content);
            scrollViewContent.addView(childView);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mEditTextAnswer = (EditText) mView.findViewById(R.id.editText_answer);
    }

    @Override
    public Answer getAnswer() {
        mAnswer.setAnswer(mEditTextAnswer.getText().toString());
        return mAnswer;
    }
}
