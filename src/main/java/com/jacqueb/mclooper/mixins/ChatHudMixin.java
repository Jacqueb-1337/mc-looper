package com.jacqueb.mclooper.mixins;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.jacqueb.mclooper.McLooperMod;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void suppressLog(Text message, MessageIndicator indicator, CallbackInfo ci) {
        if (McLooperMod.suppressClientMessageLog || 
            (McLooperMod.suppressChatSendLog && System.currentTimeMillis() < McLooperMod.suppressChatSendLogUntil)) {
            McLooperMod.suppressClientMessageLog = false;
            if (System.currentTimeMillis() >= McLooperMod.suppressChatSendLogUntil) {
                McLooperMod.suppressChatSendLog = false;
            }
            ci.cancel();
        }
    }
}