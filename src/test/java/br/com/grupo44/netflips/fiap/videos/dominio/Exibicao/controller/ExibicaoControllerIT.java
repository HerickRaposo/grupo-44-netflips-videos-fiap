package br.com.grupo44.netflips.fiap.videos.dominio.Exibicao.controller;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExibicaoControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testFindAllExibicoes() {
        webTestClient.get()
                .uri("/exibicao")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExibicaoDTO.class)
                .value(exibicoes -> assertThat(exibicoes).isNotNull().isNotEmpty());
    }

    @Test
    void testFindExibicaoById() {
        String codigo = "seu_id_de_exibicao_aqui";
        webTestClient.get()
                .uri("/exibicao/{codigo}", codigo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExibicaoDTO.class)
                .value(exibicaoDTO -> assertThat(exibicaoDTO).isNotNull());
    }

    @Test
    void testInsertExibicao() {
        ExibicaoDTO exibicaoDTO = new ExibicaoDTO();

        webTestClient.post()
                .uri("/exibicao")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exibicaoDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ExibicaoDTO.class)
                .value(savedExibicaoDTO -> {
                    assertThat(savedExibicaoDTO).isNotNull();
                    assertThat(savedExibicaoDTO.getCodigo()).isNotBlank();
                });
    }

    @Test
    void testUpdateExibicao() {
        String codigo = "seu_id_de_exibicao_aqui";
        ExibicaoDTO exibicaoDTO = new ExibicaoDTO(); // Preencha com os dados da exibição a ser atualizada

        webTestClient.put()
                .uri("/exibicao/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exibicaoDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExibicaoDTO.class)
                .value(updatedExibicaoDTO -> assertThat(updatedExibicaoDTO).isNotNull());
    }

    @Test
    void testDeleteExibicao() {
        String codigo = "seu_id_de_exibicao_aqui";

        webTestClient.delete()
                .uri("/exibicao/{codigo}", codigo)
                .exchange()
                .expectStatus().isNoContent();
    }
}
