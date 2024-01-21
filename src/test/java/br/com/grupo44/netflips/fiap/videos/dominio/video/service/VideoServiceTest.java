package br.com.grupo44.netflips.fiap.videos.dominio.video.service;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import br.com.grupo44.netflips.fiap.videos.dominio.video.repository.VideoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

 class VideoServiceTest {
    private VideoService videoService;
    @Mock
    private VideoRepository videoRepository;
     private ReactiveMongoTemplate reactiveMongoTemplate;


    AutoCloseable mock;

    //inicia todas variaveis de mocks na memoria
    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        videoService = new VideoService(videoRepository, reactiveMongoTemplate);
    }

    //Limpa variaveis da memoria
    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class obterVideo {
        @Test
        void devePermitirListarVideo() {
            VideoDTO filtro = new VideoDTO();
            PageRequest pageRequest = PageRequest.of(0, 10);

            Flux<Page<VideoDTO>> fluxResult = videoService.findAll(filtro, pageRequest);

            StepVerifier.create(fluxResult)
                    .assertNext(pageResult -> {
                        assertThat(pageResult).isNotNull();
                        assertThat(pageResult.getContent()).hasSize(2);
                    })
                    .expectComplete()
                    .verify();

            verify(videoService, times(1)).findAll(any(VideoDTO.class), any(PageRequest.class));
        }

        @Test
        void devePermitiBuscarVideo() {
            var video = ObjectHelper.gerarVideo();
            when(videoRepository.findById(any(String.class))).thenReturn(Mono.just(video));
            var videoObtido = videoService.findById(video.getCodigo()).block(); // block() para obter o resultado
            assertThat(videoObtido).isEqualTo(new VideoDTO(video));
            verify(videoRepository, times(1)).findById(any(String.class));
        }

        @Test
        void deveLancarExcessaoSeCodigoNaoExistir() {
            var id = String.valueOf(UUID.randomUUID());
            when(videoRepository.findById(id)).thenReturn(Mono.empty());

            StepVerifier.create(videoService.findById(id))
                    .expectErrorMatches(e -> e instanceof ResponseStatusException && e.getMessage().equals("Video não encontrado"))
                    .verify();

            verify(videoRepository, times(1)).findById(any(String.class));
        }

    }
    @Nested
    class atualizarVideo{
        @Test
        void devePermitirAtualizarVideo() {
            var videoAntigo = ObjectHelper.gerarVideo();
            var videoNovo = new Video();
            BeanUtils.copyProperties(videoAntigo, videoNovo);
            videoNovo.setTitulo("Novo nome");

            when(videoRepository.findById(videoAntigo.getCodigo())).thenReturn(Mono.just(videoAntigo));

            ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
            when(videoRepository.save(videoCaptor.capture())).thenAnswer(i -> Mono.just(i.getArgument(0)));

            var videoObtido = videoService.update(videoAntigo.getCodigo(), new VideoDTO(videoNovo)).block();

            assertThat(videoObtido).isNotNull();
            assertThat(videoObtido.getCodigo()).isEqualTo(videoAntigo.getCodigo());
            assertThat(videoCaptor.getValue().getTitulo()).isEqualTo("Novo nome");

            verify(videoRepository, times(1)).findById(any(String.class));
            verify(videoRepository, times(1)).save(any(Video.class));
        }


        @Test
        void deveGerarExcecaoSeIdNaoEncontrado() {
            var id = String.valueOf(UUID.randomUUID());

            when(videoRepository.findById(id)).thenReturn(Mono.empty()); // Ajuste aqui

            assertThatThrownBy(() -> videoService.findById(id))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessage("Video não encontrado");

            verify(videoRepository, times(1)).findById(any(String.class));
        }
    }

     @Test
     void devePermitirGravarVideo() {
         var video = ObjectHelper.gerarVideoDTO();
         when(videoRepository.save(any(Video.class))).thenReturn(Mono.just(ObjectHelper.gerarVideo()));
         var videoRegistrado = videoService.insert(video).block();

         assertThat(videoRegistrado).isInstanceOf(VideoDTO.class).isNotNull();
         assertThat(videoRegistrado.getCodigo()).isEqualTo(video.getCodigo());
         assertThat(videoRegistrado.getTitulo()).isEqualTo(video.getTitulo());

         verify(videoRepository, times(1)).save(any(Video.class));
     }

 }
