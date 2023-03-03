package com.minsait.testingMicroservices.respositories;

import com.minsait.testingMicroservices.models.Cuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//Pruebas de integracion
@DataJpaTest
class CuentaRespositoryTest {
    @Autowired
    CuentaRespository respository;

    @BeforeEach
    void setUp() {
        //Borrar todo de la base datos
        //Insertar los registros nuevamente
        //Van a estar cambiando los ids
    }

    @Test
    void testFindById(){
        //When
        Optional<Cuenta> cuenta=respository.findById(1L);
        //Then
        assertTrue(cuenta.isPresent());
        assertEquals("Ricardo", cuenta.get().getPersona());
    }

    @Test
    void testFindByPersona(){
        Optional<Cuenta> cuenta=respository.findByPersona("Ricardo");
        assertFalse(cuenta.isEmpty());
        assertEquals(1L, cuenta.get().getId());
    }
    @Test
    void testBuscarPorPersona(){
        Optional<Cuenta> cuenta=respository.buscarPorPersona("Ricardo");
        assertFalse(cuenta.isEmpty());
        assertEquals(1L, cuenta.get().getId());
    }

    @Test
    void testSave(){
        Cuenta cuenta=new Cuenta(null, "Daniel", new BigDecimal(100000));
        Cuenta cuenta2=new Cuenta(null, "Javier", new BigDecimal(100000));
        respository.save(cuenta2);
        Cuenta cuentaGuardada=respository.save(cuenta);

        assertEquals("Daniel", cuentaGuardada.getPersona());
        assertEquals(100000, cuentaGuardada.getSaldo().intValue());
        //assertEquals(3, cuentaGuardada.getId());
    }
}