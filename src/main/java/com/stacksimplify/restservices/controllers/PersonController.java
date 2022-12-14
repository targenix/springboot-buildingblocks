package com.stacksimplify.restservices.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.stacksimplify.restservices.entities.Person;
import com.stacksimplify.restservices.exceptions.PersonExistsException;
import com.stacksimplify.restservices.exceptions.PersonNameNotFoundException;
import com.stacksimplify.restservices.exceptions.PersonNotFoundException;
import com.stacksimplify.restservices.services.PersonService;

//Controller

@RestController
@Validated
@RequestMapping(value = "/persons")
public class PersonController {
	
	//Autowire the PersonService
	@Autowired
	private PersonService personService;
	
	//getAllUsers Method
	
	@GetMapping
	
	public List<Person> getAllPersons(){
		
		return personService.getAllPersons();
	}
	
	//Create Person
	//@RequestBody Annotation
	//@PostMapping Annotation
	@PostMapping
	
	public ResponseEntity<Void> createPerson(@Valid @RequestBody Person person,UriComponentsBuilder builder)
	{ try {
		 personService.createPerson(person);
		 HttpHeaders headers = new HttpHeaders();
		 headers.setLocation(builder.path("/persons/{id}").buildAndExpand(person.getId()).toUri());
		 return new ResponseEntity<Void>(headers,HttpStatus.CREATED);
	} catch (PersonExistsException ex)
	{
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST,ex.getMessage());
	}
	}
	
	//getPersonById
	@GetMapping("/{id}")
	public Optional<Person> getPersonById(@PathVariable("id") @Min(1) Long id){
		
		try {
			return personService.getPersonById(id);
		}catch(PersonNotFoundException ex)
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
		
	}
	
	//updatePersonById
	@PutMapping("/{id}")
	public Person updatePersonById(@PathVariable("id") Long id,@RequestBody Person person) {
		
		try {
			return personService.updatePersonById(id,person);
		}
		catch (PersonNotFoundException ex){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
		
	}
	
	//deletePersonById
	@DeleteMapping("/{id}")
	public void deletePersonById(@PathVariable("id")Long id) {
		personService.deletePersonById(id);
	}
	
	//getPersonByPersonname
	@GetMapping("/byusername/{username}")
	public Person getPersonByUsername(@PathVariable("username")String username) throws PersonNameNotFoundException {
		Person person = personService.getPersonByUsername(username);
		if(person == null)
		throw new PersonNameNotFoundException("Personname: '" + username + "'not found in Person repository' ");
		return person;
	}
	

}
