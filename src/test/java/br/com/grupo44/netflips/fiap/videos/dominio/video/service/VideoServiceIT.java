package br.com.grupo44.netflips.fiap.videos.dominio.video.service;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
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
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureTestDatabase
public class VideoServiceIT {
    @Autowired
    private VideoService videoService;

    @Nested
    class obterVideoo {
        @Test
        void devePermitirListarVideo() {
            Flux<Page<VideoDTO>> fluxoDePaginas = videoService.findAll(new VideoDTO(), PageRequest.of(0, 10));

            StepVerifier.create(fluxoDePaginas)
                    .expectNextMatches(page -> page.getContent().stream()
                            .allMatch(videoDTO -> videoDTO != null && videoDTO instanceof VideoDTO))
                    .verifyComplete();

            verify(videoService, times(1)).findAll(any(VideoDTO.class), any(PageRequest.class));
        }

        @Test
        void devePermitirBuscarVideo() {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            Mono<VideoDTO> videoMono = videoService.findById(codigo);

            StepVerifier.create(videoMono)
                    .assertNext(videoDTO -> {
                        assertThat(videoDTO).isNotNull();
                        assertThat(videoDTO.getCodigo()).isEqualTo(codigo);
                    }).expectComplete().verify();

            verify(videoService, times(1)).findById(any(String.class));
        }
        @Test
        void devePermitirLancarExcecaoSeIdNaoEncontrado() {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

            StepVerifier.create(videoService.findById(codigo))
                    .expectErrorMatches(e -> e instanceof ResponseStatusException && e.getMessage().equals("Video não encontrado"))
                    .verify();

            verify(videoService).findById(codigo);
        }
    }
    @Nested
    class gravaAtualizarVideo{
        @Test
        void devePermitirGravarVideo() {
            var videoDTO = ObjectHelper.gerarVideoDTO();

            StepVerifier.create(videoService.insert(videoDTO))
                    .expectNextMatches(savedVideo -> savedVideo.equals(videoDTO))
                    .verifyComplete();

            verify(videoService).insert(any(VideoDTO.class));
        }

        @Test
        void devePermitirAtualizarVideo() {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            var videoDTO = videoService.findById(codigo).block();
            var videoNovo = new VideoDTO();
            BeanUtils.copyProperties(videoDTO, videoNovo);
            videoNovo.setCodigo("teste");

            StepVerifier.create(videoService.update(codigo, videoNovo))
                    .expectNextMatches(updatedVideo -> updatedVideo.equals(videoNovo))
                    .verifyComplete();

            verify(videoService, times(1)).update(any(String.class), any(VideoDTO.class));
        }
    }

    @Nested
    class removerUVideo{
        @Test
        void devePermitirRemoverVideo() {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            StepVerifier.create(videoService.delete(codigo))
                    .verifyComplete();
            verify(videoService, times(1)).delete(any(String.class));
        }

        @Test
        void deveGerarExcessaoAoDeletarSeIdNaoExiste() {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

            StepVerifier.create(videoService.delete(codigo))
                    .verifyErrorMatches(throwable ->
                            throwable instanceof ResponseStatusException
                                    && ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.NOT_FOUND
                                    && throwable.getMessage().equals("Video não encontrado"));

            verify(videoService, times(1)).delete(any(String.class));
        }
    }
}
