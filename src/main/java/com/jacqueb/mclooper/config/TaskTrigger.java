package com.jacqueb.mclooper.config;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class TaskTrigger {
    @SerializedName(value="type")
    public String type;
    @SerializedName(value="params")
    public Map<String, Object> params = new HashMap<String, Object>();

    public TaskTrigger() {
    }

    public TaskTrigger(String type) {
        this();
        this.type = type;
    }
}