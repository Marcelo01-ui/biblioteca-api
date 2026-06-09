package com.example.biblioteca.controller;

import com.example.biblioteca.dto.LivroPatchGeneroRequest;
import com.example.biblioteca.dto.LivroRequest;
import com.example.biblioteca.dto.LivroResponse;
import com.example.biblioteca.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/livros")
@Tag(name = "Livros", description = "Operacoes para gerenciar o acervo de livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @Operation(
            summary = "Criar livro",
            description = "Cadastra um livro e vincula o registro a um autor existente ou recem-criado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso",
                    content = @Content(schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "409", description = "ISBN ja cadastrado")
    })
    @PostMapping
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody LivroRequest request) {
        LivroResponse response = livroService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Listar livros", description = "Retorna todos os livros cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<LivroResponse>> listar() {
        return ResponseEntity.ok(livroService.listar());
    }

    @Operation(summary = "Buscar livro por ID", description = "Retorna um livro especifico pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro encontrado",
                    content = @Content(schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Livro nao encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscarPorId(
            @Parameter(description = "ID do livro", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @Operation(summary = "Buscar livro por ISBN", description = "Retorna um livro usando seu ISBN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro encontrado",
                    content = @Content(schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Livro nao encontrado")
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<LivroResponse> buscarPorIsbn(
            @Parameter(description = "ISBN de 10 ou 13 caracteres", example = "9788535910663")
            @PathVariable String isbn) {
        return ResponseEntity.ok(livroService.buscarPorIsbn(isbn));
    }

    @Operation(
            summary = "Filtrar livros",
            description = "Busca livros por autor, genero ou pela combinacao dos dois filtros.")
    @ApiResponse(responseCode = "200", description = "Filtro processado com sucesso")
    @GetMapping("/filtro")
    public ResponseEntity<List<LivroResponse>> filtrar(
            @Parameter(description = "Trecho do nome do autor", example = "Machado")
            @RequestParam(required = false) String autor,
            @Parameter(description = "Genero literario", example = "Romance")
            @RequestParam(required = false) String genero) {
        return ResponseEntity.ok(livroService.filtrar(autor, genero));
    }

    @Operation(summary = "Atualizar livro", description = "Substitui todos os dados de um livro existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro atualizado",
                    content = @Content(schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Livro nao encontrado"),
            @ApiResponse(responseCode = "409", description = "ISBN ja cadastrado em outro livro")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(
            @Parameter(description = "ID do livro", example = "1") @PathVariable Long id,
            @Valid @RequestBody LivroRequest request) {
        return ResponseEntity.ok(livroService.atualizar(id, request));
    }

    @Operation(summary = "Atualizar genero", description = "Atualiza apenas o genero de um livro existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Genero atualizado",
                    content = @Content(schema = @Schema(implementation = LivroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Livro nao encontrado")
    })
    @PatchMapping("/{id}/genero")
    public ResponseEntity<LivroResponse> atualizarGenero(
            @Parameter(description = "ID do livro", example = "1") @PathVariable Long id,
            @Valid @RequestBody LivroPatchGeneroRequest request) {
        return ResponseEntity.ok(livroService.atualizarGenero(id, request));
    }

    @Operation(summary = "Excluir livro", description = "Remove um livro pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Livro removido"),
            @ApiResponse(responseCode = "404", description = "Livro nao encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do livro", example = "1") @PathVariable Long id) {
        livroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
