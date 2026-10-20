package com.indra.jpa.transactions.service;

import org.springframework.stereotype.Service;

import com.indra.jpa.transactions.model.TransferEvent;
import com.indra.jpa.transactions.repository.TransferEventRepository;

@Service 
public class TransferAuditService {

    private final TransferEventRepository transferEventRepository;

    public TransferAuditService(TransferEventRepository transferEventRepository) {
        this.transferEventRepository = transferEventRepository;
    }

    public void log(TransferEvent event) {
    }
}
