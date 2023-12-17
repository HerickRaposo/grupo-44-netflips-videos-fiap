package br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto.UsuarioDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import br.com.grupo44.netflips.fiap.videos.dominio.video.dto.VideoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.video.entities.Video;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExibicaoDTO {
    @Null(message = "Codigo deve ser nulo")
    private String codigo;
    @NotNull(message = "Data de visualização não pode ser nula")
    private LocalDateTime dataVisualizacao;
    @NotNull(message = "Pontuação não pode ser nula")
    private Double pontuacao;
    @NotNull(message = "Visualizado não pode ser nulo")
    private Boolean visualizado;
    @NotNull(message = "Recomenda não pode ser nulo")
    private Boolean recomenda;
    @NotNull(message = "Usuario não pode ser nulo")
    private UsuarioDTO usuario;
    @NotNull(message = "video não pode ser nulo")
    private VideoDTO video;

    public ExibicaoDTO(Exibicao entity){
        this.codigo = entity.getCodigo();
        this.dataVisualizacao= entity.getDataVisualizacao();
        this.pontuacao = entity.getPontuacao();
        this.visualizado = entity.getVisualizado();
        this.recomenda = entity.getRecomenda();
    }

    public ExibicaoDTO(Exibicao entity, Usuario usuario, Video video){
        this(entity);
        this.usuario = new UsuarioDTO(usuario);
        this.video = new VideoDTO(video);
    }


}
