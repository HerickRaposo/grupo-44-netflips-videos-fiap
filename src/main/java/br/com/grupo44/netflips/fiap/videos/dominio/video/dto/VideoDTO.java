package br.com.grupo44.netflips.fiap.videos.dominio.video.dto;

import br.com.grupo44.netflips.fiap.videos.dominio.categoria.Categoria;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private String codigo;
    @NotEmpty(message = "Titulo deve ser definido")
    private String titulo;
    @NotNull(message = "Url não pode ser nula")
    private String url;
    @NotNull(message = "Data publicação deve ser definido")
    private LocalDateTime dataPublicacao;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Lista de categorias  não pode ser nula")
    private List<Long> categorias;
    @Null(message = "Nome de categorias deve iniciar nula")
    private List<String> nomesCategorias;
    @JsonIgnore
    @Null(message = "Esta informação deve iniciar nula")
    private String categoriaBuscada;

    public VideoDTO(Video entity){
        this.codigo = entity.getCodigo();
        this.titulo = entity.getTitulo();
        this.url = entity.getUrl();
        this.dataPublicacao = entity.getDataPublicacao();
        this.categorias = entity.getCategorias();
        this.nomesCategorias = Categoria.Categorias.mapearCategorias(entity.getCategorias());
    }
}
