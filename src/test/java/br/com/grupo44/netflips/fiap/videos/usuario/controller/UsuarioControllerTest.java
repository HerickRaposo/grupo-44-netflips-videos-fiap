package br.com.grupo44.netflips.fiap.videos.usuario.controller;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.controller.UsuarioController;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 class UsuarioControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    AutoCloseable mock;

    //inicia todas variaveis de mocks na memoria
    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
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
            Page<UsuarioDTO> fakePage = new PageImpl<>(Arrays.asList(ObjectHelper.gerarUsuarioDTO(), ObjectHelper.gerarUsuarioDTO()));

            when(usuarioService.findAll(any(), any())).thenReturn(fakePage);

            mockMvc.perform(get("/usuario"))
                    .andExpect(status().isOk())
                    .andReturn();

            verify(usuarioService, times(1)).findAll(any(), any());

        }

        @Test
        void devePermitirBuscarUsuario() throws Exception {
            var codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            var usuarioDTO = ObjectHelper.gerarUsuarioDTO();
            when(usuarioService.findById(codigo)).thenReturn(usuarioDTO);
            mockMvc.perform(get("/usuario/{codigo}", codigo))
                    .andExpect(status().isOk());
            verify(usuarioService, times(1)).findById(any(String.class));
        }
    }

    @Nested
    class gravarAtualizarUsuario {
        @Test
        void devePermitirRegistrarUsuario() throws Exception {
            var usuarioDTO = ObjectHelper.gerarUsuarioDTO();
            when(usuarioService.insert(any(UsuarioDTO.class)))
                    .thenAnswer(i -> i.getArgument(0));
            mockMvc.perform(post("/usuario").content(ObjectHelper.asJsonString(usuarioDTO)))
                    .andExpect(status().isCreated());
            verify(usuarioService, times(1)).insert(any(UsuarioDTO.class));
        }

        @Test
        void devePermitirAtualizarUsuario() throws Exception {
            var codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            UsuarioDTO usuarioDTO = ObjectHelper.gerarUsuarioDTO();
            usuarioDTO.setCodigo(codigo);

            when(usuarioService.update(eq(codigo), any(UsuarioDTO.class))).thenReturn(usuarioDTO);
            mockMvc.perform(put("/usuario/{codigo}", codigo)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ObjectHelper.asJsonString(usuarioDTO)))
                    .andExpect(status().isOk());
            verify(usuarioService).update(eq(codigo), any(UsuarioDTO.class));
        }
    }

    @Test
    void devePermitirRemoverUsuario() throws Exception {
        var codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
        doNothing().when(usuarioService).delete(codigo);
        mockMvc.perform(delete("/usuario/{codigo}", codigo))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).delete(any(String.class));
    }
}
