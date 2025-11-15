package shared.paginacao;

import java.util.Map;

public class Paginavel {

    private static final Integer DEFAULT_PAGE_NUMBER = 1;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private final Integer numeroPagina;
    private final Integer tamanhoDaPagina;

    private Paginavel(Integer numeroPagina, Integer tamanhoDaPagina){
        this.numeroPagina = numeroPagina;
        this.tamanhoDaPagina = tamanhoDaPagina;
    }

    public static Paginavel fromParameters(Map<String, String> parameters){
        int pagina = parseParameter(parameters.get("pagina"), DEFAULT_PAGE_NUMBER);
        int tamanho = parseParameter(parameters.get("tamanho"), DEFAULT_PAGE_SIZE);
        return new Paginavel(pagina, tamanho);
    }

    private static Integer parseParameter(String paramValue, Integer defaultValue){
        if(paramValue == null || paramValue.trim().isEmpty()){
            return defaultValue;
        }

        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Integer getNumeroPagina() {
        return numeroPagina;
    }

    public Integer getTamanhoDaPagina() {
        return tamanhoDaPagina;
    }
}
