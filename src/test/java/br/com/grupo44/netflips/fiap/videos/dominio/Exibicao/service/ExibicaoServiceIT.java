package br.com.grupo44.netflips.fiap.videos.dominio.Exibicao.service;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service.ExibicaoService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
 class ExibicaoServiceIT {

    @Autowired
    private ExibicaoService exibicaoService;

    @Nested
    class obterExibicao {

        @Test
        void devePermitirListarExibicao() {
            Flux<ExibicaoDTO> fluxoDePaginas = exibicaoService.findAll(new ExibicaoDTO(), PageRequest.of(0, 10))
                    .flatMapIterable(page -> page.getContent());

            StepVerifier.create(fluxoDePaginas)
                    .expectNextMatches(exibicaoDTO -> exibicaoDTO != null && exibicaoDTO instanceof ExibicaoDTO)
                    .verifyComplete();
        }

        @Test
        void devePermitirBuscarExibicao() {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";
            Mono<ExibicaoDTO> exibicaoMono = exibicaoService.findById(codigo);

            StepVerifier.create(exibicaoMono)
                    .assertNext(exibicaoDTO -> {
                        assertThat(exibicaoDTO).isNotNull();
                        assertThat(exibicaoDTO.getCodigo()).isEqualTo(codigo);
                    })
                    .expectComplete()
                    .verify();
        }

        @Test
        void deveLancarExcessaoSeIdNaoEncontrado() {
            var codigo = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

            StepVerifier.create(exibicaoService.findById(codigo))
                    .expectErrorMatches(e -> e instanceof ResponseStatusException && e.getMessage().equals("Exibição não encontrada"))
                    .verify();
        }

    }

    @Nested
    class atualizarExibicao {

        @Test
        void devePermitirAtualizarExibicao() {
            var exibicaoDTOAntiga = ObjectHelper.gerarExibicaoDTO();
            var exibicaoDTONova = new ExibicaoDTO();
            BeanUtils.copyProperties(exibicaoDTOAntiga, exibicaoDTONova);
            exibicaoDTONova.setPontuacao(5D);

            StepVerifier.create(exibicaoService.update(exibicaoDTOAntiga.getCodigo(), exibicaoDTONova))
                    .expectNextMatches(updatedExibicao -> updatedExibicao.equals(exibicaoDTONova))
                    .verifyComplete();
        }

        @Test
        void deveGerarExcecaoSeIdNaoEncontrado() {
            var id = "edcf7592-2dab-4a3d-9872-8fde96a70e41";

            StepVerifier.create(exibicaoService.findById(id))
                    .verifyErrorMatches(throwable ->
                            throwable instanceof ResponseStatusException
                                    && ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.NOT_FOUND
                                    && throwable.getMessage().equals("Exibição não encontrada"));
        }
    }

    @Test
    void devePermitirGravarExibicao() {
        var exibicaoDTO = ObjectHelper.gerarExibicaoDTO();

        StepVerifier.create(exibicaoService.insert(exibicaoDTO))
                .expectNextMatches(savedExibicao -> savedExibicao.equals(exibicaoDTO))
                .verifyComplete();
    }
}
