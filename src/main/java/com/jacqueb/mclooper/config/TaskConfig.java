package com.jacqueb.mclooper.config;

import com.google.gson.annotations.SerializedName;
import com.jacqueb.mclooper.config.TaskLoop;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskConfig {
    @SerializedName(value="loops")
    public List<TaskLoop> loops = new ArrayList<TaskLoop>();
    @SerializedName(value="variables")
    public Map<String, Object> variables = new HashMap<String, Object>();
    @SerializedName(value="verboseLogging")
    public boolean verboseLogging = false;
}