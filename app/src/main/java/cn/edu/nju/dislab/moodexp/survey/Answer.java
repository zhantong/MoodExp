package cn.edu.nju.dislab.moodexp.survey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhantong on 2016/12/25.
 */

public class Answer implements Serializable {
    private String questionTitle;

    @SerializedName("question_id")
    @Expose
    private int questionId;

    @SerializedName("answer")
    @Expose
    private String answer;

    @SerializedName("id")
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
    public boolean isAnswered(){
        return answer!=null;
    }
    @Override
    public String toString() {
        return "questionTitle: "+ questionTitle +" questionId: "+questionId+" answer: "+answer+" id: "+id;
    }
}
