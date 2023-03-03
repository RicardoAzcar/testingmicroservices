package com.minsait.testingMicroservices.respositories;

import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CuentaRespository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByPersona(String persona);

    @Query("select c from Cuenta c where c.persona=?1")
    Optional<Cuenta> buscarPorPersona(String persona);//HQL (Hibernate Query Language)
}
