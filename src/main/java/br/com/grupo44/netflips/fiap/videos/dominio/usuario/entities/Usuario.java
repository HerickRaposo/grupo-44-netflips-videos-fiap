package br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities;

import br.com.grupo44.netflips.fiap.videos.dominio.exibicao.entity.Exibicao;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    private String codigo;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;
    private List<Exibicao> historicoExibicao;
    @Version
    private Long VERSION;
}
