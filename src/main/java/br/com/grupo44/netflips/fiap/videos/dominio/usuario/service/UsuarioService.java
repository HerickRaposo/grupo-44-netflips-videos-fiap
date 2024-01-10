package br.com.grupo44.netflips.fiap.videos.dominio.usuario.service;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import com.mongodb.DuplicateKeyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private final MongoTemplate mongoTemplate;


    public UsuarioService(UsuarioRepository usuarioRepository, MongoTemplate mongoTemplate){
        this.usuarioRepository = usuarioRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<String> validate(UsuarioDTO dto){
        Set<ConstraintViolation<UsuarioDTO>> violacoes = Validation.buildDefaultValidatorFactory().getValidator().validate(dto);
        List<String> violacoesToList = violacoes.stream()
                .map((violacao) -> violacao.getPropertyPath() + ":" + violacao.getMessage())
                .collect(Collectors.toList());
        return violacoesToList;
    }
    public UsuarioDTO retornaFiltroFormatado(String nome, String login){
        UsuarioDTO filtro= new UsuarioDTO();
        if (nome != null && !nome.isBlank()){
            filtro.setNome(nome);
        }
        if (login != null && !login.isBlank()){
            filtro.setEmail(login);
        }
        return filtro;
    }
    public Page<UsuarioDTO> findAll(UsuarioDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();
        if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
            criteria.and("nome").regex(filtro.getNome(), "i");
        }
        if (filtro.getEmail() != null && !filtro.getEmail().isBlank()) {
            criteria.and("login").regex(filtro.getEmail(), "i");
        }
        Query query = new Query(criteria);
        List<Usuario> listaUsuarios = mongoTemplate.find(query, Usuario.class);
        if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
            List<UsuarioDTO> listaUsuariosDTO = new ArrayList<>();
            for (Usuario usuario : listaUsuarios) {
                listaUsuariosDTO.add(new UsuarioDTO(usuario));
            }
            return new PageImpl<>(listaUsuariosDTO, page, listaUsuarios.size());
        }

        return Page.empty();
    }

    public UsuarioDTO findById(String codigo) {
        var usuario = usuarioRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada"));
        return new UsuarioDTO(usuario);
    }

    @Transactional
    public UsuarioDTO insert(UsuarioDTO usuarioDTO) {
        Usuario entity = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, entity);

        try {
            Usuario usuarioSalvo = usuarioRepository.save(entity);
            return new UsuarioDTO(usuarioSalvo);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Usuário já cadastrado: " + e.getMessage());
        } catch (OptimisticLockingFailureException ex) {
            Usuario atualizado = usuarioRepository.findById(usuarioDTO.getCodigo()).orElse(null);

            if (atualizado != null) {
                BeanUtils.copyProperties(usuarioDTO, atualizado);
                atualizado = usuarioRepository.save(atualizado);
                return new UsuarioDTO(atualizado);
            } else {
                throw new RuntimeException("Usuário não encontrado");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erro interno ao criar: " + ex.getMessage());
        }
    }


    @Transactional
    public UsuarioDTO update(String codigo, UsuarioDTO dto) {
        try {
            Usuario entity = usuarioRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrado"));
            BeanUtils.copyProperties(dto, entity);
            entity.setCodigo(codigo);
            entity = usuarioRepository.save(entity);
            return new UsuarioDTO(entity);
        } catch (OptimisticLockingFailureException ex) {
            Usuario atualizado = usuarioRepository.findById(codigo).orElse(null);
            if (atualizado != null) {
                BeanUtils.copyProperties(dto, atualizado);
                atualizado.setCodigo(codigo);
                atualizado.setVERSION(atualizado.getVERSION() + 1);
                atualizado = usuarioRepository.save(atualizado);
                return new UsuarioDTO(atualizado);
            } else {
                throw new RuntimeException("Artigo não encontrado");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erro interno ao Atualizar: " + ex.getMessage());
        }
    }

    @Transactional
    public void delete(String codigo) {
        usuarioRepository.deleteById(codigo);
    }
}
