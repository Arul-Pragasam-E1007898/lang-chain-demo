package com.freshworks.ollama.util;

import java.util.Scanner;



public class ConsoleAssistantBot {
    private static final Scanner scanner = new Scanner(System.in);

    public static void chat(Processor processor){
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
                process(processor, input);
            }
        }
    }

    private static void process(Processor processor, String input) {
        try {
            System.out.print("\uD83E\uDD16 Bot: ");
            String response = processor.process(input);
            System.out.println(response);
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
