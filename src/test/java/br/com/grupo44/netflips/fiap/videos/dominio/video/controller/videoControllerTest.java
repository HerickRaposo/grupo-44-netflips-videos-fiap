package br.com.grupo44.netflips.fiap.videos.dominio.video.controller;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.service.VideoService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class VideoControllerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideosController videosController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(videosController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Limpar variáveis da memória, se necessário
    }

    @Nested
    class obterVideo {
        @Test
        void devePermitirListarVideo() throws Exception {
            List<VideoDTO> videos = Arrays.asList(ObjectHelper.gerarVideoDTO(), ObjectHelper.gerarVideoDTO());
            Page<VideoDTO> fakePage = new PageImpl<>(videos);

            when(videoService.findAll(any(VideoDTO.class), any(PageRequest.class))).thenReturn(Flux.just(fakePage));

            mockMvc.perform(get("/video"))
                    .andExpect(status().isOk())
                    .andReturn();

            verify(videoService, times(1)).findAll(any(VideoDTO.class), any(PageRequest.class));
        }

        @Test
        void devePermitirBuscarVideo() throws Exception {
            var codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            var videoDTO = ObjectHelper.gerarVideoDTO();

            when(videoService.findById(codigo)).thenReturn(Mono.just(videoDTO));

            mockMvc.perform(get("/video/{codigo}", codigo))
                    .andExpect(status().isOk());

            verify(videoService, times(1)).findById(any(String.class));
        }
    }

    @Nested
    class gravarAtualizarVideo {
        @Test
        void devePermitirRegistrarVideo() throws Exception {
            VideoDTO videoDTO = ObjectHelper.gerarVideoDTO();

            when(videoService.insert(any(VideoDTO.class))).thenReturn(Mono.just(videoDTO));

            MvcResult result = mockMvc.perform(post("/video")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ObjectHelper.asJsonString(videoDTO)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            assertThat(responseBody).isNotEmpty().contains(videoDTO.getTitulo(), videoDTO.getUrl());

            verify(videoService, times(1)).insert(any(VideoDTO.class));
        }

        @Test
        void devePermitirAtualizarVideo() throws Exception {
            String codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            VideoDTO videoDTO = ObjectHelper.gerarVideoDTO();
            videoDTO.setCodigo(codigo);

            when(videoService.update(eq(codigo), any(VideoDTO.class))).thenReturn(Mono.just(videoDTO));

            mockMvc.perform(put("/video/{codigo}", codigo)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ObjectHelper.asJsonString(videoDTO)))
                    .andExpect(status().isOk());

            verify(videoService).update(eq(codigo), any(VideoDTO.class));
        }

    }

    @Test
    void devePermitirRemoverVideo() throws Exception {
        var codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
        doNothing().when(videoService).delete(codigo);

        mockMvc.perform(delete("/video/{codigo}", codigo))
                .andExpect(status().isNoContent());

        verify(videoService, times(1)).delete(any(String.class));
    }
}
