package com.freshworks.ollama;

import com.freshworks.ollama.util.ConsoleModelBot;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class SimpleOpenAIClient {
    
    // Initialize OpenAI chat model
    private static final ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();

    public static void main(String[] args) {
        ConsoleModelBot.chat(model);
    }

}
