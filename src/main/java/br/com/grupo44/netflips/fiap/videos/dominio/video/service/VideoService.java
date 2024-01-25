package br.com.grupo44.netflips.fiap.videos.dominio.video.service;

import br.com.grupo44.netflips.fiap.videos.dominio.categoria.Categoria;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service.ExibicaoService;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.service.UsuarioService;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import br.com.grupo44.netflips.fiap.videos.dominio.video.repository.VideoRepository;
import com.mongodb.DuplicateKeyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ExibicaoService exibicaoService;

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

    public VideoDTO retornaFiltroFormatado(String titulo, String categoriaBusca,LocalDateTime dataPublicacao) throws Exception {
        VideoDTO filtro = new VideoDTO();
        if (titulo != null && !titulo.isBlank()){
            filtro.setTitulo(titulo);
        }
        if (categoriaBusca != null && !categoriaBusca.isBlank()){
            filtro.setCategoriaBuscada(categoriaBusca);
        }

        if (dataPublicacao != null){
            filtro.setDataPublicacao(dataPublicacao);
        }
        return filtro;
    }

    public Flux<Page<VideoDTO>> findAll(VideoDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();

        if (filtro.getDataPublicacao() != null) {
            criteria.and("data_publicacao").lte(filtro.getDataPublicacao());
        }
        if (filtro.getTitulo() != null && !filtro.getTitulo().isBlank()) {
            criteria.and("titulo").regex(filtro.getTitulo(), "i");
        }
        if (filtro.getCategoriaBuscada() != null && !filtro.getCategoriaBuscada().isBlank()){
            Long codigoCategoria = Categoria.Categorias.buscarCodigoPeloNome(filtro.getCategoriaBuscada());
            if (codigoCategoria != null) {
                criteria.and("categorias").in(Collections.singletonList(codigoCategoria));
            }
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
        BeanUtils.copyProperties(videoDTO, entity);

        return videoRepository.save(entity)
                .map(savedEntity -> new VideoDTO(savedEntity))
                .onErrorResume(DuplicateKeyException.class, ex ->
                        Mono.error(new IllegalArgumentException("Video já cadastrado: " + ex.getMessage())))
                .onErrorResume(OptimisticLockingFailureException.class, ex ->
                        videoRepository.findById(videoDTO.getCodigo())
                                .flatMap(updatedEntity -> {
                                    BeanUtils.copyProperties(videoDTO, updatedEntity);
                                    return videoRepository.save(updatedEntity)
                                            .map(entity1 -> new VideoDTO(entity1));
                                })
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Video não encontrado"))))
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new RuntimeException("Erro interno ao criar: " + ex.getMessage())));
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
                        .doOnError(error -> {
                            error.printStackTrace();
                        })
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Video não encontrado")))
        );
    }

    private void  mapperDtoToEntity(VideoDTO dto, Video entity){
         entity.setTitulo(dto.getTitulo());
         entity.setUrl(dto.getUrl());
         entity.setDataPublicacao(dto.getDataPublicacao());
    }

    public Flux<Page<VideoDTO>> recomendarVideos(String codigoUsuario, PageRequest page, VideoDTO videoDTO) {
        Mono<UsuarioDTO> usuarioMono = usuarioService.findById(codigoUsuario);
        UsuarioDTO usuarioDTO = usuarioMono.block();

        if (usuarioDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        List<String> codigosExibicoesRecomendadas = usuarioDTO.getHistoricoExibicao().stream()
                .filter(ExibicaoDTO::getRecomenda)
                .map(ExibicaoDTO::getCodigo)
                .collect(Collectors.toList());

        Flux<ExibicaoDTO> exibicaoDTOFlux = exibicaoService.findExibicoesPorCodigoss(codigosExibicoesRecomendadas);
        List<ExibicaoDTO> exibicoesRecomendadas = exibicaoDTOFlux.collectList().block();

        if (exibicoesRecomendadas == null || exibicoesRecomendadas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma exibição recomendada encontrada");
        }

        Map<Long, Long> categoriasMaisFrequentes = exibicoesRecomendadas.stream()
                .flatMap(exibicao -> exibicao.getVideo().getCategorias().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Long> categoriasStringMaisFrequentes = categoriasMaisFrequentes.entrySet().stream()
                .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));

        List<String> categoriasOrdenadas = categoriasStringMaisFrequentes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<VideoDTO> videosRecomendados = new ArrayList<>();
        Set<String> videosRecomendadosCodigos = new HashSet<>();

        for (String categoria : categoriasOrdenadas) {
            Flux<Page<VideoDTO>> videosPorCategoriaFlux = findAll(new VideoDTO(), PageRequest.of(0, 10));

            List<VideoDTO> videosPorCategoria = videosPorCategoriaFlux.blockFirst().getContent().stream()
                    .filter(v -> v.getCategorias().contains(categoria))
                    .collect(Collectors.toList());
            System.out.println("Videos aqui -> " + videosPorCategoria.toString());
            if (!videosPorCategoria.isEmpty()){
                VideoDTO videoRecomendado = videosPorCategoria.get(0);
                videosRecomendados.add(videoRecomendado);
                videosRecomendadosCodigos.add(videoRecomendado.getCodigo());
            }
        }

        return Flux.just(new PageImpl<>(videosRecomendados, page, videosRecomendados.size()));
    }

}
