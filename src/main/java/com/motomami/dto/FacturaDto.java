package com.motomami.dto;

import java.sql.Date;

public class FacturaDto {
    private int id;
    private Date fecha;
    private String tipoSeguro;
    private String tipoVehicle;
    private final String nombreEmpresa = "Motomami";
    private final String cif = "41256985632";
    private String direccion = "C/ Vergel, 5 Madrid, 28080";
    private CustumerDto custumer;
    private Date fecha_de_registro;
    private Date fecha_de_fin_de_contrato;
    private float coste;
    private float iva;


    public FacturaDto (){

    }
    public FacturaDto(int id, Date fecha, String tipoSeguro, String tipoVehicle, String direccion, CustumerDto custumer, Date fecha_de_registro, Date fecha_de_fin_de_contrato, float coste, float iva) {
        this.id = id;
        this.fecha = fecha;
        this.tipoSeguro = tipoSeguro;
        this.tipoVehicle = tipoVehicle;
        this.direccion = direccion;
        this.custumer = custumer;
        this.fecha_de_registro = fecha_de_registro;
        this.fecha_de_fin_de_contrato = fecha_de_fin_de_contrato;
        this.coste = coste;
        this.iva = iva;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setTipoSeguro(String tipoSeguro) {
        this.tipoSeguro = tipoSeguro;
    }

    public void setTipoVehicle(String tipoVehicle) {
        this.tipoVehicle = tipoVehicle;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCustumer(CustumerDto custumer) {
        this.custumer = custumer;
    }

    public void setFecha_de_registro(Date fecha_de_registro) {
        this.fecha_de_registro = fecha_de_registro;
    }

    public void setFecha_de_fin_de_contrato(Date fecha_de_fin_de_contrato) {
        this.fecha_de_fin_de_contrato = fecha_de_fin_de_contrato;
    }

    public void setCoste(float coste) {
        this.coste = coste;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }


    public int getId() {
        return id;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getTipoSeguro() {
        return tipoSeguro;
    }

    public String getTipoVehicle() {
        return tipoVehicle;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public String getCif() {
        return cif;
    }

    public String getDireccion() {
        return direccion;
    }

    public CustumerDto getCustumer() {
        return custumer;
    }

    public Date getFecha_de_registro() {
        return fecha_de_registro;
    }

    public Date getFecha_de_fin_de_contrato() {
        return fecha_de_fin_de_contrato;
    }

    public float getCoste() {
        return coste;
    }

    public float getIva() {
        return iva;
    }
}
