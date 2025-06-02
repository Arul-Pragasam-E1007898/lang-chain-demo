package com.freshworks.ollama.service;

import com.freshworks.ollama.util.ConsoleAssistantBot;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

interface Translator {
    @SystemMessage("You are a professional translator. Provide accurate translations with pronunciation guides.")
    @UserMessage("Translate {{word}} in tamil and hindi")
    String translate(@V("word") String word);
}

public class TranslatorBot {
    private static ChatLanguageModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.1:8b")
            .build();

    public static void main(String[] args) {

        Translator translator = AiServices.create(Translator.class, model);
        ConsoleAssistantBot.chat(translator::translate);
    }
}
