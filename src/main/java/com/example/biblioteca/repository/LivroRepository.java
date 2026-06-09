package com.example.biblioteca.repository;

import com.example.biblioteca.model.Livro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Override
    @EntityGraph(attributePaths = "autor")
    List<Livro> findAll();

    @Override
    @EntityGraph(attributePaths = "autor")
    Optional<Livro> findById(Long id);

    @EntityGraph(attributePaths = "autor")
    Optional<Livro> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    @Query("""
            select l from Livro l
            join fetch l.autor a
            where (:autor is null or lower(a.nome) like lower(concat('%', :autor, '%')))
              and (:genero is null or lower(l.genero) like lower(concat('%', :genero, '%')))
            order by l.titulo
            """)
    List<Livro> filtrar(@Param("autor") String autor, @Param("genero") String genero);
}
