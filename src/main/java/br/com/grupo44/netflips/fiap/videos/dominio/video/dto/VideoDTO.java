package br.com.grupo44.netflips.fiap.videos.dominio.video.dto;

import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private String codigo;
    @NotEmpty(message = "Titulo deve ser definido")
    private String titulo;
    @NotNull(message = "Url não pode ser nula")
    private String url;
    @NotEmpty(message = "Data publicação deve ser definido")
    private LocalDateTime dataPublicacao;

    public VideoDTO(Video entity){
        this.codigo = entity.getCodigo();
        this.titulo = entity.getTitulo();
        this.url = entity.getUrl();
        this.dataPublicacao = entity.getDataPublicacao();
    }
}
