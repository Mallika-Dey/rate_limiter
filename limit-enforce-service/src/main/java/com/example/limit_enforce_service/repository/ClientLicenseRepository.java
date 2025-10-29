package com.example.limit_enforce_service.repository;


import com.example.limit_enforce_service.entities.ClientLicenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientLicenseRepository extends JpaRepository<ClientLicenseEntity, String> {
}