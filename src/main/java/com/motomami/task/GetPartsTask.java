package com.motomami.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.motomami.service.ProcesService;
import com.motomami.constant.Constantes;

@Component
public class GetPartsTask {

    @Autowired
    ProcesService pService;

    @Scheduled(cron = "${cron.task.schedule.GetTask}") // Cada dia
    public void task() {
        try {
            pService.readFileInfo(Constantes.C_SOURCE_PARTS);
            System.out.println("Ejecuto GET TASK read Info Part task");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
