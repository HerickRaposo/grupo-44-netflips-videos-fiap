package br.com.grupo44.netflips.fiap.videos.usuario.service;


import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.repository.UsuarioRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.service.UsuarioService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

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
            Page<UsuarioDTO> listaRetorno = usuarioService.findAll(new UsuarioDTO(), PageRequest.of(0,10));
            assertThat(listaRetorno.getContent()).asList().allSatisfy(usuario -> {
                assertThat(usuario).isNotNull().isInstanceOf(Usuario.class);
            });
        }

        @Test
        void devePermitirBuscarUsuario(){
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            UsuarioDTO usuarioDTO = usuarioService.findById(codigo);
            assertThat(usuarioDTO.getCodigo()).isNotNull().isEqualTo(codigo);
            assertThat(usuarioDTO).isInstanceOf(UsuarioDTO.class);
        }

        @Test
        void devePermitirLancarExcessaoSeIdNaoEncontrado(){
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            assertThatThrownBy(() -> usuarioService.findById(codigo)).isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuario não encontrado");
        }
    }
    @Nested
    class gravaAtualizarUsuario{
        @Test
        void devePermitirGravarUsuario(){
            var usuarioDTO = ObjectHelper.gerarUsuarioDTO();
            var usuarioGravado = usuarioService.insert(usuarioDTO);
            assertThat(usuarioGravado).isNotNull().isSameAs(usuarioDTO);
        }
        @Test
        void devePermitirAtualizarUsuario(){
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            var usuarioDTO = usuarioService.findById(codigo);
            var usuarioNovo = new UsuarioDTO();
            BeanUtils.copyProperties(usuarioDTO, usuarioNovo);
            usuarioNovo.setEmail("teste@gmail.com");
            var usuariogravado = usuarioService.update(codigo,usuarioNovo);
            assertThat(usuarioNovo.getEmail()).isNotEqualTo(usuarioDTO.getEmail());
            assertThat(usuariogravado).isInstanceOf(UsuarioDTO.class).isSameAs(usuarioNovo);
        }
    }

    @Nested
    class removerUsuario{
        @Test
        void devePermitirRemoverUsuario(){
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            usuarioService.delete(codigo);
            assertThatThrownBy(() -> usuarioService.findById(codigo)).isInstanceOf(RuntimeException.class);
        }

        @Test
        void deveGerarExcessaoAoDeletarSeIdNaoExiste(){
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            assertThatThrownBy(() -> usuarioService.delete(codigo)).isInstanceOf(RuntimeException.class)
                    .hasMessage("Usuario não encontrado");
        }
    }
}
