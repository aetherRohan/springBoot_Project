package com.smart.smartcontactmanager.Controller;

import com.smart.smartcontactmanager.Dao.ContactRepository;
import com.smart.smartcontactmanager.Dao.UserRepository;
import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public ContactRepository contactRepository;



    @GetMapping("/user/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query")String query, Principal principal){

        String username=principal.getName();
        User user=this.userRepository.findByUsername(username);
         List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(query,user);

         return ResponseEntity.ok(contacts);
    }


}
