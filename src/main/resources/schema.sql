CREATE TABLE endereco (
   id_endereco INT IDENTITY(1,1) PRIMARY KEY,
   cep CHAR(8),
   estado VARCHAR(45),
   cidade VARCHAR(45),
   bairro VARCHAR(45),
   rua VARCHAR(100),
   numero VARCHAR(10),
   complemento VARCHAR(100)
);


CREATE TABLE aluno (
   id_aluno INT IDENTITY(1,1) PRIMARY KEY,
   nome VARCHAR(100) NOT NULL,
   cpf CHAR(11) NOT NULL UNIQUE,
   telefone VARCHAR(11),
   email VARCHAR(50),
   id_endereco INT,
   created_at DATETIME2 DEFAULT SYSDATETIME(),


   CONSTRAINT fk_aluno_endereco
   FOREIGN KEY (id_endereco) REFERENCES endereco(id_endereco)
);




CREATE TABLE usuario (
   id_usuario INT IDENTITY(1,1) PRIMARY KEY,
   nome_usuario VARCHAR(100) NOT NULL,
   login VARCHAR(100) NOT NULL UNIQUE,
   senha_hash VARCHAR(255) NOT NULL,
   created_at DATETIME2 DEFAULT SYSDATETIME()
);


CREATE TABLE funcionario (
   id_funcionario INT IDENTITY(1,1) PRIMARY KEY,
   nome VARCHAR(100) NOT NULL,
   cpf CHAR(11) NOT NULL UNIQUE,
   cargo VARCHAR(50) NOT NULL,
   telefone VARCHAR(14),
   email VARCHAR(50),
   id_endereco INT,
   created_at DATETIME2 DEFAULT SYSDATETIME(),


   CONSTRAINT fk_funcionario_endereco
   FOREIGN KEY (id_endereco) REFERENCES endereco(id_endereco)
);


CREATE TABLE categoria (
   id_categoria INT IDENTITY(1,1) PRIMARY KEY,
   nome_categoria VARCHAR(100) NOT NULL,
   descricao VARCHAR(200),
   created_at DATETIME2 DEFAULT SYSDATETIME()
);




CREATE TABLE editora (
   id_editora INT IDENTITY(1,1) PRIMARY KEY,
   nome_editora VARCHAR(100) NOT NULL,
   cnpj CHAR(14),
   email VARCHAR(50),
   telefone VARCHAR(14),
   nacionalidade VARCHAR(45),
   endereco_web VARCHAR(100),
   id_endereco INT,
   created_at DATETIME2 DEFAULT SYSDATETIME(),


   CONSTRAINT fk_editora_endereco
   FOREIGN KEY (id_endereco) REFERENCES endereco(id_endereco)
);




-- AUTOR
CREATE TABLE autor (
   id_autor INT IDENTITY(1,1) PRIMARY KEY,
   nome_autor VARCHAR(100) NOT NULL,
   pseudonimo VARCHAR(45),
   nacionalidade VARCHAR(45),
   email VARCHAR(50),
   telefone VARCHAR(14),
   id_endereco INT,
   created_at DATETIME2 DEFAULT SYSDATETIME(),


   CONSTRAINT fk_autor_endereco
   FOREIGN KEY (id_endereco) REFERENCES endereco(id_endereco)
);


CREATE TABLE livro (
   id_livro INT IDENTITY(1,1) PRIMARY KEY,
   id_categoria INT NOT NULL,
   id_editora INT NOT NULL,
   titulo VARCHAR(100) NOT NULL,
   ano_publicacao INT,
   isbn VARCHAR(45) NOT NULL UNIQUE,
   created_at DATETIME2 DEFAULT SYSDATETIME(),


   CONSTRAINT fk_livro_categoria
   FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),


   CONSTRAINT fk_livro_editora
   FOREIGN KEY (id_editora) REFERENCES editora(id_editora)
);


CREATE TABLE livro_autor (
   id_livro_autor INT IDENTITY(1,1) PRIMARY KEY,
   id_livro INT NOT NULL,
   id_autor INT NOT NULL,


   CONSTRAINT fk_livro_autor_livro
   FOREIGN KEY (id_livro) REFERENCES livro(id_livro),


   CONSTRAINT fk_livro_autor_autor
   FOREIGN KEY (id_autor) REFERENCES autor(id_autor),


   CONSTRAINT uk_livro_autor UNIQUE (id_livro, id_autor)
);


