package br.com.grupo44.netflips.fiap.videos.dominio.video.service;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import br.com.grupo44.netflips.fiap.videos.dominio.video.repository.VideoRepository;
import com.mongodb.DuplicateKeyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private final ReactiveMongoTemplate reactiveMongoTemplate;



    public VideoService(VideoRepository videoRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.videoRepository = videoRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public List<String> validate(VideoDTO dto){
        Set<ConstraintViolation<VideoDTO>> violacoes = Validation.buildDefaultValidatorFactory().getValidator().validate(dto);
        List<String> violacoesToList = violacoes.stream()
                .map((violacao) -> violacao.getPropertyPath() + ":" + violacao.getMessage())
                .collect(Collectors.toList());
        return violacoesToList;
    }
    public Flux<Page<VideoDTO>> findAll(VideoDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();

        if (filtro.getDataPublicacao() != null) {
            criteria.and("data_publicacao").lte(filtro.getDataPublicacao());
        }
        if (filtro.getTitulo() != null && !filtro.getTitulo().isBlank()) {
            criteria.and("titulo").regex(filtro.getTitulo(), "i");
        }

        Query query = new Query(criteria).with(page);

        return reactiveMongoTemplate.find(query.with(page), Video.class)
                .collectList()
                .map(listaVideos -> new PageImpl<>(listaVideos, page, listaVideos.size()))
                .flux()
                .map(pageResult -> pageResult.map(VideoDTO::new));
    }
    public Mono<VideoDTO> findById(String codigo) {
        return videoRepository.findById(codigo)
                .map(VideoDTO::new)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Video não encontrado")));
    }

     @Transactional
     public Mono<VideoDTO> insert(VideoDTO videoDTO) {
         Video entity = new Video();
         mapperDtoToEntity(videoDTO, entity);

         return Mono.justOrEmpty(entity.getAutor().getCodigo())
                 .flatMap(codigo -> usuarioRepository.findById(codigo)
                         .map(autor -> {
                             entity.setAutor(autor);
                             return entity;
                         })
                         .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"))))
                 .switchIfEmpty(Mono.defer(() -> {
                     entity.setAutor(null);
                     return Mono.just(entity);
                 }))
                 .flatMap(videoRepository::save)
                 .map(videoSalvo -> new VideoDTO(videoSalvo))
                 .onErrorResume(DuplicateKeyException.class, ex ->
                         Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Video already exists")))
                 .onErrorResume(OptimisticLockingFailureException.class, ex ->
                         videoRepository.findById(videoDTO.getCodigo())
                                 .flatMap(atualizado -> {
                                     mapperDtoToEntity(videoDTO, atualizado);
                                     atualizado.setVERSION(atualizado.getVERSION() + 1);
                                     return videoRepository.save(atualizado);
                                 })
                                 .map(atualizado -> new VideoDTO(atualizado))
                                 .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"))))
                 .onErrorResume(Exception.class, ex ->
                         Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + ex.getMessage())));
     }

    @Transactional
    public Mono<VideoDTO> update(String codigo, VideoDTO dto) {
        return Mono.just(codigo)
                .flatMap(codigoVideo -> videoRepository.findById(codigoVideo)
                        .map(entity -> {
                            mapperDtoToEntity(dto, entity);
                            return entity;
                        })
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found")))))
                .flatMap(videoRepository::save)
                .map(savedEntity -> new VideoDTO(savedEntity))
                .onErrorResume(OptimisticLockingFailureException.class, ex ->
                        videoRepository.findById(codigo)
                                .flatMap(atualizado -> {
                                    mapperDtoToEntity(dto, atualizado);
                                    atualizado.setVERSION(atualizado.getVERSION() + 1);
                                    return videoRepository.save(atualizado)
                                            .map(salvo -> new VideoDTO(salvo));
                                })
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Video não encontrado"))))
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor: " + ex.getMessage())));
    }

    @Transactional
    public Mono<Void> delete(String codigo) {
        return Mono.fromRunnable(() ->
                videoRepository.deleteById(codigo)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Video não encontrado")))
        );
    }
    private void  mapperDtoToEntity(VideoDTO dto, Video entity){
         entity.setTitulo(dto.getTitulo());
         entity.setUrl(dto.getUrl());
         entity.setDataPublicacao(dto.getDataPublicacao());
    }
}
