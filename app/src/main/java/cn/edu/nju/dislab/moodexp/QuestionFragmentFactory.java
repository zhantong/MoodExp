package cn.edu.nju.dislab.moodexp;

/**
 * Created by zhantong on 2016/12/26.
 */

public class QuestionFragmentFactory {
    public static QuestionFragment get(String type){
        switch (type){
            case "CheckBoxes":
                return new CheckBoxesFragment();
            case "MultiLines":
                return new MultiLinesFragment();
            case "MultiQuestions":
                return new MultiQuestionsFragment();
            case "Number":
                return new NumberFragment();
            case "RadioButtons":
                return new RadioButtonsFragment();
            case "SingleLine":
                return new SingleLineFragment();
            default:
                return null;
        }
    }
}
