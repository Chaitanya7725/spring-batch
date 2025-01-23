package com.example.csvtosqlbatch.repository;

import com.example.csvtosqlbatch.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact,Long> {
}
