package br.com.grupo44.netflips.fiap.videos.dominio.usuario.controller;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/usuario",produces = {"application/json"})
@Tag(name = "API Usuarios")

public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Retorna lista de alocacoes paginada podendo ser filtrada por marca,modelo,matriculaa", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping
    public Flux<ResponseEntity<Page<UsuarioDTO>>> searchVideos(
            @ModelAttribute UsuarioDTO filtro,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false, defaultValue = "nome") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
        return usuarioService.findAll(filtro, pageRequest)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK));
    }

    @Operation(summary = "Consulta usuário por id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error in the service")})
    @GetMapping("/{codigo}")
    public Mono<ResponseEntity<UsuarioDTO>> findById(@PathVariable String codigo) {
        return usuarioService.findById(codigo)
                .map(usuario -> ResponseEntity.ok(usuario))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Inserir usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Error in the service")})
    @PostMapping
    public Mono<ResponseEntity<?>> insert(@RequestBody UsuarioDTO usuarioDTO) {
        List<String> violacoesToList = usuarioService.validate(usuarioDTO);
        if (!violacoesToList.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(violacoesToList));
        }

        return usuarioService.insert(usuarioDTO)
                .map(usuarioSaved -> ResponseEntity.created(
                                ServletUriComponentsBuilder.fromCurrentRequest().path("/{codigo}")
                                        .buildAndExpand(usuarioSaved.getCodigo()).toUri())
                        .body(usuarioSaved));
    }

    @Operation(summary = "Atualiza usuário", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error in the service")})
    @PutMapping("/{codigo}")
    public Mono<ResponseEntity<UsuarioDTO>> update(@RequestBody UsuarioDTO usuarioDTO, @PathVariable String codigo) {
        List<String> violacoesToList = usuarioService.validate(usuarioDTO);

        return Mono.justOrEmpty(violacoesToList.isEmpty())
                .flatMap(valid -> usuarioService.update(codigo, usuarioDTO))
                .map(updatedUser -> ResponseEntity.ok(updatedUser))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Deleta alocação", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error in the service")})
    @DeleteMapping("/{codigo}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String codigo) {
        return usuarioService.delete(codigo)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
