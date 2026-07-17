CREATE TABLE IF NOT EXISTS tipos_usuario (
    id   BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO tipos_usuario (nome)
SELECT 'DONO_RESTAURANTE' WHERE NOT EXISTS (SELECT 1 FROM tipos_usuario WHERE nome = 'DONO_RESTAURANTE');

INSERT INTO tipos_usuario (nome)
SELECT 'CLIENTE' WHERE NOT EXISTS (SELECT 1 FROM tipos_usuario WHERE nome = 'CLIENTE');

CREATE TABLE IF NOT EXISTS usuarios (
    id                    BIGSERIAL PRIMARY KEY,
    nome                  VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL UNIQUE,
    login                 VARCHAR(255) NOT NULL,
    senha                 VARCHAR(255) NOT NULL,
    tipo_usuario_id       BIGINT NOT NULL REFERENCES tipos_usuario(id),
    data_ultima_alteracao TIMESTAMP,
    rua                   VARCHAR(255),
    numero                VARCHAR(50),
    cidade                VARCHAR(255),
    estado                VARCHAR(100),
    cep                   VARCHAR(20)
);

INSERT INTO usuarios (nome, email, login, senha, tipo_usuario_id, data_ultima_alteracao, rua, numero, cidade, estado, cep)
SELECT 'Admin Restaurante', 'admin@restaurante.com', 'admin', '$2a$10$OjXmh3CsFgNdiiNyyVcPd.MC5yZzt1bLT5R/ceKStOugOybIBCd16',
       (SELECT id FROM tipos_usuario WHERE nome = 'DONO_RESTAURANTE'), NOW(),
       'Rua dos Restaurantes', '100', 'São Paulo', 'SP', '01310-100'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@restaurante.com');

INSERT INTO usuarios (nome, email, login, senha, tipo_usuario_id, data_ultima_alteracao, rua, numero, cidade, estado, cep)
SELECT 'Cliente Teste', 'cliente@email.com', 'cliente', '$2a$10$H8ok78qK6wyRCTs.9SskDOaMG5B3q2SfzpDrnrot2ktfulLZToS0i',
       (SELECT id FROM tipos_usuario WHERE nome = 'CLIENTE'), NOW(),
       'Rua dos Clientes', '200', 'São Paulo', 'SP', '01310-200'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'cliente@email.com');

CREATE TABLE IF NOT EXISTS restaurantes (
    id                     BIGSERIAL PRIMARY KEY,
    nome                   VARCHAR(255) NOT NULL,
    rua                    VARCHAR(255),
    numero                 VARCHAR(50),
    cidade                 VARCHAR(255),
    estado                 VARCHAR(100),
    cep                    VARCHAR(20),
    tipo_cozinha           VARCHAR(100) NOT NULL,
    horario_funcionamento  VARCHAR(255) NOT NULL,
    admin_id                BIGINT NOT NULL REFERENCES usuarios(id)
);

INSERT INTO restaurantes (nome, rua, numero, cidade, estado, cep, tipo_cozinha, horario_funcionamento, admin_id)
SELECT 'Cantina da Nonna', 'Rua Itália', '50', 'São Paulo', 'SP', '01310-300', 'Italiana', 'Seg a Dom, 11h às 23h',
       (SELECT id FROM usuarios WHERE email = 'admin@restaurante.com')
WHERE NOT EXISTS (SELECT 1 FROM restaurantes WHERE nome = 'Cantina da Nonna');

CREATE TABLE IF NOT EXISTS itens_cardapio (
    id                        BIGSERIAL PRIMARY KEY,
    restaurante_id            BIGINT NOT NULL REFERENCES restaurantes(id),
    nome                      VARCHAR(255) NOT NULL,
    descricao                 TEXT,
    preco                     NUMERIC(10,2) NOT NULL,
    disponivel_somente_local  BOOLEAN NOT NULL DEFAULT false,
    foto_path                 VARCHAR(500)
);

INSERT INTO itens_cardapio (restaurante_id, nome, descricao, preco, disponivel_somente_local, foto_path)
SELECT (SELECT id FROM restaurantes WHERE nome = 'Cantina da Nonna'), 'Fettuccine Alfredo',
       'Massa fresca ao molho branco cremoso', 49.90, false, '/fotos/fettuccine-alfredo.jpg'
WHERE NOT EXISTS (SELECT 1 FROM itens_cardapio WHERE nome = 'Fettuccine Alfredo');
