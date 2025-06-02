package com.freshworks.ex.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.freshworks.ex.utils.RandomGenerator;
import dev.langchain4j.agent.tool.Tool;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TicketProxy extends AbstractProxy {
    private static final Logger logger = LoggerFactory.getLogger(TicketProxy.class);
    public static final String TICKETS = "/tickets";

    public TicketProxy(String domain) {
        super(domain);
        logger.debug("Initialized TicketProxy with domain: {}", domain);
    }

    /**
     * Creates a new ticket with the given details.
     *
     * @param subject The subject of the ticket
     * @param description The description of the ticket
     * @param requesterId The ID of the requester
     * @return JsonNode containing the created ticket's data
     * @throws IOException if the request fails
     */
    @Tool(name = "createTicket")
    public JsonNode createTicket(String subject, String description, Long requesterId) throws IOException {
        Map<String, Object> ticket = new HashMap<>();
        ticket.put("subject", subject);
        ticket.put("description", description);
        ticket.put("requester_id", requesterId);
        ticket.put("priority", RandomGenerator.generate(1,4));
        ticket.put("status", RandomGenerator.generate(2,6));

        logger.info("Creating ticket with subject: {}", subject);
        String jsonBody = serializer.serialize(ticket);
        logger.debug("Sending create ticket request with body: {}", jsonBody);
        Response response = restClient.post(TICKETS, jsonBody);
        if (!response.isSuccessful()) {
            System.out.println("Response : " + parse(response));
            logger.error("Failed to create ticket. Status code: {}", response.code());
            throw new IOException("Failed to create ticket: " + response.code());
        }
        return parse(response);
    }

    /**
     * Updates an existing ticket's information.
     *
     * @param ticketId The ID of the ticket to update
     * @param subject The new subject of the ticket
     * @param description The new description of the ticket
     * @param priority The new priority of the ticket (1-4)
     * @param status The new status of the ticket (2-6)
     * @param groupId The new group ID to assign the ticket to
     * @param agentId The new agent ID to assign the ticket to
     * @return JsonNode containing the updated ticket's data
     * @throws IOException if the request fails
     */
    @Tool(name = "updateTicket")
    public JsonNode updateTicket(Long ticketId, String subject, String description, 
            Integer priority, Integer status, Long groupId, Long agentId) throws IOException {
        logger.info("Updating ticket with ID: {}", ticketId);
        Map<String, Object> ticket = new HashMap<>();
        if (subject != null) ticket.put("subject", subject);
        if (description != null) ticket.put("description", description);
        if (priority != null) ticket.put("priority", priority);
        if (status != null) ticket.put("status", status);
        if (groupId != null) ticket.put("group_id", groupId);
        if (agentId != null) ticket.put("responder_id", agentId);

        String jsonBody = serializer.serialize(ticket);
        logger.debug("Sending update ticket request with body: {}", jsonBody);
        Response response = restClient.put(TICKETS + '/' + ticketId, jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to update ticket. Status code: {}", response.code());
            throw new IOException("Failed to update ticket: " + response.code());
        }
        return parse(response);
    }

    /**
     * Deletes a ticket from the system.
     *
     * @param ticketId The ID of the ticket to delete
     * @return JsonNode containing the deletion status
     * @throws IOException if the request fails
     */
    @Tool(name = "deleteTicket")
    public JsonNode deleteTicket(Long ticketId) throws IOException {
        logger.info("Deleting ticket with ID: {}", ticketId);
        Response response = restClient.delete(TICKETS + '/' + ticketId);
        if (!response.isSuccessful()) {
            logger.error("Failed to delete ticket. Status code: {}", response.code());
            throw new IOException("Failed to delete ticket: " + response.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Ticket deleted successfully");
        result.put("ticket_id", ticketId);
        return serializer.parse(serializer.serialize(result));
    }

    /**
     * Retrieves a ticket's information.
     *
     * @param ticketId The ID of the ticket to retrieve
     * @return JsonNode containing the ticket's data
     * @throws IOException if the request fails
     */
    @Tool(name = "getTicket")
    public JsonNode getTicket(Long ticketId) throws IOException {
        logger.info("Fetching ticket with ID: {}", ticketId);
        Response response = restClient.get(TICKETS + '/' + ticketId);
        if (!response.isSuccessful()) {
            logger.error("Failed to get ticket. Status code: {}", response.code());
            throw new IOException("Failed to get ticket: " + response.code());
        }
        return parse(response);
    }

    /**
     * Lists all tickets in the system.
     *
     * @return JsonNode containing the list of tickets
     * @throws IOException if the request fails
     */
    @Tool(name = "listTickets")
    public JsonNode listTickets() throws IOException {
        logger.info("Fetching list of tickets");
        Response response = restClient.get(TICKETS);
        if (!response.isSuccessful()) {
            logger.error("Failed to list tickets. Status code: {}", response.code());
            throw new IOException("Failed to list tickets: " + response.code());
        }
        return parse(response);
    }

    /**
     * Adds a note to a ticket.
     *
     * @param ticketId The ID of the ticket
     * @param note The note content to add
     * @param isPrivate Whether the note should be private
     * @return JsonNode containing the updated ticket's data
     * @throws IOException if the request fails
     */
    @Tool(name = "addNote")
    public JsonNode addNote(Long ticketId, String note, boolean isPrivate) throws IOException {
        logger.info("Adding note to ticket with ID: {}", ticketId);
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("body", note);
        noteData.put("private", isPrivate);

        String jsonBody = serializer.serialize(noteData);
        logger.debug("Sending add note request with body: {}", jsonBody);
        Response response = restClient.post(TICKETS + '/' + ticketId + "/notes", jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to add note. Status code: {}", response.code());
            throw new IOException("Failed to add note: " + response.code());
        }
        return parse(response);
    }

    /**
     * Merges a source ticket into a target ticket.
     *
     * @param sourceTicketId The ID of the source ticket to merge
     * @param targetTicketId The ID of the target ticket to merge into
     * @return JsonNode containing the merge operation status
     * @throws IOException if the request fails
     */
    @Tool(name = "mergeTickets")
    public JsonNode mergeTickets(Long sourceTicketId, Long targetTicketId) throws IOException {
        logger.info("Merging ticket {} into ticket {}", sourceTicketId, targetTicketId);
        Map<String, Object> mergeData = new HashMap<>();
        mergeData.put("source_ticket_id", sourceTicketId);

        String jsonBody = serializer.serialize(mergeData);
        logger.debug("Sending merge tickets request with body: {}", jsonBody);
        Response response = restClient.post(TICKETS + '/' + targetTicketId + "/merge", jsonBody);
        if (!response.isSuccessful()) {
            logger.error("Failed to merge tickets. Status code: {}", response.code());
            throw new IOException("Failed to merge tickets: " + response.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Tickets merged successfully");
        result.put("source_ticket_id", sourceTicketId);
        result.put("target_ticket_id", targetTicketId);
        return serializer.parse(serializer.serialize(result));
    }
} 