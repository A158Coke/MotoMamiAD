package com.motomami.dto;

import java.util.Date;

public class PartDto {
    private Date datePartExternal;
    private String descriptionPartExternal;
    private String codeDamageExternal;
    private String codeDamage; // Internal code
    private Integer Id; // Internal code
    private String IdExternal;
    private String dni;
    private String vehicleID;

    public Date getDatePartExternal() {
        return datePartExternal;
    }

    public String getDescriptionPartExternal() {
        return descriptionPartExternal;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public String getCodeDamageExternal() {
        return codeDamageExternal;
    }

    public String getCodeDamage() {
        return codeDamage;
    }

    public Integer getId() {
        return Id;
    }

    public String getdni() {
        return dni;
    }

    public String getIdExternal() {
        return IdExternal;
    }

    public void setDatePartExternal(Date datePartExternal) {
        this.datePartExternal = datePartExternal;
    }

    public void setDescriptionPartExternal(String descriptionPartExternal) {
        this.descriptionPartExternal = descriptionPartExternal;
    }

    public void setCodeDamageExternal(String codeDamageExternal) {
        this.codeDamageExternal = codeDamageExternal;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public void setCodeDamage(String codeDamage) {
        this.codeDamage = codeDamage;
    }

    public void setId(Integer id) {
        this.Id = id;
    }

    public void setdni(String dni) {
        this.dni = dni;
    }

    public void setIdExternal(String IdExternal) {
        this.IdExternal = IdExternal;
    }

    @Override
    public String toString() {
        return "PartDto{" +
                "datePartExternal=" + datePartExternal +
                ", descriptionPartExternal='" + descriptionPartExternal + '\'' +
                ", codeDamageExternal='" + codeDamageExternal + '\'' +
                ", id='" + Id + '\'' +
                '}';
    }
}
