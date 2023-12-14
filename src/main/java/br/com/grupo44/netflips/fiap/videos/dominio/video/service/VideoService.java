package br.com.grupo44.netflips.fiap.videos.dominio.video.service;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import br.com.grupo44.netflips.fiap.videos.dominio.video.repository.VideoRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<VideoDTO> findAll(String titulo, LocalDateTime dataPublicacao) {
        Criteria criteria = new Criteria();
        if (dataPublicacao != null){
            criteria.and("data_publicacao").lte(dataPublicacao);
        }
        if (titulo != null && !titulo.isBlank()){
            criteria.and("titulo").regex(titulo,"i");
        }
        Query query = new Query(criteria);
        List<Video> listaVideos = mongoTemplate.find(query,Video.class);
        if (listaVideos != null && !listaVideos.isEmpty()){
            List<VideoDTO> listaVideosDTO = new ArrayList<>();
            for(Video video: listaVideos){
                listaVideosDTO.add(new VideoDTO(video, video.getAutor()));
            }
            return listaVideosDTO;
        }
        return null;
    }

    public VideoDTO findById(String codigo) {
        var video = videoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrada"));
        return new VideoDTO(video,video.getAutor());
    }
     @Transactional
    public ResponseEntity<?> insert(VideoDTO videoDTO) {
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
           return ResponseEntity.status(HttpStatus.CREATED).body(new VideoDTO(videoSalvo, videoSalvo.getAutor()));
       } catch (DuplicateKeyException e){
           return ResponseEntity.status(HttpStatus.CONFLICT).body("Artigo ja existe na coleção");
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
               return ResponseEntity.status(HttpStatus.CREATED).body(new VideoDTO(atualizado, atualizado.getAutor()));
           } else {
               throw new RuntimeException("Artigo não encontrado");
           }
       } catch (Exception ex){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao criar: " + ex.getMessage());
       }
    }

    @Transactional
    public ResponseEntity<?> update(String codigo, VideoDTO dto) {
        try {
            Video entity = videoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Video não encontrado"));
            mapperDtoToEntity(dto,entity);
            entity = videoRepository.save(entity);
            return ResponseEntity.status(HttpStatus.OK).body(new VideoDTO(entity, entity.getAutor()));
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
                return ResponseEntity.status(HttpStatus.OK).body(new VideoDTO(atualizado, atualizado.getAutor()));
            } else {
                throw new RuntimeException("Artigo não encontrado");
            }
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao atualizar: " + ex.getMessage());
        }
    }

    @Transactional
    public void deleteById(String codigo){
        videoRepository.deleteById(codigo);
    }

    private void  mapperDtoToEntity(VideoDTO dto, Video entity){
         entity.setTitulo(dto.getTitulo());
         entity.setUrl(dto.getUrl());
         entity.setDataPublicacao(dto.getDataPublicacao());
        usuarioRepository.findById(dto.getAutor().getCodigo());
    }
}
