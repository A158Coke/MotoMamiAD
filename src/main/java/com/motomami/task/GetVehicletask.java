package com.motomami.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.motomami.Services.ProcesService;
import com.motomami.Utils.Constantes;

public class GetVehicletask {
    @Autowired
    ProcesService pService;

    @Scheduled(cron = "${cron.task.schedule.GetTask}") // Cada dia
    public void task() {
        // readFileInfo("");
        try {
            // pService.readFileInfo(Constantes.C_SOURCE_PARTS);
            // pService.readFileInfo(Constantes.C_SOURCE_CUSTUMERS);
            pService.readFileInfo(Constantes.C_SOURCE_VEHICLES);
            System.out.println("Ejecuto");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
