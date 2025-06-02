package com.freshworks.ollama.util;

@FunctionalInterface
public interface Processor {
    String process(String input);
}
