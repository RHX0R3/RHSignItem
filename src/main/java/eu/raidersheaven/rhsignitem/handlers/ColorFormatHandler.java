package eu.raidersheaven.rhsignitem.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public abstract class ColorFormatHandler {

    private static final MiniMessage msg = MiniMessage.miniMessage();
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#[0-9a-fA-F]{6})");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&([0-9a-fA-Fk-oK-OrR])");


    /**
     * Converts Legacy (&) and Legacy hex colors (&#rrggbb) to MiniMessage
     *
     * @param text String that contains Legacy color codes
     * @return Converted MiniMessage String
     */
    public static String convertToMiniMessage(String text) {
        if (text == null || text.isEmpty()) return "";

        // Hex-Farben konvertieren
        Matcher hexMatcher = HEX_PATTERN.matcher(text);
        while (hexMatcher.find()) {
            String hexColor = hexMatcher.group(1);
            text = text.replace("&" + hexColor, "<" + hexColor + ">");
        }

        // Legacy-Farben konvertieren
        Matcher legacyMatcher = LEGACY_PATTERN.matcher(text);
        while (legacyMatcher.find()) {
            String legacyCode = legacyMatcher.group(1);
            String miniMessageColor = convertLegacyToMiniMessage(legacyCode);
            text = text.replace("&" + legacyCode, miniMessageColor);
        }

        return text;
    }


    /**
     * Converts Legacy to MiniMessage
     *
     * @param code Legacy color code
     * @return MiniMessage color code
     */
    private static String convertLegacyToMiniMessage(String code) {
        return switch (code.toLowerCase()) {
            case "0" -> "<black>";
            case "1" -> "<dark_blue>";
            case "2" -> "<dark_green>";
            case "3" -> "<dark_aqua>";
            case "4" -> "<dark_red>";
            case "5" -> "<dark_purple>";
            case "6" -> "<gold>";
            case "7" -> "<gray>";
            case "8" -> "<dark_gray>";
            case "9" -> "<blue>";
            case "a" -> "<green>";
            case "b" -> "<aqua>";
            case "c" -> "<red>";
            case "d" -> "<light_purple>";
            case "e" -> "<yellow>";
            case "f" -> "<white>";
            case "k" -> "<obfuscated>";
            case "l" -> "<bold>";
            case "m" -> "<strikethrough>";
            case "n" -> "<underlined>";
            case "o" -> "<italic>";
            case "r" -> "<reset>";
            default -> "";
        };
    }


    /**
     * Formats a String to Adventure Component with standard decorations (if not already set)
     *
     * @param content String that needs to be formatted
     * @return Adventure Component
     */
    public static Component formatComponent(String content) {
        if (content == null || content.isEmpty()) return Component.empty();

        content = convertToMiniMessage(content);
        return msg.deserialize(content).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }


    /**
     * Convert any String to a MiniMessage compatible String
     *
     * @param content String that needs to be converted
     * @return MiniMessage String
     */
    public static String formatString(String content) {
        if (content == null || content.isEmpty()) return "";
        return convertToMiniMessage(content);
    }


    /**
     * Converts Stringlist to Adventure Component list
     *
     * @param lines Strings thats need to be converted
     * @return List of Adventure Components
     */
    public static List<Component> formatStringList(List<String> lines) {
        if (lines == null || lines.isEmpty()) return List.of();
        return lines.stream().map(ColorFormatHandler::formatComponent).toList();
    }
}
