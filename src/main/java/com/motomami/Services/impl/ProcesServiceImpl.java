package com.motomami.Services.impl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motomami.dto.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.motomami.Services.ProcesService;
import com.motomami.Utils.Constantes;

@Service
@RequiredArgsConstructor
public class ProcesServiceImpl implements ProcesService {
    @Getter
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Value("${path.folder.outFiles}")
    private String outFilesPath;

    @Value("${path.folder.inFiles}")
    private String inFilePath;

    @Value("${path.folder.providers}")
    private String providers;

    @Value("${path.folder.parts.providers}")
    private String parts_providers;

    @Value("${path.folder.vehicles.providers}")
    private String vehicle_providers;

    @Value("${path.folder.customers.providers}")
    private String customers_providers;

    @Value("${extension.file.providers}")
    private String extencion;
    @Value("${databaseUrl}")
    private String DBUrl;
    @Value("${databaseUser}")
    private String DBUser;
    @Value("${databasePassword}")
    private String DBPassword;

    public ProcesServiceImpl(ObjectMapper objectMapper) {
        ProcesServiceImpl.objectMapper = objectMapper;
    }

    @Override
    public void readFileInfo(String source) {
        try {
            switch (source) {
                case Constantes.C_SOURCE_PARTS:
                    readFileInfoParts();
                    break;
                case Constantes.C_SOURCE_CUSTUMERS:
                    readFileInfoCustumers();
                    break;
                case Constantes.C_SOURCE_VEHICLES:
                    readFileInfoVehicle();
                    break;
                default:
                    // 处理未匹配的情况 manaje caso que no match
            }
        } catch (Exception e1) {
            System.err.println("GetPartsTask Class Error");
        }
    }

    // EntryPoint de generate invoice
    @Override
    public void generateProviderInvoice(String source) {
        try {
            if (source.equals(Constantes.C_SOURCE_INVOICE)) {
                System.out.println("Estoy dentro del if en generateProviderInvoice");
                generateInvoice();
            }
        } catch (Exception e) {
            System.err.println("Error generando la factura: " + e.getMessage());
        }
    }

    // Generar el instancia de Clase Factura
    private void generateInvoice() {
        ArrayList<InvoiceDto> invoices = null;
        for (String provider : Constantes.providers) {
            try {
                insertDataInvoiceToDB(provider);
                invoices = getDataInvoiceToDB(provider);
            } catch (SQLException e) {
                System.err.println("Error al acceder a los datos de mm_invoice: " + e.getMessage());
            }
        }
        if (invoices != null) {
            for (InvoiceDto invoice : invoices) {
                try {
                    writeInvoiceFile(invoice);
                } catch (IOException e) {
                    System.err.println("Error al escribir las facturas: " + e.getMessage());
                }
            }
        }
    }

    // Escribir los datos al fichero de factura
    private void writeInvoiceFile(InvoiceDto invoice) throws IOException {
        String absolutePath = outFilesPath + "/Invoices/";
        FileWriter fw = new FileWriter(absolutePath + "MM_invoices_" + invoice.getInvoiceDate() + ".csv", true);
        BufferedWriter bw = new BufferedWriter(fw);
        double totalWithOutTax = invoice.getPeopleQuantity() * invoice.getUnitPrice();
        double totalWithTax = totalWithOutTax + (totalWithOutTax * invoice.getTax() / 100);
        String invoiceStruct = "\nFactura\n\n"
                + "Nº: " + invoice.getInvoiceNum() + ",\n"
                + "Fecha: " + invoice.getInvoiceDate() + ",\n\n"
                + "Datos de la Empresa:\n"
                + "Nombre: " + Constantes.COMPANY + ",\n"
                + "CIF: " + Constantes.CIF + ",\n"
                + "Dirección: " + Constantes.COMPANY_ADDRESS + ",\n\n"
                + "Datos del Proveedor:\n"
                + "Nombre: " + invoice.getProviderName() + ",\n"
                + "Costos:,\n"
                + "Asegurados: " + invoice.getPeopleQuantity() + " personas,\n"
                + "Coste unitario: " + String.format("%.2f", invoice.getUnitPrice()) + " €,\n"
                + "Coste total sin IVA: " + String.format("%.2f", totalWithOutTax) + ",\n"
                + "IVA (21%): " + invoice.getTax() + " €,\n\n"
                + "Total a Pagar: " + String.format("%.2f", totalWithTax) + " €\n\n";
        bw.write(invoiceStruct);
        bw.flush();
        bw.close();
        System.out.println("Escritura de la factura con exito");
    }

