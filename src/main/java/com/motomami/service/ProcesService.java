package com.motomami.service;

import org.springframework.stereotype.Service;

@Service
public interface ProcesService {
    void readFileInfo(String pSource);

    void processInfo(String pSource);

    void procesarVehicles();

    void procesarCustomers();

    void generateProviderInvoice(String pSource);

    void procesarParts();
}
