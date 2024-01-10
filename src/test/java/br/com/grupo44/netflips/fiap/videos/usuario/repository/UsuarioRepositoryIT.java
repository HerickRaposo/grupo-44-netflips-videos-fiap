package br.com.grupo44.netflips.fiap.videos.usuario.repository;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
 class UsuarioRepositoryIT {
    @Autowired
    private  UsuarioRepository usuarioRepository;

    @Autowired
    private  MongoTemplate mongoTemplate;
    @Nested
    class obterUsuario {
        @Test
        void devePermitirListarUsuario() throws Exception {

        }
        @Test
        void devePermitirObterUsuario() throws Exception {
            var id ="cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            var usuarioOpcional = usuarioRepository.findById(id);
            assertThat(usuarioOpcional).isPresent();
            usuarioOpcional.ifPresent(usuario -> {
                assertThat(usuario.getCodigo()).isEqualTo(id);
            });
        }
    }
    @Nested
    class deletarUsuario {
        @Test
        void devePermitirRemoverUsuario()throws Exception{
            var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            usuarioRepository.deleteById(id);
            verify(usuarioRepository,times(1)).deleteById(any(String.class));
        }
        @Test
        void deveGerarExcessaoSeIdNaoExiste()throws Exception {
            assertThatThrownBy(() -> usuarioRepository.findById(String.valueOf(UUID.randomUUID())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Usuario não encontrada");
        }
    }

    @Test
    void devePermitirGerarUsuario(){
        var usuario = ObjectHelper.gerarUsuario();
        var usuarioRecebido = usuarioRepository.save(usuario);
        assertThat(usuarioRecebido).isInstanceOf(Usuario.class).isNotNull();
        assertThat(usuarioRecebido.getCodigo()).isEqualTo(usuario.getCodigo());
    }

    @Test
    void devePermitirAtualizarUsuario()throws Exception {
        var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
        var usuarioOptional = usuarioRepository.findById(id);
        usuarioOptional.ifPresent(usuarioRecebido -> {
            usuarioRecebido.setNome("Novo nome");
            var usuarioSalvo = usuarioRepository.save(usuarioRecebido);
            assertThat(usuarioSalvo.getCodigo()).isEqualTo(usuarioRecebido.getCodigo());
        });

        verify(usuarioRepository,times(1)).findById(any(String.class));
        verify(usuarioRepository,times(1)).save(any(Usuario.class));
    }

}
