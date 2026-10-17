package br.com.fintrack.controller;

import br.com.fintrack.dto.SubcategoriaDTO;
import br.com.fintrack.service.SubcategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
    "/api/v1/usuarios/{usuarioId}/categorias/{categoriaId}/subcategorias"
)
@Tag(name = "Subcategorias")
@PreAuthorize("#usuarioId == authentication.principal.id")
public class SubcategoriaController {
    private final SubcategoriaService service;

    public SubcategoriaController(SubcategoriaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar subcategorias da categoria")
    public List<SubcategoriaDTO> listar(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId) {
        return service.listar(usuarioId, categoriaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar subcategoria")
    public SubcategoriaDTO criar(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId,
            @Valid @RequestBody SubcategoriaDTO dados) {
        return service.criar(usuarioId, categoriaId, dados);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar subcategoria")
    public SubcategoriaDTO atualizar(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId,
            @PathVariable Long id,
            @Valid @RequestBody SubcategoriaDTO dados) {
        return service.atualizar(usuarioId, categoriaId, id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover subcategoria")
    public void remover(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId,
            @PathVariable Long id) {
        service.remover(usuarioId, categoriaId, id);
    }
}
