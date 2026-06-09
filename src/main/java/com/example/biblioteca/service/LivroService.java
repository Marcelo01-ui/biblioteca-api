package com.example.biblioteca.service;

import com.example.biblioteca.dto.AutorResumoResponse;
import com.example.biblioteca.dto.LivroPatchGeneroRequest;
import com.example.biblioteca.dto.LivroRequest;
import com.example.biblioteca.dto.LivroResponse;
import com.example.biblioteca.exception.BusinessRuleException;
import com.example.biblioteca.exception.ResourceNotFoundException;
import com.example.biblioteca.model.Autor;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.repository.AutorRepository;
import com.example.biblioteca.repository.LivroRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional
    public LivroResponse criar(LivroRequest request) {
        String isbn = limpar(request.isbn());
        if (livroRepository.existsByIsbn(isbn)) {
            throw new BusinessRuleException("Ja existe um livro cadastrado com este ISBN");
        }

        Autor autor = resolverAutor(request.autorNome(), request.autorNacionalidade());
        Livro livro = new Livro(
                limpar(request.titulo()),
                isbn,
                request.anoPublicacao(),
                limpar(request.genero()),
                autor);

        return toResponse(livroRepository.save(livro));
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> listar() {
        return livroRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public LivroResponse buscarPorIsbn(String isbn) {
        return livroRepository.findByIsbn(limpar(isbn))
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Livro nao encontrado para o ISBN informado"));
    }

    @Transactional(readOnly = true)
    public List<LivroResponse> filtrar(String autor, String genero) {
        return livroRepository.filtrar(normalizarFiltro(autor), normalizarFiltro(genero))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LivroResponse atualizar(Long id, LivroRequest request) {
        Livro livro = buscarEntidadePorId(id);
        String isbn = limpar(request.isbn());
        if (livroRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new BusinessRuleException("ISBN ja cadastrado em outro livro");
        }

        livro.setTitulo(limpar(request.titulo()));
        livro.setIsbn(isbn);
        livro.setAnoPublicacao(request.anoPublicacao());
        livro.setGenero(limpar(request.genero()));
        livro.setAutor(resolverAutor(request.autorNome(), request.autorNacionalidade()));

        return toResponse(livro);
    }

    @Transactional
    public LivroResponse atualizarGenero(Long id, LivroPatchGeneroRequest request) {
        Livro livro = buscarEntidadePorId(id);
        livro.setGenero(limpar(request.genero()));
        return toResponse(livro);
    }

    @Transactional
    public void excluir(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Livro nao encontrado");
        }
        livroRepository.deleteById(id);
    }

    private Livro buscarEntidadePorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro nao encontrado"));
    }

    private Autor resolverAutor(String nome, String nacionalidade) {
        String nomeNormalizado = limpar(nome);
        String nacionalidadeNormalizada = limpar(nacionalidade);
        return autorRepository.findByNomeIgnoreCase(nomeNormalizado)
                .map(autor -> {
                    autor.setNacionalidade(nacionalidadeNormalizada);
                    return autor;
                })
                .orElseGet(() -> autorRepository.save(new Autor(nomeNormalizado, nacionalidadeNormalizada)));
    }

    private LivroResponse toResponse(Livro livro) {
        Autor autor = livro.getAutor();
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getGenero(),
                new AutorResumoResponse(autor.getId(), autor.getNome(), autor.getNacionalidade()));
    }

    private String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private String normalizarFiltro(String valor) {
        String valorLimpo = limpar(valor);
        return valorLimpo == null || valorLimpo.isBlank() ? null : valorLimpo;
    }
}
