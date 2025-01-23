package com.example.csvtosqlbatch.processor;

import com.example.csvtosqlbatch.model.Contact;
import org.springframework.batch.item.ItemProcessor;

public class ContactItemProcessor implements ItemProcessor<Contact, Contact> {
    @Override
    public Contact process(Contact contact) {
        // Set id to null to allow auto-increment
        contact.setId(null);
        return contact;
    }
}