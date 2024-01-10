package br.com.grupo44.netflips.fiap.videos.dominio.usuario.dto;


import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.dto.ExibicaoDTO;
import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    @Id
    @Null(message= "Codigo deve ser nulo")
    private String codigo;
    @NotEmpty(message = "Nome não pode ser vazio")
    @NotNull(message = "Nome usuario não pode ser nulo")
    private String nome;
    @Email(message = "Formatação email invalida")
    @NotEmpty(message = "Email não pode ser vazio")
    @NotNull(message = "Email usuario não pode ser nulo")
    private String email;
    @CPF(message = "Cpf invalido")
    @NotEmpty(message = "cpf não pode ser vazio")
    @NotNull(message = "CPF não pode ser nulo")
    private String cpf;
    @Pattern(regexp = "\\(\\d{2}\\)\\d{5}-\\d{4}", message = "Formato de telefone inválido")
    @NotEmpty(message = "Telefone não pode ser vazio")
    @NotNull(message = "Email nao pode ser nulo")
    String telefone;
    @Past(message = "A data de nascimento deve estar no passado")
    @NotNull(message = "Nata de nascimento não pode ser nula")
    private LocalDate dataNascimento;
    private List<ExibicaoDTO> historicoExibicao;

    public UsuarioDTO(Usuario entity){
        this.codigo = entity.getCodigo();
        this.nome = entity.getNome();
        this.email = entity.getEmail();
        this.cpf = entity.getCpf();
        this.telefone = entity.getTelefone();
        this.dataNascimento = entity.getDataNascimento();
    }

    public UsuarioDTO(Usuario entity, List<Exibicao> historicoExibicao){
        this(entity);
        this.historicoExibicao = new ArrayList<>();
        if (historicoExibicao != null && historicoExibicao.isEmpty()){
            for (Exibicao exibicao : historicoExibicao){
                this.historicoExibicao.add(new ExibicaoDTO(exibicao));
            }
        }
    }
}
