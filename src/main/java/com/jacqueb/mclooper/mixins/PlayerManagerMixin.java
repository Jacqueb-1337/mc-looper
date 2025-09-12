package com.jacqueb.mclooper.mixins;

import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.jacqueb.mclooper.McLooperMod;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), cancellable = true)
    private void suppressChatBroadcast(Text message, boolean overlay, CallbackInfo ci) {
        if (McLooperMod.suppressChatSendLog && System.currentTimeMillis() < McLooperMod.suppressChatSendLogUntil) {
            ci.cancel();
        }
    }
}
