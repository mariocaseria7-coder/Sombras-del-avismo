package com.sombrasdelavismo.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JToggleButton;

public class CardButton extends JToggleButton {
    private static final Map<String, BufferedImage> IMAGE_CACHE = new HashMap<>();

    private final String title;
    private final int manaCost;
    private final String typeLabel;
    private final String description;
    private final String footer;
    private final Color accent;
    private final BufferedImage artwork;
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
        this.title = title;
        this.manaCost = manaCost;
        this.typeLabel = typeLabel;
        this.description = description;
        this.footer = footer;
        this.accent = accent;
        this.artwork = hiddenCard ? null : loadImage(imagePath);
        this.hiddenCard = hiddenCard;

        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setPreferredSize(new Dimension(155, 225));
        setMinimumSize(new Dimension(155, 225));
        setToolTipText(buildTooltip());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth() - 1;
        int height = getHeight() - 1;
        RoundRectangle2D card = new RoundRectangle2D.Float(0, 0, width, height, 22, 22);

        Color topColor = hiddenCard ? new Color(40, 44, 58) : accent.brighter();
        Color bottomColor = hiddenCard ? new Color(17, 18, 28) : accent.darker().darker();
        g2.setPaint(new GradientPaint(0, 0, topColor, 0, height, bottomColor));
        g2.fill(card);

        if (artwork != null) {
            Image scaled = artwork.getScaledInstance(width - 18, 85, Image.SCALE_SMOOTH);
            g2.setClip(new RoundRectangle2D.Float(9, 36, width - 18, 85, 16, 16));
            g2.drawImage(scaled, 9, 36, null);
            g2.setClip(null);
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(9, 36, width - 18, 85, 16, 16);
        } else {
            g2.setColor(new Color(255, 255, 255, 28));
            g2.fillRoundRect(9, 36, width - 18, 85, 16, 16);
            drawCenteredText(g2, hiddenCard ? "?" : "Sin arte", 9, 36, width - 18, 85, new Font("SansSerif", Font.BOLD, 22), new Color(255, 255, 255, 180));
        }

        g2.setColor(new Color(14, 14, 22, 210));
        g2.fillRoundRect(10, 8, width - 20, 22, 12, 12);
        g2.setColor(new Color(255, 255, 255, 230));
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        drawTrimmedText(g2, title, 16, 24, width - 48);

        if (manaCost >= 0) {
            g2.setColor(new Color(250, 210, 94));
            g2.fillOval(width - 38, 8, 26, 26);
            g2.setColor(new Color(59, 40, 8));
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawCenteredText(g2, String.valueOf(manaCost), width - 38, 8, 26, 26, g2.getFont(), g2.getColor());
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawString(typeLabel, 14, 136);

        int descriptionY = 148;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(245, 245, 245));
        drawWrappedText(g2, description, 14, descriptionY, width - 28, 4, 14);

        g2.setColor(new Color(15, 16, 24, 190));
        g2.fillRoundRect(10, height - 32, width - 20, 20, 10, 10);
        g2.setColor(new Color(255, 255, 255, 220));
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        drawCenteredText(g2, footer, 10, height - 32, width - 20, 20, g2.getFont(), g2.getColor());

        g2.setStroke(new BasicStroke(isSelected() ? 3f : 2f));
        g2.setColor(isSelected() ? new Color(255, 224, 140) : new Color(255, 255, 255, 90));
        g2.draw(card);

        if (!isEnabled()) {
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fill(card);
        }

        g2.dispose();
    }

    private String buildTooltip() {
        return "<html><b>" + title + "</b><br>" + description + "<br><i>" + footer + "</i></html>";
    }

    private static BufferedImage loadImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        if (IMAGE_CACHE.containsKey(imagePath)) {
            return IMAGE_CACHE.get(imagePath);
        }

        try (InputStream stream = CardButton.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (stream == null) {
                IMAGE_CACHE.put(imagePath, null);
                return null;
            }
            BufferedImage image = ImageIO.read(stream);
            IMAGE_CACHE.put(imagePath, image);
            return image;
        } catch (Exception exception) {
            IMAGE_CACHE.put(imagePath, null);
            return null;
        }
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
