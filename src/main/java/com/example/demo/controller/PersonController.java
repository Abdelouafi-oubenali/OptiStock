//package com.example.demo.controller;
//
//import com.example.demo.entity.Person;
//import com.example.demo.service.PersonService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/persons")
//public class PersonController {
//
//    private final PersonService service;
//
//    public PersonController(PersonService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public Person addPerson(@RequestBody Person person)
//    {
//        return service.save(person);
//    }
//
//    @GetMapping
//    public List<Person> getAll() {
//        return service.findAll();
//    }
//}
