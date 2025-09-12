package com.jacqueb.mclooper.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.jacqueb.mclooper.McLooperMod;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void suppressServerChatLog(CallbackInfo ci) {
        if (McLooperMod.suppressChatSendLog && System.currentTimeMillis() < McLooperMod.suppressChatSendLogUntil) {
            ci.cancel();
        }
    }

    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void suppressServerChatLog2(CallbackInfo ci) {
        if (McLooperMod.suppressChatSendLog && System.currentTimeMillis() < McLooperMod.suppressChatSendLogUntil) {
            ci.cancel();
        }
    }
}
