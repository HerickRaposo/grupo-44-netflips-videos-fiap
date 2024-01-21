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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/exibicao",produces = {"application/json"})
@Tag(name = "API Exibição")
public class ExibicaoController {

    @Autowired
    private ExibicaoService exibicaoService;
    @Operation(summary = "Retorna lista de exibições paginadas podendo ser paginadas por nome de usuario e titulo de video",method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping
    public Flux<ResponseEntity<Page<ExibicaoDTO>>> findAll(
            @ModelAttribute ExibicaoDTO filtro,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false, defaultValue = "dataVisualizacao") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));

        return exibicaoService.findAll(filtro, pageRequest)
                .map(resultPage -> new ResponseEntity<>(resultPage, HttpStatus.OK));

    }

    @Operation(summary = "Consulta exibição por codigo", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping("/{codigo}")
    public Mono<ResponseEntity<ExibicaoDTO>> findById(@PathVariable String codigo) {
        return exibicaoService.findById(codigo)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Inserir exibição", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PostMapping
    public Mono<ResponseEntity<?>> insert(@RequestBody ExibicaoDTO exibicaoDTO) {
        List<String> violacoesToList = exibicaoService.validate(exibicaoDTO);
        if (!violacoesToList.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(violacoesToList));
        }

        return exibicaoService.insert(exibicaoDTO)
                .map(exibicaoSaved -> ResponseEntity.created(
                                ServletUriComponentsBuilder.fromCurrentRequest().path("/{codigo}")
                                        .buildAndExpand(exibicaoSaved.getCodigo()).toUri())
                        .body(exibicaoSaved));
    }

    @Operation(summary = "Atualiza exibição", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PutMapping("/{codigo}")
    public Mono<ResponseEntity<ExibicaoDTO>> update(@RequestBody ExibicaoDTO exibicaoDTO, @PathVariable String codigo) {
        return Mono.just(exibicaoService.validate(exibicaoDTO))
                .filter(List::isEmpty)
                .flatMap(valid -> exibicaoService.update(codigo, exibicaoDTO))
                .map(videoSaved -> ResponseEntity.ok(videoSaved))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Deleta exibição", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @DeleteMapping("/{codigo}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String codigo) {
        return exibicaoService.delete(codigo)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
