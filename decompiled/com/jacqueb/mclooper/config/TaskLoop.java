/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.jacqueb.mclooper.config;

import com.google.gson.annotations.SerializedName;
import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskTrigger;
import java.util.ArrayList;
import java.util.List;

public class TaskLoop {
    @SerializedName(value="name")
    public String name;
    @SerializedName(value="enabled")
    public boolean enabled = true;
    @SerializedName(value="startup_delay")
    public int startupDelay = 0;
    @SerializedName(value="trigger")
    public TaskTrigger trigger;
    @SerializedName(value="blocks")
    public List<TaskBlock> blocks = new ArrayList<TaskBlock>();

    public TaskLoop() {
    }

    public TaskLoop(String name) {
        this();
        this.name = name;
    }
}
