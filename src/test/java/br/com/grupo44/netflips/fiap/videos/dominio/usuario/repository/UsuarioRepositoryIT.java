package br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class UsuarioRepositoryIT {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Nested
    class obterUsuario {

        @Test
        void devePermitirListarUsuario() {
            StepVerifier.create(usuarioRepository.findAll())
                    .expectNextCount(1)
                    .verifyComplete();
        }

        @Test
        void devePermitirObterUsuario() {
            var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            StepVerifier.create(usuarioRepository.findById(id))
                    .assertNext(usuario -> assertThat(usuario.getCodigo()).isEqualTo(id))
                    .verifyComplete();
        }
    }

    @Nested
    class deletarUsuario {

        @Test
        void devePermitirRemoverUsuario() {
            var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            StepVerifier.create(usuarioRepository.deleteById(id))
                    .verifyComplete();
        }

        @Test
        void deveGerarExcecaoSeIdNaoExiste() {
            StepVerifier.create(usuarioRepository.findById(UUID.randomUUID().toString()))
                    .consumeErrorWith(throwable -> {
                        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                        assertThat(throwable.getMessage()).isEqualTo("Usuario não encontrada");
                    }).verify();
        }
    }

    @Test
    void devePermitirGerarUsuario() {
        var usuario = ObjectHelper.gerarUsuario();
        StepVerifier.create(usuarioRepository.save(usuario))
                .assertNext(usuarioRecebido -> {
                    assertThat(usuarioRecebido).isInstanceOf(Usuario.class).isNotNull();
                    assertThat(usuarioRecebido.getCodigo()).isEqualTo(usuario.getCodigo());
                })
                .verifyComplete();
    }

    @Test
    void devePermitirAtualizarUsuario() {
        var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";

        StepVerifier.create(usuarioRepository.findById(id))
                .assertNext(usuarioRecebido -> {
                    usuarioRecebido.setNome("Novo nome");
                    Mono<Usuario> usuarioSalvoMono = usuarioRepository.save(usuarioRecebido);

                    StepVerifier.create(usuarioSalvoMono)
                            .assertNext(usuarioSalvo -> assertThat(usuarioSalvo.getCodigo()).isEqualTo(usuarioRecebido.getCodigo()))
                            .verifyComplete();
                }).verifyComplete();
    }
}