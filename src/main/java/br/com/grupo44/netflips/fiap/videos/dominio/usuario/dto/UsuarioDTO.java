package br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto;


import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class UsuarioDTO {
    @Id
    private String codigo;
    @NotNull(message = "Nome usuario não pode ser nulo")
    private String nome;
    @NotNull(message = "Login usuario não pode ser nulo")
    @NotEmpty(message = "Login usuario não pode ser vazio")
    private String login;
    @NotEmpty(message = "Senha não pode ser vazio")
    private String senha;

    public UsuarioDTO(Usuario entity){
        this.codigo = entity.getCodigo();
        this.nome = entity.getNome();
        this.login = entity.getLogin();
        this.senha = entity.getSenha();
    }
}
