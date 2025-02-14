package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exibicao {
    @Id
    private String codigo;
    private LocalDateTime dataVisualizacao;
    private Double pontuacao;
    private Boolean visualizado;
    private Boolean recomenda;
    @DBRef
    private Usuario usuario;
    @DBRef
    private Video video;
    @Version
    private Long VERSION;
}
