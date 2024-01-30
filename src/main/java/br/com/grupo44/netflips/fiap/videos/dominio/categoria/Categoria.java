package br.com.grupo44.netflips.fiap.videos.dominio.categoria;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Categoria {

    @Getter
    @AllArgsConstructor
    public enum Categorias {
        FILME(1L, "Filme"),
        SERIE(2L, "Serie"),
        DOCUMENTARIO(3L, "Documentario"),
        COMEDIA(4L, "Comedia"),
        ACAO(5L, "Ação"),
        AVENTURA(6L, "Aventura"),
        SUSPENSE(7L, "Suspense"),
        TERROR(8L, "Terror"),
        FANTASIA(9L, "Fantasia"),
        FICCAO_CIENTIFICA(10L, "Ficcção cientifica"),
        MUSICAK(11L, "Musical"),
        HISTORICO(12L, "Historico"),
        ANIME(13L, "Anime"),
        DORAMA(14L, "Dorama"),
        TEEN(15L, "Teem"),
        SITCOM(16L, "Sitcom");

        private final Long codigo;
        private final String descricao;

        public static List<Categorias> listaPath() {
            return Arrays.asList(Categorias.values());
        }

        public static Categorias buscarPatch(Long codigo) {
            if (codigo == null || codigo.equals(0L)) {
                return null;
            }
            return Arrays.asList(Categorias.values()).stream()
                    .filter(cat -> cat.getCodigo().equals(codigo))
                    .findFirst()
                    .orElse(null);
        }

        public static String buscarDescricaoCategoria(Long codigo) {
            Categorias status = buscarPatch(codigo);
            if (status == null) {
                return null;
            }
            return status.getDescricao();
        }

        public static List<String> listaNomeCategorias(List<Long> codigos) {
            return codigos.stream()
                    .map(Categorias::getByCodigo)
                    .map(Categorias::getDescricao)
                    .collect(Collectors.toList());
        }

        public static Categorias getByCodigo(long codigo) {
            return Arrays.stream(Categorias.values())
                    .filter(cat -> cat.getCodigo() == codigo)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Código de categoria inválido: " + codigo));
        }

        public static List<String> mapearCategorias(List<Long> codigos) {
            if (codigos!= null && !codigos.isEmpty()){
                return codigos.stream()
                        .map(Categorias::buscarDescricaoCategoria)
                        .collect(Collectors.toList());
            }
            return null;
        }

        public static Long buscarCodigoPeloNome(String nome) {
            return Arrays.stream(values())
                    .filter(cat -> cat.getDescricao().equalsIgnoreCase(nome))
                    .map(Categorias::getCodigo)
                    .findFirst()
                    .orElse(null);
        }
    }
}
