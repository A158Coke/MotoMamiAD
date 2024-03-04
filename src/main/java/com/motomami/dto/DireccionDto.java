package com.motomami.dto;

public class DireccionDto {
    private String codigoPostal;
    private String TipoVia;
    private String Ciudad;
    private int numero;

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public void setTipoVia(String tipoVia) {
        TipoVia = tipoVia;
    }

    public void setCiudad(String ciudad) {
        Ciudad = ciudad;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public String getTipoVia() {
        return TipoVia;
    }

    public String getCiudad() {
        return Ciudad;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public String toString() {
        return "DireccionDto{" +
                "codigoPostal='" + codigoPostal + '\'' +
                ", TipoVia='" + TipoVia + '\'' +
                ", Ciudad='" + Ciudad + '\'' +
                ", numero=" + numero +
                '}';
    }
}
