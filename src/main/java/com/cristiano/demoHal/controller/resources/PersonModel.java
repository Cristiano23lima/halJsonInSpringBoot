package com.cristiano.demoHal.controller.resources;

import org.springframework.hateoas.RepresentationModel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(setterPrefix = "set")
public class PersonModel extends RepresentationModel<PersonModel> {
    private Integer id;
    private String firstName;
    private String lastName;
    private String profession;
    private Double salary;
}
