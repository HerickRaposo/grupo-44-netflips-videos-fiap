package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.controller;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service.ExibicaoService;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
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
@RequestMapping(value = "/exibicao",produces = {"application/json"})
@Tag(name = "API Exibição")
public class ExibicaoController {

    @Autowired
    private ExibicaoService exibicaoServic;
    @Operation(summary = "Retorna lista de exibições paginadas podendo ser paginadas por nome de usuario e titulo de video",method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping
    public ResponseEntity<Page<ExibicaoDTO>> findAll(
            @ModelAttribute ExibicaoDTO filtro,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false, defaultValue = "data_visualizacao") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<ExibicaoDTO> result = exibicaoServic.findAll(filtro, pageRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Operation(summary = "Consulta exibição por codigo",method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping("/{codigo}")
    public ResponseEntity<ExibicaoDTO> findById(@PathVariable String codigo) {
        ExibicaoDTO exibicao = exibicaoServic.findById(codigo);
        return ResponseEntity.ok(exibicao);
    }

    @Operation(summary = "Inserir exibição",method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PostMapping
    public ResponseEntity insert(@RequestBody ExibicaoDTO exibicaoDTO) {
        List<String> violacoesToList = exibicaoServic.validate(exibicaoDTO);
        if (!violacoesToList.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToList);
        }
        ExibicaoDTO exibicaoSaved = exibicaoServic.insert(exibicaoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand((exibicaoSaved.getCodigo())).toUri();
        return ResponseEntity.created(uri).body(exibicaoSaved);
    }

    @Operation(summary = "Atualiza exibição",method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PutMapping("/{codigo}")
    public ResponseEntity update(@RequestBody ExibicaoDTO  exibicaoDTO, @PathVariable String codigo) {
        List<String> violacoesToList = exibicaoServic.validate(exibicaoDTO);
        if (!violacoesToList.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToList);
        }
        var alocacaoUpdated = exibicaoServic.update(codigo, exibicaoDTO);
        return  ResponseEntity.ok(alocacaoUpdated);
    }

    @Operation(summary = "Deleta exibição",method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @DeleteMapping("/{codigo}")
    public ResponseEntity delete(@PathVariable String codigo) {
        exibicaoServic.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
