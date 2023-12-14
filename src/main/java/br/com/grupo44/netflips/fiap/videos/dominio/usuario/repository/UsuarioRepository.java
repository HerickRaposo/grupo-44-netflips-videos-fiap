package br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario,String> {
}
