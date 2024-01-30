package br.com.grupo44.netflips.fiap.videos.dominio.usuario.controller;

import br.com.grupo44.netflips.fiap.videos.Util.ObjectHelper;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 class UsuarioControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        // Configurações iniciais, se necessário
    }

    @AfterEach
    void tearDown() {
        // Limpeza após cada teste, se necessário
    }

    @Test
    @DisplayName("Deve permitir listar usuários")
    void shouldAllowListingUsers() throws Exception {
        ResponseEntity<Page<UsuarioDTO>> response = restTemplate.exchange(
                "/usuario",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Page<UsuarioDTO>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Page<UsuarioDTO> usuarios = response.getBody();
        assertThat(usuarios).isNotNull();
    }

    @Test
    @DisplayName("Deve permitir buscar usuário por ID")
    void shouldAllowFindingUserById() throws Exception {
        ResponseEntity<UsuarioDTO> response = restTemplate.getForEntity("/usuario/{codigo}", UsuarioDTO.class, "cdcf7592-2dab-4a3d-9872-8fde96a70e11");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UsuarioDTO usuarioRetornado = response.getBody();
        assertThat(usuarioRetornado).isNotNull();
    }

    @Test
    @DisplayName("Deve permitir registrar usuário")
    void shouldAllowCreatingUser() throws Exception {
        UsuarioDTO usuarioDTO = ObjectHelper.gerarUsuarioDTO();

        ResponseEntity<UsuarioDTO> response = restTemplate.postForEntity("/usuario", usuarioDTO, UsuarioDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UsuarioDTO createdUsuario = response.getBody();
        assertThat(createdUsuario).isNotNull();
        assertThat(createdUsuario.getNome()).isEqualTo(usuarioDTO.getNome());
        assertThat(createdUsuario.getEmail()).isEqualTo(usuarioDTO.getEmail());
    }

    @Test
    @DisplayName("Deve permitir atualizar usuário")
    void shouldAllowUpdatingUser() throws Exception {
        UsuarioDTO usuarioDTO = ObjectHelper.gerarUsuarioDTO();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/usuario/{codigo}",
                HttpMethod.PUT,
                new HttpEntity<>(usuarioDTO),
                Void.class,
                usuarioDTO.getCodigo());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<UsuarioDTO> updatedResponse = restTemplate.getForEntity("/usuario/{codigo}", UsuarioDTO.class, usuarioDTO.getCodigo());
        assertThat(updatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedResponse.getBody()).isNotNull();
        assertThat(updatedResponse.getBody().getNome()).isEqualTo(usuarioDTO.getNome());
        assertThat(updatedResponse.getBody().getEmail()).isEqualTo(usuarioDTO.getEmail());
    }

    @Test
    @DisplayName("Deve permitir remover usuário")
    void shouldAllowDeletingUser() throws Exception {
        ResponseEntity<Void> response = restTemplate.exchange("/usuario/{codigo}", HttpMethod.DELETE, null, Void.class,
                "cdcf7592-2dab-4a3d-9872-8fde96a70e11");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Void> notFoundResponse = restTemplate.getForEntity("/usuario/{codigo}", Void.class, "cdcf7592-2dab-4a3d-9872-8fde96a70e11");
        assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
