package br.com.grupo44.netflips.fiap.videos.dominio.usuario.service;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private final MongoTemplate mongoTemplate;

    public UsuarioService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<UsuarioDTO> findAll(String nome, String login) {
        Criteria criteria = new Criteria();
        if (nome != null && !nome.isBlank()) {
            criteria.and("nome").regex(nome, "i");
        }
        if (login != null && !login.isBlank()) {
            criteria.and("login").regex(login, "i");
        }
        Query query = new Query(criteria);
        List<Usuario> listaUsuarios = mongoTemplate.find(query, Usuario.class);
        if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
            List<UsuarioDTO> listaUsuariosDTO = new ArrayList<>();
            for (Usuario usuario : listaUsuarios) {
                listaUsuariosDTO.add(new UsuarioDTO(usuario));
            }
            return listaUsuariosDTO;
        }
        return null;
    }

    public UsuarioDTO findById(String codigo) {
        var usuario = usuarioRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Usuario não encontrada"));
        return new UsuarioDTO(usuario);
    }

    @Transactional
    public ResponseEntity<?> insert(UsuarioDTO usuarioDTO) {
        Usuario entity = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, entity);
        try {
            Usuario usuarioSalvo = usuarioRepository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(usuarioSalvo));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Artigo ja existe na coleção");
        } catch (OptimisticLockingFailureException ex) {
            Usuario atualizado = usuarioRepository.findById(usuarioDTO.getCodigo()).orElse(null);
            if (atualizado != null) {
                BeanUtils.copyProperties(usuarioDTO, entity);
                atualizado.setVERSION(atualizado.getVERSION() + 1);
                atualizado = usuarioRepository.save(atualizado);
                return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(atualizado));
            } else {
                throw new RuntimeException("Artigo não encontrado");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao criar: " + ex.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> update(String codigo, UsuarioDTO dto) {
        try {
            Usuario entity = usuarioRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrado"));
            BeanUtils.copyProperties(dto, entity);
            entity = usuarioRepository.save(entity);
            return ResponseEntity.status(HttpStatus.OK).body(new UsuarioDTO(entity));
        } catch (OptimisticLockingFailureException ex) {
            Usuario atualizado = usuarioRepository.findById(codigo).orElse(null);
            if (atualizado != null) {
                BeanUtils.copyProperties(dto, atualizado);
                atualizado.setVERSION(atualizado.getVERSION() + 1);
                atualizado = usuarioRepository.save(atualizado);
                return ResponseEntity.status(HttpStatus.OK).body(new UsuarioDTO(atualizado));
            } else {
                throw new RuntimeException("Artigo não encontrado");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao atualizar: " + ex.getMessage());
        }
    }

    @Transactional
    public void deleteById(String codigo) {
        usuarioRepository.deleteById(codigo);
    }
}
