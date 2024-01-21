package br.com.grupo44.netflips.fiap.videos.dominio.video.repository;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;


@DataMongoTest
public class VideoRepositoryIT {

    @Autowired
    private VideoRepository videoRepository;

    @Nested
    class obterVideo {

        @Test
        void devePermitirListarVideo() {
            StepVerifier.create(videoRepository.findAll())
                    .expectNextCount(1) .verifyComplete();
        }

        @Test
        void devePermitirObterVideo() {
            String id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            StepVerifier.create(videoRepository.findById(id))
                    .assertNext(video -> assertThat(video.getCodigo()).isEqualTo(id))
                    .verifyComplete();
        }
    }

    @Nested
    class deletarVideo {

        @Test
        void devePermitirRemoverVideo() {
            var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            StepVerifier.create(videoRepository.deleteById(id))
                    .verifyComplete();
        }

        @Test
        void deveGerarExcecaoSeIdNaoExiste() {
            assertThatThrownBy(() -> videoRepository.findById("idInexistente"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessage("Video não encontrado");
        }
    }

    @Nested
    class gravarAtualizarVideo {

        @Test
        void devePermitirGerarUsuario() {
            Video video = ObjectHelper.gerarVideo();
            StepVerifier.create(videoRepository.insert(video))
                    .expectNext(video)
                    .verifyComplete();
        }

        @Test
        void devePermitirAtualizarUsuario() {
            var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";

            StepVerifier.create(videoRepository.findById(id))
                    .assertNext(videoRecebido -> {
                        videoRecebido.setTitulo("Novo nome");
                        Mono<Video> videoSalvoMono = videoRepository.save(videoRecebido);

                        StepVerifier.create(videoSalvoMono)
                                .assertNext(videoSalvo -> assertThat(videoSalvo.getCodigo()).isEqualTo(videoRecebido.getCodigo()))
                                .verifyComplete();
                    }).verifyComplete();
        }
    }
}