package com.minsait.testingMicroservices.respositories;

import com.minsait.testingMicroservices.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
}
