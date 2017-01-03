package cn.edu.nju.dislab.moodexp.survey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhantong on 2016/12/26.
 */

public class Choice implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("description")
    @Expose
    private String description;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "id: " + id + " description: " + description;
    }
}
