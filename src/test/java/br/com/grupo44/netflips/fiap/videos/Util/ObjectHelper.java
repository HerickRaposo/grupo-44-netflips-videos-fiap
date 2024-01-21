package br.com.grupo44.netflips.fiap.videos.Util;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    public static Video gerarVideo(){
        Integer numero = new Random().nextInt();
        return Video.builder()
                .codigo(String.valueOf(UUID.randomUUID()))
                .titulo("Meu video " + numero)
                .url("http://netflips/"+ numero)
                .dataPublicacao(gerarDataHoraAleatoria())
                .build();
    }

    public static VideoDTO gerarVideoDTO(){
        Integer numero = new Random().nextInt();
        return VideoDTO.builder()
                .codigo(String.valueOf(UUID.randomUUID()))
                .titulo("Meu video " + numero)
                .url("http://netflips/"+ numero)
                .dataPublicacao(gerarDataHoraAleatoria())
                .build();
    }

    public static ExibicaoDTO gerarExibicaoDTO(){
        return ExibicaoDTO.builder()
                .codigo(String.valueOf(UUID.randomUUID()))
                .dataVisualizacao(gerarDataHoraAleatoria())
                .recomenda(new Random().nextBoolean())
                .pontuacao(new Random().nextDouble() * 5)
                .usuario(gerarUsuarioDTO())
                .video(gerarVideoDTO())
                .build();
    }

    public static Exibicao gerarExibicao(){
        return Exibicao.builder()
                .codigo(String.valueOf(UUID.randomUUID()))
                .dataVisualizacao(gerarDataHoraAleatoria())
                .recomenda(new Random().nextBoolean())
                .pontuacao(new Random().nextDouble() * 5)
                .usuario(gerarUsuario())
                .video(gerarVideo())
                .build();
    }

    private static LocalDate gerarDataNascimento(){
        LocalDate dataAtual = LocalDate.now();
        Random random = new Random();
        int anosAleatorios = random.nextInt(82) + 18;
        LocalDate dataNascimento = dataAtual.minusYears(anosAleatorios);
        return dataNascimento;
    }

    private static LocalDateTime gerarDataHoraAleatoria() {
        LocalDateTime dataInicial = LocalDateTime.of(2022, 1, 1, 0, 0);
        long diferencaEmDias = ChronoUnit.DAYS.between(dataInicial, LocalDateTime.now());
        long diasAleatorios = new Random().nextInt((int) diferencaEmDias + 1);
        LocalDateTime dataHoraAleatoria = dataInicial.plusDays(diasAleatorios)
                .withHour(new Random().nextInt(24))
                .withMinute(new Random().nextInt(60))
                .withSecond(new Random().nextInt(60));
        return dataHoraAleatoria;
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
