package br.com.grupo44.netflips.fiap.videos.usuario.repository;


import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

 class UsuarioRepositoryTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
    }

    //Limpa variaveis da memoria
    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class obterUsuario {
        @Test
        void devePermitirListarUsuario() throws Exception {
            Usuario usuario1 = ObjectHelper.gerarUsuario();
            Usuario usuario2 = ObjectHelper.gerarUsuario();
            when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));
            var usariosRecebiodos = usuarioRepository.findAll();
            assertThat(usariosRecebiodos).hasSize(2).containsExactlyInAnyOrder(usuario1, usuario2);
            verify(usuarioRepository, times(1)).findAll();
        }

        @Test
        void devePermitirBuscarMensagem() throws Exception {
            var usuario = ObjectHelper.gerarUsuario();
            when(usuarioRepository.findById(any(String.class))).thenReturn(Optional.of(usuario));
            var mensagemOpcional = usuarioRepository.findById(usuario.getCodigo());
            assertThat(mensagemOpcional).isPresent().containsSame(usuario);
            mensagemOpcional.ifPresent(usuarioRecebido -> {
                assertThat(usuarioRecebido.getCodigo()).isEqualTo(usuario.getCodigo());
            });
            verify(usuarioRepository, times(1)).findById(any(String.class));
        }

        @Test
        void deveLancarExcessaoSeIdBuscadoNaoExiste() throws Exception {
            assertThatThrownBy(() -> usuarioRepository.findById(String.valueOf(UUID.randomUUID())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Usuario não encontrada");
        }
    }
    @Nested
    class deletarUsuario {
        @Test
         void devePermitirDeletarUsuario()throws Exception {
            var id = String.valueOf( UUID.randomUUID());
            doNothing().when(usuarioRepository).deleteById(any(String.class));
            usuarioRepository.deleteById(id);
            verify(usuarioRepository,times(1)).deleteById(any(String.class));
        }
        @Test
        void deveLancarExcessaoSeIdBNaoExiste()throws Exception  {
            assertThatThrownBy(() -> usuarioRepository.deleteById(String.valueOf(UUID.randomUUID())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Usuario não encontrada");
        }
    }
    @Test
    void devePermitirInserirUsuario()throws Exception {
        //ARANGE
        var usuario = ObjectHelper.gerarUsuario();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        //ACT
        var mensagemCriada = usuarioRepository.save(usuario);

        //Assert
        assertThat(mensagemCriada).isNotNull().isEqualTo(usuario);
        verify(usuarioRepository,times(1)).save(any(Usuario.class));
    }

    @Test
    void devePermitirAtualizarUsuario()throws Exception {
        var usuario = ObjectHelper.gerarUsuario();
        when(usuarioRepository.findById(any(String.class))).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var usuarioOptional = usuarioRepository.findById(usuario.getCodigo());
        assertThat(usuarioOptional).isPresent().containsSame(usuario);
        usuarioOptional.ifPresent(usuarioRecebido -> {
            usuarioRecebido.setNome("Novo nome");
            var usuarioSalvo = usuarioRepository.save(usuarioRecebido);
            assertThat(usuarioSalvo.getCodigo()).isEqualTo(usuarioRecebido.getCodigo());
        });

        verify(usuarioRepository,times(1)).findById(any(String.class));
        verify(usuarioRepository,times(1)).save(any(Usuario.class));
    }
}
