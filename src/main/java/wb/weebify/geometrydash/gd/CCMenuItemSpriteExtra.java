package wb.weebify.geometrydash.gd;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wb.weebify.geometrydash.GeometryDashify;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CCMenuItemSpriteExtra extends ClickableWidget  {
    public final Identifier texture;
    public final CCMenuItemSpriteExtra.Callback callback;
    public int x;
    public int y;
    public float baseScale = 1.0f;
    protected float beginScale = baseScale;
    private float currentScale = baseScale;
    private float deltaScale;
    private int deltaNum = 0;
    protected float targetScale = baseScale;
    public float scaleMultiplier = 1.26f;

    public static boolean mouseDownOnWidget = false;
    public static List<CCMenuItemSpriteExtra> ccMenuItems = new ArrayList<>();
    public boolean isScaleUp; // scale is going up or down
    public boolean isMouseDown;

    public CCMenuItemSpriteExtra(Identifier texture, int centerX, int centerY, int width, int height, CCMenuItemSpriteExtra.Callback callback) {
        super(centerX, centerY, width, height, Text.of(""));
        this.x = centerX;
        this.y = centerY;
        this.texture = texture;
        this.callback = callback;
        ccMenuItems.add(this);
    }

    public static CCMenuItemSpriteExtra.Builder builder(Identifier texture, CCMenuItemSpriteExtra.Callback callback) {
        return new CCMenuItemSpriteExtra.Builder(texture, callback);
    }

    protected float bounceTime(float time) { // bounceTime(deltaNum / 18)
        if (time < 1 / 2.75) {
            return 7.5625f * time * time;
        } else if (time < 2 / 2.75) {
            time -= 1.5f / 2.75f;
            return 7.5625f * time * time + 0.75f;
        } else if (time < 2.5 / 2.75) {
            time -= 2.25f / 2.75f;
            return 7.5625f * time * time + 0.9375f;
        }

        time -= 2.625f / 2.75f;
        return 7.5625f * time * time + 0.984375f;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.hovered  = context.scissorContains(mouseX, mouseY)
                && mouseX >= this.x - this.width/2
                && mouseY >= this.y - this.height/2
                && mouseX < this.x + this.width/2
                && mouseY < this.y + this.height/2;

        boolean check = this.isMouseDown && this.hovered;
        if (this.isScaleUp != check && mouseDownOnWidget) {

            this.beginScale = this.currentScale;
            this.isScaleUp = check;

            if (check) this.targetScale = this.scaleMultiplier;
            else this.targetScale = this.baseScale;

            this.deltaNum = 0;
            this.deltaScale = this.targetScale - this.beginScale;
        }

        if (this.deltaNum < 18) this.deltaNum++;
        this.currentScale = this.beginScale + this.deltaScale * this.bounceTime(this.deltaNum / 18.0f);

        RenderSystem.enableBlend(); // probably transparency
        RenderSystem.enableDepthTest(); // what does this even do
        int widgetWidth = Math.round(this.width * this.currentScale);
        int widgetHeight = Math.round(this.height * this.currentScale);
        context.drawTexture(
                this.texture,
                this.x - widgetWidth/2,
                this.y - widgetHeight/2,
                0,
                0,
                widgetWidth,
                widgetHeight,
                widgetWidth,
                widgetHeight
        );
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.callback.onRelease(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            setAllIsDown(true);

            if (this.isValidClickButton(button)) {
                if (this.clicked(mouseX, mouseY)) {
                    this.onClick(mouseX, mouseY);
                    return true;
                }
            }
        }
        return false;
    }

    public void setAllIsDown(boolean value) {
        for (CCMenuItemSpriteExtra item : ccMenuItems) {
            item.isMouseDown = value;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        setAllIsDown(false);
        for (CCMenuItemSpriteExtra item : ccMenuItems) {
            if (mouseDownOnWidget && item.hovered) {
                mouseDownOnWidget = false;
                item.isScaleUp = false;

                item.targetScale = item.baseScale;
                item.beginScale = item.currentScale;

                item.deltaNum = 0;
                item.deltaScale = item.targetScale - item.beginScale;

                item.onRelease(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active
                && this.visible
                && mouseX >= (double)(this.getX() - this.getWidth() / 2)
                && mouseY >= (double)(this.getY() - this.getHeight() / 2)
                && mouseX <  (double)(this.getX() + this.getWidth() / 2)
                && mouseY <  (double)(this.getY() + this.getHeight() / 2);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Identifier texture;
        private final CCMenuItemSpriteExtra.Callback callback;
        private int x;
        private int y;
        private int width = 100;
        private int height = 100;

        public Builder(Identifier texture, CCMenuItemSpriteExtra.Callback callback) {
            this.texture = texture;
            this.callback = callback;
        }

        public CCMenuItemSpriteExtra.Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public CCMenuItemSpriteExtra.Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public CCMenuItemSpriteExtra.Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public CCMenuItemSpriteExtra build() {
            return new CCMenuItemSpriteExtra(this.texture, this.x, this.y, this.width, this.height, this.callback);
        }
    }

    @Environment(EnvType.CLIENT)
    public interface Callback {
        void onRelease(CCMenuItemSpriteExtra menuItemSpriteExtra);
    }
}