CREATE TABLE exemplar (
   id_exemplar INT IDENTITY(1,1) PRIMARY KEY,
   id_livro INT NOT NULL,
   status VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
   data_entrada DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),


   CONSTRAINT fk_exemplar_livro
   FOREIGN KEY (id_livro) REFERENCES livro(id_livro)
);




CREATE TABLE emprestimo (
   id_emprestimo INT IDENTITY(1,1) PRIMARY KEY,
   id_usuario INT NOT NULL,
   id_aluno INT NOT NULL,
   id_funcionario INT NULL,
   data_emprestimo DATE NOT NULL,
   data_devolucao_prevista DATE NOT NULL,
   status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',


   CONSTRAINT fk_emprestimo_usuario
   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),


   CONSTRAINT fk_emprestimo_aluno
   FOREIGN KEY (id_aluno) REFERENCES aluno(id_aluno),


   CONSTRAINT fk_emprestimo_funcionario
   FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario)
);




CREATE TABLE emprestimo_livro (
   id_emprestimo_livro INT IDENTITY(1,1) PRIMARY KEY,
   id_emprestimo INT NOT NULL,
   id_exemplar INT NOT NULL,
   data_devolucao DATE NULL,


   CONSTRAINT fk_emp_livro_emp
   FOREIGN KEY (id_emprestimo) REFERENCES emprestimo(id_emprestimo),


   CONSTRAINT fk_emp_livro_ex
   FOREIGN KEY (id_exemplar) REFERENCES exemplar(id_exemplar)
);


CREATE TABLE multa (
   id_multa INT IDENTITY(1,1) PRIMARY KEY,
   id_emprestimo_livro INT NOT NULL UNIQUE,
   valor DECIMAL(10,2) NOT NULL,
   data_pagamento DATE NULL,


   CONSTRAINT fk_multa_emp_livro
   FOREIGN KEY (id_emprestimo_livro)
   REFERENCES emprestimo_livro(id_emprestimo_livro)
);


-- =========================
-- ÍNDICES
-- =========================
CREATE INDEX idx_emprestimo_aluno
ON emprestimo (id_aluno);


CREATE INDEX idx_emprestimo_livro_exemplar
ON emprestimo_livro (id_exemplar);


-- =========================
-- PROCEDURE
-- =========================
GO
CREATE PROCEDURE sp_novo_emprestimo
    @id_usuario INT,
    @id_aluno INT,
    @id_funcionario INT,
    @data_prevista DATE
AS
BEGIN
    INSERT INTO emprestimo (
        id_usuario,
        id_aluno,
        id_funcionario,
        data_emprestimo,
        data_devolucao_prevista,
        status
    )
    VALUES (
        @id_usuario,
        @id_aluno,
        @id_funcionario,
        GETDATE(),
        @data_prevista,
        'ATIVO'
    );
END;
GO


-- =========================
-- TRANSAÇÃO COM COMMIT
-- =========================
BEGIN TRANSACTION;


INSERT INTO emprestimo (id_usuario, id_aluno, id_funcionario, data_emprestimo, data_devolucao_prevista, status)
VALUES (1, 3, 1, GETDATE(), DATEADD(DAY, 7, GETDATE()), 'ATIVO');


COMMIT;


-- =========================
-- TRANSAÇÃO COM ROLLBACK
-- =========================
BEGIN TRANSACTION;


INSERT INTO emprestimo (id_usuario, id_aluno, id_funcionario, data_emprestimo, data_devolucao_prevista, status)
VALUES (1, 4, 1, GETDATE(), DATEADD(DAY, 7, GETDATE()), 'ATIVO');


ROLLBACK;


-- =========================
-- CURSOR
-- =========================
DECLARE @nome VARCHAR(100);


DECLARE aluno_cursor CURSOR FOR
SELECT nome FROM aluno;


OPEN aluno_cursor;


FETCH NEXT FROM aluno_cursor INTO @nome;


WHILE @@FETCH_STATUS = 0
BEGIN
    PRINT @nome;
    FETCH NEXT FROM aluno_cursor INTO @nome;
END;


CLOSE aluno_cursor;
DEALLOCATE aluno_cursor;
