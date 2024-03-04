package com.motomami.dto;

import java.util.List;

public class CustomerVehicleDto {
    private String typeCustomer;

    private String customer_DNICIF;
    List<VehicleDto> vehiculosAsegurados;

    public CustomerVehicleDto() {
    }


    public void setTypeCustomer(String typeCustomer) {
        this.typeCustomer = typeCustomer;
    }

    public void setCustomer_DNICIF(String customer_DNICIF) {
        this.customer_DNICIF = customer_DNICIF;
    }

    public void setVehiculosAsegurados(List<VehicleDto> vehiculosAsegurados) {
        this.vehiculosAsegurados = vehiculosAsegurados;
    }


    public String getTypeCustomer() {
        return typeCustomer;
    }

    public String getCustomer_DNICIF() {
        return customer_DNICIF;
    }

    public List<VehicleDto> getVehiculosAsegurados() {
        return vehiculosAsegurados;
    }
}
