package com.freshworks.ollama;

import com.freshworks.ollama.util.ConsoleModelBot;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * Temperature  Best ForExample                             Use Cases
 * 0.0-0.3      Factual answers, code generation            Math problems, technical docs
 * 0.4-0.8      General conversation, balanced responses    Chatbots, Q&A systems
 * 0.9-2.0      Creative writing, brainstorming             Stories, poems, creative ideas
 */
public class SimpleClient {

    // Initialize Ollama chat model
    private static final ChatLanguageModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.1:8b")
            .build();

    public static void main(String[] args) {
        ConsoleModelBot.chat(model);
    }

}
