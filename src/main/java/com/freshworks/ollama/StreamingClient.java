package com.freshworks.ollama;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;

public class StreamingClient {

    // Initialize Ollama chat model
    private static final StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.1:8b")
            .temperature(0.8)
            .build();

    public static void main(String[] args) {

        // Stream response
        model.generate("Write a short crisp prediction about software developer life in 2100",
                new StreamingResponseHandler<>() {
                    @Override
                    public void onNext(String response) {
                        System.out.print(response);
                    }

                    @Override
                    public void onComplete(Response<AiMessage> response) {
                        StreamingResponseHandler.super.onComplete(response);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

}
