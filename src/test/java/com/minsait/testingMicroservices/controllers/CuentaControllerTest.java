package com.minsait.testingMicroservices.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minsait.testingMicroservices.exceptions.DineroInsuficienteException;
import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.models.TransferirDTO;
import com.minsait.testingMicroservices.services.CuentaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.mockito.InOrder;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService service;

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper=new ObjectMapper();
    }

    @Test
    void testFindAll() throws Exception {
        //Given
        when(service.findAll()).thenReturn(List.of(Datos.crearCuenta1().get(),Datos.crearCuenta2().get()));
        //When
        mvc.perform(get("/api/v1/cuentas/listar").contentType(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(jsonPath("$[0].persona").value("Ricardo"))
                .andExpect(jsonPath("$[1].persona").value("Yamani"));
    }

    @Test
    void testFindById() throws Exception{
        when(service.findById(1L)).thenReturn(Datos.crearCuenta1().get());

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/cuentas/listar/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona", Matchers.is("Ricardo")))
                .andExpect(jsonPath("$.saldo").value("1000"));
    }

    @Test
    void testFindByIdIfDoesntExist() throws Exception {
        when(service.findById(1L)).thenThrow(NoSuchElementException.class);

        mvc.perform(get("/api/v1/cuentas/listar/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGuardar() throws Exception{
        //Given
        Cuenta cuenta=new Cuenta(null, "Daniel", new BigDecimal(100000));
        when(service.save(any(Cuenta.class))).then(invocationOnMock -> {
            Cuenta cuentaTemporal=invocationOnMock.getArgument(0);
            cuentaTemporal.setId(3L);
            return cuentaTemporal;
        });

        //When
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/cuentas/guardar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
        //Then
                .andExpectAll(
                        jsonPath("$.id", Matchers.is(3)),
                        jsonPath("$.persona", Matchers.is("Daniel")),
                        jsonPath("$.saldo", Matchers.is(100000)),
                        status().isCreated()
                );
    }
    //Realizar las pruebas unitarias de los demas controladores
    //guardar OK
    //actualizar

    @Test
    void testActualizar() throws Exception{
        Cuenta cuenta=new Cuenta(null, "Francisco", new BigDecimal(2000));
        when(service.findById(1L)).thenReturn(Datos.crearCuenta1().get());
        when(service.save(any())).then(invocationOnMock -> {
            Cuenta cuentaActualizada=cuenta;
            cuentaActualizada.setId(1L);
            return  cuentaActualizada;
        });

        mvc.perform(put("/api/v1/cuentas/actualizar/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.persona").value("Francisco"))
                .andExpect(jsonPath("$.saldo", Matchers.is(2000)))
                .andExpect(jsonPath("$.id", Matchers.is(1)));

        InOrder order = inOrder(service);
        order.verify(service, calls(1)).findById(1L);
        order.verify(service, calls(1)).save(any());
    }

    @Test
    void testActualizarSiNoExiste() throws Exception {
        Cuenta cuenta=new Cuenta(null, "Francisco", new BigDecimal(2000));
        when(service.findById(1L)).thenThrow(NoSuchElementException.class);

        mvc.perform(put("/api/v1/cuentas/actualizar/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
                .andExpect(status().isNotFound());
        verify(service, only()).findById(1L);
        verify(service, times(0)).save(any());
    }
    //Borrar
    @Test
    void testBorrar() throws Exception {
        when(service.deleteById(1L)).thenReturn(true);

        mvc.perform(delete("/api/v1/cuentas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testBorrarSiNoExiste() throws Exception{
        when(service.deleteById(1L)).thenReturn(false);

        mvc.perform(delete("/api/v1/cuentas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTransferencia() throws Exception{
        //Given

        TransferirDTO dto=new TransferirDTO();
        dto.setIdCuentaOrigen(1L);
        dto.setIdCuentaDestino(2L);
        dto.setMonto(new BigDecimal(1000));
        dto.setIdBanco(1L);

        Map<String, Object> response=new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("peticion", dto);
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito");

        //When
        mvc.perform(post("/api/v1/cuentas").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(response))
                );
    }

    @Test
    void testTransferirDineroInsuficienteException() throws Exception {
        BigDecimal monto=new BigDecimal(1001);
        Cuenta cuenta=Datos.crearCuenta1().get();
        //DineroInsuficienteException exception=new DineroInsuficienteException("Ya no tienes dinero para transferir");
        Exception exception=assertThrows(DineroInsuficienteException.class, () -> cuenta.retirar(monto));
        doThrow(exception).when(service).transferir(anyLong(), anyLong(), any(), anyLong());

        TransferirDTO dto=new TransferirDTO();
        dto.setIdCuentaOrigen(1L);
        dto.setIdCuentaDestino(2L);
        dto.setMonto(monto);
        dto.setIdBanco(1L);

        Map<String, Object> response=new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("peticion", dto);
        response.put("status", "OK");
        response.put("mensaje", exception.getMessage());

        mvc.perform(post("/api/v1/cuentas").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(response)),
                        jsonPath("$.mensaje").value(exception.getMessage())
                );
    }
    //Realizar la prueba del metodo de transferir cuando no se encuentre algun Id dado
    @Test
    void testTransferirNotFoundException() throws Exception {
        BigDecimal monto=new BigDecimal(1001);
        doThrow(NoSuchElementException.class).when(service).transferir(anyLong(), anyLong(), any(), anyLong());

        TransferirDTO dto=new TransferirDTO();
        dto.setIdCuentaOrigen(1L);
        dto.setIdCuentaDestino(2L);
        dto.setMonto(monto);
        dto.setIdBanco(1L);

        Map<String, Object> response=new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("peticion", dto);
        response.put("status", "Not Found");
        response.put("mensaje", "Not found");

        mvc.perform(post("/api/v1/cuentas").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(response))
                );
    }
}