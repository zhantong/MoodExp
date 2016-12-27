package cn.edu.nju.dislab.moodexp.survey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import cn.edu.nju.dislab.moodexp.survey.Question;

/**
 * Created by zhantong on 2016/12/25.
 */

public class Survey implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("session")
    private String session;

    @SerializedName("questions")
    private List<Question> questions;

    public int getId(){
        return id;
    }
    public String getSession(){
        return session;
    }

    List<Question> getQuestions(){
        return questions;
    }
    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        builder.append("Survey:\n");
        if(questions!=null) {
            builder.append("id: "+id+"\n");
            builder.append("session: "+session+"\n");
            builder.append("questions:\n--------\n");
            builder.append(questions.toString() + "\n");
            builder.append("--------\n");
        }
        return builder.toString();
    }
}
