package com.sombrasdelavismo.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JToggleButton;

public class CardButton extends JToggleButton {
    private static final Dimension DEFAULT_SIZE = new Dimension(172, 252);

    private final String title;
    private final int manaCost;
    private final String typeLabel;
    private final String description;
    private final String footer;
    private final Color accent;
    private final ImageUtils.ResolvedImage artwork;
    private final boolean hiddenCard;

    public CardButton(
            String title,
            int manaCost,
            String typeLabel,
            String description,
            String footer,
            Color accent,
            String imagePath,
            boolean hiddenCard) {
        this(title, manaCost, typeLabel, description, footer, accent, imagePath, hiddenCard, DEFAULT_SIZE);
    }

    public CardButton(
            String title,
            int manaCost,
            String typeLabel,
            String description,
            String footer,
            Color accent,
            String imagePath,
            boolean hiddenCard,
            Dimension size) {
        this.title = title;
        this.manaCost = manaCost;
        this.typeLabel = typeLabel;
        this.description = description;
        this.footer = footer;
        this.accent = accent;
        this.artwork = hiddenCard ? null : ImageUtils.resolveCardImage(imagePath);
        this.hiddenCard = hiddenCard;

        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setToolTipText(buildTooltip());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int width = getWidth() - 1;
        int height = getHeight() - 1;
        boolean compact = height <= 230;
        boolean preview = height >= 390;
        boolean artworkCard = artwork != null && !hiddenCard;

        int outerPad = compact ? Math.max(5, height / 26) : 9;
        int titleHeight = preview ? 34 : compact ? Math.max(18, height / 7) : 28;
        int manaBubbleSize = preview ? 38 : compact ? Math.max(22, Math.min(28, width / 4)) : 32;
        int footerHeight = preview ? 30 : compact ? Math.max(18, height / 7) : 26;
        int arc = preview ? 22 : compact ? 14 : 18;

        RoundRectangle2D card = new RoundRectangle2D.Float(0, 0, width, height, arc, arc);
        Color topColor = hiddenCard ? new Color(38, 41, 54) : accent.brighter();
        Color bottomColor = hiddenCard ? new Color(18, 20, 30) : accent.darker().darker();
        g2.setPaint(new GradientPaint(0, 0, topColor, 0, height, bottomColor));
        g2.fill(card);

        g2.setColor(new Color(255, 255, 255, 10));
        g2.fillRoundRect(outerPad, outerPad, width - (outerPad * 2), height - (outerPad * 2), arc - 6, arc - 6);

        int footerY = height - outerPad - footerHeight;
        if (artworkCard) {
            int artX = outerPad + 3;
            int artY = outerPad + 3;
            int artWidth = width - ((outerPad + 3) * 2);
            int artHeightFull = footerY - artY - 8;
            drawArtwork(g2, artX, artY, artWidth, artHeightFull, compact ? 14 : 18, false);
        } else {
            int artX = outerPad + 2;
            int artY = outerPad + titleHeight + (compact ? 5 : 10);
            int artWidth = width - ((outerPad + 2) * 2);
            int artBottomGap = compact ? 6 : 18;
            int typeSpace = compact ? 14 : 18;
            int artHeight = Math.max(28, footerY - artY - artBottomGap - typeSpace);
            int typeY = artY + artHeight + artBottomGap;

            drawArtwork(g2, artX, artY, artWidth, artHeight, compact ? 12 : 16, true);

            g2.setColor(new Color(12, 14, 22, 214));
            g2.fillRoundRect(outerPad, outerPad, width - (outerPad * 2), titleHeight, 14, 14);
            g2.setColor(new Color(255, 255, 255, 232));
            g2.setFont(new Font("Serif", Font.BOLD, preview ? 22 : compact ? 13 : 17));
            drawTrimmedText(
                    g2,
                    title,
                    outerPad + 10,
                    outerPad + titleHeight - 8,
                    width - (outerPad * 2) - manaBubbleSize - 22);

            g2.setColor(new Color(236, 239, 246, 220));
            g2.setFont(new Font("SansSerif", Font.BOLD, preview ? 15 : compact ? 11 : 13));
            drawTrimmedText(g2, typeLabel, outerPad + 4, typeY, artWidth - 8);
        }

        if (manaCost >= 0) {
            int manaX = width - outerPad - manaBubbleSize - 8;
            int manaY = outerPad + 8;
            g2.setColor(new Color(252, 209, 82));
            g2.fillOval(manaX, manaY, manaBubbleSize, manaBubbleSize);
            g2.setColor(new Color(67, 45, 10));
            g2.setFont(new Font("SansSerif", Font.BOLD, preview ? 18 : compact ? 12 : 15));
            drawCenteredText(
                    g2,
                    String.valueOf(manaCost),
                    manaX,
                    manaY,
                    manaBubbleSize,
                    manaBubbleSize,
                    g2.getFont(),
                    g2.getColor());
        }

        g2.setColor(new Color(14, 16, 24, 206));
        g2.fillRoundRect(outerPad, footerY, width - (outerPad * 2), footerHeight, 12, 12);
        g2.setColor(new Color(255, 255, 255, 228));
        g2.setFont(new Font("SansSerif", Font.BOLD, preview ? 14 : compact ? 10 : 12));
        drawCenteredText(g2, footer, outerPad, footerY, width - (outerPad * 2), footerHeight, g2.getFont(), g2.getColor());

        g2.setStroke(new BasicStroke(isSelected() ? 1.8f : compact ? 0.8f : 1.0f));
        g2.setColor(isSelected() ? new Color(255, 224, 132, 230) : new Color(255, 255, 255, compact ? 36 : 48));
        g2.draw(card);

        if (!isEnabled()) {
            g2.setColor(new Color(0, 0, 0, 110));
            g2.fill(card);
        }

        g2.dispose();
    }

