package cn.edu.nju.dislab.moodexp.survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.widget.EditText;

import cn.edu.nju.dislab.moodexp.R;
import cn.edu.nju.dislab.moodexp.survey.LineFragment;

/**
 * Created by zhantong on 2016/12/26.
 */

public class SingleLineFragment extends LineFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EditText editTextAnswer=(EditText)mView.findViewById(R.id.editText_answer);
        editTextAnswer.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        editTextAnswer.setSingleLine();
    }
}
