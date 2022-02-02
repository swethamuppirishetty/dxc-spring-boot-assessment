package com.dxc.personrestapi.repository;

import com.dxc.personrestapi.model.Person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Integer>{
    
}
