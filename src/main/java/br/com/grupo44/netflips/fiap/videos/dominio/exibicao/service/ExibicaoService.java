package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository.ExibicaoRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import br.com.grupo44.netflips.fiap.videos.dominio.video.repository.VideoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExibicaoService {
    @Autowired
    private ExibicaoRepository exibicaoRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public ExibicaoService(ExibicaoRepository exibicaoRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.exibicaoRepository = exibicaoRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }


    public List<String> validate(ExibicaoDTO dto){
        Set<ConstraintViolation<ExibicaoDTO>> violacoes = Validation.buildDefaultValidatorFactory().getValidator().validate(dto);
        List<String> violacoesToList = violacoes.stream()
                .map((violacao) -> violacao.getPropertyPath() + ":" + violacao.getMessage())
                .collect(Collectors.toList());
        return violacoesToList;
    }

    public Flux<Page<ExibicaoDTO>> findAll(ExibicaoDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();

        if (filtro.getUsuario().getCodigo() != null) {
            criteria.and("usuario.codigo").is(filtro.getUsuario().getCodigo());
        }
        if (filtro.getVideo().getCodigo() != null) {
            criteria.and("video.codigo").is(filtro.getVideo().getCodigo());
        }

        Query query = new Query(criteria).with(page);

        return reactiveMongoTemplate.find(query.with(page), Exibicao.class)
                .collectList()
                .map(listaExibicao -> new PageImpl<>(listaExibicao, page, listaExibicao.size()))
                .flux()
                .map(pageResult -> pageResult.map(ExibicaoDTO::new));
    }

    public Mono<ExibicaoDTO> findById(String codigo) {
        return exibicaoRepository.findById(codigo)
                .map(exibicao -> new ExibicaoDTO(exibicao, exibicao.getUsuario(), exibicao.getVideo()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Video não encontrado")));
    }

    @Transactional
    public Mono<ExibicaoDTO> insert(ExibicaoDTO dto) {
        Exibicao entity = new Exibicao();
        mapperDtoToEntity(dto, entity);

        return saveExibicao(entity)
                .flatMap(savedExibicaoDTO -> {
                    if (entity.getUsuario().getCodigo() != null) {
                        return usuarioRepository.findById(entity.getUsuario().getCodigo())
                                .flatMap(usuario -> {
                                    usuario.getHistoricoExibicao().add(entity);
                                    return usuarioRepository.save(usuario);
                                })
                                .thenReturn(savedExibicaoDTO);
                    } else {
                        return Mono.just(savedExibicaoDTO);
                    }
                });
    }

    @Transactional
    public Mono<ExibicaoDTO> update(String codigo, ExibicaoDTO dto) {
        return exibicaoRepository.findById(codigo)
                .flatMap(entity -> {
                    mapperDtoToEntity(dto, entity);
                    return saveExibicao(entity)
                            .flatMap(savedExibicaoDTO -> {
                                if (entity.getUsuario() != null) {
                                    return usuarioRepository.findById(entity.getUsuario().getCodigo())
                                            .flatMap(usuario -> {
                                                for (Exibicao exibicaoUsuario : usuario.getHistoricoExibicao()) {
                                                    if (exibicaoUsuario.getCodigo().equals(entity.getCodigo())) {
                                                        BeanUtils.copyProperties(entity, exibicaoUsuario);
                                                    }
                                                }
                                                return usuarioRepository.save(usuario);
                                            })
                                            .thenReturn(savedExibicaoDTO);
                                } else {
                                    return Mono.just(savedExibicaoDTO);
                                }
                            });
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exibição não encontrada")));
    }

    @Transactional
    public Mono<Void> delete(String codigo) {
        return exibicaoRepository.findById(codigo)
                .flatMap(exibicao -> {
                    Usuario usuario = usuarioRepository.findById(exibicao.getUsuario().getCodigo())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario não encontrado")))
                            .block();
                    usuario.getHistoricoExibicao().removeIf(exibicaoUsuario -> exibicaoUsuario.getCodigo().equals(codigo));
                    return usuarioRepository.save(usuario)
                            .then(exibicaoRepository.deleteById(codigo));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exibicao não encontrada")));
    }

    private Mono<ExibicaoDTO> saveExibicao(Exibicao entity) {
        return reactiveMongoTemplate.save(entity)
                .map(savedExibicao -> new ExibicaoDTO(savedExibicao, savedExibicao.getUsuario(), savedExibicao.getVideo()));
    }

    private void mapperDtoToEntity(ExibicaoDTO dto, Exibicao entity) {
        entity.setDataVisualizacao(dto.getDataVisualizacao());
        entity.setPontuacao(dto.getPontuacao());
        entity.setDataVisualizacao(dto.getDataVisualizacao());
        entity.setRecomenda(dto.getRecomenda());

        if (dto.getUsuario() != null && dto.getUsuario().getCodigo() != null) {
            usuarioRepository.findById(dto.getUsuario().getCodigo())
                    .doOnNext(usuario -> entity.setUsuario(usuario))
                    .switchIfEmpty(Mono.defer(() -> Mono.justOrEmpty(entity.getUsuario())))
                    .subscribe();
        }

        if (dto.getVideo() != null && dto.getVideo().getCodigo() != null) {
            videoRepository.findById(dto.getVideo().getCodigo())
                    .doOnNext(video -> entity.setVideo(video))
                    .switchIfEmpty(Mono.defer(() -> Mono.justOrEmpty(entity.getVideo())))
                    .subscribe();
        }
    }

}
