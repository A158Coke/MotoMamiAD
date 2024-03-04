package com.motomami.dto;

import java.util.Date;

public class CustumerDto {
    private String DNI;
    private String Nombre;
    private String Apellido1;
    private String Apellido2;
    private String Correo;
    private Date fechaNacimiento;
    private DireccionDto direccion;
    private String telefono;
    private String Sexo;

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setApellido1(String apellido1) {
        Apellido1 = apellido1;
    }

    public void setApellido2(String apellido2) {
        Apellido2 = apellido2;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public void setFechaNacimiento(java.util.Date date) {
        this.fechaNacimiento = date;
    }

    public void setDireccionDto(DireccionDto direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public String getDNI() {
        return DNI;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getApellido1() {
        return Apellido1;
    }

    public String getApellido2() {
        return Apellido2;
    }

    public String getCorreo() {
        return Correo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public DireccionDto getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getSexo() {
        return Sexo;
    }


    @Override
    public String toString() {
        return "CustumerDto{" +
                "DNI='" + DNI + '\'' +
                ", Nombre='" + Nombre + '\'' +
                ", Apellido1='" + Apellido1 + '\'' +
                ", Apellido2='" + Apellido2 + '\'' +
                ", Correo='" + Correo + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", direccion=" + direccion +
                ", telefono='" + telefono + '\'' +
                ", Sexo='" + Sexo + '\'' +
                '}';
    }
}