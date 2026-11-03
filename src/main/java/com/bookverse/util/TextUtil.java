package com.bookverse.util;

public final class TextUtil {
    private TextUtil() { }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0) != -1;
    }

    public static int indexOfIgnoreCase(String src, String what, int fromIndex) {
        if (what.isEmpty()) return fromIndex <= src.length() ? fromIndex : -1;
        final int srcLen = src.length();
        final int whatLen = what.length();
        final int max = srcLen - whatLen;
        if (max < 0 || fromIndex > max) return -1;
        char firstLower = Character.toLowerCase(what.charAt(0));
        char firstUpper = Character.toUpperCase(what.charAt(0));
        for (int i = fromIndex; i <= max; i++) {
            char c = src.charAt(i);
            if (c == firstLower || c == firstUpper) {
                if (src.regionMatches(true, i, what, 0, whatLen)) return i;
            }
        }
        return -1;
    }
}
