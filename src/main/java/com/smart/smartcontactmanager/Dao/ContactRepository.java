package com.smart.smartcontactmanager.Dao;

import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ContactRepository extends JpaRepository<Contact,Integer> {

    @Query(" from Contact as c where c.user.id=:userId")
     Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

    List<Contact> findByNameContainingAndUser(String name , User user);

}
