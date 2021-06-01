package com.example.springboot_test;

import com.example.springboot_test.models.Banco;
import com.example.springboot_test.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
    //public static final Cuenta CUENTA_001 = new Cuenta(1L, "Ludwig", new BigDecimal("1000"));
    //public static final Cuenta CUENTA_002 = new Cuenta(2L, "Amadeus", new BigDecimal("2000"));
    //public static final Banco BANCO = new Banco(1L, "Banco Patito", 0);

    public static Optional<Cuenta> crearCuenta001(){
        return Optional.of(new Cuenta(1L, "Ludwig", new BigDecimal("1000")));
    }

    public static Optional<Cuenta> crearCuenta002(){
        return Optional.of(new Cuenta(2L, "Amadeus", new BigDecimal("2000")));
    }

    public static Optional<Banco> crearBanco(){
        return Optional.of(new Banco(1L, "Banco Patito", 0));
    }
}
