package br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UsuarioRepository extends ReactiveMongoRepository<Usuario,String> {
}
