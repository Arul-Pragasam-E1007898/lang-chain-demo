package com.freshworks.ollama.util;

import dev.langchain4j.model.chat.ChatLanguageModel;

import java.util.Scanner;




public class ConsoleModelBot {
    private static final Scanner scanner = new Scanner(System.in);

    public static void chat(ChatLanguageModel assistant){
        System.out.println("Hello. How can i help (type 'exit' to quit):");
        System.out.println(("=" .repeat(50)));

        // Generate response
        while (true) {
            System.out.print("\uD83D\uDC64 You: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (!input.isEmpty()) {
                process(assistant, input);
            }
        }
    }

    private static void process(ChatLanguageModel assistant, String input) {
        try {
            System.out.print("\uD83E\uDD16 Bot: ");
            String response = assistant.generate(input);
            System.out.println(response);
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
