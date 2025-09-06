/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.jacqueb.mclooper.config;

import com.google.gson.annotations.SerializedName;
import com.jacqueb.mclooper.config.TaskCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskBlock {
    @SerializedName(value="type")
    public String type;
    @SerializedName(value="params")
    public Map<String, Object> params = new HashMap<String, Object>();
    @SerializedName(value="condition")
    public TaskCondition condition;
    @SerializedName(value="blocks")
    public List<TaskBlock> blocks = new ArrayList<TaskBlock>();

    public TaskBlock() {
    }

    public TaskBlock(String type) {
        this();
        this.type = type;
    }
}

