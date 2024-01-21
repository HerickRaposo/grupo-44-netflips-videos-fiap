package br.com.grupo44.netflips.fiap.videos.dominio.Exibicao.service;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.repository.ExibicaoRepository;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.service.ExibicaoService;
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

class ExibicaoServiceTest {

    private ExibicaoService exibicaoService;

    @Mock
    private ExibicaoRepository exibicaoRepository;

    private ReactiveMongoTemplate reactiveMongoTemplate;

    AutoCloseable mock;

    // Inicia todas variáveis de mocks na memória
    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        reactiveMongoTemplate = mock(ReactiveMongoTemplate.class);
        exibicaoService = new ExibicaoService(exibicaoRepository, reactiveMongoTemplate);
    }

    // Limpa variáveis da memória
    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class obterExibicao {

        @Test
        void devePermitirListarExibicao() {
            ExibicaoDTO filtro = new ExibicaoDTO();
            PageRequest pageRequest = PageRequest.of(0, 10);

            Flux<Page<ExibicaoDTO>> fluxResult = exibicaoService.findAll(filtro, pageRequest);

            StepVerifier.create(fluxResult)
                    .assertNext(pageResult -> {
                        assertThat(pageResult).isNotNull();
                        assertThat(pageResult.getContent()).hasSize(2);
                    })
                    .expectComplete()
                    .verify();

            verify(exibicaoService, times(1)).findAll(any(ExibicaoDTO.class), any(PageRequest.class));
        }

        @Test
        void devePermitiBuscarExibicao() {
            var exibicao = ObjectHelper.gerarExibicao();
            when(exibicaoRepository.findById(any(String.class))).thenReturn(Mono.just(exibicao));
            var exibicaoObtida = exibicaoService.findById(exibicao.getCodigo()).block();

            assertThat(exibicaoObtida).isEqualTo(new ExibicaoDTO(exibicao));
            verify(exibicaoRepository, times(1)).findById(any(String.class));
        }

        @Test
        void deveLancarExcessaoSeCodigoNaoExistir() {
            var id = String.valueOf(UUID.randomUUID());
            when(exibicaoRepository.findById(id)).thenReturn(Mono.empty());

            StepVerifier.create(exibicaoService.findById(id))
                    .expectErrorMatches(e -> e instanceof ResponseStatusException && e.getMessage().equals("Exibição não encontrada"))
                    .verify();

            verify(exibicaoRepository, times(1)).findById(any(String.class));
        }

    }

    @Nested
    class atualizarExibicao {

        @Test
        void devePermitirAtualizarExibicao() {
            var exibicaoAntiga = ObjectHelper.gerarExibicao();
            var exibicaoNova = new Exibicao();
            BeanUtils.copyProperties(exibicaoAntiga, exibicaoNova);
            exibicaoNova.setPontuacao(5D);

            when(exibicaoRepository.findById(exibicaoAntiga.getCodigo())).thenReturn(Mono.just(exibicaoAntiga));

            ArgumentCaptor<Exibicao> exibicaoCaptor = ArgumentCaptor.forClass(Exibicao.class);
            when(exibicaoRepository.save(exibicaoCaptor.capture())).thenAnswer(i -> Mono.just(i.getArgument(0)));

            var exibicaoObtida = exibicaoService.update(exibicaoAntiga.getCodigo(), new ExibicaoDTO(exibicaoNova)).block();

            assertThat(exibicaoObtida).isNotNull();
            assertThat(exibicaoObtida.getCodigo()).isEqualTo(exibicaoAntiga.getCodigo());
            assertThat(exibicaoCaptor.getValue().getPontuacao()).isEqualTo(5);

            verify(exibicaoRepository, times(1)).findById(any(String.class));
            verify(exibicaoRepository, times(1)).save(any(Exibicao.class));
        }

        @Test
        void deveGerarExcecaoSeIdNaoEncontrado() {
            var id = String.valueOf(UUID.randomUUID());

            when(exibicaoRepository.findById(id)).thenReturn(Mono.empty());

            assertThatThrownBy(() -> exibicaoService.findById(id))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessage("Exibição não encontrada");

            verify(exibicaoRepository, times(1)).findById(any(String.class));
        }
    }

    @Test
    void devePermitirGravarExibicao() {
        var exibicao = ObjectHelper.gerarExibicaoDTO();
        when(exibicaoRepository.save(any(Exibicao.class))).thenReturn(Mono.just(ObjectHelper.gerarExibicao()));
        var exibicaoRegistrada = exibicaoService.insert(exibicao).block();

        assertThat(exibicaoRegistrada).isInstanceOf(ExibicaoDTO.class).isNotNull();
        assertThat(exibicaoRegistrada.getCodigo()).isEqualTo(exibicao.getCodigo());
        assertThat(exibicaoRegistrada.getPontuacao()).isEqualTo(exibicao.getPontuacao());

        verify(exibicaoRepository, times(1)).save(any(Exibicao.class));
    }
}
