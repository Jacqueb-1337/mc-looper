/*
 * Decompiled with CFR 0.152.
 */
package com.jacqueb.mclooper.loop;

import com.jacqueb.mclooper.config.TaskCondition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public static class LoopExecutor.ExecutionContext {
    public boolean shouldStop = false;
    public long waitTicks = 0L;
    public TaskCondition waitCondition = null;
    public int currentBlockIndex = 0;
    public String currentLoopName = null;
    public Set<String> scopeCreatedVariables = new HashSet<String>();
    public Map<String, Object> scopeVariableValues = new HashMap<String, Object>();

    public String getCurrentLoopName() {
        return this.currentLoopName;
    }

    public void setCurrentLoopName(String loopName) {
        this.currentLoopName = loopName;
    }

    public void recordVariableCreated(String variableName, Object value) {
        this.scopeCreatedVariables.add(variableName);
        this.scopeVariableValues.put(variableName, value);
    }

    public void clearScopeVariables() {
        this.scopeCreatedVariables.clear();
        this.scopeVariableValues.clear();
    }
}
