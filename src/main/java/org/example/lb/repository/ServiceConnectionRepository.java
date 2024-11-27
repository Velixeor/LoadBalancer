package org.example.lb.repository;


import org.example.lb.entity.ServiceConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServiceConnectionRepository extends JpaRepository<ServiceConnection, Integer> {
}
