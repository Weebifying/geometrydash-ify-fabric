package wb.weebify.geometrydash.mixin;

import com.terraformersmc.modmenu.gui.ModsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
    @Unique
    MinecraftClient client;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("RETURN"))
    void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        CCMenuItemSpriteExtra.mouseDownOnWidget = cir.getReturnValue();
    }

    @Unique
    void setupMainButtons(TitleScreen thisInst) {
        CCMenuItemSpriteExtra singleplayerButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/main/gj_play_btn_001.png"), (button) -> client.setScreen(new SelectWorldScreen(thisInst)))
                        .dimensions(Math.round(thisInst.width * 0.5f), Math.round(thisInst.height * (1.f/2 - 17.f/540)), Math.round(thisInst.height * 0.35f), Math.round(thisInst.height * 0.35f))
                        .build()
        );

        CCMenuItemSpriteExtra multiplayerButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/main/gj_creator_btn_001.png"), (button) -> {
                            Screen screen = client.options.skipMultiplayerWarning ? new MultiplayerScreen(thisInst) : new MultiplayerWarningScreen(thisInst);
                            client.setScreen(screen);
                        })
                        .dimensions(singleplayerButton.x + Math.round(thisInst.height * 0.34f), singleplayerButton.y, Math.round(thisInst.height * 0.22f), Math.round(thisInst.height * 0.22f))
                        .build()
        );

        CCMenuItemSpriteExtra realmsButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/main/gj_garage_btn_001.png"), (button) -> client.setScreen(new RealmsMainScreen(thisInst)))
                        .dimensions(singleplayerButton.x - Math.round(thisInst.height * 0.34f), singleplayerButton.y, Math.round(thisInst.height * 0.22f), Math.round(thisInst.height * 0.22f))
                        .build()
        );
    }

    @Unique
    private void setupBottomMenu(TitleScreen thisInst) {
        int bottomY = Math.round(thisInst.height * 0.859375f); // (1 - 0.140625f)
        float baseBottom = thisInst.height * 0.168f;
        int bottomBaseWidth = Math.round(baseBottom / 215 * 202);
        int bottomBaseHeight = Math.round(baseBottom);
        int distanceX = Math.round(baseBottom + 17.f / client.options.getGuiScale().getValue());

        CCMenuItemSpriteExtra languageButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/bottom/gj_ach_btn_001.png"), (button) -> client.setScreen(new LanguageOptionsScreen(thisInst, client.options, client.getLanguageManager())))
                        .dimensions(thisInst.width / 2 - distanceX*3/2, bottomY, bottomBaseWidth, bottomBaseHeight)
                        .build()
        );

        CCMenuItemSpriteExtra settingsButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/bottom/gj_options_btn_001.png"), (button) -> client.setScreen(new OptionsScreen(thisInst, client.options)))
                        .dimensions(thisInst.width / 2 - distanceX/2, bottomY, bottomBaseWidth, bottomBaseHeight)
                        .build()
        );

        CCMenuItemSpriteExtra accessibilityButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/bottom/gj_stats_btn_001.png"), (button) -> client.setScreen(new AccessibilityOptionsScreen(thisInst, client.options)))
                        .dimensions(thisInst.width / 2 + distanceX/2, bottomY, bottomBaseWidth, bottomBaseHeight)
                        .build()
        );

        CCMenuItemSpriteExtra modsButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/bottom/gj_geode_btn_001.png"), (button) -> client.setScreen(new ModsScreen(thisInst)))
                        .dimensions(thisInst.width / 2 + distanceX*3/2, bottomY, bottomBaseWidth, bottomBaseHeight)
                        .build()
        );
    }

    @Unique
    private void setupEtc(TitleScreen thisInst) {
        float baseQuit = thisInst.height * 0.104f;
        CCMenuItemSpriteExtra quitButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/etc/gj_close_btn_001.png"), (button) -> client.scheduleStop())
                        .dimensions(3 + Math.round(baseQuit / 190 * 184)/2, 3 + Math.round(baseQuit)/2, Math.round(baseQuit / 190 * 184), Math.round(baseQuit))
                        .build()
        );

        float baseProfile = (float) (thisInst.height * 58) / 320;
        CCMenuItemSpriteExtra profileButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/etc/gj_profile_button_001.png"), (button) -> Util.getOperatingSystem().open("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
                        .dimensions(Math.round(43.5f / 320 * thisInst.height), thisInst.height - Math.round(105.f / 320 * thisInst.height), Math.round(baseProfile / 58 * 55), Math.round(baseProfile))
                        .build()
        );

    }

    @Unique
    private void setupSocialsMenu(TitleScreen thisInst) {
        float blXY = thisInst.height * 0.04f;
        float buttonSize = thisInst.height * 0.07875f;
        float gapX = 0.009f * 540;
        float gapY = 0.00656f * 540; // LOL


        float logoHeight = thisInst.height * 0.07f;
        float logoWidth = logoHeight / 7 * 25;
        CCMenuItemSpriteExtra robtopButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/socials/robtoplogo_small.png"), (button) -> Util.getOperatingSystem().open("https://www.minecraft.com"))
                        .dimensions(Math.round(blXY + logoWidth/2), thisInst.height - Math.round(blXY + logoHeight/2), Math.round(logoWidth), Math.round(logoHeight))
                        .build()
        );

        CCMenuItemSpriteExtra facebookButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/socials/gj_fb_icon_001.png"), (button) -> Util.getOperatingSystem().open("https://www.facebook.com/minecraft"))
                        .dimensions(Math.round(blXY + buttonSize/2), thisInst.height - Math.round(blXY + buttonSize*3/2 + gapY), Math.round(buttonSize), Math.round(buttonSize))
                        .build()
        );
        CCMenuItemSpriteExtra twitterButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/socials/gj_tw_icon_001.png"), (button) -> Util.getOperatingSystem().open("https://www.twitter.com/Minecraft"))
                        .dimensions(Math.round(blXY + buttonSize*3/2 + gapX), thisInst.height - Math.round(blXY + buttonSize*3/2 + gapY), Math.round(buttonSize), Math.round(buttonSize))
                        .build()
        );
        CCMenuItemSpriteExtra youtubeButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/socials/gj_yt_icon_001.png"), (button) -> Util.getOperatingSystem().open("https://www.youtube.com/minecraft"))
                        .dimensions(Math.round(blXY + buttonSize*5/2 + gapX*2), thisInst.height - Math.round(blXY + buttonSize*3/2 + gapY), Math.round(buttonSize), Math.round(buttonSize))
                        .build()
        );
        CCMenuItemSpriteExtra twitchButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/socials/gj_twitch_icon_001.png"), (button) -> Util.getOperatingSystem().open("https://www.twitch.tv/minecraft"))
                        .dimensions(Math.round(blXY + buttonSize*7/2 + gapX*3), thisInst.height - Math.round(blXY + buttonSize*3/2 + gapY), Math.round(buttonSize), Math.round(buttonSize))
                        .build()
        );
        CCMenuItemSpriteExtra discordButton = this.addDrawableChild(
                CCMenuItemSpriteExtra.builder(Identifier.of(GeometryDashify.MOD_ID, "textures/socials/gj_discord_icon_001.png"), (button) -> Util.getOperatingSystem().open("https://www.discord.gg/minecraft"))
                        .dimensions(Math.round(blXY + buttonSize*7/2 + gapX*3), thisInst.height - Math.round(blXY + buttonSize/2), Math.round(buttonSize), Math.round(buttonSize))
                        .build()
        );
    }

    @Inject(at = @At("HEAD"), method = "init", cancellable = true)
    public void onInit(CallbackInfo ci) {
        GeometryDashifyClient.MENU_GAME_LAYER = new MenuGameLayer(MinecraftClient.getInstance());
        client = MinecraftClient.getInstance();
        TitleScreen thisInst = (TitleScreen)(Object) this;

        CCMenuItemSpriteExtra.ccMenuItems.clear();
        setupMainButtons(thisInst);
        setupBottomMenu(thisInst);
        setupSocialsMenu(thisInst);
        setupEtc(thisInst);

        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "renderPanoramaBackground", cancellable = true)
    protected void renderPanoramaBackground(DrawContext context, float delta, CallbackInfo ci) {
        GeometryDashifyClient.MENU_GAME_LAYER.render(context, this.width, this.height, delta);

        ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Session session = client.getSession();

        float widthLimit = 70.f / 320 * height;
        float defaultScale = 0.7f;

        float labelWidth = GeometryDashify.GOLDFONT_DRAWER.getTextWidthFor(session.getUsername());
        float labelHeight = GeometryDashify.GOLDFONT_DRAWER.getTextHeight();
        float scale = Math.min(defaultScale, widthLimit / labelWidth);


        GeometryDashify.GOLDFONT_DRAWER.drawText(context, session.getUsername(), 47.f/320*height - labelWidth*scale/2, height * (1.f - 141.f/320) - labelHeight*scale/4, 2, scale);
    }
}