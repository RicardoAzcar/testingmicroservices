package com.minsait.testingMicroservices.services;

import com.minsait.testingMicroservices.clients.PersonaClient;
import com.minsait.testingMicroservices.models.Banco;
import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.respositories.BancoRepository;
import com.minsait.testingMicroservices.respositories.CuentaRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaServiceImpl implements CuentaService{
    @Autowired
    private CuentaRespository cuentaRespository;
    @Autowired
    private BancoRepository bancoRepository;
    //@Autowired
    //private PersonaClient client;

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaRespository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long idCuenta) {
        Cuenta cuenta=cuentaRespository.findById(idCuenta).orElseThrow();
        //cuenta.setPersona(client.findById(idCuenta).getNombre());
        return cuenta;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer revisarTotalTransferencias(Long idBanco) {
        Banco banco=bancoRepository.findById(idBanco).orElseThrow();
        return banco.getTotalTransferencias();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal revisarSaldo(Long idCuenta) {
        return cuentaRespository.findById(idCuenta).orElseThrow().getSaldo();
    }

    @Override
    @Transactional
    public void transferir(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto, Long idBanco) {
        Cuenta origen=cuentaRespository.findById(idCuentaOrigen).orElseThrow();
        origen.retirar(monto);
        cuentaRespository.save(origen);

        Cuenta destino=cuentaRespository.findById(idCuentaDestino).orElseThrow();
        destino.depositar(monto);
        cuentaRespository.save(destino);

        Banco banco=bancoRepository.findById(idBanco).orElseThrow();
        int totalTransferencias=banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totalTransferencias);
        bancoRepository.save(banco);
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        return cuentaRespository.save(cuenta);
    }

    @Override
    @Transactional
    public boolean deleteById(Long idCuenta) {
        Optional<Cuenta> cuenta=cuentaRespository.findById(idCuenta);
        if (cuenta.isPresent()){
            cuentaRespository.deleteById(idCuenta);
            return true;
        }
        return false;
    }
}
