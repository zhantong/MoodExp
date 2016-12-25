package cn.edu.nju.dislab.moodexp;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by zhantong on 2016/12/25.
 */

public class Answer implements Serializable {
    @Expose
    private String title;

    @Expose
    private String answer;

    public void setTitle(String title){
        this.title=title;
    }
    public void setAnswer(String answer){
        this.answer=answer;
    }

    @Override
    public String toString() {
        return "title: "+title+"\n"+"answer: "+answer;
    }
}
