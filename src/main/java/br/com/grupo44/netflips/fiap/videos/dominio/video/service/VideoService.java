package br.com.grupo44.netflips.fiap.videos.dominio.video.service;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final MongoTemplate mongoTemplate;

    public VideoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<String> validate(VideoDTO dto){
        Set<ConstraintViolation<VideoDTO>> violacoes = Validation.buildDefaultValidatorFactory().getValidator().validate(dto);
        List<String> violacoesToList = violacoes.stream()
                .map((violacao) -> violacao.getPropertyPath() + ":" + violacao.getMessage())
                .collect(Collectors.toList());
        return violacoesToList;
    }
    public Page<VideoDTO> findAll(VideoDTO filtro, PageRequest page) {
        Criteria criteria = new Criteria();

        if (filtro.getDataPublicacao() != null) {
            criteria.and("data_publicacao").lte(filtro.getDataPublicacao());
        }
        if (filtro.getTitulo() != null && !filtro.getTitulo().isBlank()) {
            criteria.and("titulo").regex(filtro.getTitulo(), "i");
        }

        Query query = new Query(criteria);
        query.with(page);

        List<Video> listaVideos = mongoTemplate.find(query, Video.class);

        if (listaVideos != null && !listaVideos.isEmpty()) {
            List<VideoDTO> listaVideosDTO = new ArrayList<>();
            for (Video video : listaVideos) {
                listaVideosDTO.add(new VideoDTO(video));
            }
            return new PageImpl<>(listaVideosDTO, page, listaVideos.size());
        }

        return Page.empty();
    }


    public VideoDTO findById(String codigo) {
        var video = videoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrada"));
        return new VideoDTO(video);
    }
     @Transactional
    public VideoDTO insert(VideoDTO videoDTO) {
         Video entity = new Video();
         mapperDtoToEntity(videoDTO,entity);
        if (entity.getAutor().getCodigo() != null){
            Usuario autor = usuarioRepository.findById(entity.getAutor().getCodigo()).orElseThrow(() -> new IllegalArgumentException("\"Codigo autor nao encontrado\""));
            entity.setAutor(autor);
        } else{
            entity.setAutor(null);
        }
       try {
           Video videoSalvo = videoRepository.save(entity);
           return new VideoDTO(videoSalvo);
       } catch (DuplicateKeyException e){
           throw new RuntimeException("Artigo ja existe na coleção");
       } catch (OptimisticLockingFailureException ex){
           //Trata erro concorrencia
           //1 - Recupera artigo
           Video atualizado = videoRepository.findById(videoDTO.getCodigo()).orElse(null);
           if (atualizado != null){
               //2- Atualiza campos
               mapperDtoToEntity(videoDTO,atualizado);
               //3- Atualiza status de forma incremental
               atualizado.setVERSION( atualizado.getVERSION() + 1);
               //4 - Tenta salvar novamente
               atualizado = videoRepository.save(atualizado);
               return new VideoDTO(atualizado);
           } else {
               throw new RuntimeException("Artigo não encontrado");
           }
       } catch (Exception ex){
           throw new RuntimeException("Erro interno ao criar: " + ex.getMessage());
       }
    }

    @Transactional
    public VideoDTO update(String codigo, VideoDTO dto) {
        try {
            Video entity = videoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrado"));
            mapperDtoToEntity(dto,entity);
            entity = videoRepository.save(entity);
            return new VideoDTO(entity);
        } catch (OptimisticLockingFailureException ex){
            //Trata erro concorrencia
            //1 - Recupera artigo
            Video atualizado = videoRepository.findById(codigo).orElse(null);
            if (atualizado != null){
                //2- Atualiza campos
                mapperDtoToEntity(dto,atualizado);
                //3- Atualiza status de forma incremental
                atualizado.setVERSION( atualizado.getVERSION() + 1);
                //4 - Tenta salvar novamente
                atualizado = videoRepository.save(atualizado);
                return new VideoDTO(atualizado);
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
        videoRepository.deleteById(codigo);
    }

    private void  mapperDtoToEntity(VideoDTO dto, Video entity){
         entity.setTitulo(dto.getTitulo());
         entity.setUrl(dto.getUrl());
         entity.setDataPublicacao(dto.getDataPublicacao());
    }
}
