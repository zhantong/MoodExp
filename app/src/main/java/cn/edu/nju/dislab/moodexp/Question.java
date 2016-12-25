package cn.edu.nju.dislab.moodexp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhantong on 2016/12/25.
 */

public class Question implements Serializable{
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("random_choices")
    @Expose
    private Boolean randomChoices;

    @SerializedName("choices")
    @Expose
    private List<String> choices;

    @SerializedName("questions")
    @Expose
    private List<Question> questions;

    public String getType(){
        return type;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public List<String> getChoices(){
        return choices;
    }
    public List<Question> getQuestions(){
        return questions;
    }
    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        builder.append("Question:\n");
        builder.append("type: "+type+"\n");
        builder.append("title: "+title+"\n");
        builder.append("description: "+description+"\n");
        builder.append("random_choices: "+randomChoices+"\n");
        if(questions!=null) {
            builder.append("questions:\n--------\n");
            builder.append(questions.toString() + "\n");
            builder.append("--------\n");
        }
        return builder.toString();
    }
}
