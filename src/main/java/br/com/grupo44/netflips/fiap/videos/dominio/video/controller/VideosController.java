package br.com.grupo44.netflips.fiap.videos.dominio.video.controller;

import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.service.VideoService;
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
@RequestMapping(value = "/video",produces = {"application/json"})
@Tag(name = "API Video")
public class VideosController {
    @Autowired
    private VideoService videoService;
    @Operation(summary = "Retorna lista de videos podendo ser filtrada por titulo do videoa e data de publicação",method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping
    public ResponseEntity<Page<VideoDTO>> findAll(
            @ModelAttribute VideoDTO filtro,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false, defaultValue = "dataPublicacao") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<VideoDTO> result = videoService.findAll(filtro, pageRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Operation(summary = "Consulta usuário por id",method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @GetMapping("/{codigo}")
    public ResponseEntity<VideoDTO> findById(@PathVariable String codigo) {
        VideoDTO usuario = videoService.findById(codigo);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Inserir video",method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PostMapping
    public ResponseEntity insert(@RequestBody VideoDTO videoDTO) {
        List<String> violacoesToList = videoService.validate(videoDTO);
        if (!violacoesToList.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToList);
        }
        VideoDTO videoSaved = videoService.insert(videoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand((videoSaved.getCodigo())).toUri();
        return ResponseEntity.created(uri).body(videoSaved);
    }

    @Operation(summary = "Atualiza video",method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PutMapping("/{codigo}")
    public ResponseEntity update(@RequestBody VideoDTO  videoDTO, @PathVariable String codigo) {
        List<String> violacoesToList = videoService.validate(videoDTO);
        if (!violacoesToList.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToList);
        }
        var alocacaoUpdated = videoService.update(codigo, videoDTO);
        return  ResponseEntity.ok(alocacaoUpdated);
    }

    @Operation(summary = "Deleta video",method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @DeleteMapping("/{codigo}")
    public ResponseEntity delete(@PathVariable String codigo) {
        videoService.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
