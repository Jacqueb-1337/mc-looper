/*
 * Decompiled with CFR 0.152.
 */
package com.jacqueb.mclooper.loop;

import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.loop.LoopExecutor;

@FunctionalInterface
public static interface LoopExecutor.BlockExecutor {
    public void execute(TaskBlock var1, LoopExecutor.ExecutionContext var2);
}
