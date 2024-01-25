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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/video", produces = {"application/json"})
@Tag(name = "API Video")
public class VideosController {
    @Autowired
    private VideoService videoService;

    @Operation(summary = "Retorna lista de vídeos podendo ser filtrada por título do vídeo e data de publicação", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found videos"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Error in the service")})
    @GetMapping
    public Flux<ResponseEntity<Page<VideoDTO>>> findAll(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String categoriaBusca,
            @RequestParam(required = false) LocalDateTime dataPub,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false, defaultValue = "dataPublicacao") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) throws Exception {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
        VideoDTO filtro = videoService.retornaFiltroFormatado(titulo,categoriaBusca,dataPub);
        return videoService.findAll(filtro, pageRequest)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK));
    }
    @Operation(summary = "Consulta vídeo por id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the video"),
            @ApiResponse(responseCode = "404", description = "Video not found"),
            @ApiResponse(responseCode = "500", description = "Error in the service")})
    @GetMapping("/{codigo}")
    public Mono<ResponseEntity<VideoDTO>> findById(@PathVariable String codigo) {
        return videoService.findById(codigo)
                .map(video -> ResponseEntity.ok(video))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @Operation(summary = "Inserir video", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PostMapping
    public Mono<ResponseEntity<?>> insertVideo(@RequestBody VideoDTO videoDTO) {
        List<String> violacoesToList = videoService.validate(videoDTO);
        if (!violacoesToList.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(violacoesToList));
        }

        return videoService.insert(videoDTO)
                .map(videoSaved -> ResponseEntity.created(URI.create("/video/" + videoSaved.getCodigo()))
                        .body(videoSaved));
    }


    @Operation(summary = "Atualiza video", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @PutMapping("/{codigo}")
    public Mono<ResponseEntity<VideoDTO>> update(@RequestBody VideoDTO videoDTO, @PathVariable String codigo) {
        return Mono.just(videoService.validate(videoDTO))
                .filter(List::isEmpty)
                .flatMap(valid -> videoService.update(codigo, videoDTO))
                .map(videoSaved -> ResponseEntity.ok(videoSaved))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Deleta video", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Erro no seervio")})
    @DeleteMapping("/{codigo}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String codigo) {
        return videoService.delete(codigo)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Operation(summary = "Retorna lista de vides recomendados para usuario podendo ser filtrado por titulo e data", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found videos"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "500", description = "Error in the service")})
    @GetMapping("/recomendavideousuario/{codigoUsuario}")
    public Flux<ResponseEntity<Page<VideoDTO>>> obterVideosRecomendadosParaUsuario(
            @PathVariable String codigoUsuario,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String categoriaBusca,
            @RequestParam(required = false) LocalDateTime dataPub,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false, defaultValue = "dataPublicacao") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) throws Exception {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
        VideoDTO filtro = videoService.retornaFiltroFormatado(titulo,categoriaBusca,dataPub);
        return videoService.recomendarVideos(codigoUsuario, pageRequest,filtro)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK));
    }

}
