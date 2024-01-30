package br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
    }

    // Limpa variáveis da memória
    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class obterUsuario {

        @Test
        void devePermitirListarUsuario() {
            Usuario usuario1 = ObjectHelper.gerarUsuario();
            Usuario usuario2 = ObjectHelper.gerarUsuario();
            when(usuarioRepository.findAll()).thenReturn(Flux.just(usuario1, usuario2));

            Flux<Usuario> usuariosRecebidos = usuarioRepository.findAll();
            StepVerifier.create(usuariosRecebidos)
                    .expectNext(usuario1, usuario2)
                    .verifyComplete();

            verify(usuarioRepository, times(1)).findAll();
        }

        @Test
        void devePermitirBuscarMensagem() {
            var usuario = ObjectHelper.gerarUsuario();
            when(usuarioRepository.findById(any(String.class))).thenReturn(Mono.just(usuario));

            Mono<Usuario> usuarioMono = usuarioRepository.findById(usuario.getCodigo());
            StepVerifier.create(usuarioMono)
                    .expectNext(usuario)
                    .verifyComplete();

            verify(usuarioRepository, times(1)).findById(any(String.class));
        }

        @Test
        void deveLancarExcessaoSeIdBuscadoNaoExiste() {
            String idNaoExistente = UUID.randomUUID().toString();
            when(usuarioRepository.findById(any(String.class))).thenReturn(Mono.empty());

            StepVerifier.create(usuarioRepository.findById(idNaoExistente))
                    .expectErrorSatisfies(error -> {
                        assertThat(error).isInstanceOf(IllegalArgumentException.class);
                        assertThat(error.getMessage()).isEqualTo("Usuario não encontrada");
                    })
                    .verify();

            verifyNoMoreInteractions(usuarioRepository);
        }

    }

    @Nested
    class deletarUsuario {

        @Test
        void devePermitirDeletarUsuario() {
            var id = UUID.randomUUID().toString();
            when(usuarioRepository.deleteById(any(String.class))).thenReturn(Mono.empty());

            StepVerifier.create(usuarioRepository.deleteById(id))
                    .verifyComplete();

            verify(usuarioRepository, times(1)).deleteById(any(String.class));
        }

        @Test
        void deveLancarExcessaoSeIdNaoExiste() {
            String idNaoExistente = UUID.randomUUID().toString();
            when(usuarioRepository.deleteById(any(String.class))).thenReturn(Mono.error(new IllegalArgumentException("Usuario não encontrada")));

            StepVerifier.create(usuarioRepository.deleteById(idNaoExistente))
                    .expectErrorSatisfies(error -> {
                        assertThat(error).isInstanceOf(IllegalArgumentException.class);
                        assertThat(error.getMessage()).isEqualTo("Usuario não encontrada");
                    })
                    .verify();

            verifyNoMoreInteractions(usuarioRepository);
        }
    }

    @Test
    void devePermitirInserirUsuario() {
        var usuario = ObjectHelper.gerarUsuario();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioRepository.save(usuario))
                .expectNext(usuario)
                .verifyComplete();

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void devePermitirAtualizarUsuario()throws Exception {
        var usuario = ObjectHelper.gerarUsuario();
        when(usuarioRepository.findById(any(String.class))).thenReturn(Mono.just(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        Mono<Usuario> usuarioMono = usuarioRepository.save(usuario);
        StepVerifier.create(usuarioMono)
                .expectNext(usuario)
                .verifyComplete();

        verify(usuarioRepository, times(1)).findById(any(String.class));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

}
