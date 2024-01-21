package br.com.grupo44.netflips.fiap.videos.dominio.usuario.service;

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
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    private ReactiveMongoTemplate reactiveMongoTemplate;


    AutoCloseable mock;

    //inicia todas variaveis de mocks na memoria
    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        reactiveMongoTemplate = mock(ReactiveMongoTemplate.class);
        usuarioService = new UsuarioService(usuarioRepository, reactiveMongoTemplate);
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

            when(reactiveMongoTemplate.find(any(Query.class), eq(Usuario.class)))
                    .thenReturn(Flux.fromIterable(listaUsuarios));

            Flux<Page<UsuarioDTO>> resultadoObtido = usuarioService.findAll(new UsuarioDTO(), PageRequest.of(0, 10));

            StepVerifier.create(resultadoObtido)
                    .expectNextMatches(page -> page.getContent().size() == 2)
                    .verifyComplete();

            verify(usuarioRepository, never()).findById(anyString());
        }


        @Test
        void devePermitiBuscarUsuario() {
            var usuario = ObjectHelper.gerarUsuario();
            when(usuarioRepository.findById(any(String.class))).thenReturn(Mono.just(usuario));
            var usuarioObtido = usuarioService.findById(usuario.getCodigo());

            StepVerifier.create(usuarioObtido)
                    .expectNextMatches(usuarioDTO -> usuarioDTO.equals(new UsuarioDTO(usuario)))
                    .verifyComplete();

            verify(usuarioRepository, times(1)).findById(eq(usuario.getCodigo()));
        }


        @Test
        void deveLancarExcessaoSeCodigoNaoExistir() {
            var id = String.valueOf(UUID.randomUUID());

            when(usuarioRepository.findById(id)).thenReturn(Mono.empty());

            StepVerifier.create(usuarioService.findById(id))
                    .expectErrorMatches(throwable ->
                            throwable instanceof ResponseStatusException &&
                                    ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.NOT_FOUND &&
                                    ((ResponseStatusException) throwable).getReason().equals("Usuario não encontrado"))
                    .verify();

            verify(usuarioRepository, never()).findById(eq(id));
        }
    }
    @Nested
    class atualizarMensagem{
        @Test
        void devePermitirAtualizarMensagem() {
            var usuarioAntigo = ObjectHelper.gerarUsuario();
            var usuarioNovo = new Usuario();
            BeanUtils.copyProperties(usuarioAntigo, usuarioNovo);
            usuarioNovo.setNome("Joao");

            when(usuarioRepository.findById(usuarioAntigo.getCodigo())).thenReturn(Mono.just(usuarioAntigo));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuarioNovo));
            var usuarioObtido = usuarioService.update(usuarioAntigo.getCodigo(), new UsuarioDTO(usuarioNovo)).block(); // block() para fins de teste

            assertThat(usuarioObtido.getCodigo()).isEqualTo(usuarioAntigo.getCodigo());
            assertThat(usuarioObtido.getNome()).isNotEqualTo(usuarioAntigo.getNome());

            verify(usuarioRepository, times(1)).findById(eq(usuarioAntigo.getCodigo()));
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcessaoSeIdNaoEncontrado() {
            var id = String.valueOf(UUID.randomUUID());

            when(usuarioRepository.findById(id)).thenReturn(Mono.empty());

            assertThatThrownBy(() -> usuarioService.update(id, new UsuarioDTO()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Erro interno ao atualizar: Usuario não encontrado");

            verify(usuarioRepository, times(1)).findById(eq(id));
        }

    }

    @Test
    void devePermitirGravarUsuario() {
        var usuario = ObjectHelper.gerarUsuarioDTO();
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        var usuarioRegistrado = usuarioService.insert(usuario).block();

        assertThat(usuarioRegistrado).isInstanceOf(UsuarioDTO.class).isNotNull();
        assertThat(usuarioRegistrado.getCodigo()).isEqualTo(usuario.getCodigo());
        assertThat(usuarioRegistrado.getNome()).isEqualTo(usuario.getNome());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

}
