package com.github.marbor.shortcutsstats;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TextUtilsTest {

    @ParameterizedTest
    @MethodSource("hugeNumberToTunedFormatProvider")
    public void test(long hugeNumber, String expectedTunedValue) {
        // when
        final String result = TextUtils.makeHugeNumberShorter(hugeNumber);

        // then
        Assertions.assertThat(result).isEqualTo(expectedTunedValue);
    }

    private static Stream<Arguments> hugeNumberToTunedFormatProvider() {
        return Stream.of(
                Arguments.of(1230, "1.2k"),
                Arguments.of(1231, "1.2k"),
                Arguments.of(12310, "12k"),
                Arguments.of(123100, "123k"),
                Arguments.of(999_999, "999k"),
                Arguments.of(1_000_000, "1M"),
                Arguments.of(1_100_000, "1.1M"),
                Arguments.of(110_200_000, "110M"),
                Arguments.of(1_200_000_000, "1.2G"),
                Arguments.of(1_200_000_000_000L, "1.2T"),
                Arguments.of(1_200_000_000_000_000L, "1.2P"),
                Arguments.of(1_200_000_000_000_000_000L, "1.2E")
        );
    }
}