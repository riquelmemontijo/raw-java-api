package shared.paginacao;

import java.util.Set;

public class Pagina<T> {

    private Integer tamanhoDaPagina;
    private Integer numeroPagina;
    private Integer totalElementos;
    private Set<T> content;

    public Pagina(Set<T> content, Paginavel paginavel, Integer totalElementos) {
        this.content = content;
        this.tamanhoDaPagina = paginavel.getTamanhoDaPagina();
        this.numeroPagina = paginavel.getNumeroPagina();
        this.totalElementos =   totalElementos;
    }

    public Integer getTamanhoDaPagina() {
        return tamanhoDaPagina;
    }

    public void setTamanhoDaPagina(Integer tamanhoDaPagina) {
        this.tamanhoDaPagina = tamanhoDaPagina;
    }

    public Integer getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(Integer numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    public Integer getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(Integer totalElementos) {
        this.totalElementos = totalElementos;
    }

    public Set<T> getContent() {
        return content;
    }

    public void setContent(Set<T> content) {
        this.content = content;
    }
}
