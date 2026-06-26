# Sistema de Gerenciamento de Biblioteca

Trabalho final da disciplina de Programação Orientada a Objetos. Aplicação desktop em Java para gerenciar o acervo, os usuários (alunos e funcionários) e os empréstimos de uma biblioteca.

## Funcionalidades

- Cadastro de alunos, funcionários, autores, categorias e editoras
- Cadastro de livros e seus exemplares físicos
- Login e cadastro de usuários do sistema (com senha protegida por hash)
- Registro de empréstimos (com múltiplos livros por empréstimo) e devoluções
- Controle de multas por atraso na devolução

## Tecnologias

- **Java 25**
- **JavaFX 24** — interface gráfica
- **JDBC** (`mssql-jdbc`) — acesso a dados
- **SQL Server** — banco de dados relacional
- **Maven** — build e gerenciamento de dependências

## Arquitetura

O projeto segue uma separação em camadas:

```
src/main/java/
├── model/      # Entidades (Pessoa, Aluno, Funcionario, Livro, Emprestimo, ...)
├── dao/        # Acesso a dados via JDBC (CRUD de cada entidade)
├── view/       # Telas JavaFX
├── app/        # Estado da aplicação (sessão do usuário logado)
├── config/     # Configuração de conexão com o banco
├── database/   # Fábrica de conexões JDBC
└── Main.java   # Ponto de entrada da aplicação
```

### Conceitos de POO aplicados

- **Herança**: `Pessoa` (classe abstrata) é a superclasse de `Aluno` e `Funcionario`.
- **Polimorfismo**: `toString()` sobrescrito nas subclasses de `Pessoa`.
- **Encapsulamento**: atributos privados/protegidos com getters e setters em todos os models.
- **Classe abstrata**: `Pessoa` concentra os atributos e comportamentos comuns a alunos e funcionários.

## Banco de dados

O schema completo está em [`src/main/resources/schema.sql`](src/main/resources/schema.sql) e inclui as tabelas de endereço, aluno, usuário, funcionário, categoria, editora, autor, livro, exemplar, empréstimo e multa, além de uma stored procedure e índices.

Configuração da conexão em [`src/main/java/config/Config.java`](src/main/java/config/Config.java):

```java
URL  = jdbc:sqlserver://localhost:1433;databaseName=final_project;encrypt=false;trustServerCertificate=true
USER = sa
```

Para preparar o banco:

1. Suba uma instância do SQL Server (ex.: container Docker `mcr.microsoft.com/mssql/server`).
2. Crie o banco `final_project`.
3. Execute o script `schema.sql` nesse banco para criar as tabelas.

## Como executar

```bash
mvn clean compile
mvn javafx:run
```

A tela inicial é de login, com opção de cadastro de um novo usuário do sistema.
