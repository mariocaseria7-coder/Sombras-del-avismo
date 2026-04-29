package com.sombrasdelavismo.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

public final class ImageUtils {
    private static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg"};
    private static final Set<String> ARTICLE_TOKENS = Set.of("el", "la", "los", "las", "de", "del");
    private static final Map<String, BufferedImage> ORIGINAL_CACHE = new ConcurrentHashMap<>();
    private static final Set<String> MISSING_ORIGINALS = ConcurrentHashMap.newKeySet();
    private static final Map<String, BufferedImage> SCALED_CACHE = new ConcurrentHashMap<>();

    private ImageUtils() {
    }

    public static ResolvedImage resolveCardImage(String imagePath) {
        return resolveBestImage(imagePath, null, false);
    }

    public static ResolvedImage resolveInspectorImage(String imagePath, String cardTitle) {
        return resolveBestImage(imagePath, cardTitle, true);
    }

    public static BufferedImage getScaledToFit(
            ResolvedImage resolvedImage,
            int maxWidth,
            int maxHeight,
            boolean allowUpscale) {
        if (resolvedImage == null || resolvedImage.image() == null || maxWidth <= 0 || maxHeight <= 0) {
            return null;
        }

        int[] targetSize = calculateFitSize(
                resolvedImage.image().getWidth(),
                resolvedImage.image().getHeight(),
                maxWidth,
                maxHeight,
                allowUpscale);
        int targetWidth = targetSize[0];
        int targetHeight = targetSize[1];
        if (targetWidth <= 0 || targetHeight <= 0) {
            return null;
        }

        if (targetWidth == resolvedImage.image().getWidth() && targetHeight == resolvedImage.image().getHeight()) {
            return resolvedImage.image();
        }

        String cacheKey = resolvedImage.resourcePath()
                + "|" + targetWidth + "x" + targetHeight
                + "|upscale=" + allowUpscale;
        return SCALED_CACHE.computeIfAbsent(
                cacheKey,
                ignored -> scaleHighQuality(resolvedImage.image(), targetWidth, targetHeight));
    }

    private static ResolvedImage resolveBestImage(String imagePath, String cardTitle, boolean inspectorPreferred) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        addPrimaryPathCandidates(candidates, imagePath);
        if (inspectorPreferred) {
            addInspectorCandidates(candidates, imagePath, cardTitle);
        }

