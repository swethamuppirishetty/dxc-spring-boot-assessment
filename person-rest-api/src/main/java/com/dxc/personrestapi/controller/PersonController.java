package com.dxc.personrestapi.controller;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.dxc.personrestapi.services.PersonService;
import com.dxc.personrestapi.dto.PersonDto;
import com.dxc.personrestapi.model.Person;
import com.dxc.personrestapi.controller.PersonNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org .springframework.validation.FieldError;




@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/person")
    public ResponseEntity<List<Person>> findAllPeople(){
        List<Person> persons=personService.findAllPeople();
        return ResponseEntity.status(HttpStatus.OK).body(persons);

    }

    @PostMapping("/person")
    public ResponseEntity<Person> createPerson(@Valid @RequestBody PersonDto personReqObj){

        Person person = modelMapper.map(personReqObj, Person.class);
        person.setDob(LocalDate.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(person);


    }

    @GetMapping("/person/{id}")
    public Person getPersonById(@PathVariable int id){
        return personService.getPersonById(id)
        .orElseThrow(() -> new PersonNotFoundException(id));
    }

    @PutMapping("/person/{id}")
    public Person updatePerson(@RequestBody Person person,@PathVariable int id){
       return personService.getPersonById(id)
       .map(author -> {
         person.setName(person.getName());
         person.setEmail(person.getEmail());
         person.setDOB(LocalDate.now());
         return personService.save(person);
       })
       .orElseGet(() -> {
         person.setId(id);
         return personService.save(person);
       });
    }

    @DeleteMapping("/person/{id}")
    public void deletePerson(@PathVariable int id){
        personService.deletePerson(id);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult=ex.getBindingResult();
        Map<String,String> errors=new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName =((FieldError) error).getField();
            String errorMessage= error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        errors.put("Message" , "validation failed");
        errors.put("status",HttpStatus.BAD_REQUEST.name());
        errors.put("code", String.valueOf(HttpStatus.BAD_REQUEST.value()));


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

    }
}
