package br.com.grupo44.netflips.fiap.videos.dominio.usuario.service;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import com.mongodb.DuplicateKeyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.beans.BeanUtils;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;


    private final ReactiveMongoTemplate reactiveMongoTemplate;


    public UsuarioService(UsuarioRepository usuarioRepository, ReactiveMongoTemplate reactiveMongoTemplate){
        this.usuarioRepository = usuarioRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public List<String> validate(UsuarioDTO dto){
        Set<ConstraintViolation<UsuarioDTO>> violacoes = Validation.buildDefaultValidatorFactory().getValidator().validate(dto);
        List<String> violacoesToList = violacoes.stream()
                .map((violacao) -> violacao.getPropertyPath() + ":" + violacao.getMessage())
                .collect(Collectors.toList());
        return violacoesToList;
    }

    public UsuarioDTO retornaFiltroFormatado(String nome, String email) throws Exception {
        UsuarioDTO filtro = new UsuarioDTO();
        if (nome != null && !nome.isBlank()){
            filtro.setNome(nome);
        }
        if (email != null && !email.isBlank()){
            filtro.setEmail(email);
        }

        return filtro;
    }

    public Flux<Page<UsuarioDTO>> findAll(UsuarioDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();

        if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
            criteria.and("nome").regex(filtro.getNome(), "i");
        }

        if (filtro.getEmail() != null && !filtro.getEmail().isBlank()) {
            criteria.and("login").regex(filtro.getEmail(), "i");
        }

        Query query = new Query(criteria);

        return reactiveMongoTemplate.find(query.with(page), Usuario.class)
                .collectList()
                .map(listaUsuarios -> new PageImpl<>(listaUsuarios, page, listaUsuarios.size()))
                .flux()
                .map(pageResult -> pageResult.map(UsuarioDTO::new));
    }

    public Mono<UsuarioDTO> findById(String codigo) {
        return usuarioRepository.findById(codigo)
                .flatMap(usuario -> {
                    UsuarioDTO usuarioDTO = new UsuarioDTO(usuario, usuario.getHistoricoExibicao());
                    return Mono.just(usuarioDTO);
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado")));
    }


    @Transactional
    public Mono<UsuarioDTO> insert(UsuarioDTO usuarioDTO) {
        Usuario entity = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, entity);

        return usuarioRepository.save(entity)
                .map(savedEntity -> new UsuarioDTO(savedEntity, new ArrayList<>()))
                .onErrorResume(DuplicateKeyException.class, ex ->
                        Mono.error(new IllegalArgumentException("Usuário já cadastrado: " + ex.getMessage())))
                .onErrorResume(OptimisticLockingFailureException.class, ex ->
                        usuarioRepository.findById(usuarioDTO.getCodigo())
                                .flatMap(updatedEntity -> {
                                    BeanUtils.copyProperties(usuarioDTO, updatedEntity);
                                    return usuarioRepository.save(updatedEntity)
                                            .map(entity1 -> new UsuarioDTO(entity1));
                                })
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuário não encontrado"))))
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new RuntimeException("Erro interno ao criar: " + ex.getMessage())));
    }


    @Transactional
    public Mono<UsuarioDTO> update(String codigo, UsuarioDTO usuarioDTO) {
        Usuario entity = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, entity);

        return usuarioRepository.save(entity)
                .map(updatedEntity -> new UsuarioDTO(updatedEntity, updatedEntity.getHistoricoExibicao()))
                .onErrorResume(DuplicateKeyException.class, ex ->
                        Mono.error(new RuntimeException("Usuário já cadastrado: " + ex.getMessage())))
                .onErrorResume(OptimisticLockingFailureException.class, ex ->
                        Mono.error(new RuntimeException("Erro de concorrência ao atualizar o usuário")))
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new RuntimeException("Erro interno ao atualizar: " + ex.getMessage())));
    }

    @Transactional
    public Mono<Void> delete(String codigo) {
        return Mono.fromRunnable(() ->
                usuarioRepository.deleteById(codigo)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado")))
        );
    }
}
