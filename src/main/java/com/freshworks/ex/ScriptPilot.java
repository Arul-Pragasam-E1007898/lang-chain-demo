package com.freshworks.ex;

import com.freshworks.ex.proxy.AgentGroupProxy;
import com.freshworks.ex.proxy.AgentProxy;
import com.freshworks.ex.proxy.ContactProxy;
import com.freshworks.ex.proxy.TicketProxy;
import com.freshworks.ex.scenarios.Testcase;
import com.freshworks.ex.scenarios.TestcaseRepository;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptPilot {
    private static final Logger logger = LoggerFactory.getLogger(ScriptPilot.class);

    interface Assistant {
        @SystemMessage("You are a helpful Freshservice assistant capable of orchestrating a sequence of actions using a configured toolbox of APIs and functions.\n" +
                "When a user request involves multiple actions, you should execute them sequentially, ensuring each step completes before moving to the next.\n" +
                "If any required parameters are missing, use javadoc to fill appropriate values or generate realistic placeholder values to complete the task.\n" +
                "Always return clear, structured output that reflects the execution steps taken and their results as Pass/Fail.")
        String execute(String userMessage);
    }

    public static void main(String[] args) {
        logger.info("Starting ScriptPilot application");

        Assistant assistant = init();

        for (Testcase testcase : TestcaseRepository.load()) {
            String results = assistant.execute(testcase.steps());
            System.out.println("Testcase : " + testcase.id() + ": " + results);
        }
    }

    private static Assistant init() {
        // Initialize the services
        ContactProxy contactProxy = new ContactProxy("freshworks299");
        AgentProxy agentProxy = new AgentProxy("freshworks299");
        AgentGroupProxy agentGroupProxy = new AgentGroupProxy("freshworks299");
        TicketProxy ticketProxy = new TicketProxy("freshworks299");

        // Create the chat model (replace with your OpenAI API key)
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();
        logger.debug("Initialized OpenAI chat model");

        // Create chat memory with a window of 10 messages
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        logger.debug("Created chat memory with window size: 10");

        // Create the assistant with function calling capability and chat memory
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .tools(contactProxy, agentProxy, agentGroupProxy, ticketProxy)
                .build();
        logger.info("Assistant service initialized successfully");
        return assistant;
    }
}
