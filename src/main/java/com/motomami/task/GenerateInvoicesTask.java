package com.motomami.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.motomami.Services.ProcesService;
import com.motomami.Utils.Constantes;

@Component
public class GenerateInvoicesTask {

    @Autowired
    ProcesService pService;

    @Scheduled(cron = "${cron.task.schedule.GenerateInvoicesTask}") // dia1 cada mes lo ejecurta
    public void executeTask() {
        pService.generateProviderInvoice(Constantes.C_SOURCE_INVOICE);
        System.out.println("Ejecuto GET TASK generate invoice task");
    }
}