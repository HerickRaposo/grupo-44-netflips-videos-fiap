package br.com.grupo44.netflips.fiap.videos.Util;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public abstract class ObjectHelper {
    public static Usuario gerarUsuario(){
        return Usuario.builder()
                .codigo(String.valueOf(UUID.randomUUID()))
                .nome("Usuario")
                .cpf("34856746291")
                .email("usuario@tahoo.com.br")
                .telefone("(35)98887-1778")
                .dataNascimento(gerarDataNascimento())
                .historicoExibicao(new ArrayList<>()).build();
    }

    public static UsuarioDTO gerarUsuarioDTO(){
        return UsuarioDTO.builder()
                .codigo(String.valueOf(UUID.randomUUID()))
                .nome("Usuario")
                .cpf("34856746291")
                .email("usuario@tahoo.com.br")
                .telefone("(35)98887-1778")
                .dataNascimento(gerarDataNascimento())
                .historicoExibicao(new ArrayList<>()).build();
    }
    private static LocalDate gerarDataNascimento(){
        LocalDate dataAtual = LocalDate.now();
        Random random = new Random();
        int anosAleatorios = random.nextInt(82) + 18;
        LocalDate dataNascimento = dataAtual.minusYears(anosAleatorios);
        return dataNascimento;
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
