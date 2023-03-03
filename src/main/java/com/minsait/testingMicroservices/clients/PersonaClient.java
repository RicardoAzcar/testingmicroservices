package com.minsait.testingMicroservices.clients;

import com.minsait.testingMicroservices.models.Persona;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "msvc-personas", url = "http://localhost:8090/api/v1/personas")
public interface PersonaClient {

    @GetMapping
    List<Persona> findAll();

    @GetMapping("/{id}")
    Persona findById(@PathVariable Long id);

    @GetMapping("/nombre/{nombre}")
    Persona findByNombre(@PathVariable String nombre);
}
