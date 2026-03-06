package com.hibernate.repository;

import com.hibernate.entity.Interprete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInterpreteRepository extends JpaRepository<Interprete,Integer> {
}
