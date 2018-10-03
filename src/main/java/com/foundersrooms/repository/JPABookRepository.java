package com.foundersrooms.repository;

import org.springframework.data.repository.CrudRepository;

import com.foundersrooms.domain.Book;

public interface JPABookRepository extends CrudRepository<Book, String> {

}
