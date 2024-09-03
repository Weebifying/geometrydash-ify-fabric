package wb.weebify.geometrydash.gd;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import wb.weebify.geometrydash.GeometryDashify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FntFontDrawer {
    public String font;
    public String fntFile;
    public String pngFile;
    public JsonObject fntData = new JsonObject();

    public FntFontDrawer(String font) {
        this.font = font;
        this.fntFile = font + ".fnt";
        this.pngFile = font + ".png";
        loadFntData(fntFile);
    }

    protected void loadFntData(String fntFile) {
        FabricLoader.getInstance().getModContainer(GeometryDashify.MOD_ID).ifPresent(modContainer -> {
            try {
                Path path = modContainer.findPath("assets/geometrydash-ify/textures/fonts/" + fntFile).orElseThrow();
                List<String> lines = Files.readAllLines(path);
                int currentLine = 0;

                String infoLine = lines.get(currentLine++);
                JsonObject infoJson = new JsonObject();
                for (String s : infoLine.split("\\s+")) {
                    if (s.equals("info")) continue;

                    String[] split = s.split("=");
                    infoJson.addProperty(split[0], split[1]);
                }
                fntData.add("info", infoJson);

                String commonLine = lines.get(currentLine++);
                JsonObject commonJson = new JsonObject();
                for (String s : commonLine.split("\\s+")) {
                    if (s.equals("common")) continue;

                    String[] split = s.split("=");
                    commonJson.addProperty(split[0], split[1]);
                }
                fntData.add("common", commonJson);

                currentLine++;
                fntData.addProperty("file", pngFile);

                String charsCountLine = lines.get(currentLine++);
                int charsCount = Integer.parseInt(charsCountLine.split("=")[1]);
                fntData.addProperty("chars-count", String.valueOf(charsCount));

                JsonObject chars = new JsonObject();
                for (int i = 0; i < charsCount; i++) {
                    String charLine = lines.get(currentLine++);
                    String id = "";
                    JsonObject charJson = new JsonObject();
                    for (String s : charLine.split("\\s+")) {
                        if (s.equals("char")) continue;

                        String[] split = s.split("=");
                        if (split[0].equals("id")) {
                            id = split[1];
                        } else {
                            if (split[0].equals("letter")) {
                                if (split[1].length() == 1) { // "="
                                    charJson.addProperty(split[0], "\"=\"");
                                } else {
                                    charJson.addProperty(split[0], split[1]);
                                }
                            } else {
                                charJson.addProperty(split[0], Integer.parseInt(split[1]));
                            }
                        }
                    }
                    if (!id.isEmpty()) chars.add(id, charJson);
                }
                fntData.add("chars", chars);

                String kerningsCountLine = lines.get(currentLine++);
                int kerningsCount = Integer.parseInt(kerningsCountLine.split("=")[1]);
                fntData.addProperty("kernings-count", String.valueOf(kerningsCount));

                JsonObject kernings = new JsonObject();
                for (int i = 0; i < kerningsCount; i++) {
                    String kerningLine = lines.get(currentLine++);
                    String first = "";
                    String second = "";
                    JsonObject kerningJson = new JsonObject();
                    for (String s : kerningLine.split("\\s+")) {
                        if (s.equals("kerning")) continue;

                        String[] split = s.split("=");
                        switch (split[0]) {
                            case "first" -> {
                                first = split[1];
                                if (kernings.has(first)) {
                                    kerningJson = kernings.get(first).getAsJsonObject();
                                }
                            }
                            case "second" -> second = split[1];
                            case "amount" -> kerningJson.addProperty(second, Integer.parseInt(split[1]));
                        }
                    }
                    kernings.add(first, kerningJson);
                }
                fntData.add("kernings", kernings);
                GeometryDashify.LOGGER.info("{}", fntData);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void drawTextureFloat(DrawContext context, Identifier texture, float x, float y, int z, float width, float height, float u, float v, float regionWidth, float regionHeight, float textureWidth, float textureHeight) {
        float x2 =  x + width;
        float y2 = y + height;
        float u1 = u / textureWidth;
        float u2 = (u + regionWidth) / textureWidth;
        float v1 = v / textureHeight;
        float v2 = (v + regionHeight) / textureHeight;

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x, y, (float) z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, x, y2, (float) z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, x2, y2, (float) z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, x2, y, (float) z).texture(u2, v1);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public float getTextHeight() {
        return Integer.parseInt(fntData.get("common").getAsJsonObject().get("lineHeight").getAsString());
    }

    public float getTextWidthFor(String text) {
        JsonObject chars = fntData.getAsJsonObject("chars");
        JsonObject kernings = fntData.getAsJsonObject("kernings");
        int lineHeight = Integer.parseInt(fntData.get("common").getAsJsonObject().get("lineHeight").getAsString());

        assert MinecraftClient.getInstance().currentScreen != null;
        int winHeight = MinecraftClient.getInstance().currentScreen.height;
        float scale = (float) winHeight / 4 / 320;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        float cursorX = 0;

        char c;
        char lastC;

        for (int i = 0; i < text.length(); i++) {
            c = text.charAt(i);
            lastC = i > 0 ? text.charAt(i - 1) : 0;
            if (chars.has(String.valueOf((int)c))) {
                JsonObject charObject = chars.getAsJsonObject(String.valueOf((int)c));
                int charXAdvance = charObject.get("xadvance").getAsInt();

                if (kernings.has(String.valueOf((int)lastC))) {
                    JsonObject kerning = kernings.getAsJsonObject(String.valueOf((int)lastC));
                    if (kerning.has(String.valueOf((int)c))) {
                        cursorX += kerning.get(String.valueOf((int)c)).getAsInt() * scale;
                    }
                }

                cursorX += charXAdvance * scale;
            }
        }
        return cursorX;
    }

    public void drawText(DrawContext context, String text, float x, float y, int z, float baseScale) {
        JsonObject chars = fntData.getAsJsonObject("chars");
        JsonObject kernings = fntData.getAsJsonObject("kernings");
        int lineHeight = Integer.parseInt(fntData.get("common").getAsJsonObject().get("lineHeight").getAsString());

        assert MinecraftClient.getInstance().currentScreen != null;
        int winHeight = MinecraftClient.getInstance().currentScreen.height;
        float scale = (float) winHeight / 4 / 320;
        scale *= baseScale;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        float cursorX = 0;
        float cursorY = 0;
        int textureWidth = Integer.parseInt(fntData.get("common").getAsJsonObject().get("scaleW").getAsString());
        int textureHeight = Integer.parseInt(fntData.get("common").getAsJsonObject().get("scaleH").getAsString());

        char c;
        char lastC;

        for (int i = 0; i < text.length(); i++) {
            c = text.charAt(i);
            lastC = i > 0 ? text.charAt(i - 1) : 0;
            if (chars.has(String.valueOf((int)c))) {
                JsonObject charObject = chars.getAsJsonObject(String.valueOf((int)c));
                int charX = charObject.get("x").getAsInt();
                int charY = charObject.get("y").getAsInt();
                int charWidth = charObject.get("width").getAsInt();
                int charHeight = charObject.get("height").getAsInt();
                int charXOffset = charObject.get("xoffset").getAsInt();
                int charYOffset = charObject.get("yoffset").getAsInt();
                int charXAdvance = charObject.get("xadvance").getAsInt();

                if (kernings.has(String.valueOf((int)lastC))) {
                    JsonObject kerning = kernings.getAsJsonObject(String.valueOf((int)lastC));
                    if (kerning.has(String.valueOf((int)c))) {
                        cursorX += kerning.get(String.valueOf((int)c)).getAsInt() * scale;
                    }
                }

                drawTextureFloat(
                        context,
                        Identifier.of(GeometryDashify.MOD_ID, "textures/fonts/" + pngFile),
                        x + cursorX + charXOffset * scale,
                        y + cursorY + charYOffset * scale,
                        z,
                        charWidth * scale,
                        charHeight * scale,
                        charX,
                        charY,
                        charWidth,
                        charHeight,
                        textureWidth,
                        textureHeight
                );
                cursorX += charXAdvance * scale;
            }
        }

//        context.drawBorder((int)x, (int)y, (int) (cursorX), (int) (lineHeight * scale), 0XFF00FF00);
    }
}
