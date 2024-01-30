package br.com.grupo44.netflips.fiap.videos.dominio.usuario.service;


import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureTestDatabase
class UsuarioServiceIT {

    @Autowired
    private UsuarioService usuarioService;

    @Nested
    class obterUsuario {
        @Test
        void devePermitirListarUsuario() {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNome("John Doe");
            usuarioDTO.setEmail("john.doe@example.com");

            Mono<UsuarioDTO> usuarioSalvo = usuarioService.insert(usuarioDTO);
            Flux<Page<UsuarioDTO>> listaRetorno = usuarioService.findAll(usuarioDTO, PageRequest.of(0, 10));

            StepVerifier.create(listaRetorno)
                    .expectNextMatches(page ->
                            page.getContent().stream().anyMatch(usuarioRetornado ->
                                    usuarioRetornado.getNome().equals("John Doe") && usuarioRetornado.getEmail().equals("john.doe@example.com")))
                    .expectComplete()
                    .verify();
        }

        @Test
        void devePermitirBuscarUsuario() {
            String codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

            UsuarioDTO usuarioDTO = usuarioService.findById(codigo).block();
            assertThat(usuarioDTO).isNotNull();
            assertThat(usuarioDTO.getCodigo()).isEqualTo(codigo);
            assertThat(usuarioDTO).isInstanceOf(UsuarioDTO.class);

            verify(usuarioService, times(1)).findById(any(String.class));
        }

        @Test
        void devePermitirLancarExcessaoSeIdNaoEncontrado() {
            String codigoNaoExistente = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

            assertThatThrownBy(() -> usuarioService.findById(codigoNaoExistente).block())
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasFieldOrPropertyWithValue("reason", "Usuario não encontrado");

            verify(usuarioService, times(1)).findById(any(String.class));
        }
    }

    @Nested
    class gravaAtualizarUsuario {
        @Test
        void devePermitirGravarUsuario() {
            UsuarioDTO usuarioDTO = ObjectHelper.gerarUsuarioDTO();

            UsuarioDTO usuarioGravado = usuarioService.insert(usuarioDTO).block();

            assertThat(usuarioGravado).isNotNull();
            assertThat(usuarioGravado.getCodigo()).isNotNull();
            assertThat(usuarioGravado.getNome()).isEqualTo(usuarioDTO.getNome());
            assertThat(usuarioGravado.getEmail()).isEqualTo(usuarioDTO.getEmail());

            verify(usuarioService, times(1)).insert(any(UsuarioDTO.class));
        }

        @Test
        void devePermitirAtualizarUsuario() {
            String codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

            UsuarioDTO usuarioDTO = usuarioService.findById(codigo).block();

            UsuarioDTO usuarioNovo = new UsuarioDTO();
            BeanUtils.copyProperties(usuarioDTO, usuarioNovo);
            usuarioNovo.setEmail("teste@gmail.com");

            UsuarioDTO usuarioGravado = usuarioService.update(codigo, usuarioNovo).block();

            assertThat(usuarioGravado).isNotNull();
            assertThat(usuarioGravado.getEmail()).isNotEqualTo(usuarioDTO.getEmail());
            assertThat(usuarioGravado).isSameAs(usuarioNovo);

            verify(usuarioService, times(1)).update(any(String.class), any(UsuarioDTO.class));
        }

        @Nested
        class removerUsuario {
            @Test
            void devePermitirRemoverUsuario() {
                String codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

                usuarioService.delete(codigo);

                assertThatThrownBy(() -> usuarioService.findById(codigo).block())
                        .isInstanceOf(ResponseStatusException.class)
                        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                        .hasFieldOrPropertyWithValue("reason", "Usuario não encontrado");

                verify(usuarioService, times(1)).delete(any(String.class));
            }
        }
    }
}
