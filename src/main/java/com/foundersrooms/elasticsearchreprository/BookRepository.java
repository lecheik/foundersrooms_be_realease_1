package com.foundersrooms.elasticsearchreprository;

import java.util.Set;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.foundersrooms.domain.Book;
import com.foundersrooms.domain.people.User;

public interface BookRepository extends ElasticsearchRepository<Book, String> {
    //Page<User> findByLastName(String lastName, Pageable pageable);

}
