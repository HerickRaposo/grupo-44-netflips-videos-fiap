package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ExibicaoRepository extends ReactiveMongoRepository<Exibicao,String> {
}