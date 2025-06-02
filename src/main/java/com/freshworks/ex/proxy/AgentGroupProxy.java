package com.freshworks.ex.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.Tool;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AgentGroupProxy extends AbstractProxy {
    private static final Logger logger = LoggerFactory.getLogger(AgentGroupProxy.class);
    public static final String GROUPS = "/groups";

    public AgentGroupProxy(String domain) {
        super(domain);
        logger.debug("Initialized AgentGroupProxy with domain: {}", domain);
    }

    /**
     * Creates an agent group with the given details.
     *
     * @param name The name of the group
     * @param description The description of the group
     * @return JsonNode containing the created group's data
     * @throws IOException if the request fails
     */
    @Tool(name = "createAgentGroup")
    public JsonNode createAgentGroup(String name, String description) throws IOException {
        Map<String, Object> group = new HashMap<>();
        group.put("name", name);
        group.put("description", description);

        logger.info("Creating agent group with name: {}", name);
        String jsonBody = serializer.serialize(group);
        logger.debug("Sending create agent group request with body: {}", jsonBody);
        Response response = restClient.post(GROUPS, jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to create agent group. Status code: {}", response.code());
            throw new IOException("Failed to create agent group: " + response.code());
        }
        return parse(response);
    }

    /**
     * Updates an existing agent group's information.
     *
     * @param groupId The ID of the group to update
     * @param name The new name of the group
     * @param description The new description of the group
     * @return JsonNode containing the updated group's data
     * @throws IOException if the request fails
     */
    @Tool(name = "updateAgentGroup")
    public JsonNode updateAgentGroup(Long groupId, String name, String description) throws IOException {
        logger.info("Updating agent group with ID: {}", groupId);
        Map<String, Object> group = new HashMap<>();
        if (name != null) group.put("name", name);
        if (description != null) group.put("description", description);

        String jsonBody = serializer.serialize(group);
        logger.debug("Sending update agent group request with body: {}", jsonBody);
        Response response = restClient.put(GROUPS + '/' + groupId, jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to update agent group. Status code: {}", response.code());
            throw new IOException("Failed to update agent group: " + response.code());
        }
        return parse(response);
    }

    /**
     * Deletes an agent group from the system.
     *
     * @param groupId The ID of the group to delete
     * @return JsonNode containing the deletion status
     * @throws IOException if the request fails
     */
    @Tool(name = "deleteAgentGroup")
    public JsonNode deleteAgentGroup(Long groupId) throws IOException {
        logger.info("Deleting agent group with ID: {}", groupId);
        Response response = restClient.delete(GROUPS + '/' + groupId);
        if (!response.isSuccessful()) {
            logger.error("Failed to delete agent group. Status code: {}", response.code());
            throw new IOException("Failed to delete agent group: " + response.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Agent group deleted successfully");
        result.put("group_id", groupId);
        return serializer.parse(serializer.serialize(result));
    }

    /**
     * Retrieves an agent group's information.
     *
     * @param groupId The ID of the group to retrieve
     * @return JsonNode containing the group's data
     * @throws IOException if the request fails
     */
    @Tool(name = "getAgentGroup")
    public JsonNode getAgentGroup(Long groupId) throws IOException {
        logger.info("Fetching agent group with ID: {}", groupId);
        Response response = restClient.get(GROUPS + '/' + groupId);
        if (!response.isSuccessful()) {
            logger.error("Failed to get agent group. Status code: {}", response.code());
            throw new IOException("Failed to get agent group: " + response.code());
        }
        return parse(response);
    }

    /**
     * Lists all agent groups in the system.
     *
     * @return JsonNode containing the list of agent groups
     * @throws IOException if the request fails
     */
    @Tool(name = "listAgentGroups")
    public JsonNode listAgentGroups() throws IOException {
        logger.info("Fetching list of agent groups");
        Response response = restClient.get(GROUPS);
        if (!response.isSuccessful()) {
            logger.error("Failed to list agent groups. Status code: {}", response.code());
            throw new IOException("Failed to list agent groups: " + response.code());
        }
        return parse(response);
    }
} 