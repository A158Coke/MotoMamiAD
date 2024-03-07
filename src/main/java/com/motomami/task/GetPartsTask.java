package com.motomami.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.motomami.Services.ProcesService;
import com.motomami.Utils.Constantes;
import com.motomami.dto.PartDto;

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
