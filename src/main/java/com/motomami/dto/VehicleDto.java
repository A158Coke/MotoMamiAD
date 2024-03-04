package com.motomami.dto;

public class VehicleDto {
    private String idVehicle;
    private String idVehicleExternal;
    private String numberPlate;
    private String typeVehicle;
    private String brand;
    private String model;
    private String color;
    private String serialNumber;
    private String dniCif;

    public VehicleDto() {

    }

    public void setDniCif(String dniCif) {
        this.dniCif = dniCif;
    }

    public void setIdVehicle(String idVehicle) {
        this.idVehicle = idVehicle;
    }

    public void setIdVehicleExternal(String idVehicleExternal) {
        this.idVehicleExternal = idVehicleExternal;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public void setTypeVehicle(String typeVehicle) {
        this.typeVehicle = typeVehicle;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIdVehicle() {
        return idVehicle;
    }

    public String getIdVehicleExternal() {
        return idVehicleExternal;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public String getTypeVehicle() {
        return typeVehicle;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDniCif() {
        return dniCif;
    }

    @Override
    public String toString() {
        return "VehicleDto{" +
                "idVehicle='" + idVehicle + '\'' +
                ", idVehicleExternal='" + idVehicleExternal + '\'' +
                ", numberPlate='" + numberPlate + '\'' +
                ", typeVehicle='" + typeVehicle + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                '}';
    }
}
