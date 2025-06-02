package com.freshworks.ollama.rag;

import com.freshworks.ollama.util.Assistant;
import com.freshworks.ollama.util.ConsoleAssistantBot;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class CodeAssistant {
    private static final String codebase = "/Users/apragasam/Documents/Work/GitHub/itildesk";

    public static void main(String[] args) throws IOException {
        // Initialize embedding model
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

        // Create embedding store
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Process documents from a folder
        List<Document> documents = process(codebase);
        System.out.println("Successfully read " + documents.size() + " documents");
        index(embeddingModel, embeddingStore, documents);
        System.out.println("Successfully indexed " + documents.size() + " documents");

        // Initialize Ollama model
        ChatLanguageModel chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.1:8b")
                .build();

        // Create RAG assistant
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(100))
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .build())
                .build();

        // Ask questions about the documents
        ConsoleAssistantBot.chat(assistant::answer);
    }

    private static List<Document> process(String folderPath) throws IOException {
        List<Document> documents = new ArrayList<>();

        // Walk through the directory recursively
        Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith("db/schema.rb")) // Only process Schema files
                .forEach(path -> { process(path, documents); });
        return documents;
    }

    private static void index(EmbeddingModel embeddingModel,
                              EmbeddingStore<TextSegment> embeddingStore, List<Document> documents) {
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);

        // Process all documents
        for (Document document : documents) {
            List<TextSegment> segments = splitter.split(document);
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddings, segments);
            System.out.println("Indexed " + embeddings.size() + " embeddings");
        }
    }

    private static void process(Path path, List<Document> documents) {
        try {
            System.out.println("Processing " + path.toAbsolutePath());
            String content = Files.readString(path);
            if (!content.isEmpty())
                documents.add(Document.from(content));
        } catch (IOException e) {
            System.err.println("Error reading file: " + path);
        }
    }

    private static String read(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }
}
