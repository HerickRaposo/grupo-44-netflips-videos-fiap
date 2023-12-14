package br.com.grupo44.netflips.fiap.videos.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultError {

    private Instant timeStamp;
    private  Integer status;
    private String error;
    private String message;
    private String path;
}
