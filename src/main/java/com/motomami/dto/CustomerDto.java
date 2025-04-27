package com.motomami.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Date;

@Builder(toBuilder = true)
@Value
@RequiredArgsConstructor
public class CustomerDto {
     String DNI;
     String Nombre;
     String Apellido1;
     String Apellido2;
     String Correo;
     Date fechaNacimiento;
     DireccionDto direccion;
     String telefono;
     String Sexo;
}