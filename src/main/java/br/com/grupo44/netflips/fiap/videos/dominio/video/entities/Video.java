package br.com.grupo44.netflips.fiap.videos.dominio.video.entities;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
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
public class Video {
    @Id
    private String codigo;
    private String titulo;
    private String url;
    private LocalDateTime dataPublicacao;
    @DBRef
    private Usuario autor;
    @Version
    private Long VERSION;
}
