package br.com.grupo44.netflips.fiap.videos.dominio.video.repository;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class VideoRepositoryTest {

    @Mock
    private VideoRepository videoRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
    }

    // Limpa variáveis da memória
    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class obterVideo {
        @Test
        void devePermitirListarVideo() {
            var video1 = ObjectHelper.gerarVideo();
            var video2 = ObjectHelper.gerarVideo();
            when(videoRepository.findAll()).thenReturn(Flux.just(video1, video2));

            StepVerifier.create(videoRepository.findAll())
                    .expectNext(video1, video2)
                    .verifyComplete();

            verify(videoRepository, times(1)).findAll();
        }

        @Test
        void devePermitirBuscarVideo() {
            var video = ObjectHelper.gerarVideo();
            when(videoRepository.findById(any(String.class))).thenReturn(Mono.just(video));

            StepVerifier.create(videoRepository.findById(video.getCodigo()))
                    .expectNext(video)
                    .verifyComplete();

            verify(videoRepository, times(1)).findById(any(String.class));
        }

        @Test
        void deveLancarExcecaoSeIdBuscadoNaoExiste() {
            when(videoRepository.findById(any(String.class))).thenReturn(Mono.empty());

            StepVerifier.create(videoRepository.findById(UUID.randomUUID().toString()))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verify(videoRepository, times(1)).findById(any(String.class));
        }
    }

    @Nested
    class deletarVideo {
        @Test
        void devePermitirDeletarVideo() {
            var id = UUID.randomUUID().toString();
            when(videoRepository.deleteById(any(String.class))).thenReturn(Mono.empty());

            StepVerifier.create(videoRepository.deleteById(id))
                    .verifyComplete();

            verify(videoRepository, times(1)).deleteById(any(String.class));
        }

        @Test
        void deveLancarExcecaoSeIdNaoExiste() {
            when(videoRepository.deleteById(any(String.class))).thenReturn(Mono.error(new IllegalArgumentException("Video não encontrado")));

            StepVerifier.create(videoRepository.deleteById(UUID.randomUUID().toString()))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verify(videoRepository, times(1)).deleteById(any(String.class));
        }
    }

    @Nested
    class gravarAtualizarVideo {
        @Test
        void devePermitirInserirVideo() {
            var video = ObjectHelper.gerarVideo();
            when(videoRepository.save(any(Video.class))).thenReturn(Mono.just(video));

            StepVerifier.create(videoRepository.save(video))
                    .expectNext(video)
                    .verifyComplete();

            verify(videoRepository, times(1)).save(any(Video.class));
        }

        @Test
        void devePermitirAtualizarVideo() {
            var video = ObjectHelper.gerarVideo();
            when(videoRepository.findById(any(String.class))).thenReturn(Mono.just(video));
            when(videoRepository.save(any(Video.class))).thenReturn(Mono.just(video));

            StepVerifier.create(videoRepository.findById(video.getCodigo()))
                    .expectNext(video)
                    .verifyComplete();

            StepVerifier.create(videoRepository.save(video))
                    .expectNext(video)
                    .verifyComplete();

            verify(videoRepository, times(1)).findById(any(String.class));
            verify(videoRepository, times(1)).save(any(Video.class));
        }
    }
}

