# Raw Java API

Este projeto é uma implementação de uma API RESTful leve em Java puro, sem o uso de frameworks pesados como Spring Boot, Quarkus ou Jakarta EE. O objetivo é demonstrar como construir um servidor HTTP com um sistema de roteamento dinâmico baseado em anotações, utilizando as bibliotecas nativas do JDK.

## ✨ Funcionalidades

- **Servidor HTTP Embutido:** Utiliza a classe `com.sun.net.httpserver.HttpServer` do Java para criar um servidor HTTP leve e autônomo.
- **Roteamento Baseado em Anotações:** As rotas da API (endpoints) são definidas de forma declarativa usando anotações customizadas. Isso torna o código mais limpo e fácil de manter.
- **Scanner de Anotações:** Um mecanismo de scanner (`AnnotationScanner`) varre o classpath em tempo de execução para encontrar e registrar automaticamente todas as rotas definidas nos controladores.
- **Roteador Principal (`MasterRouter`):** Um único `HttpHandler` que recebe todas as requisições e as delega para o controlador apropriado com base no método HTTP (GET, POST, etc.) e no caminho (path) da URL.
- **Manipulação de Erros:** Responde com um status `404 - Rota nao encontrada` para qualquer rota que não tenha sido registrada.
- **Arquitetura desacoplada:** A lógica de roteamento é separada da lógica de negócio (que fica nos controladores), promovendo um código mais organizado.

## 🛠️ Tecnologias Utilizadas

- **Java 25:** Linguagem principal do projeto.
- **JDK HTTP Server:** Para a criação do servidor web.
- **Java Reflection:** Utilizada pelo `AnnotationScanner` para inspecionar as classes e métodos em tempo de execução.

## ⚙️ Como Funciona

A arquitetura do projeto gira em torno do `MasterRouter`, que atua como o ponto de entrada para todas as requisições HTTP.

1.  **Inicialização:**
    -   Ao ser instanciado, o `MasterRouter` invoca o `AnnotationScanner`.
    -   O `AnnotationScanner` examina todas as classes do projeto em busca de métodos anotados (por exemplo, com uma anotação como `@Controller`).
    -   Para cada anotação encontrada, ele extrai o método HTTP, o caminho da rota e cria uma instância do objeto `Route`.
    -   A lista de todas as rotas (`List<Route>`) é armazenada no `MasterRouter`.

2.  **Processamento da Requisição:**
    -   Quando uma requisição chega ao servidor, o método `handle(HttpExchange exchange)` do `MasterRouter` é chamado.
    -   Ele extrai o método HTTP (ex: "GET") e o caminho (ex: "/users") da requisição.
    -   O roteador itera sobre a lista de rotas pré-carregadas.
    -   Para cada rota, ele verifica se o método e o caminho correspondem aos da requisição (`route.matches(method, path)`).
    -   Se uma rota correspondente for encontrada, o handler associado a ela (`route.handler()`) é executado, passando o objeto `HttpExchange` para que o controlador possa processar a requisição e enviar uma resposta.
    -   Se nenhuma rota for encontrada após verificar todas as opções, uma resposta `404 Not Found` é enviada ao cliente.

## 🚀 Como Executar o Projeto

1.  **Inicie os serviços de dependência (ex: banco de dados):**

    O ptojeto utiliza o docker para rodar o banco de dados MySQL, então, execute o comando na raiz do projeto para iniciá-los em segundo plano:
     ```bash
     docker-compose up -d

2.  **Inicie a API Java:**

    Execute a classe principal (`Main`) que contém a anotação `@ApiApplication`. Isso irá configurar e iniciar o servidor HTTP.

**Exemplo de classe `Main`:**

```java
import com.sun.net.httpserver.HttpServer;
import router.MasterRouter;
import router.anotacoes.ApiApplication;

import java.io.IOException;
import java.net.InetSocketAddress;

@ApiApplication
public class Main {
    static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new MasterRouter());

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor iniciado na porta " + port);
    }
}
```

Após executar esta classe, o servidor estará ativo e pronto para receber requisições.

## 📝 Como Adicionar Novas Rotas

Para criar um novo endpoint, basta criar um método em uma classe de controlador e anotá-lo adequadamente. O `AnnotationScanner` cuidará do resto.

**Exemplo de um controlador:**

```java

import infra.controlleradvide.GlobalExceptionHandler;
import router.anotacoes.Controller;

@Controller(path = "users")
public class UserController {

    @Get
    public void getAllUsers(HttpExchange exchange) {
        GlobalExceptionHandler.handle(exchange, () ->{
            // lógica do metodo
        });
    }
}
```