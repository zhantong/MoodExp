package cn.edu.nju.dislab.moodexp.survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import cn.edu.nju.dislab.moodexp.R;

/**
 * Created by zhantong on 2016/12/26.
 */

public class FinishFragment extends QuestionFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.start_and_finish, container, false);

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

        Button buttonFinish = (Button) mView.findViewById(R.id.btn_next_question);
        buttonFinish.setText(R.string.done);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SurveyActivity) getActivity()).onSurveyFinished();
            }
        });
    }
}
