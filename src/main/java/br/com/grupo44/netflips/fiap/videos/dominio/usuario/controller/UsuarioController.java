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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/usuario",produces = {"application/json"})
@Tag(name = "API Usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    @Operation(summary = "Retorna lista de alocacoes paginada podendo ser filtrada por marca,modelo,matriculaa",method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>> searchVideos(
            @ModelAttribute UsuarioDTO filtro,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false, defaultValue = "nome") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<UsuarioDTO> result = usuarioService.findAll(filtro, pageRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Operation(summary = "Consulta usuário por id",method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping("/{codigo}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable String codigo) {
        UsuarioDTO usuario = usuarioService.findById(codigo);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Inserir usuario",method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PostMapping
    public ResponseEntity insert(@RequestBody UsuarioDTO usuarioDTO) {
        List<String> violacoesToList = usuarioService.validate(usuarioDTO);
        if (!violacoesToList.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToList);
        }
        UsuarioDTO usuarioSaved = usuarioService.insert(usuarioDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand((usuarioSaved.getCodigo())).toUri();
        return ResponseEntity.created(uri).body(usuarioSaved);
    }

    @Operation(summary = "Atualiza usuario",method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PutMapping("/{codigo}")
    public ResponseEntity update(@RequestBody UsuarioDTO usuarioDTO, @PathVariable String codigo) {
        List<String> violacoesToList = usuarioService.validate(usuarioDTO);
        if (!violacoesToList.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToList);
        }
        var alocacaoUpdated = usuarioService.update(codigo, usuarioDTO);
        return  ResponseEntity.ok(alocacaoUpdated);
    }

    @Operation(summary = "Deleta alocação",method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @DeleteMapping("/{codigo}")
    public ResponseEntity delete(@PathVariable String codigo) {
        usuarioService.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
