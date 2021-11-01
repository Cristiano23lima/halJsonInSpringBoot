package com.cristiano.demoHal.controller;

import org.hibernate.EntityMode;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.cristiano.demoHal.models.Family;
import com.cristiano.demoHal.models.Person;
import com.cristiano.demoHal.repository.*;

@RestController
@RequestMapping(produces = "application/hal+json", value = "/person")
@RequiredArgsConstructor
public class PersonController {
    private final PersonRepository personRepository;

    @GetMapping
    public ResponseEntity<List<EntityModel<Person>>> all() {
        List<Person> persons = this.personRepository.findAll();
        List<EntityModel<Person>> personsModel = persons.stream().map(person -> {
            EntityModel<Person> personModel = EntityModel.of(person);
            // ADD _links, informando o link que foi acessado
            personModel.add(Link.of(ServletUriComponentsBuilder.fromCurrentRequest().toUriString()));
            return personModel;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(personsModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Person>> getById(@PathVariable Integer id) {
        EntityModel<Person> personModel = EntityModel.of(this.personRepository.findById(id).get());
        personModel.add(Link.of(ServletUriComponentsBuilder.fromCurrentRequest().toUriString()));
        personModel
                .add(Link.of("/family/1").withRel(LinkRelation.of("family")).expand(personModel.getContent().getId()));
        return ResponseEntity.ok(personModel);
    }
}
