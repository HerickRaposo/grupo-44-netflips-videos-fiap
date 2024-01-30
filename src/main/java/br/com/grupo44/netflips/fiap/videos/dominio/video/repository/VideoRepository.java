package br.com.grupo44.netflips.fiap.videos.dominio.video.repository;


import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VideoRepository extends ReactiveMongoRepository<Video,String> {
}
