/*
 * Decompiled with CFR 0.152.
 */
package com.jacqueb.mclooper.ui;

import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.ui.BlockEditorScreen;

private static class BlockEditorScreen.NestedBlockEditingContext {
    TaskBlock originalBlock;
    BlockEditorScreen originalScreen;
    TaskLoop tempLoop;

    BlockEditorScreen.NestedBlockEditingContext(TaskBlock block, BlockEditorScreen screen) {
        this.originalBlock = block;
        this.originalScreen = screen;
    }
}
