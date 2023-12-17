package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository.ExibicaoRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final MongoTemplate mongoTemplate;

    public ExibicaoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<String> validate(ExibicaoDTO dto){
        Set<ConstraintViolation<ExibicaoDTO>> violacoes = Validation.buildDefaultValidatorFactory().getValidator().validate(dto);
        List<String> violacoesToList = violacoes.stream()
                .map((violacao) -> violacao.getPropertyPath() + ":" + violacao.getMessage())
                .collect(Collectors.toList());
        return violacoesToList;
    }
    public Page<ExibicaoDTO> findAll(ExibicaoDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();

        if (filtro.getUsuario().getCodigo() != null) {
            criteria.and("usuario.codigo").is(filtro.getUsuario().getCodigo());
        }
        if (filtro.getVideo().getCodigo() != null) {
            criteria.and("video.codigo").is(filtro.getVideo().getCodigo());
        }

        Query query = new Query(criteria);
        query.with(page);

        List<Exibicao> listaExibicao = mongoTemplate.find(query, Exibicao.class);

        if (listaExibicao != null && !listaExibicao.isEmpty()) {
            List<ExibicaoDTO> listaExibicaoDTO = new ArrayList<>();
            for (Exibicao exibicao : listaExibicao) {
                listaExibicaoDTO.add(new ExibicaoDTO(exibicao, exibicao.getUsuario(),exibicao.getVideo()));
            }
            return new PageImpl<>(listaExibicaoDTO, page, listaExibicao.size());
        }

        return Page.empty();
    }


    public ExibicaoDTO findById(String codigo) {
        var exibicao = exibicaoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrada"));
        return new ExibicaoDTO(exibicao, exibicao.getUsuario(),exibicao.getVideo());
    }
    @Transactional
    public ExibicaoDTO insert(ExibicaoDTO dto) {
        Exibicao entity = new Exibicao();
        mapperDtoToEntity(dto,entity);
        if (entity.getUsuario().getCodigo() != null){
            Usuario usuario = usuarioRepository.findById(entity.getUsuario().getCodigo()).orElseThrow(() -> new IllegalArgumentException("Codigo autor nao encontrado"));
            usuario.getHistoricoExibicao().add(entity);
            usuario = usuarioRepository.save(usuario);
            entity.setUsuario(usuario);
        }
        try {
            Exibicao exibicaoSalva = exibicaoRepository.save(entity);
            return new ExibicaoDTO(exibicaoSalva, exibicaoSalva.getUsuario(),exibicaoSalva.getVideo());
        } catch (DuplicateKeyException e){
            throw new RuntimeException("Artigo ja existe na coleção");
        } catch (OptimisticLockingFailureException ex){
            //Trata erro concorrencia
            //1 - Recupera artigo
            Exibicao atualizado = exibicaoRepository.findById(dto.getCodigo()).orElse(null);
            if (atualizado != null){
                //2- Atualiza campos
                mapperDtoToEntity(dto,atualizado);
                //3- Atualiza status de forma incremental
                atualizado.setVERSION( atualizado.getVERSION() + 1);
                //4 - Tenta salvar novamente
                if (entity.getUsuario().getCodigo() != null){
                    Usuario usuario = usuarioRepository.findById(entity.getUsuario().getCodigo()).orElseThrow(() -> new IllegalArgumentException("Codigo autor nao encontrado"));
                    usuario.getHistoricoExibicao().add(entity);
                    usuario = usuarioRepository.save(usuario);
                    entity.setUsuario(usuario);
                }
                atualizado = exibicaoRepository.save(atualizado);
                return new ExibicaoDTO(atualizado, atualizado.getUsuario(),atualizado.getVideo());
            } else {
                throw new RuntimeException("Artigo não encontrado");
            }
        } catch (Exception ex){
            throw new RuntimeException("Erro interno ao criar: " + ex.getMessage());
        }
    }

    @Transactional
    public ExibicaoDTO update(String codigo, ExibicaoDTO dto) {
        try {
            Exibicao entity = exibicaoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrado"));
            mapperDtoToEntity(dto,entity);
            entity = exibicaoRepository.save(entity);

            //Realiza alteração nos dados da lista de exibição do usuario
            if (entity.getUsuario()!= null){
                Usuario usuario = usuarioRepository.findById(entity.getUsuario().getCodigo()).orElseThrow(() -> new IllegalArgumentException("Usuario não encontrado"));
                for (Exibicao exibicaoUsuario : usuario.getHistoricoExibicao()){
                    if (exibicaoUsuario.getCodigo().equals(entity.getCodigo())){
                        BeanUtils.copyProperties(entity, exibicaoUsuario);
                    }
                }
                usuarioRepository.save(usuario);
            }
            return new ExibicaoDTO(entity,entity.getUsuario(),entity.getVideo());

        } catch (OptimisticLockingFailureException ex){
            //Trata erro concorrencia
            //1 - Recupera artigo
            Exibicao atualizado = exibicaoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Exibição não encontrada"));
            if (atualizado != null){
                //2- Atualiza campos
                mapperDtoToEntity(dto,atualizado);
                //3- Atualiza status de forma incremental
                atualizado.setVERSION( atualizado.getVERSION() + 1);
                //4 - Tenta salvar novamente
                atualizado = exibicaoRepository.save(atualizado);
                if (atualizado.getUsuario()!= null){
                    Usuario usuario = usuarioRepository.findById(atualizado.getUsuario().getCodigo()).orElseThrow(() -> new IllegalArgumentException("Usuario não encontrado"));

                    for (Exibicao exibicaoUsuario : usuario.getHistoricoExibicao()){
                        if (exibicaoUsuario.getCodigo().equals(atualizado.getCodigo())){
                            BeanUtils.copyProperties(atualizado, exibicaoUsuario);
                        }
                    }
                    usuarioRepository.save(usuario);
                }
                return new ExibicaoDTO(atualizado, atualizado.getUsuario(),atualizado.getVideo());
            } else {
                throw new RuntimeException("Artigo não encontrado");
            }
        } catch (Exception ex){
            String mensagem = "Erro interno ao atualizar: " + ex.getMessage();
            throw new RuntimeException( "Erro interno ao atualizar: " + mensagem);
        }
    }

    @Transactional
    public void delete(String codigo){
        Exibicao exibicao = exibicaoRepository.findById(codigo).orElseThrow(()-> new IllegalArgumentException("Exibicao não encontrada"));
        Usuario usuario = usuarioRepository.findById(exibicao.getUsuario().getCodigo()).orElseThrow(()-> new IllegalArgumentException("Usuario não encontrado"));
        if (usuario.getHistoricoExibicao()!= null && !usuario.getHistoricoExibicao().isEmpty()){
            Optional<Exibicao> first = usuario.getHistoricoExibicao().stream().filter(exibicaoUsuario -> exibicaoUsuario.getCodigo().equals(codigo)).findFirst();
            if (first.isPresent()){
                usuario.getHistoricoExibicao().remove(first.get());
            }
        }
        exibicaoRepository.deleteById(codigo);

    }

        private void  mapperDtoToEntity(ExibicaoDTO dto, Exibicao entity){
        entity.setDataVisualizacao(dto.getDataVisualizacao());
        entity.setPontuacao(dto.getPontuacao());
        entity.setDataVisualizacao(dto.getDataVisualizacao());
        entity.setRecomenda(dto.getRecomenda());
        entity.setUsuario(usuarioRepository.findById(dto.getUsuario().getCodigo()).orElse(null));
        entity.setVideo(videoRepository.findById(dto.getVideo().getCodigo()).orElse(null));
    }
}
