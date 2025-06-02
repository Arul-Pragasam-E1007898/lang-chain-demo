package com.freshworks.ex.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.Tool;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AgentProxy extends AbstractProxy {
    private static final Logger logger = LoggerFactory.getLogger(AgentProxy.class);
    public static final String AGENTS = "/agents";

    public AgentProxy(String domain) {
        super(domain);
        logger.debug("Initialized AgentProxy with domain: {}", domain);
    }

    /**
     * Creates an agent with the given details.
     *
     * @param email The agent's email address
     * @param firstName The agent's first name
     * @param lastName The agent's last name
     * @param jobTitle The agent's job title
     * @return JsonNode containing the created agent's data
     * @throws IOException if the request fails
     */
    @Tool(name = "createAgent")
    public JsonNode createAgent(String email, String firstName, String lastName, String jobTitle) throws IOException {
        Map<String, Object> agent = new HashMap<>();
        agent.put("email", email);
        agent.put("first_name", firstName);
        agent.put("last_name", lastName);
        agent.put("job_title", jobTitle);

        logger.info("Creating agent with email: {}", email);
        String jsonBody = serializer.serialize(agent);
        logger.debug("Sending create agent request with body: {}", jsonBody);
        Response response = restClient.post(AGENTS, jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to create agent. Status code: {}", response.code());
            throw new IOException("Failed to create agent: " + response.code());
        }
        return parse(response);
    }

    /**
     * Updates an existing agent's information.
     *
     * @param agentId The ID of the agent to update
     * @param firstName The agent's first name
     * @param lastName The agent's last name
     * @param email The agent's email address
     * @param jobTitle The agent's job title
     * @return JsonNode containing the updated agent's data
     * @throws IOException if the request fails
     */
    @Tool(name = "updateAgent")
    public JsonNode updateAgent(Long agentId, String firstName, String lastName, String email, String jobTitle) throws IOException {
        logger.info("Updating agent with ID: {}", agentId);
        Map<String, Object> agent = new HashMap<>();
        if (firstName != null) agent.put("first_name", firstName);
        if (lastName != null) agent.put("last_name", lastName);
        if (email != null) agent.put("email", email);
        if (jobTitle != null) agent.put("job_title", jobTitle);

        String jsonBody = serializer.serialize(agent);
        logger.debug("Sending update agent request with body: {}", jsonBody);
        Response response = restClient.put(AGENTS + '/' + agentId, jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to update agent. Status code: {}", response.code());
            throw new IOException("Failed to update agent: " + response.code());
        }
        return parse(response);
    }

    /**
     * Deletes an agent from the system.
     *
     * @param agentId The ID of the agent to delete
     * @return JsonNode containing the deletion status
     * @throws IOException if the request fails
     */
    @Tool(name = "deleteAgent")
    public JsonNode deleteAgent(Long agentId) throws IOException {
        logger.info("Deleting agent with ID: {}", agentId);
        Response response = restClient.delete(AGENTS + '/' + agentId);
        if (!response.isSuccessful()) {
            logger.error("Failed to delete agent. Status code: {}", response.code());
            throw new IOException("Failed to delete agent: " + response.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Agent deleted successfully");
        result.put("agent_id", agentId);
        return serializer.parse(serializer.serialize(result));
    }

    /**
     * Retrieves an agent's information.
     *
     * @param agentId The ID of the agent to retrieve
     * @return JsonNode containing the agent's data
     * @throws IOException if the request fails
     */
    @Tool(name = "getAgent")
    public JsonNode getAgent(Long agentId) throws IOException {
        logger.info("Fetching agent with ID: {}", agentId);
        Response response = restClient.get(AGENTS + '/' + agentId);
        if (!response.isSuccessful()) {
            logger.error("Failed to get agent. Status code: {}", response.code());
            throw new IOException("Failed to get agent: " + response.code());
        }
        return parse(response);
    }

    /**
     * Lists all agents in the system.
     *
     * @return JsonNode containing the list of agents
     * @throws IOException if the request fails
     */
    @Tool(name = "listAgents")
    public JsonNode listAgents() throws IOException {
        logger.info("Fetching list of agents");
        Response response = restClient.get(AGENTS);
        if (!response.isSuccessful()) {
            logger.error("Failed to list agents. Status code: {}", response.code());
            throw new IOException("Failed to list agents: " + response.code());
        }
        return parse(response);
    }

    /**
     * Forgets an agent from the system.
     * This operation permanently removes the agent's data from the system.
     *
     * @param agentId The ID of the agent to forget
     * @return JsonNode containing the forget operation status
     * @throws IOException if the request fails
     */
    @Tool(name = "forgetAgent")
    public JsonNode forgetAgent(Long agentId) throws IOException {
        logger.info("Forgetting agent with ID: {}", agentId);
        Response response = restClient.delete(AGENTS + '/' + agentId + "/forget");
        if (!response.isSuccessful()) {
            logger.error("Failed to forget agent. Status code: {}", response.code());
            throw new IOException("Failed to forget agent: " + response.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Agent forgotten successfully");
        result.put("agent_id", agentId);
        return serializer.parse(serializer.serialize(result));
    }

    /**
     * Reactivates a forgotten agent in the system.
     *
     * @param agentId The ID of the agent to reactivate
     * @return JsonNode containing the reactivation status
     * @throws IOException if the request fails
     */
    @Tool(name = "reactivateAgent")
    public JsonNode reactivateAgent(Long agentId) throws IOException {
        logger.info("Reactivating agent with ID: {}", agentId);
        Map<String, Object> reactivation = new HashMap<>();
        
        String jsonBody = serializer.serialize(reactivation);
        Response response = restClient.put(AGENTS + '/' + agentId + "/reactivate", jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to reactivate agent. Status code: {}", response.code());
            throw new IOException("Failed to reactivate agent: " + response.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Agent reactivated successfully");
        result.put("agent_id", agentId);
        return serializer.parse(serializer.serialize(result));
    }
} 