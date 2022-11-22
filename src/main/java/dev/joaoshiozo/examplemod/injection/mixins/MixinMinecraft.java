package dev.joaoshiozo.examplemod.injection.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.Display;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    public GuiScreen currentScreen;

    @Inject(at = @At("RETURN"), method = "startGame")
    private void startGame(CallbackInfo info) {
        Display.setTitle("ExampleMod");
    }
}