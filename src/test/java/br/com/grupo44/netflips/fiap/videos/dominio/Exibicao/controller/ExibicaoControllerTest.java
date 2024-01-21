package br.com.grupo44.netflips.fiap.videos.dominio.Exibicao.controller;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.controller.ExibicaoController;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service.ExibicaoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ExibicaoControllerTest {

    @Mock
    private ExibicaoService exibicaoService;

    @InjectMocks
    private ExibicaoController exibicaoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(exibicaoController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Limpar variáveis da memória, se necessário
    }

    @Nested
    class obterExibicao {
        @Test
        void devePermitirListarExibicao() throws Exception {
            List<ExibicaoDTO> exibicoes = Arrays.asList(ObjectHelper.gerarExibicaoDTO(), ObjectHelper.gerarExibicaoDTO());
            Page<ExibicaoDTO> fakePage = new PageImpl<>(exibicoes);

            when(exibicaoService.findAll(any(ExibicaoDTO.class), any(PageRequest.class))).thenReturn(Flux.just(fakePage));

            mockMvc.perform(get("/exibicao"))
                    .andExpect(status().isOk())
                    .andReturn();

            verify(exibicaoService, times(1)).findAll(any(ExibicaoDTO.class), any(PageRequest.class));
        }

        @Test
        void devePermitirBuscarExibicao() throws Exception {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e11";
            var exibicaoDTO = ObjectHelper.gerarExibicaoDTO();

            when(exibicaoService.findById(codigo)).thenReturn(Mono.just(exibicaoDTO));

            mockMvc.perform(get("/exibicao/{codigo}", codigo))
                    .andExpect(status().isOk());

            verify(exibicaoService, times(1)).findById(any(String.class));
        }
    }

    @Nested
    class gravarAtualizarExibicao {
        @Test
        void devePermitirRegistrarExibicao() throws Exception {
            ExibicaoDTO exibicaoDTO = ObjectHelper.gerarExibicaoDTO();

            when(exibicaoService.insert(any(ExibicaoDTO.class))).thenReturn(Mono.just(exibicaoDTO));

            MvcResult result = mockMvc.perform(post("/exibicao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ObjectHelper.asJsonString(exibicaoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            assertThat(responseBody).isNotEmpty();

            verify(exibicaoService, times(1)).insert(any(ExibicaoDTO.class));
        }

        @Test
        void devePermitirAtualizarExibicao() throws Exception {
            String codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e11";
            ExibicaoDTO exibicaoDTO = ObjectHelper.gerarExibicaoDTO();
            exibicaoDTO.setCodigo(codigo);

            when(exibicaoService.update(eq(codigo), any(ExibicaoDTO.class))).thenReturn(Mono.just(exibicaoDTO));

            mockMvc.perform(put("/exibicao/{codigo}", codigo)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ObjectHelper.asJsonString(exibicaoDTO)))
                    .andExpect(status().isOk());

            verify(exibicaoService).update(eq(codigo), any(ExibicaoDTO.class));
        }
    }

    @Test
    void devePermitirRemoverExibicao() throws Exception {
        var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e11";
        doNothing().when(exibicaoService).delete(codigo);

        mockMvc.perform(delete("/exibicao/{codigo}", codigo))
                .andExpect(status().isNoContent());

        verify(exibicaoService, times(1)).delete(any(String.class));
    }
}