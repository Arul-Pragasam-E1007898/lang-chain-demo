package com.freshworks.ollama.service;

import com.freshworks.ollama.util.ConsoleAssistantBot;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

interface Chef {
    @SystemMessage("You are a professional cook. Provide accurate step by step instructions to prepare the specified meal")
    @UserMessage("Steps to prepare {{recipe}}")
    String prepare(@V("recipe") String recipe);
}

public class CookingGuru {
    private static ChatLanguageModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.1:8b")
            .build();

    public static void main(String[] args) {

        Chef cook = AiServices.create(Chef.class, model);
        ConsoleAssistantBot.chat(cook::prepare);
    }
}
