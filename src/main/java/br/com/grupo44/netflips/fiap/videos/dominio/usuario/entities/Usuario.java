package br.com.grupo44.netflips.fiap.videos.dominio.usuario.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    private String codigo;
    private String nome;
    private String login;
    private String senha;
    @Version
    private Long VERSION;
}
