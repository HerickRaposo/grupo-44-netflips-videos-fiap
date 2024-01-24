package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository.ExibicaoRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
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

import java.util.ArrayList;
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


    public List<String> validate(ExibicaoDTO dto, boolean isUpdate) {
        Set<ConstraintViolation<ExibicaoDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(dto);

        if (isUpdate) {
            violations.removeIf(violation ->
                    "usuario".equals(violation.getPropertyPath().toString()) ||
                            "video".equals(violation.getPropertyPath().toString())
            );
        }

        List<String> violationsToList = violations.stream()
                .map(violation -> violation.getPropertyPath() + ":" + violation.getMessage())
                .collect(Collectors.toList());

        return violationsToList;
    }

    public Flux<Page<ExibicaoDTO>> findAll(ExibicaoDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();

        if (filtro.getUsuario() != null && filtro.getUsuario().getCodigo() != null) {
            criteria.and("usuario.codigo").is(filtro.getUsuario().getCodigo());
        }
        if (filtro.getVideo() != null && filtro.getVideo().getCodigo() != null) {
            criteria.and("video.codigo").is(filtro.getVideo().getCodigo());
        }

        Query query = new Query(criteria).with(page);

        return reactiveMongoTemplate.find(query.with(page), Exibicao.class)
                .collectList()
                .map(listaExibicao -> new PageImpl<>(listaExibicao, page, listaExibicao.size()))
                .map(pageResult -> pageResult.map(exibicao -> new ExibicaoDTO(exibicao, exibicao.getUsuario(), exibicao.getVideo())))
                .flux();
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
                                    if (usuario.getHistoricoExibicao() == null){
                                        usuario.setHistoricoExibicao(new ArrayList<>());
                                    }
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
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exibição não encontrada")))
                .flatMap(entity -> {
                    // Mapear os dados do DTO para a entidade recuperada
                    mapperDtoToEntity(dto, entity);

                    // Salvar a entidade no repositório
                    return saveExibicao(entity)
                            .flatMap(savedExibicaoDTO -> {
                                if (entity.getUsuario() != null) {
                                    String usuarioCodigo = entity.getUsuario().getCodigo();
                                    return usuarioRepository.findById(usuarioCodigo)
                                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuário não encontrado")))
                                            .flatMap(usuario -> {
                                                // Atualizar a lista de histórico de exibição do usuário
                                                for (Exibicao exibicaoUsuario : usuario.getHistoricoExibicao()) {
                                                    if (exibicaoUsuario.getCodigo().equals(codigo)) {
                                                        BeanUtils.copyProperties(entity, exibicaoUsuario);
                                                    }
                                                }

                                                // Salvar o usuário com a lista atualizada
                                                return usuarioRepository.save(usuario)
                                                        .thenReturn(savedExibicaoDTO);
                                            });
                                } else {
                                    // Se não houver informações de usuário, apenas retornar a entidade salva
                                    return Mono.just(savedExibicaoDTO);
                                }
                            });
                });
    }

    @Transactional
    public Mono<Void> delete(String codigo) {
        return exibicaoRepository.findById(codigo)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Exibicao não encontrada")))
                .flatMap(exibicao -> {
                    String usuarioCodigo = exibicao.getUsuario().getCodigo();
                    return exibicaoRepository.deleteById(codigo)
                            .then(usuarioRepository.findById(usuarioCodigo))
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario não encontrado")))
                            .flatMap(usuario -> {
                                boolean removed = usuario.getHistoricoExibicao().removeIf(exibicaoUsuario -> exibicaoUsuario.getCodigo().equals(codigo));

                                if (removed) {
                                    return usuarioRepository.save(usuario).then();
                                } else {
                                    return Mono.empty();
                                }
                            });
                });
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
        entity.setVisualizado(dto.getVisualizado());

        if (dto.getUsuario() != null && dto.getUsuario().getCodigo() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuario().getCodigo()).block();
            entity.setUsuario(usuario);
        }

        if (dto.getVideo() != null && dto.getVideo().getCodigo() != null) {
            Video video = videoRepository.findById(dto.getVideo().getCodigo()).block();
            entity.setVideo(video);
        }
    }

}
