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

### Opção 1: Usando Docker (Recomendado)

A forma mais simples de executar o projeto é utilizando Docker Compose, que irá subir tanto a aplicação quanto o banco de dados MySQL automaticamente.

1.  **Execute o comando na raiz do projeto:**

    ```bash
    docker-compose up --build
    ```

    Este comando irá:
    - Compilar o projeto Java usando Maven
    - Criar um JAR executável com todas as dependências
    - Subir o banco de dados MySQL
    - Subir a aplicação Java

2.  **Acesse a aplicação:**

    A API estará disponível em `http://localhost:8080`

3.  **Para parar os containers:**

    ```bash
    docker-compose down
    ```

**Variáveis de ambiente configuradas:**
- `DB_URL`: jdbc:mysql://db:3306/rawdb
- `DB_USER`: user
- `DB_PASSWORD`: password

### Opção 2: Executando Localmente

Se preferir rodar a aplicação diretamente na sua máquina:

1.  **Inicie apenas o banco de dados com Docker:**

    ```bash
    docker-compose up -d db
    ```

2.  **Compile o projeto:**

    ```bash
    mvn clean package
    ```

3.  **Execute a aplicação:**

    ```bash
    java -jar target/raw-java-api-1.0-SNAPSHOT.jar
    ```

    Ou execute a classe `Main` diretamente pela sua IDE.

**Exemplo de classe `Main`:**

```java
import com.sun.net.httpserver.HttpServer;
import database.DataBaseInitializerConfig;
import router.MasterRouter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        var databaseInitializer = new DataBaseInitializerConfig();
        databaseInitializer.initializeDatabase();

        var server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MasterRouter());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor rodando na porta 8080");
    }
}
```

Após executar, o servidor estará ativo e pronto para receber requisições.

## 📋 Documentação da API

### Endpoints Disponíveis

#### Produtos

**Base URL:** `/produtos`

| Método | Endpoint | Descrição | Body | Resposta |
|--------|----------|-----------|------|----------|
| `GET` | `/produtos` | Lista todos os produtos (com paginação) | - | `200 OK` - Lista paginada de produtos |
| `GET` | `/produtos/{id}` | Busca um produto por ID | - | `200 OK` - Produto encontrado<br>`404 Not Found` - Produto não existe |
| `POST` | `/produtos` | Cria um novo produto | `{"nome": "string", "preco": number}` | `201 Created` - Produto criado |
| `PUT` | `/produtos/{id}` | Atualiza um produto existente | `{"nome": "string", "preco": number}` | `200 OK` - Produto atualizado<br>`204 No Content` - Nenhuma linha afetada |
| `DELETE` | `/produtos/{id}` | Deleta um produto por ID | - | `204 No Content` - Produto deletado<br>`404 Not Found` - Produto não existe |

**Parâmetros de Query (Paginação):**
- `page`: Número da página (padrão: 0)
- `size`: Quantidade de itens por página (padrão: 10)

**Exemplo de requisição:**
```bash
# Criar produto
curl -X POST http://localhost:8080/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome": "Notebook", "preco": 3500.00}'

# Listar produtos com paginação
curl http://localhost:8080/produtos?page=0&size=10

# Buscar produto por ID
curl http://localhost:8080/produtos/1

# Atualizar produto
curl -X PUT http://localhost:8080/produtos/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "Notebook Gamer", "preco": 4500.00}'

# Deletar produto
curl -X DELETE http://localhost:8080/produtos/1
```

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
        // lógica do metodo
    }
}
```