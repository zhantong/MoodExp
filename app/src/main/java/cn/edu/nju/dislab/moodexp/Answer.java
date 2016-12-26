package cn.edu.nju.dislab.moodexp;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by zhantong on 2016/12/25.
 */

public class Answer implements Serializable {
    @Expose
    private String questionTitle;

    @Expose
    private int questionId;

    @Expose
    private String answer;

    @Expose
    private int id;

    public void setQuestionTitle(String questionTitle){
        this.questionTitle = questionTitle;
    }

    public void setQuestionId(int id){
        this.questionId=id;
    }
    public void setAnswer(String answer){
        this.answer=answer;
    }
    public void setId(int id){
        this.id=id;
    }
    public int getQuestionId(){
        return questionId;
    }
    @Override
    public String toString() {
        return "questionTitle: "+ questionTitle +" questionId: "+questionId+" answer: "+answer+" id: "+id;
    }
}
