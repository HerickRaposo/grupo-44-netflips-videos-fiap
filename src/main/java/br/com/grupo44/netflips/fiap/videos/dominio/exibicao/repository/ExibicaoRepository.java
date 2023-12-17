package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExibicaoRepository extends MongoRepository<Exibicao,String> {
}