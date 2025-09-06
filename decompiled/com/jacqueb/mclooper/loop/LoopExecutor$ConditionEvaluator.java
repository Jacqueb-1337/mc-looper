/*
 * Decompiled with CFR 0.152.
 */
package com.jacqueb.mclooper.loop;

import com.jacqueb.mclooper.config.TaskCondition;

@FunctionalInterface
public static interface LoopExecutor.ConditionEvaluator {
    public boolean evaluate(TaskCondition var1);
}
