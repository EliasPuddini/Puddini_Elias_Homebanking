package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("/clients")
    public List<ClientDTO> getClient(){
        return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(Collectors.toList());
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){

        return new ClientDTO(clientRepository.findById(id).orElse(null));
    }

    @RequestMapping(value = "/clients/current", method = RequestMethod.GET)
    public ClientDTO getCurrent ( Authentication authentication){
        return new ClientDTO(clientRepository.findByEmail(authentication.name()));
    }


    @RequestMapping(path = "/clients", method = RequestMethod.POST)

    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        if(!(firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && password.isEmpty())) {

            Account account= new Account(("VIN"+String.format("%03d") + accountRepository.count()+1, 0.0, LocalDate.now());
            Client newClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));
            newClient.addAccount(account);
            clientRepository.save(newClient);
            accountRepository.save(account);

        }
        return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
    @RequestMapping("/clients/{id}")
    public ClientDTO getById(@PathVariable Long id){
        return new ClientDTO(clientRepository.findById(id).orElse(null));
    }

    @RequestMapping("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication) {
        return new ClientDTO(clientRepository.findByEmail(authentication.getDeclaringClass().getName()));
    }
}