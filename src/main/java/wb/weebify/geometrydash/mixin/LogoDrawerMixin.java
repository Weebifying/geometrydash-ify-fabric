package wb.weebify.geometrydash.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wb.weebify.geometrydash.GeometryDashify;

@Mixin(LogoDrawer.class)
public abstract class LogoDrawerMixin {
    @Unique
    private static final Identifier GD_LOGO_TEXTURE = Identifier.of(GeometryDashify.MOD_ID, "textures/bg/gj_logo_001.png");

    @Inject(at = @At("HEAD"), method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", cancellable = true)
    private void onDraw(DrawContext context, int screenWidth, float alpha, int y, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof TitleScreen) {
            RenderSystem.enableBlend();
            int logoWidth = Math.round(screenWidth * 0.75f);
            int logoHeight = Math.round(logoWidth / 8.25f);
            context.drawTexture(GD_LOGO_TEXTURE, (screenWidth - logoWidth) / 2, Math.round(client.currentScreen.height * 0.15625f) - logoHeight/2, 0, 0, 0, logoWidth, logoHeight, logoWidth, logoHeight);

            ci.cancel();
        }
    }
}
