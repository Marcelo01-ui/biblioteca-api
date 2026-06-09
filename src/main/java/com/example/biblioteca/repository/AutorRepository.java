package com.example.biblioteca.repository;

import com.example.biblioteca.model.Autor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNomeIgnoreCase(String nome);
}
