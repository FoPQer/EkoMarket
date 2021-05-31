package com.example.ecomarket.repos;

import com.example.ecomarket.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepos extends CrudRepository<Product, Integer> {
    List<Product> findByName(String name);
    Product findById(int id);
}
