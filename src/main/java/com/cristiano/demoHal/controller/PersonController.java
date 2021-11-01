package com.cristiano.demoHal.controller;

import org.hibernate.EntityMode;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.cristiano.demoHal.controller.resources.PersonModel;
import com.cristiano.demoHal.models.Family;
import com.cristiano.demoHal.models.Person;
import com.cristiano.demoHal.repository.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = "application/hal+json", value = "/person")
@RequiredArgsConstructor
public class PersonController {
    private final PersonRepository personRepository;
    private final FamilyRepository familyRepository;

    @GetMapping
    public ResponseEntity<CollectionModel<PersonModel>> all() {
        List<Person> persons = this.personRepository.findAll();
        List<PersonModel> personModels = new ArrayList<>();

        for (Person person : persons) {
            PersonModel personModel = PersonModel.builder().setFirstName(person.getFirstName()).setId(person.getId())
                    .setLastName(person.getLastName()).setProfession(person.getProfession())
                    .setSalary(person.getSalary()).build();

            String personId = personModel.getId().toString();
            Link selfLink = linkTo(PersonController.class).slash(personId).withSelfRel();
            personModel.add(selfLink);

            if (person.getFamily().size() > 0) {
                Link familyLink = linkTo(methodOn(PersonController.class).allFamily(Integer.parseInt(personId)))
                        .withRel("allFamily");
                personModel.add(familyLink);
            }

            personModels.add(personModel);
        }

        // Aqui ele cria o link pai do JSON, no caso o link que está sendo acessado
        Link link = linkTo(PersonController.class).withSelfRel();
        // Ele pega a list de personmodel, e joga dentro do atributo _embedded do json
        CollectionModel<PersonModel> result = CollectionModel.of(personModels, link);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/family/{id}")
    public ResponseEntity<List<Family>> allFamily(@PathVariable("id") Integer idPerson) {
        return ResponseEntity.ok(this.familyRepository.findByPersonId(idPerson));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Person>> getById(@PathVariable Integer id) {
        EntityModel<Person> personModel = EntityModel.of(this.personRepository.findById(id).get());
        personModel.add(Link.of(ServletUriComponentsBuilder.fromCurrentRequest().toUriString()));
        personModel.add(
                Link.of("/family/" + id).withRel(LinkRelation.of("family")).expand(personModel.getContent().getId()));
        return ResponseEntity.ok(personModel);
    }

    @PostMapping
    public ResponseEntity<PersonModel> postPerson(@RequestBody Person person) {
        Person personSaved = this.personRepository.save(person);
        PersonModel personModel = PersonModel.builder().setFirstName(personSaved.getFirstName())
                .setId(personSaved.getId()).setLastName(personSaved.getLastName())
                .setProfession(personSaved.getProfession()).setSalary(personSaved.getSalary()).build();

        personModel.add(Link.of(ServletUriComponentsBuilder.fromCurrentRequest().toUriString()));

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/").build().toUri())
                .body(personModel);
    }
}
