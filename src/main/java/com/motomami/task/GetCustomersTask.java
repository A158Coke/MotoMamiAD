package com.motomami.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.motomami.service.ProcesService;
import com.motomami.constant.Constantes;

public class GetCustomersTask {
    @Autowired
    ProcesService pService;

    @Scheduled(cron = "${cron.task.schedule.GetPartsTask}") // Cada dia
    public void task() {
        try {
            pService.readFileInfo(Constantes.C_SOURCE_CUSTUMERS);
            System.out.println("Ejecuto GET TASK read Info Customer task");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
