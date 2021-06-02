package com.example.springboot_test.controllers;

import com.example.springboot_test.models.Cuenta;
import com.example.springboot_test.models.TransaccionDTO;
import com.example.springboot_test.services.CuentaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.springboot_test.Datos.crearCuenta001;
import static com.example.springboot_test.Datos.crearCuenta002;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService cuentaService;

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void testDetalle() throws Exception, JsonProcessingException {
        // Given
        when(cuentaService.findById(1L)).thenReturn(crearCuenta001().orElseThrow());
        //When
        mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
        //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Ludwig"))
                .andExpect(jsonPath("$.saldo").value("1000"));
    }

    @Test
    void testTransferir() throws Exception {
        //Given
        TransaccionDTO dto = new TransaccionDTO();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        System.out.println(mapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "Ok");
        response.put("mensaje", "Transferencia Realizada con Exito");
        response.put("transaccion", dto);

        System.out.println(mapper.writeValueAsString(response));

        //When
        mvc.perform(post("/api/cuentas/transferir").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(dto)))
        //Then
        .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.mensaje").value("Transferencia Realizada con Exito"))
            .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(dto.getCuentaOrigenId()))
        .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void testListar() throws Exception {
        //Given
        List<Cuenta> cuentas = Arrays.asList(crearCuenta001().orElseThrow(),
                crearCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);
        //When
        mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
        //Then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].persona").value("Ludwig"))
            .andExpect(jsonPath("$[1].persona").value("Amadeus"))
            .andExpect(jsonPath("$[0].saldo").value("1000"))
            .andExpect(jsonPath("$[1].saldo").value("2000"))
            .andExpect(jsonPath("$").value(hasSize(2)))
            .andExpect(content().json(mapper.writeValueAsString(cuentas)));
        verify(cuentaService).findAll();
    }

    @Test
    void testGuardar() throws Exception {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        when(cuentaService.save(any())).then( invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });
        //When
        mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(cuenta)))
                //Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.persona", is("Pepe")))
                .andExpect(jsonPath("$.saldo", is(3000)));
        verify(cuentaService).save(any());
    }
}