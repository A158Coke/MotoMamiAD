package com.motomami.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.motomami.Services.ProcesService;
import com.motomami.Utils.Constantes;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RestController
@EnableScheduling
public class HelloController {

    @Autowired
    ProcesService pService;
    // ProcesServiceImpl.readFileInfoParts(Constantes.pSource);

    // Localhost 8080/
    @RequestMapping("/")
    String hellow() {
        return "Hello World!";
    }

    // localhost 8080/readInfo/{resource}
    @RequestMapping(value = { "/readInfo/{resource}" }, method = RequestMethod.GET, produces = "application/json")
    String callProcessReadInfo(@PathVariable String resource) {
        System.out.println("Valor de resouce: " + resource);
        try {
            switch (resource) {
                case Constantes.C_SOURCE_PARTS:
                    pService.readFileInfo(resource);
                    break;

                case Constantes.C_SOURCE_CUSTUMERS:
                    pService.readFileInfo(resource);
                    break;

                case Constantes.C_SOURCE_VEHICLES:
                    pService.readFileInfo(resource);
                    break;
                default:
                    System.err.println("Error msg about resource: " + resource);
                    break;
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
        return "Hello World! callProcessReadInfo con llamada a " + resource;
    }

    // localhost 8080/readInfo/{resource}
    @RequestMapping(value = { "/processInfo/{resource}" }, method = RequestMethod.GET, produces = "application/json")
    String callProcess(@PathVariable String resource) {
        System.out.println("Valor de resouce: " + resource);
        try {
            switch (resource) {
                case Constantes.C_SOURCE_PARTS:
                    pService.procesarParts();
                    break;

                case Constantes.C_SOURCE_CUSTUMERS:
                    pService.procesarCustomers();
                    break;

                case Constantes.C_SOURCE_VEHICLES:
                    pService.procesarVehicles();
                    break;
                default:
                    System.err.println("Error msg about resource: " + resource);
                    break;
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
        return "Hello World! callProcess con llamada a " + resource;
    }

    // localhost 8080/readInfo/{resource}
    @RequestMapping(value = { "/generate/{resource}" }, method = RequestMethod.GET, produces = "application/json")
    String callGenerate(@PathVariable String resource) {
        System.out.println("Valor de resouce: " + resource);
        try {
            /*
             * if (!resource.equals(Constantes.C_SOURCE_PARTS)
             * && !resource.equals(Constantes.C_SOURCE_CUSTUMERS)
             * && !resource.equals(Constantes.C_SOURCE_VEHICLES)) {
             * System.out.println("Error msg about resoucer: " + resource);
             * } else {
             * pService.readFileInfo(resource);
             * }
             */

            if (Constantes.C_SOURCE_INVOICE.equalsIgnoreCase(resource)) {
                pService.generateProviderInvoice(resource.toUpperCase());
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
        return "Hello World! generate con llamada a " + resource;
    }
}
