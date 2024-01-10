package br.com.grupo44.netflips.fiap.videos.usuario.service;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UsuarioServiceTest {
    private UsuarioService usuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;
    private MongoTemplate mongoTemplate;


    AutoCloseable mock;

    //inicia todas variaveis de mocks na memoria
    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioService(usuarioRepository, mongoTemplate);
    }

    //Limpa variaveis da memoria
    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class obterUsuario {
        @Test
        void devePermitirListarUsuario() {
            List<Usuario> listaUsuarios = Arrays.asList(ObjectHelper.gerarUsuario(), ObjectHelper.gerarUsuario());
            when(mongoTemplate.find(any(Query.class), eq(Usuario.class))).thenReturn(listaUsuarios);
            Page<UsuarioDTO> resultadoObtido = usuarioService.findAll(new UsuarioDTO(), PageRequest.of(0, 10));
            assertThat(resultadoObtido.getContent()).hasSize(2);
            verify(usuarioRepository, times(1)).findById(any(String.class));
        }

        @Test
        void devePermitiBuscarMensagem() {
            var usuario = ObjectHelper.gerarUsuario();
            when(usuarioRepository.findById(any(String.class))).thenReturn(Optional.of(usuario));
            var usuarioObtido = usuarioService.findById(usuario.getCodigo());
            assertThat(usuarioObtido).isEqualTo(new UsuarioDTO(usuario));
            verify(usuarioRepository, times(1)).findById(any(String.class));
        }

        @Test
        void deveLancarExcessaoSeCodigoNaoExistir() {
            var id = String.valueOf(UUID.randomUUID());
            when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
            assertThatThrownBy(()-> usuarioService.findById(id))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuario não encontrado");
            verify(usuarioRepository, times(1)).findById(any(String.class));
        }
    }
    @Nested
    class atualizarMensagem{
        @Test
        void devePermitirAtualizarMensagem(){
            var usuarioAntigo = ObjectHelper.gerarUsuario();
            var usuarioNovo = new Usuario();
            BeanUtils.copyProperties(usuarioAntigo, usuarioNovo);
            usuarioNovo.setNome("Joao");
            when(usuarioRepository.findById(usuarioAntigo.getCodigo())).thenReturn(Optional.of(usuarioNovo));
            when(usuarioRepository.save(usuarioNovo)).thenAnswer(i -> i.getArgument(0));
            var usuarioObtido = usuarioService.update(usuarioAntigo.getCodigo(), new UsuarioDTO(usuarioNovo));

            assertThat(usuarioObtido.getCodigo()).isEqualTo(usuarioAntigo.getCodigo());
            assertThat(usuarioObtido.getNome()).isNotEqualTo(usuarioAntigo.getNome());
            verify(usuarioRepository, times(1)).findById(any(String.class));
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }
        @Test
        void deveGerarExcessaoSeIdNaoEncontrado(){
            var id = String.valueOf(UUID.randomUUID());
            when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
            assertThatThrownBy(()-> usuarioService.findById(id))
                    .isInstanceOf(RuntimeException.class);
            verify(usuarioRepository, times(1)).findById(any(String.class));
        }
    }

    @Test
    void devePermitirravarUsuario(){
        var usuario = ObjectHelper.gerarUsuarioDTO();
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        var usuarioRegistrado = usuarioService.insert(usuario);
        assertThat(usuarioRegistrado).isInstanceOf(UsuarioDTO.class).isNotNull();
        assertThat(usuarioRegistrado.getCodigo()).isEqualTo(usuario.getCodigo());
        assertThat(usuarioRegistrado.getNome()).isEqualTo(usuario.getNome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
