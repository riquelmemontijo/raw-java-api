package shared;

import java.util.Set;
import java.util.stream.Collectors;

public class Pagina<T> {

    private Set<T> content;
    private Integer totalElementos;
    private Integer tamanhoPagina;
    private Integer totalPaginas;

    public Pagina(Set<T> content) {
        this.content = content.stream().limit(10).collect(Collectors.toSet());
        this.totalElementos = content.size();
        this.tamanhoPagina = 10; //Objects.requireNonNullElse(tamanhoPagina, 10);
        this.totalPaginas = this.totalElementos < this.tamanhoPagina ? 1 : this.totalElementos / this.tamanhoPagina;
    }

    public Set<T> getContent() {
        return content;
    }

    public void setContent(Set<T> content) {
        this.content = content;
    }

    public Integer getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(Integer totalElementos) {
        this.totalElementos = totalElementos;
    }

    public Integer getTamanhoPagina() {
        return tamanhoPagina;
    }

    public void setTamanhoPagina(Integer tamanhoPagina) {
        this.tamanhoPagina = tamanhoPagina;
    }

    public Integer getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }
}
