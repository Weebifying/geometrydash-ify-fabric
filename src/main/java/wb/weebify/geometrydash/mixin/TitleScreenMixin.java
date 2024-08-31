package wb.weebify.geometrydash.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wb.weebify.geometrydash.GeometryDashify;
import wb.weebify.geometrydash.GeometryDashifyClient;
import wb.weebify.geometrydash.gd.CCMenuItemSpriteExtra;
import wb.weebify.geometrydash.gd.MenuGameLayer;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("RETURN"))
    void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        CCMenuItemSpriteExtra.mouseDownOnWidget = cir.getReturnValue();
    }

    @Inject(at = @At("HEAD"), method = "init", cancellable = true)
    public void onInit(CallbackInfo ci) {

        GeometryDashifyClient.MENU_GAME_LAYER = new MenuGameLayer();
        MinecraftClient client = MinecraftClient.getInstance();
        TitleScreen thisInst = (TitleScreen)(Object)this;

        CCMenuItemSpriteExtra.ccMenuItems.clear();

        this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/gj_play_btn_001.png"), (button) -> client.setScreen(new SelectWorldScreen(thisInst)))
                        .dimensions(Math.round(thisInst.width * 0.5f), thisInst.height/2 - 17, Math.round(thisInst.height * 0.35f), Math.round(thisInst.height * 0.35f))
                        .build()
        );

        this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/gj_creator_btn_001.png"), (button) -> {
                            Screen screen = client.options.skipMultiplayerWarning ? new MultiplayerScreen(thisInst) : new MultiplayerWarningScreen(thisInst);
                            client.setScreen(screen);
                        })
                        .dimensions(Math.round(thisInst.width * 0.69f), thisInst.height/2 - 17, Math.round(thisInst.height * 0.22f), Math.round(thisInst.height * 0.22f))
                        .build()
        );

        ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "renderPanoramaBackground", cancellable = true)
    protected void renderPanoramaBackground(DrawContext context, float delta, CallbackInfo ci) {
        GeometryDashifyClient.MENU_GAME_LAYER.render(context, this.width, this.height, delta);

        ci.cancel();
    }

//    @Inject(at = @At("TAIL"), method = "render")
//    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        GeometryDashify.LOGGER.info("{}", CCMenuItemSpriteExtra.mouseDownOnWidget);
//    }
}