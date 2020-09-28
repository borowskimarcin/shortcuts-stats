package com.github.marbor.shortcutsstats;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TextUtils {
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }


    public static String makeHugeNumberShorter(long number) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (number == Long.MIN_VALUE) return makeHugeNumberShorter(Long.MIN_VALUE + 1);
        if (number < 0) return "-" + makeHugeNumberShorter(-number);
        if (number < 1000) return Long.toString(number); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(number);
        long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = number / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String timeOrTimes(long number) {
        return number == 1 ? "time" : "times";
    }
}
