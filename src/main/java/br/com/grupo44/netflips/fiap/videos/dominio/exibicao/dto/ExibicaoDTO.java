package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExibicaoDTO {
    private String codigo;
    private LocalDateTime dataVisualizacao;
    private Double pontuacao;
    private Boolean visualizado;
    private Boolean recomenda;
    private Usuario usuario;
    private Video video;
}