    // Devuelve el listado de Factura
    private ArrayList<InvoiceDto> getDataInvoiceToDB(String provider) throws SQLException {
        ArrayList<InvoiceDto> invoices = null;
        try (Connection con = DriverManager.getConnection(DBUrl, DBUser,
                DBPassword);) {
            String query = "SELECT * FROM mm_invoice WHERE provider_name = ?";
            invoices = new ArrayList<>();
            InvoiceDto invoiceDto = new InvoiceDto();
            PreparedStatement ps;
            ResultSet rs;
            ps = con.prepareStatement(query);
            ps.setString(1, provider);
            rs = ps.executeQuery();
            invoices.add(invoiceDto);
            while (rs.next()) {
                invoiceDto.setInvoiceNum(rs.getLong("invoice_num"));
                invoiceDto.setProviderName(rs.getString("provider_name"));
                invoiceDto.setInvoiceDate(rs.getDate("invoice_date"));
                invoiceDto.setPeopleQuantity(rs.getInt("people_quantity"));
                invoiceDto.setUnitPrice(rs.getDouble("unit_price"));
                invoiceDto.setTax(rs.getInt("tax"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el total de asegurados: " + e.getMessage());
        }
        return invoices;
    }

    // Insertar el Factura al db
    private void insertDataInvoiceToDB(String provider) throws SQLException {
        int total = getTotalPeopleByProvider(provider);
        try (Connection con = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            String query = "INSERT INTO mm_invoice (invoice_num, invoice_date, provider_name, people_quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps;
            ps = con.prepareStatement(query);
            ps.setLong(1, 00000000001);
            ps.setDate(2, new java.sql.Date(new Date().getTime()));
            ps.setString(3, provider);
            ps.setInt(4, total);
            ps.setDouble(5, 20.3);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al obtener el total de asegurados: " + e.getMessage());
        }
    }

    // Devuelve el numero total de customer de cada Provider
    private int getTotalPeopleByProvider(String provider) throws SQLException {
        int totalPeople = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DriverManager.getConnection(DBUrl, DBUser, DBPassword);) {
            String query = "SELECT COUNT(*) AS total_personas FROM mm_interfacecustomers WHERE id_pre = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, provider);
            rs = ps.executeQuery();
            while (rs.next()) {
                totalPeople = rs.getInt("total_personas");
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar la query en getTotalPeopleByProvider: " + e.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return totalPeople;
    }

    @Override
    public void processInfo(String source) {
        try {
            switch (source) {
                case Constantes.C_SOURCE_PARTS:
                    procesarParts();
                    break;
                case Constantes.C_SOURCE_CUSTUMERS:
                    procesarCustomers();
                    break;
                case Constantes.C_SOURCE_VEHICLES:
                    procesarVehicles();
                    break;
                default:
                    // 处理未匹配的情况 manaje caso que no match
            }
        } catch (Exception e1) {
            System.err.println("GetPartsTask Class Error");
        }
    }

    // Wheels--> date format
    private static Date getDateFormatMotoMami(String fecha) {
        Date WrongDate = null;
        Date DateReturn = null;
        try {
            DateReturn = new SimpleDateFormat("dd/mm/yyyy").parse(fecha);
        } catch (ParseException e1) {
            System.err.println("Problema al convertir fecha " + fecha);
            try {
                String[] fechaGuiones = fecha.split("-");
                if (fechaGuiones.length != 3) {
                    System.err.println("Falta el campo de la fecha // Formato error");
                    // System.exit(1);
                } else {
                    WrongDate = new SimpleDateFormat("dd/mm/yyyy")
                            .parse(fechaGuiones[0] + "/" + fechaGuiones[1] + "/" + fechaGuiones[2]);
                    return WrongDate;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            e1.getStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.getStackTrace();
        }
        return DateReturn;
    }

    // Leer los Datos de Parts
    private void readFileInfoParts() {
        ArrayList<PartDto> listPartDtos = new ArrayList<>();
        String[] splitProviders = providers.split(";");
        String[] splitInsurance = parts_providers.split(";");
        String[] splitExtention = extencion.split(";");
        String filePath = inFilePath + "/" + splitProviders[0] + "/" + splitInsurance[0] + "." + splitExtention[0];
        System.out.println(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            int numlineas = 0;
            while ((linea = br.readLine()) != null) {
                if (numlineas == 0) {
                    numlineas++;
                    continue;
                }
                PartDto partDto = new PartDto();
                linea += ";";
                String[] partes = linea.split(";");
                partDto.setDatePartExternal(getDateFormatMotoMami(partes[0].trim()));
                partDto.setdni(partes[1].trim());
                partDto.setDescriptionPartExternal(partes[2].trim());
                partDto.setCodeDamageExternal(partes[3].trim().toUpperCase());
                partDto.setIdExternal(partes[4].trim());
                partDto.setVehicleID(partes[5]);
                listPartDtos.add(partDto);

                ObjectMapper mapper = new ObjectMapper();
                String partJsonString = mapper.writeValueAsString(partDto);
                LocalDate today = LocalDate.now();
                java.sql.Date sqlDate = java.sql.Date.valueOf(today);

                System.out.println("Checking if part exists: " + partes[4].trim());
                if (!existInfoParts(partes[4].trim())) {
                    System.out.println("Inserting new part: " + partes[4].trim());
                    insertPartIntoDatabase(splitProviders[0], partJsonString, sqlDate, partes[4].trim(), "NEW",
                            listPartDtos);
                    listPartDtos.clear();
                } else {
                    if (!existJsonParts(partJsonString)) {
                        System.out.println("Inserting new part: " + partes[4].trim());
                        insertPartIntoDatabase(splitProviders[0], partJsonString, sqlDate, partes[4].trim(), "UPD",
                                listPartDtos);
                        listPartDtos.clear();
                    } else {
                        // No hace nada porque id external y json son iguales
                        System.out.println("No hay nada nuevo");
                        // Pero hay que eliminar la lista sino el siguiente loop va tener 2 record
                        listPartDtos.clear();

                    }
                }
                numlineas++;
            }
        } catch (IOException e) {
            System.err.println("Input output stream error");
        } catch (Exception e) {
            System.err.println("GetPartsTask Class Error");
        }
    }

    // Leer los datos de Custumers
    private void readFileInfoCustumers() throws FileNotFoundException, IOException {
        String[] splitProviders = providers.split(";");
        String[] splitInsurance = customers_providers.split(";");
        String[] splitExtention = extencion.split(";");
        String filePath = inFilePath + "/" + splitProviders[0] + "/" + splitInsurance[0] + "." + splitExtention[0];
        System.out.println(filePath);
        List<CustomerDto> listCustomerDtos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            int numlineas = 0;
            while ((linea = br.readLine()) != null) {
                if (numlineas == 0) {
                    numlineas++;
                    continue;
                }
                if (numlineas > 0) {
                    CustomerDto customerDto = new CustomerDto();
                    linea += ";";
                    String[] customerInfo = linea.split(";");
                    String direccionfield = customerInfo[7].trim();
                    String[] dirFields = direccionfield.split(",");
                    customerDto.setDNI(customerInfo[0].trim());
                    customerDto.setNombre(customerInfo[1].trim().toUpperCase());
                    customerDto.setApellido1(customerInfo[2].trim().toUpperCase());
                    customerDto.setApellido2(customerInfo[3].trim().toUpperCase());
                    customerDto.setCorreo(customerInfo[4].trim().toUpperCase());
                    customerDto.setTelefono(customerInfo[5].trim());
                    customerDto.setFechaNacimiento(getDateFormatMotoMami(customerInfo[6].trim()));

                    // Direccion
                    DireccionDto direccion = new DireccionDto();
                    direccion.setCodigoPostal(dirFields[0].trim());
                    direccion.setTipoVia(dirFields[1].trim().toUpperCase());
                    direccion.setCiudad(dirFields[2].trim().toUpperCase());
                    direccion.setNumero(Integer.parseInt(dirFields[3].trim()));

                    customerDto.setDireccionDto(direccion);
                    customerDto.setSexo(customerInfo[8].trim().toUpperCase());
                    listCustomerDtos.add(customerDto);
                    System.out.println("Custumers data: " + customerDto);

                    String customerJsonString = getObjectMapper().toJson(listCustomerDtos.get(0));
                    InputStream targetStream = new ByteArrayInputStream(
                            customerJsonString.getBytes(StandardCharsets.UTF_8));
                    LocalDate today = LocalDate.now();
                    java.sql.Date sqlDate = java.sql.Date.valueOf(today);

                    if (!existCustomer(customerInfo[0].trim())) {
                        try {
                            insertCustomerIntoDatabase(splitProviders[0], customerJsonString, sqlDate,
                                    customerInfo[0].trim(),
                                    "NEW");
                            listCustomerDtos.clear();
                        } catch (SQLException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (!existInfoJsonCustomer(customerJsonString)) {
                        try {
                            insertCustomerIntoDatabase(splitProviders[0], customerJsonString, sqlDate,
                                    customerInfo[0].trim(),
                                    "UPD");
                            listCustomerDtos.clear();
                        } catch (SQLException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        // No hace nada
                        System.out.println("NO hace nada porque no hay nada nuevo");
                        listCustomerDtos.clear();
                    }
                }
                numlineas++;
            }
        }
    }

    // Leer datos de Vehicle
    private void readFileInfoVehicle() {
        String[] splitProviders = providers.split(";");
        String[] splitInsurance = vehicle_providers.split(";");
        String[] splitExtention = extencion.split(";");
        String filePath = inFilePath + "/" + splitProviders[0] + "/" + splitInsurance[0] + "." + splitExtention[0];
        System.out.println(filePath);
        List<VehicleDto> listVehicleDtos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            String dniAnteriol = "";
            int numlineas = 0;
            // customerVehicleDto.setCustomer_DNICIF(partesVehicles[1]);
            while ((linea = br.readLine()) != null) {
                if (numlineas == 0) { // 跳过第一行（通常是标题行）
                    numlineas++;
                    continue;
                }
                VehicleDto vehicleDto = new VehicleDto();
                CustomerVehicleDto customerVehicleDto = new CustomerVehicleDto();
                String[] partesVehicles = linea.split(";");
                if (partesVehicles.length < 10) {
                    // 数组长度不足，跳过这行数据
                    continue;
                }
                if (dniAnteriol.isEmpty() || dniAnteriol != partesVehicles[0]) {
                    dniAnteriol = partesVehicles[0];
                }
                customerVehicleDto.setTypeCustomer(partesVehicles[0].toUpperCase().trim());
                vehicleDto.setDniCif(partesVehicles[1].toUpperCase().trim());
                vehicleDto.setIdVehicle(partesVehicles[2].trim());
                vehicleDto.setIdVehicleExternal(partesVehicles[3].trim().toUpperCase());
                vehicleDto.setNumberPlate(partesVehicles[4].trim().toUpperCase());
                vehicleDto.setTypeVehicle(partesVehicles[5].trim().toUpperCase());
                vehicleDto.setBrand(partesVehicles[6].trim().toUpperCase());
                vehicleDto.setModel(partesVehicles[7].trim().toUpperCase());
                vehicleDto.setColor(partesVehicles[8].trim().toUpperCase());
                vehicleDto.setSerialNumber(partesVehicles[9].trim().toUpperCase());
                listVehicleDtos.add(vehicleDto);
                System.out.println("Vehicle data: " + vehicleDto);
                String vehicleJsonString = getObjectMapper().writeValueAsString(vehicleDto);
                System.out.println(vehicleJsonString);
                LocalDate today = LocalDate.now();
                java.sql.Date sqlDate = java.sql.Date.valueOf(today);
                if (!existInfoVehicle(partesVehicles[3].trim())) {
                    insertVehicleIntoDatabase(splitProviders[0], vehicleJsonString, sqlDate,
                            partesVehicles[3].trim().toUpperCase(), "NEW");
                    listVehicleDtos.clear();
                } else if (!existJsonVehicle(vehicleJsonString)) {
                    insertVehicleIntoDatabase(splitProviders[0], vehicleJsonString, sqlDate,
                            partesVehicles[3].trim().toUpperCase(), "UPD");
                    listVehicleDtos.clear();
                } else {
                    // NO hace nada
                    System.out.println("nada nuevo, nahhh");
                    listVehicleDtos.clear();
                }

                numlineas++;
            }

        } catch (IOException e) {
            System.err.println("Input output stream error");
            e.printStackTrace();
        } catch (Exception e1) {
            System.err.println("GetPartsTask Class Error");
            e1.printStackTrace();
        }
    }

    // Si existe fichero json al tabla INT. Customer
    public boolean existInfoJsonCustomer(String p_json) {
        // MotoMami es el nombre de base de datos
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            if (connection == null) {
                System.err.println("Connection failed");
                return false;
            } else {
                System.out.println("Connection Success");
                // Prepared Statement
                String preparedStatementSQL = "SELECT COUNT(*) as numCustom FROM MM_InterfaceCustomers WHERE contentJson = ?;";
                try (PreparedStatement preparedStatement = connection.prepareStatement(preparedStatementSQL)) {
                    preparedStatement.setString(1, p_json);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            int count = resultSet.getInt("numCustom");
                            System.out.println(count);
                            resultSet.close();
                            return count > 0; // return true if count es mayor de 0, sino return false
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    // Si existe el id external al tabla Customer
    public boolean existCustomer(String idExternal) {
        // MotoMami es el nombre de base de datos
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            if (connection == null) {
                System.err.println("Connection failed");
                return false;
            } else {
                System.out.println("Connection Success");
                // Prepared Statement
                String preparedStatementSQL = "SELECT COUNT(*) as numCustom FROM MM_InterfaceCustomers WHERE id_externa = ?;";
                try (PreparedStatement preparedStatement = connection.prepareStatement(preparedStatementSQL)) {
                    preparedStatement.setString(1, idExternal);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            int count = resultSet.getInt("numCustom");
                            System.out.println(count);
                            resultSet.close();
                            return count > 0; // return true if count es mayor de 0, sino return false
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    // Si existe fichero json al tabla INT. Vehicle
    public boolean existJsonVehicle(String p_json) {
        // MotoMami es el nombre de base de datos
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            if (connection == null) {
                System.err.println("Connection failed");
                return false;
            } else {
                System.out.println("Connection Success");
                // Prepared Statement
                String preparedStatementSQL = "SELECT COUNT(*) as numVehicle FROM MM_Interfacevehicles WHERE contentJson = ?;";
                try (PreparedStatement preparedStatement = connection.prepareStatement(preparedStatementSQL)) {
                    preparedStatement.setString(1, p_json);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            int count = resultSet.getInt("numVehicle");
                            System.out.println(count);
                            resultSet.close();
                            return count > 0; // return true if count es mayor de 0, sino return false
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    // Si existe id external al tabla INT. Vehicle
    public boolean existInfoVehicle(String idExterna) {
        // MotoMami es el nombre de base de datos
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            if (connection == null) {
                System.err.println("Connection failed");
                return false;
            } else {
                System.out.println("Connection Success");
                // Prepared Statement
                String preparedStatementSQL = "SELECT COUNT(*) as numVehicle FROM MM_Interfacevehicles WHERE id_externa = ?;";
                try (PreparedStatement preparedStatement = connection.prepareStatement(preparedStatementSQL)) {
                    preparedStatement.setString(1, idExterna);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            int count = resultSet.getInt("numVehicle");
                            System.out.println(count);
                            resultSet.close();
                            return count > 0; // return true if count es mayor de 0, sino return false
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    // Si existe fichero json al tabla INT. Parts
    public boolean existJsonParts(String p_json) {
        // MotoMami es el nombre de base de datos
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            if (connection == null) {
                System.err.println("Connection failed");
                return false;
            } else {
                System.out.println("Connection Success");
                // Prepared Statement
                String preparedStatementSQL = "SELECT COUNT(*) as numParts FROM MM_Interfaceparts WHERE contentJson = ?;";
                try (PreparedStatement preparedStatement = connection.prepareStatement(preparedStatementSQL)) {
                    preparedStatement.setString(1, p_json);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            int count = resultSet.getInt("numParts");
                            System.out.println(count);
                            resultSet.close();
                            return count > 0; // return true if count es mayor de 0, sino return false
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    // Si existe id External al tabla INT. Parts
    public boolean existInfoParts(String idExternal) {
        // MotoMami es el nombre de base de datos
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            if (connection == null) {
                System.err.println("Connection failed");
                return false;
            } else {
                System.out.println("Connection Success");
                // Prepared Statement
                String preparedStatementSQL = "SELECT COUNT(*) as numParts FROM MM_Interfaceparts WHERE id_externa = ?;";
                try (PreparedStatement preparedStatement = connection.prepareStatement(preparedStatementSQL)) {
                    preparedStatement.setString(1, idExternal);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            int count = resultSet.getInt("numParts");
                            System.out.println(count);
                            resultSet.close();
                            return count > 0; // return true if count es mayor de 0, sino return false
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    // Insertar los datos de ficheto .dat al database interface Parts
    private void insertPartIntoDatabase(String provider, String targetStream, java.sql.Date sqlDate,
            String idExternal, String operation, ArrayList<PartDto> listPartDtos) throws SQLException, IOException {
        String PreparedStatementSQL = "INSERT INTO mm_interfaceparts (id_pre, contentJson, creation_date, last_update_date, created_by, updated_by, estatus, operacion, id_externa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            PreparedStatement ps = connection.prepareStatement(PreparedStatementSQL);
            ps.setString(1, provider);
            ps.setString(2, targetStream);
            ps.setDate(3, sqlDate);
            ps.setDate(4, sqlDate);
            ps.setString(5, "CHEN");
            ps.setString(6, "CHEN");
            ps.setString(7, Constantes.NoProcesado);
            ps.setString(8, operation);
            ps.setString(9, idExternal); // 设置 id_external
            ps.executeUpdate();
        } finally {
            listPartDtos.clear();
        }
    }

    // Insertar los datos de ficheto .dat al database interface Customers
    private void insertCustomerIntoDatabase(String provider, String targetStream, java.sql.Date sqlDate,
            String idExternal, String operation) throws SQLException, IOException {
        String PreparedStatementSQL = "INSERT INTO MM_InterfaceCustomers (id_pre, contentJson, creation_date, last_update_date, created_by, updated_by, estatus, id_externa, operacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            PreparedStatement ps = connection.prepareStatement(PreparedStatementSQL);
            ps.setString(1, provider);
            ps.setString(2, targetStream);
            ps.setDate(3, sqlDate);
            ps.setDate(4, sqlDate);
            ps.setString(5, "CHEN");
            ps.setString(6, "CHEN");
            ps.setString(7, Constantes.NoProcesado);
            ps.setString(8, idExternal);
            ps.setString(9, operation);
            ps.executeUpdate();
        } finally {
            // targetStream.close();
        }
    }

    // Insertar los datos de ficheto .dat al database interface Vehicles
    private void insertVehicleIntoDatabase(String provider, String targetStream, java.sql.Date sqlDate,
            String idVehicleExternal, String operation) throws SQLException, IOException {
        String PreparedStatementSQL = "INSERT INTO mm_interfacevehicles (id_pre, contentJson, creation_date, last_update_date, created_by, updated_by, estatus, operacion, id_externa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser,
                DBPassword)) {
            PreparedStatement ps = connection.prepareStatement(PreparedStatementSQL);
            ps.setString(1, provider);
            ps.setString(2, targetStream);
            ps.setDate(3, sqlDate);
            ps.setDate(4, sqlDate);
            ps.setString(5, "CHEN");
            ps.setString(6, "CHEN");
            ps.setString(7, Constantes.NoProcesado);
            ps.setString(8, operation);
            ps.setString(9, idVehicleExternal);
            ps.executeUpdate();
        }
    }

    // Procesar el Contentjson e insertar el contentJson(Int table) to customer //
    public void procesarCustomers() {
        try (Connection conn = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            ArrayList<CustomerDto> listCustomers = getCustomerInfoWithStatus("N".toUpperCase());
            String sql = "INSERT INTO mm_customer (dni, nombre, apellido1, apellido2, email, fecha_nacimiento, telefono, sexo, direccionId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (CustomerDto customer : listCustomers) {
                    DireccionDto direccion = customer.getDireccion();
                    int addressId = insertAddres(direccion);
                    if (addressId != -1) {
                        ps.setString(1, customer.getDNI());
                        ps.setString(2, customer.getNombre());
                        ps.setString(3, customer.getApellido1());
                        ps.setString(4, customer.getApellido2());
                        ps.setString(5, customer.getCorreo());
                        ps.setDate(6, new java.sql.Date(customer.getFechaNacimiento().getTime()));
                        ps.setString(7, customer.getTelefono());
                        ps.setString(8, customer.getSexo());
                        ps.setInt(9, addressId);
                        ps.executeUpdate();
                    }
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    // Procesar el Contentjson e insertar el contentJson(Int table) to Vehicle table
    public void procesarVehicles() {
        try (Connection conn = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            ArrayList<VehicleDto> listVehicles = getVehicleInfoWithStatus("N".toUpperCase());
            String sql = "INSERT INTO mm_vehicle (customerDni, idVehicleExternal, numberPlate, vehicleType, brand, model, color, serialNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (VehicleDto vehicle : listVehicles) {
                    ps.setString(1, vehicle.getDniCif());
                    ps.setString(2, vehicle.getIdVehicleExternal());
                    ps.setString(3, vehicle.getNumberPlate());
                    ps.setString(4, vehicle.getTypeVehicle());
                    ps.setString(5, vehicle.getBrand());
                    ps.setString(6, vehicle.getModel());
                    ps.setString(7, vehicle.getColor());
                    ps.setString(8, vehicle.getSerialNumber());
                    ps.executeUpdate();

                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    // Procesar el Contentjson e insertar el contentJson(Int table) to Parts table
    public void procesarParts() {
        try (Connection conn = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            ArrayList<PartDto> listParts = getPartsInfoWithStatus("N".toUpperCase());
            String sql = "INSERT INTO mm_part (vehicleId, identityCode, datePartExternal, descriptionPartExternal, codeDamageExternal, idExternal) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (PartDto part : listParts) {
                    ps.setString(1, part.getVehicleID());
                    ps.setString(2, part.getdni());
                    ps.setDate(3, new java.sql.Date(part.getDatePartExternal().getTime()));
                    ps.setString(4, part.getDescriptionPartExternal());
                    ps.setString(5, part.getCodeDamageExternal());
                    ps.setString(6, part.getIdExternal());
                    // 执行插入
                    ps.executeUpdate();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    // Devuelve la listado
    public ArrayList<CustomerDto> getCustomerInfoWithStatus(String pStatus) throws SQLException {
        ArrayList<CustomerDto> customers = new ArrayList<CustomerDto>();
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser,
                DBPassword)) {
            Gson gson = getObjectMapper();
            String query = "SELECT contentJson, operacion FROM mm_interfacecustomers WHERE estatus = ?;";
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = connection.prepareStatement(query);
            ps.setString(1, pStatus);
            rs = ps.executeQuery();
            while (rs.next()) {
                CustomerDto customer;
                String jsonCustomer = rs.getString("contentJson");
                String operation = rs.getString("operacion");
                customer = gson.fromJson(jsonCustomer, CustomerDto.class);
                customers.add(customer);
            }
        } catch (

        SQLException e) {
            System.err.println("Error executing SELECT query: " + e.getMessage());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return customers;
    }

    // Devuelve la listado
    public ArrayList<PartDto> getPartsInfoWithStatus(String pStatus) throws SQLException {
        ArrayList<PartDto> parts = new ArrayList<PartDto>();
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser,
                DBPassword)) {
            Gson gson = getObjectMapper();
            String query = "SELECT contentJson, operacion FROM mm_interfaceparts WHERE estatus = ?;";
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = connection.prepareStatement(query);
            ps.setString(1, pStatus.toUpperCase());
            rs = ps.executeQuery();
            while (rs.next()) {
                PartDto part;
                String jsonPart = rs.getString("contentJson");
                String operation = rs.getString("operacion");
                part = gson.fromJson(jsonPart, PartDto.class);
                parts.add(part);
            }
        } catch (

        SQLException e) {
            System.err.println("Error executing SELECT query: " + e.getMessage());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return parts;
    }

    // Devuelve la listado
    public ArrayList<VehicleDto> getVehicleInfoWithStatus(String pStatus) {
        ArrayList<VehicleDto> vehicles = new ArrayList<VehicleDto>();
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser,
                DBPassword)) {
            Gson gson = getObjectMapper();
            String query = "SELECT contentJson, operacion FROM mm_interfacevehicles WHERE estatus = ?;";
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = connection.prepareStatement(query);
            ps.setString(1, pStatus);
            rs = ps.executeQuery();
            while (rs.next()) {
                VehicleDto vehicle;
                String jsonCustomer = rs.getString("contentJson");
                String operation = rs.getString("operacion");
                vehicle = gson.fromJson(jsonCustomer, VehicleDto.class);
                vehicles.add(vehicle);
            }
        } catch (

        SQLException e) {
            System.err.println("Error executing SELECT query: " + e.getMessage());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return vehicles;
    }

    // Devuelve el int de direccion de customer
    public int insertAddres(DireccionDto direccion) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUser, DBPassword)) {
            String selectQuery = "SELECT id FROM mm_address \n" +
                    "             WHERE upper(calle) = upper(?) \n" +
                    "            AND upper(numero) = upper(?) \n" +
                    "            AND upper(ciudad) = upper(?) \n" +
                    "            AND upper(codPostal) = upper(?);";
            PreparedStatement selectPs = connection.prepareStatement(selectQuery);
            selectPs.setString(1, direccion.getTipoVia());
            selectPs.setInt(2, direccion.getNumero());
            selectPs.setString(3, direccion.getCiudad());
            selectPs.setString(4, direccion.getCodigoPostal());

            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {
                // Address already exists, return its ID
                return rs.getInt("id");
            } else {
                // Address does not exist, insert it
                String insertQuery = "INSERT INTO mm_address (calle, numero, ciudad, codPostal) VALUES (?,?,?,?)";
                PreparedStatement insertPs = connection.prepareStatement(insertQuery,
                        Statement.RETURN_GENERATED_KEYS);
                insertPs.setString(1, direccion.getTipoVia());
                insertPs.setInt(2, direccion.getNumero());
                insertPs.setString(3, direccion.getCiudad());
                insertPs.setString(4, direccion.getCodigoPostal());

                int affectedRows = insertPs.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Inserting address failed, no rows affected.");
                }

                try (ResultSet generatedKeys = insertPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Inserting address failed, no ID obtained.");
                    }
                }
            }
        }
    }
}
