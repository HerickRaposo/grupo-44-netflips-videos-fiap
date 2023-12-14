package br.com.grupo44.netflips.fiap.videos.dominio.video.repository;


import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video,String> {
}
