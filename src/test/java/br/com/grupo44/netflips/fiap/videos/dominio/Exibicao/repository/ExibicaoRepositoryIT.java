package br.com.grupo44.netflips.fiap.videos.dominio.Exibicao.repository;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository.ExibicaoRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;

@DataMongoTest
public class ExibicaoRepositoryIT {
    @Autowired
    private ExibicaoRepository exibicaoRepository;

    @Nested
    class obterExibicao {

        @Test
        void devePermitirListarExibicao() {
            StepVerifier.create(exibicaoRepository.findAll())
                    .expectNextCount(1)
                    .verifyComplete();
        }

        @Test
        void devePermitirObterExibicao() {
            String id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            StepVerifier.create(exibicaoRepository.findById(id))
                    .assertNext(exibicao -> assertThat(exibicao.getCodigo()).isEqualTo(id))
                    .verifyComplete();
        }
    }

    @Nested
    class deletarExibicao {

        @Test
        void devePermitirRemoverExibicao() {
            var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
            StepVerifier.create(exibicaoRepository.deleteById(id))
                    .verifyComplete();
        }

        @Test
        void deveGerarExcecaoSeIdNaoExiste() {
            assertThatThrownBy(() -> exibicaoRepository.findById("cdcf7592-2dab-4a3d-9872-8fde96a70e11"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessage("Exibicao não encontrada");
        }
    }

    @Nested
    class gravarAtualizarExibicao {

        @Test
        void devePermitirGerarExibicao() {
            Exibicao exibicao = ObjectHelper.gerarExibicao();
            StepVerifier.create(exibicaoRepository.insert(exibicao))
                    .expectNext(exibicao)
                    .verifyComplete();
        }

        @Test
        void devePermitirAtualizarExibicao() {
            var id = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";

            StepVerifier.create(exibicaoRepository.findById(id))
                    .assertNext(exibicaoRecebida -> {
                        exibicaoRecebida.setPontuacao(5D);
                        Mono<Exibicao> exibicaoSalvaMono = exibicaoRepository.save(exibicaoRecebida);

                        StepVerifier.create(exibicaoSalvaMono)
                                .assertNext(exibicaoSalva -> assertThat(exibicaoSalva.getCodigo()).isEqualTo(exibicaoRecebida.getCodigo()))
                                .verifyComplete();
                    }).verifyComplete();
        }
    }
}
