package com.motomami.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.motomami.service.ProcesService;
import com.motomami.constant.Constantes;
import org.springframework.stereotype.Component;

@Component
public class GetVehicletask {
    @Autowired
    ProcesService pService;

    @Scheduled(cron = "${cron.task.schedule.GetTask}") // Cada dia
    public void task() {
        try {
            pService.readFileInfo(Constantes.C_SOURCE_VEHICLES);
            System.out.println("Ejecuto GET TASK read Info Vehicle task");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
