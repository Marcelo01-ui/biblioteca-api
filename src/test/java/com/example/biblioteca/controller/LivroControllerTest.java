package com.example.biblioteca.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.biblioteca.dto.AutorResumoResponse;
import com.example.biblioteca.dto.LivroPatchGeneroRequest;
import com.example.biblioteca.dto.LivroRequest;
import com.example.biblioteca.dto.LivroResponse;
import com.example.biblioteca.exception.GlobalExceptionHandler;
import com.example.biblioteca.exception.ResourceNotFoundException;
import com.example.biblioteca.service.LivroService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

    @Mock
    private LivroService livroService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new LivroController(livroService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void deveCriarLivro() throws Exception {
        when(livroService.criar(any(LivroRequest.class))).thenReturn(responsePadrao());

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLivroValido()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/livros/1"))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));
    }

    @Test
    void deveRetornarBadRequestAoCriarLivroInvalido() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "",
                                  "isbn": "123",
                                  "anoPublicacao": 1200,
                                  "genero": "",
                                  "autorNome": "",
                                  "autorNacionalidade": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.titulo").exists())
                .andExpect(jsonPath("$.fields.isbn").exists());
    }

    @Test
    void deveListarLivros() throws Exception {
        when(livroService.listar()).thenReturn(List.of(responsePadrao()));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("9788535910663"));
    }

    @Test
    void deveBuscarLivroPorId() throws Exception {
        when(livroService.buscarPorId(1L)).thenReturn(responsePadrao());

        mockMvc.perform(get("/api/livros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deveRetornarNotFoundQuandoLivroNaoExistir() throws Exception {
        when(livroService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Livro nao encontrado"));

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Livro nao encontrado"));
    }

    @Test
    void deveBuscarLivroPorIsbn() throws Exception {
        when(livroService.buscarPorIsbn("9788535910663")).thenReturn(responsePadrao());

        mockMvc.perform(get("/api/livros/isbn/9788535910663"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"));
    }

    @Test
    void deveFiltrarLivros() throws Exception {
        when(livroService.filtrar("Machado", "Romance")).thenReturn(List.of(responsePadrao()));

        mockMvc.perform(get("/api/livros/filtro")
                        .param("autor", "Machado")
                        .param("genero", "Romance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genero").value("Romance"));
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        when(livroService.atualizar(eq(1L), any(LivroRequest.class))).thenReturn(responsePadrao());

        mockMvc.perform(put("/api/livros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLivroValido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deveAtualizarGenero() throws Exception {
        LivroResponse response = new LivroResponse(
                1L,
                "Dom Casmurro",
                "9788535910663",
                1899,
                "Realismo",
                new AutorResumoResponse(1L, "Machado de Assis", "Brasileira"));

        when(livroService.atualizarGenero(eq(1L), any(LivroPatchGeneroRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/livros/1/genero")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "genero": "Realismo"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genero").value("Realismo"));
    }

    @Test
    void deveExcluirLivro() throws Exception {
        doNothing().when(livroService).excluir(1L);

        mockMvc.perform(delete("/api/livros/1"))
                .andExpect(status().isNoContent());
    }

    private LivroResponse responsePadrao() {
        return new LivroResponse(
                1L,
                "Dom Casmurro",
                "9788535910663",
                1899,
                "Romance",
                new AutorResumoResponse(1L, "Machado de Assis", "Brasileira"));
    }

    private String jsonLivroValido() {
        return """
                {
                  "titulo": "Dom Casmurro",
                  "isbn": "9788535910663",
                  "anoPublicacao": 1899,
                  "genero": "Romance",
                  "autorNome": "Machado de Assis",
                  "autorNacionalidade": "Brasileira"
                }
                """;
    }
}
