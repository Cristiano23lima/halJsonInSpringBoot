package com.cristiano.demoHal.repository;

import java.util.List;
import java.util.Optional;

import com.cristiano.demoHal.models.Family;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Integer> {
    public List<Family> findByPersonId(Integer id);
}