    private void drawArtwork(Graphics2D g2, int x, int y, int width, int height, int arc, boolean overlay) {
        if (artwork != null) {
            BufferedImage scaled = ImageUtils.getScaledToFit(artwork, width, height, false);
            if (scaled == null) {
                return;
            }
            int drawX = x + ((width - scaled.getWidth()) / 2);
            int drawY = y + ((height - scaled.getHeight()) / 2);
            g2.setClip(new RoundRectangle2D.Float(x, y, width, height, arc, arc));
            g2.drawImage(scaled, drawX, drawY, null);
            g2.setClip(null);

            if (overlay) {
                g2.setPaint(new GradientPaint(
                        x,
                        y,
                        new Color(0, 0, 0, 18),
                        x,
                        y + height,
                        new Color(0, 0, 0, 118)));
                g2.fillRoundRect(x, y, width, height, arc, arc);
            }
            g2.setColor(new Color(255, 255, 255, 16));
            g2.drawRoundRect(x, y, width, height, arc, arc);
            return;
        }

        g2.setColor(new Color(255, 255, 255, 24));
        g2.fillRoundRect(x, y, width, height, arc, arc);
        g2.setColor(new Color(255, 255, 255, 170));
        drawCenteredText(
                g2,
                hiddenCard ? "?" : "Sin arte",
                x,
                y,
                width,
                height,
                new Font("SansSerif", Font.BOLD, height >= 150 ? 28 : 20),
                g2.getColor());
    }

    private String buildTooltip() {
        return "<html><div style='width:260px'><b>" + title + "</b><br><br>" + description
                + "<br><br><i>" + footer + "</i></div></html>";
    }

    private void drawTrimmedText(Graphics2D g2, String text, int x, int y, int width) {
        FontMetrics metrics = g2.getFontMetrics();
        String candidate = text;
        if (metrics.stringWidth(candidate) <= width) {
            g2.drawString(candidate, x, y);
            return;
        }

        String ellipsis = "...";
        int cut = text.length();
        while (cut > 0 && metrics.stringWidth(text.substring(0, cut) + ellipsis) > width) {
            cut--;
        }
        candidate = cut > 0 ? text.substring(0, cut) + ellipsis : ellipsis;
        g2.drawString(candidate, x, y);
    }

    private void drawCenteredText(
            Graphics2D g2,
            String text,
            int x,
            int y,
            int width,
            int height,
            Font font,
            Color color) {
        g2.setFont(font);
        g2.setColor(color);
        FontMetrics metrics = g2.getFontMetrics(font);
        int drawX = x + (width - metrics.stringWidth(text)) / 2;
        int drawY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.drawString(text, drawX, drawY);
    }

    private void drawWrappedText(
            Graphics2D g2,
            String text,
            int x,
            int y,
            int width,
            int maxLines,
            int lineHeight) {
        FontMetrics metrics = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        int renderedLines = 0;

        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (metrics.stringWidth(candidate) <= width) {
                line.setLength(0);
                line.append(candidate);
                continue;
            }

            if (line.isEmpty()) {
                g2.drawString(trimToWidth(word, metrics, width), x, currentY);
                currentY += lineHeight;
                renderedLines++;
                if (renderedLines >= maxLines) {
                    return;
                }
                continue;
            }

            g2.drawString(line.toString(), x, currentY);
            currentY += lineHeight;
            renderedLines++;
            line.setLength(0);
            line.append(word);
            if (renderedLines >= maxLines - 1) {
                break;
            }
        }

        if (renderedLines < maxLines && !line.isEmpty()) {
            g2.drawString(trimToWidth(line.toString(), metrics, width), x, currentY);
        }
    }

    private String trimToWidth(String text, FontMetrics metrics, int width) {
        if (metrics.stringWidth(text) <= width) {
            return text;
        }

        String ellipsis = "...";
        int cut = text.length();
        while (cut > 0 && metrics.stringWidth(text.substring(0, cut) + ellipsis) > width) {
            cut--;
        }
        return cut > 0 ? text.substring(0, cut) + ellipsis : ellipsis;
    }
}
