package com.freshworks.ex.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerator {
    public static int generate(int lower, int upper) {// Exclusive upper bound
        return ThreadLocalRandom.current().nextInt(lower, upper);
    }
}
