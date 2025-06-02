package com.freshworks.ex.scenarios;

import java.util.Arrays;
import java.util.List;

import static com.freshworks.ex.scenarios.Testcase.newCase;

public class TestcaseRepository {

    private static List<Testcase> testcases = Arrays.asList(
            newCase(1, """
                    Generate a unique, random email address using the yopmail domain, 
                    create a contact with that email, and then retrieve the newly created contact.
                    """, Category.Requester),
            newCase(2, """
                    Generate a unique, random email address using the yopmail domain, 
                    create a contact with that email, and then delete the newly created contact.
                    """, Category.Requester),
            newCase(3, """
                    Generate a unique, random email address using the yopmail domain, 
                    create a contact with that email, and then forget the newly created contact.
                    """, Category.Requester)
            /*newCase(4, """
                    Create a support ticket with the following details:
                        Requester: fetch the contact with email 'andomemail123@yopmail.com'
                        Description: Generate a realistic computer hardware issue using ChatGPT (e.g., a faulty graphics card, overheating CPU, etc.).
                        Subject: Generate a concise and unique subject line that summarizes the issue described above.
                        Use the generated description and subject to create the ticket.
                    """, Category.Requester)*/
    );


    public static List<Testcase> load() {
        return testcases;
    }
}
