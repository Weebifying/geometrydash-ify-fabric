package wb.weebify.geometrydash.gd;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import wb.weebify.geometrydash.GeometryDashify;


@Environment(EnvType.CLIENT)
public class MenuGameLayer {
    public static final Identifier BACKGROUND_TEXTURE = Identifier.of(GeometryDashify.MOD_ID, "textures/bg/game_bg_01_001.png");
    public static final Identifier GROUND_TEXTURE = Identifier.of(GeometryDashify.MOD_ID, "textures/bg/ground_square_01_001.png");
    private final MinecraftClient client;
    public ccColor3B startColor;
    public ccColor3B currentColor;
    public ccColor3B endColor;

    public int colID = 1;
    private int count = 0;
    private int waitCount = 0;

    public float groundX = 0;
    public float bgX = 0;

    public MenuGameLayer(MinecraftClient client) {
        this.client = client;
        startColor = getBGColor(0);
        currentColor = getBGColor(0);
        endColor = getBGColor(1);
    }

    private void fillGradientHORIZONTAL(DrawContext context, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        RenderLayer layer = RenderLayer.getGui();
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(layer);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).color(colorEnd);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).color(colorEnd);

        context.draw();
    }

    public ccColor3B getBGColor(int colorID) {
        switch(colorID) {
            case 0:
                return new ccColor3B(0x00, 0x66, 0xff);
            case 1:
                return new ccColor3B(0xff, 0x00, 0xff);
            case 2:
                return new ccColor3B(0xff, 0x00, 0x7d);
            case 3:
                return new ccColor3B(0xff, 0x00, 0x00);
            case 4:
                return new ccColor3B(0xff, 0x7d, 0x00);
            case 5:
                return new ccColor3B(0xff, 0xff, 0x00);
            case 6:
                return new ccColor3B(0x00, 0xff, 0x00);
            case 7:
                return new ccColor3B(0x00, 0xff, 0xff);
            default:
                if (colorID < 0) {
                    return getBGColor(colorID + 8);
                } else if (colorID > 7) {
                    return getBGColor(colorID - 8);
                }
        }
        return new ccColor3B(0xFF, 0xFF, 0xFF); // shouldn't happen lol
    }

    public void render(DrawContext context, int width, int height, float tickDelta) {

        count++;
        if (count >= 300) {
            waitCount++;
            if (waitCount >= 60) {
                count = 0;
                waitCount = 0;
                colID++;
                if (colID > 7) {
                    colID = 0;
                }
                startColor = endColor;
                endColor = getBGColor(colID);
            }
        }
        if (waitCount == 0) {
            float stepR = (endColor.r - startColor.r) / 300;
            float stepG = (endColor.g - startColor.g) / 300;
            float stepB = (endColor.b - startColor.b) / 300;
            currentColor.r += stepR;
            currentColor.g += stepG;
            currentColor.b += stepB;
        }

        context.setShaderColor(currentColor.r / 0xFF, currentColor.g / 0xFF, currentColor.b / 0xFF, 1.0F);

        float bgSpeed = 2.f / client.options.getGuiScale().getValue();
        float groundSpeed = 16.0f / client.options.getGuiScale().getValue();

        int bgHeight = Math.round((float)height / 0.625f); // 864
        int bgWidth = bgHeight * 3;
        bgX += bgSpeed;
        if (bgX > bgHeight) {
            bgX = 0;
        }
        context.drawTexture(BACKGROUND_TEXTURE, Math.round(-bgX), height - bgHeight, 0, 0, 0, bgWidth, bgHeight, bgHeight, bgHeight);

        int groundHeight = Math.round((float)height * 0.4f); // 216
        int groundWidth = groundHeight * 6;
        int k = Math.round(90.f / 320 * height);
        int groundTopY = height - k;
        groundX += groundSpeed;
        if (groundX > groundHeight) {
            groundX = 0;
        }
        context.drawTexture(GROUND_TEXTURE, Math.round(-groundX), groundTopY, 0, 0, 0, groundWidth, groundHeight, groundHeight, groundHeight);

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        fillGradientHORIZONTAL(context, width/2 - 296, groundTopY, width/2, groundTopY + 2, 0, 0x00FFFFFF, 0xFFFFFFFF);
        fillGradientHORIZONTAL(context, width/2, groundTopY, width/2 + 296, groundTopY + 2, 0, 0xFFFFFFFF, 0x00FFFFFF);
        fillGradientHORIZONTAL(context, -1, groundTopY, k - 1, height, 0, 0X64000000, 0x00000000);
        fillGradientHORIZONTAL(context, width + 1 - k, groundTopY, width + 1, height, 0, 0x00000000, 0X64000000);
    }

    public static class ccColor3B {
        public float r;
        public float g;
        public float b;

        public ccColor3B(float r, float g, float b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public ccColor3B(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public boolean equals(ccColor3B other) {
            return this.r == other.r && this.g == other.g && this.b == other.b;
        }
    }
}
