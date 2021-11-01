package com.cristiano.demoHal.repository;

import com.cristiano.demoHal.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
