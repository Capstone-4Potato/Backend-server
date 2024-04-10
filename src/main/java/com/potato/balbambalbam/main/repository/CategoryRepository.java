package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    List<Category> findAll();
    Optional<Category> findByName(String name);
    Optional<Category> findByNameAndParentId(String name, Long parentId);
}
