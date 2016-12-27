package cn.edu.nju.dislab.moodexp.survey;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhantong on 2016/12/27.
 */

public class SurveyAnswer implements Serializable {
    @Expose
    private int id;

    @Expose
    private String session;

    @Expose
    private List<Answer> answers;

    public String getSession(){
        return session;
    }
    public SurveyAnswer(int id,String session,List<Answer> answers){
        this.id=id;
        this.session=session;
        this.answers=answers;
    }

}
