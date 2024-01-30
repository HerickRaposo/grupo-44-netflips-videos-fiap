package br.com.grupo44.netflips.fiap.videos.dominio.Exibicao.repository;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository.ExibicaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@SpringBootTest
public class ExibicaoRepositoryTest {

    @Mock
    private ExibicaoRepository exibicaoRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class obterExibicao {
        @Test
        void devePermitirBuscarExibicao() {
            var exibicao = ObjectHelper.gerarExibicao();
            when(exibicaoRepository.findById(any(String.class))).thenReturn(Mono.just(exibicao));

            StepVerifier.create(exibicaoRepository.findById(exibicao.getCodigo()))
                    .expectNext(exibicao)
                    .verifyComplete();

            verify(exibicaoRepository, times(1)).findById(any(String.class));
        }

        @Test
        void deveLancarExcecaoSeIdBuscadoNaoExiste() {
            when(exibicaoRepository.findById(any(String.class))).thenReturn(Mono.empty());

            StepVerifier.create(exibicaoRepository.findById(UUID.randomUUID().toString()))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verify(exibicaoRepository, times(1)).findById(any(String.class));
        }
    }

    @Nested
    class deletarExibicao {
        @Test
        void devePermitirDeletarExibicao() {
            var exibicao = ObjectHelper.gerarExibicao();
            when(exibicaoRepository.deleteById(any(String.class))).thenReturn(Mono.empty());

            StepVerifier.create(exibicaoRepository.deleteById(exibicao.getCodigo()))
                    .verifyComplete();

            verify(exibicaoRepository, times(1)).deleteById(any(String.class));
        }

        @Test
        void deveLancarExcecaoSeIdNaoExiste() {
            when(exibicaoRepository.deleteById(any(String.class))).thenReturn(Mono.error(new IllegalArgumentException("Exibicao não encontrada")));

            StepVerifier.create(exibicaoRepository.deleteById(UUID.randomUUID().toString()))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verify(exibicaoRepository, times(1)).deleteById(any(String.class));
        }
    }

    @Nested
    class salvarAtualizarExibicao {
        @Test
        void devePermitirSalvarExibicao() {
            var exibicao = ObjectHelper.gerarExibicao();
            when(exibicaoRepository.save(any(Exibicao.class))).thenReturn(Mono.just(exibicao));

            StepVerifier.create(exibicaoRepository.save(exibicao))
                    .expectNext(exibicao)
                    .verifyComplete();

            verify(exibicaoRepository, times(1)).save(any(Exibicao.class));
        }

        @Test
        void devePermitirAtualizarExibicao() {
            var exibicao = ObjectHelper.gerarExibicao();
            when(exibicaoRepository.findById(any(String.class))).thenReturn(Mono.just(exibicao));
            when(exibicaoRepository.save(any(Exibicao.class))).thenReturn(Mono.just(exibicao));

            StepVerifier.create(exibicaoRepository.findById(exibicao.getCodigo()))
                    .expectNext(exibicao)
                    .verifyComplete();

            StepVerifier.create(exibicaoRepository.save(exibicao))
                    .expectNext(exibicao)
                    .verifyComplete();

            verify(exibicaoRepository, times(1)).findById(any(String.class));
            verify(exibicaoRepository, times(1)).save(any(Exibicao.class));
        }
    }
}