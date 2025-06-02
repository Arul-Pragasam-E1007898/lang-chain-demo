package com.freshworks.ollama;

import com.freshworks.ollama.util.Assistant;
import com.freshworks.ollama.util.ConsoleAssistantBot;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

public class SimpleClientWithMemory {

    private static final ChatLanguageModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.1:8b")
            .build();

    private static final Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(3))
            .build();

    public static void main(String[] args) {
        ConsoleAssistantBot.chat(assistant::answer);
    }

}
