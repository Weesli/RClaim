package net.weesli.rclaim.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtil {

    private static final Pattern PART = Pattern.compile("(\\d+)([smhdw])");

    /**
     * Parses durations like "2h", "1d12h", "30m". Returns -1 if the input has no valid parts.
     */
    public static long parseSeconds(String input) {
        if (input == null || input.isEmpty()) return -1;

        Matcher matcher = PART.matcher(input.toLowerCase());
        long total = 0;
        boolean matchedAny = false;

        while (matcher.find()) {
            matchedAny = true;
            long value = Long.parseLong(matcher.group(1));
            total += switch (matcher.group(2)) {
                case "s" -> value;
                case "m" -> value * 60;
                case "h" -> value * 3600;
                case "d" -> value * 86400;
                case "w" -> value * 604800;
                default -> 0;
            };
        }

        return matchedAny ? total : -1;
    }
}
