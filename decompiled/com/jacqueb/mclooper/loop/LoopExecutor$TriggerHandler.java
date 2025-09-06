/*
 * Decompiled with CFR 0.152.
 */
package com.jacqueb.mclooper.loop;

import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.config.TaskTrigger;

@FunctionalInterface
public static interface LoopExecutor.TriggerHandler {
    public boolean shouldTrigger(TaskTrigger var1, TaskLoop var2);
}
