package com.example.biblioteca.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private AutorRepository autorRepository;

    private LivroService livroService;

    @BeforeEach
    void setUp() {
        livroService = new LivroService(livroRepository, autorRepository);
    }

    @Test
    void deveCriarLivroComSucesso() {
        LivroRequest request = requestPadrao();
        Autor autor = autorPadrao();
        Livro livro = livroPadrao(autor);

        when(livroRepository.existsByIsbn("9788535910663")).thenReturn(false);
        when(autorRepository.findByNomeIgnoreCase("Machado de Assis")).thenReturn(Optional.of(autor));
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroResponse response = livroService.criar(request);

        assertThat(response.titulo()).isEqualTo("Dom Casmurro");
        assertThat(response.autor().nome()).isEqualTo("Machado de Assis");
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    void deveCriarAutorQuandoAutorNaoExistir() {
        LivroRequest request = requestPadrao();
        Autor autor = autorPadrao();
        Livro livro = livroPadrao(autor);

        when(livroRepository.existsByIsbn("9788535910663")).thenReturn(false);
        when(autorRepository.findByNomeIgnoreCase("Machado de Assis")).thenReturn(Optional.empty());
        when(autorRepository.save(any(Autor.class))).thenReturn(autor);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroResponse response = livroService.criar(request);

        assertThat(response.autor().nacionalidade()).isEqualTo("Brasileira");
        verify(autorRepository).save(any(Autor.class));
    }

    @Test
    void deveFalharAoCriarLivroComIsbnDuplicado() {
        LivroRequest request = requestPadrao();
        when(livroRepository.existsByIsbn("9788535910663")).thenReturn(true);

        assertThatThrownBy(() -> livroService.criar(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ISBN");

        verify(livroRepository, never()).save(any());
    }

    @Test
    void deveBuscarLivroPorId() {
        Livro livro = livroPadrao(autorPadrao());
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        LivroResponse response = livroService.buscarPorId(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.isbn()).isEqualTo("9788535910663");
    }

    @Test
    void deveFalharAoBuscarLivroInexistente() {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Livro nao encontrado");
    }

    @Test
    void deveBuscarLivroPorIsbn() {
        Livro livro = livroPadrao(autorPadrao());
        when(livroRepository.findByIsbn("9788535910663")).thenReturn(Optional.of(livro));

        LivroResponse response = livroService.buscarPorIsbn("9788535910663");

        assertThat(response.titulo()).isEqualTo("Dom Casmurro");
    }

    @Test
    void deveFalharAoBuscarLivroPorIsbnInexistente() {
        when(livroRepository.findByIsbn("0000000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.buscarPorIsbn("0000000000"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ISBN");
    }

    @Test
    void deveAtualizarLivroComSucesso() {
        Livro livro = livroPadrao(autorPadrao());
        Autor novoAutor = new Autor("Clarice Lispector", "Brasileira");
        LivroRequest request = new LivroRequest(
                "A Hora da Estrela",
                "9788520923258",
                1977,
                "Ficcao",
                "Clarice Lispector",
                "Brasileira");

        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(livroRepository.existsByIsbnAndIdNot("9788520923258", 1L)).thenReturn(false);
        when(autorRepository.findByNomeIgnoreCase("Clarice Lispector")).thenReturn(Optional.of(novoAutor));

        LivroResponse response = livroService.atualizar(1L, request);

        assertThat(response.titulo()).isEqualTo("A Hora da Estrela");
        assertThat(response.autor().nome()).isEqualTo("Clarice Lispector");
    }

    @Test
    void deveFalharAoAtualizarComIsbnDeOutroLivro() {
        Livro livro = livroPadrao(autorPadrao());
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(livroRepository.existsByIsbnAndIdNot("9788535910663", 1L)).thenReturn(true);

        assertThatThrownBy(() -> livroService.atualizar(1L, requestPadrao()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ISBN");
    }

    @Test
    void deveAtualizarGenero() {
        Livro livro = livroPadrao(autorPadrao());
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        LivroResponse response = livroService.atualizarGenero(1L, new LivroPatchGeneroRequest("Realismo"));

        assertThat(response.genero()).isEqualTo("Realismo");
    }

    @Test
    void deveFiltrarLivros() {
        Livro livro = livroPadrao(autorPadrao());
        when(livroRepository.filtrar("Machado", "Romance")).thenReturn(List.of(livro));

        List<LivroResponse> responses = livroService.filtrar(" Machado ", " Romance ");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).titulo()).isEqualTo("Dom Casmurro");
    }

    @Test
    void deveExcluirLivro() {
        when(livroRepository.existsById(1L)).thenReturn(true);

        livroService.excluir(1L);

        verify(livroRepository).deleteById(1L);
    }

    @Test
    void deveFalharAoExcluirLivroInexistente() {
        when(livroRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> livroService.excluir(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(livroRepository, never()).deleteById(99L);
    }

    private LivroRequest requestPadrao() {
        return new LivroRequest(
                "Dom Casmurro",
                "9788535910663",
                1899,
                "Romance",
                "Machado de Assis",
                "Brasileira");
    }

    private Autor autorPadrao() {
        return new Autor("Machado de Assis", "Brasileira");
    }

    private Livro livroPadrao(Autor autor) {
        return new Livro("Dom Casmurro", "9788535910663", 1899, "Romance", autor) {
            @Override
            public Long getId() {
                return 1L;
            }
        };
    }
}
