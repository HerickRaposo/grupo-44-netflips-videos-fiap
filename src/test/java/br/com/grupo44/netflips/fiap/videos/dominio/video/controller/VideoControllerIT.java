package br.com.grupo44.netflips.fiap.videos.dominio.video.controller;

import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VideoControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testFindAllVideos() {
        webTestClient.get()
                .uri("/video")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VideoDTO.class);
    }

    @Test
    void testFindVideoById() {
        String codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
        webTestClient.get()
                .uri("/video/{codigo}", codigo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VideoDTO.class);
    }

    @Test
    void testInsertVideo() {
        VideoDTO videoDTO = new VideoDTO();

        webTestClient.post()
                .uri("/video/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(videoDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VideoDTO.class);
    }

    @Test
    void testUpdateVideo() {
        String codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";
        VideoDTO videoDTO = new VideoDTO(); // Preencha com os dados do vídeo a ser atualizado

        webTestClient.put()
                .uri("/video/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(videoDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VideoDTO.class);
    }

    @Test
    void testDeleteVideo() {
        String codigo = "cdcf7592-2dab-4a3d-9872-8fde96a70e11";

        webTestClient.delete()
                .uri("/video/{codigo}", codigo)
                .exchange()
                .expectStatus().isNoContent();
    }
}