        ResolvedImage bestMatch = null;
        long bestArea = -1L;
        for (String candidate : candidates) {
            BufferedImage image = loadOriginal(candidate);
            if (image == null) {
                continue;
            }

            long area = (long) image.getWidth() * image.getHeight();
            if (bestMatch == null || area > bestArea) {
                bestMatch = new ResolvedImage(candidate, image);
                bestArea = area;
            }
        }
        return bestMatch;
    }

    private static void addPrimaryPathCandidates(Set<String> candidates, String imagePath) {
        String basePath = stripExtension(imagePath);
        if (imagePath.startsWith("cards/")) {
            String cardRelativePath = imagePath.substring("cards/".length());
            String hiresBasePath = "cards/hires/" + stripExtension(cardRelativePath);

            // If you later add 800x1120 or 1000x1400 assets, place them in cards/hires/
            // with the same file name. The resolver will automatically prefer them here.
            addWithExtensions(candidates, hiresBasePath);
        }

        addWithExtensions(candidates, basePath);
        candidates.add(imagePath);
    }

    private static void addInspectorCandidates(Set<String> candidates, String imagePath, String cardTitle) {
        for (String alias : buildInspectorAliases(imagePath, cardTitle)) {
            addWithExtensions(candidates, "images/carta_" + alias);
            addWithExtensions(candidates, "images/hechizo_" + alias);
            addWithExtensions(candidates, "images/" + alias);
        }
    }

    private static List<String> buildInspectorAliases(String imagePath, String cardTitle) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        addAliasVariants(aliases, cardTitle);

        String fileName = fileNameWithoutExtension(imagePath);
        fileName = fileName
                .replace("createcard", "")
                .replace("carta", "")
                .replace("hechizo", "")
                .trim();
        addAliasVariants(aliases, fileName);
        return new ArrayList<>(aliases);
    }

    private static void addAliasVariants(Set<String> aliases, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return;
        }

        String normalized = normalizeKey(rawValue);
        if (normalized.isBlank()) {
            return;
        }

        String[] tokens = normalized.split("_");
        List<String> cleanTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isBlank()) {
                cleanTokens.add(token);
            }
        }
        if (cleanTokens.isEmpty()) {
            return;
        }

        aliases.add(String.join("_", cleanTokens));
        aliases.add(String.join("", cleanTokens));
        aliases.add(cleanTokens.get(0));
        aliases.add(cleanTokens.get(cleanTokens.size() - 1));

        List<String> filteredTokens = cleanTokens.stream()
                .filter(token -> !ARTICLE_TOKENS.contains(token))
                .toList();
        if (!filteredTokens.isEmpty()) {
            aliases.add(String.join("_", filteredTokens));
            aliases.add(String.join("", filteredTokens));
            aliases.add(filteredTokens.get(0));
            aliases.add(filteredTokens.get(filteredTokens.size() - 1));
        }
    }

    private static void addWithExtensions(Set<String> candidates, String basePath) {
        for (String extension : IMAGE_EXTENSIONS) {
            candidates.add(basePath + extension);
        }
    }

    private static BufferedImage loadOriginal(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank() || MISSING_ORIGINALS.contains(resourcePath)) {
            return null;
        }

        BufferedImage cached = ORIGINAL_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        try (InputStream stream = ImageUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                MISSING_ORIGINALS.add(resourcePath);
                return null;
            }
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                MISSING_ORIGINALS.add(resourcePath);
                return null;
            }

            BufferedImage safeImage = toSafeBufferedImage(image);
            ORIGINAL_CACHE.put(resourcePath, safeImage);
            return safeImage;
        } catch (Exception exception) {
            MISSING_ORIGINALS.add(resourcePath);
            return null;
        }
    }

    private static BufferedImage toSafeBufferedImage(BufferedImage source) {
        int targetType = source.getColorModel().hasAlpha()
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;
        if (source.getType() == targetType) {
            return source;
        }

        BufferedImage converted = new BufferedImage(source.getWidth(), source.getHeight(), targetType);
        Graphics2D graphics = converted.createGraphics();
        applyQualityHints(graphics);
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();
        return converted;
    }

    private static BufferedImage scaleHighQuality(BufferedImage source, int targetWidth, int targetHeight) {
        BufferedImage current = toSafeBufferedImage(source);
        int currentWidth = current.getWidth();
        int currentHeight = current.getHeight();

        // Multi-step downscaling keeps more detail when shrinking large originals to hand/board sizes.
        while (currentWidth / 2 >= targetWidth && currentHeight / 2 >= targetHeight) {
            currentWidth = Math.max(targetWidth, currentWidth / 2);
            currentHeight = Math.max(targetHeight, currentHeight / 2);
            current = scaleSinglePass(current, currentWidth, currentHeight);
        }

        if (currentWidth != targetWidth || currentHeight != targetHeight) {
            current = scaleSinglePass(current, targetWidth, targetHeight);
        }
        return current;
    }

    private static BufferedImage scaleSinglePass(BufferedImage source, int targetWidth, int targetHeight) {
        int targetType = source.getColorModel().hasAlpha()
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, targetType);
        Graphics2D graphics = scaled.createGraphics();
        applyQualityHints(graphics);
        graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return scaled;
    }

    private static void applyQualityHints(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private static int[] calculateFitSize(
            int sourceWidth,
            int sourceHeight,
            int maxWidth,
            int maxHeight,
            boolean allowUpscale) {
        double widthRatio = (double) maxWidth / sourceWidth;
        double heightRatio = (double) maxHeight / sourceHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        if (!allowUpscale) {
            ratio = Math.min(ratio, 1.0d);
        }

        int width = Math.max(1, (int) Math.round(sourceWidth * ratio));
        int height = Math.max(1, (int) Math.round(sourceHeight * ratio));
        return new int[] {width, height};
    }

    private static String stripExtension(String path) {
        int dotIndex = path.lastIndexOf('.');
        return dotIndex >= 0 ? path.substring(0, dotIndex) : path;
    }

    private static String fileNameWithoutExtension(String path) {
        String fileName = stripExtension(path);
        int slashIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        return slashIndex >= 0 ? fileName.substring(slashIndex + 1) : fileName;
    }

    private static String normalizeKey(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return normalized;
    }

    public record ResolvedImage(String resourcePath, BufferedImage image) {
    }
}
