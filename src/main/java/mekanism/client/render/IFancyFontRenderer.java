package mekanism.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mekanism.client.SpecialColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;

public interface IFancyFontRenderer {

    int getXSize();

    TextRenderer getFont();

    default int titleTextColor() {
        return SpecialColors.TEXT_TITLE.argb();
    }

    default int headingTextColor() {
        return SpecialColors.TEXT_HEADING.argb();
    }

    default int subheadingTextColor() {
        return SpecialColors.TEXT_SUBHEADING.argb();
    }

    default int screenTextColor() {
        return SpecialColors.TEXT_SCREEN.argb();
    }

    default int drawString(MatrixStack matrix, Text component, int x, int y, int color) {
        return getFont().draw(matrix, component, x, y, color);
    }

    default int getStringWidth(Text component) {
        return getFont().getWidth(component);
    }

    default void drawCenteredText(MatrixStack matrix, Text component, float x, float y, int color) {
        drawCenteredText(matrix, component, x, 0, y, color);
    }

    default void drawCenteredText(MatrixStack matrix, Text component, float xStart, float areaWidth, float y, int color) {
        int textWidth = getStringWidth(component);
        float centerX = xStart + (areaWidth / 2F) - (textWidth / 2F);
        drawTextExact(matrix, component, centerX, y, color);
    }

    default void drawTitleText(MatrixStack matrix, Text text, float y) {
        drawCenteredTextScaledBound(matrix, text, getXSize() - 8, y, titleTextColor());
    }

    default void drawScaledCenteredText(MatrixStack matrix, Text text, float left, float y, int color, float scale) {
        int textWidth = getStringWidth(text);
        float centerX = left - (textWidth / 2F) * scale;
        drawTextWithScale(matrix, text, centerX, y, color, scale);
    }

    default void drawCenteredTextScaledBound(MatrixStack matrix, Text text, float maxLength, float y, int color) {
        float scale = Math.min(1, maxLength / getStringWidth(text));
        drawScaledCenteredText(matrix, text, getXSize() / 2F, y, color, scale);
    }

    default void drawTextExact(MatrixStack matrix, Text text, float x, float y, int color) {
        matrix.push();
        matrix.translate(x, y, 0);
        drawString(matrix, text, 0, 0, color);
        matrix.pop();
    }

    default float getNeededScale(Text text, float maxLength) {
        int length = getStringWidth(text);
        return length <= maxLength ? 1 : maxLength / length;
    }

    default void drawTextScaledBound(MatrixStack matrix, String text, float x, float y, int color, float maxLength) {
        drawTextScaledBound(matrix, new LiteralText(text), x, y, color, maxLength);
    }

    default void drawTextScaledBound(MatrixStack matrix, Text component, float x, float y, int color, float maxLength) {
        int length = getStringWidth(component);

        if (length <= maxLength) {
            drawTextExact(matrix, component, x, y, color);
        } else {
            drawTextWithScale(matrix, component, x, y, color, maxLength / length);
        }
        //Make sure the color does not leak from having drawn the string
        MekanismRenderer.resetColor();
    }

    default void drawScaledTextScaledBound(MatrixStack matrix, Text text, float x, float y, int color, float maxX, float textScale) {
        float width = getStringWidth(text) * textScale;
        float scale = Math.min(1, maxX / width) * textScale;
        drawTextWithScale(matrix, text, x, y, color, scale);
    }

    default void drawTextWithScale(MatrixStack matrix, Text text, float x, float y, int color, float scale) {
        prepTextScale(matrix, m -> drawString(m, text, 0, 0, color), x, y, scale);
    }

    default void prepTextScale(MatrixStack matrix, Consumer<MatrixStack> runnable, float x, float y, float scale) {
        float yAdd = 4 - (scale * 8) / 2F;
        matrix.push();
        matrix.translate(x, y + yAdd, 0);
        matrix.scale(scale, scale, scale);
        runnable.accept(matrix);
        matrix.pop();
        MekanismRenderer.resetColor();
    }

    default void drawWrappedCenteredText(MatrixStack matrix, Text text, float x, float y, int color, float maxLength) {
        new WrappedTextRenderer(this).render(matrix, text.getString(), x, y, color, maxLength);
    }

    // efficient tool to draw word-by-word wrapped text based on a horizontal bound. looks intimidating but runs in O(n)
    class WrappedTextRenderer {

        final IFancyFontRenderer font;
        final List<Pair<Text, Float>> linesToDraw = new ArrayList<>();
        StringBuilder lineBuilder = new StringBuilder(), wordBuilder = new StringBuilder();
        float lineLength = 0, wordLength = 0;
        final float SPACE_LENGTH;

        WrappedTextRenderer(IFancyFontRenderer font) {
            this.font = font;
            SPACE_LENGTH = font.getFont().getWidth(" ");
        }

        void render(MatrixStack matrix, String text, float x, float y, int color, float maxLength) {
            for (char c : text.toCharArray()) {
                if (c == ' ') {
                    addWord(maxLength);
                    continue;
                }
                wordBuilder.append(c);
                wordLength += font.getFont().getWidth(Character.toString(c));
            }
            if (wordBuilder.length() > 0) {
                addWord(maxLength);
            }
            if (lineBuilder.length() > 0) {
                linesToDraw.add(Pair.of(new LiteralText(lineBuilder.toString()), lineLength));
            }
            float startY = y;
            for (Pair<Text, Float> p : linesToDraw) {
                font.drawTextExact(matrix, p.getLeft(), x - p.getRight() / 2, startY, color);
                startY += 9;
            }
        }

        void addWord(float maxLength) {
            // ignore spacing if this is the first word of the line
            float spacingLength = lineBuilder.length() == 0 ? 0 : SPACE_LENGTH;
            if (lineLength + spacingLength + wordLength > maxLength) {
                linesToDraw.add(Pair.of(new LiteralText(lineBuilder.toString()), lineLength));
                lineBuilder = new StringBuilder(wordBuilder);
                lineLength = wordLength;
            } else {
                if (spacingLength > 0) {
                    lineBuilder.append(" ");
                }
                lineBuilder.append(wordBuilder);
                lineLength += spacingLength + wordLength;
            }
            wordLength = 0;
            wordBuilder = new StringBuilder();
        }
    }
}
