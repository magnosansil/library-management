-- Script SQL para popular dados iniciais (opcional)
-- Este arquivo será executado automaticamente pelo Spring Boot se spring.jpa.hibernate.ddl-auto=create ou create-drop
-- Inserir usuários de exemplo
INSERT INTO
  users (name, email, max_loans, created_at)
VALUES
  ('João Silva', 'joao@email.com', 3, NOW()),
  ('Maria Santos', 'maria@email.com', 5, NOW()),
  ('Pedro Oliveira', 'pedro@email.com', 3, NOW());

-- Inserir livros de exemplo
INSERT INTO
  books (
    isbn,
    title,
    author,
    cover_image_url,
    keywords,
    synopsis,
    entry_date,
    quantity
  )
VALUES
  (
    '978-8535914093',
    'Dom Casmurro',
    'Machado de Assis',
    'https://m.media-amazon.com/images/I/61x1ZHomWUL.jpg',
    'literatura brasileira, romance, século XIX',
    'Romance clássico da literatura brasileira que narra a história de Bentinho e Capitu.',
    NOW(),
    5
  ),
  (
    '978-8572326978',
    'O Cortiço',
    'Aluísio Azevedo',
    'https://m.media-amazon.com/images/I/81m1emiSp-S.jpg',
    'naturalismo, literatura brasileira, romance social',
    'Romance naturalista que retrata a vida em um cortiço no Rio de Janeiro do século XIX.',
    NOW(),
    3
  ),
  (
    '978-8535914094',
    'Memórias Póstumas de Brás Cubas',
    'Machado de Assis',
    'https://m.media-amazon.com/images/I/91GAAzBixYL._UF1000,1000_QL80_.jpg',
    'literatura brasileira, romance, realismo',
    'Romance narrado por um defunto que conta sua própria história de forma irônica.',
    NOW(),
    4
  ),
  (
    '978-8572326979',
    'Iracema',
    'José de Alencar',
    'https://m.media-amazon.com/images/I/71LCDi6E2oL.jpg',
    'romantismo, literatura brasileira, romance indianista',
    'Romance indianista que narra a história de amor entre Iracema e Martim.',
    NOW(),
    2
  ),
  (
    '978-8572326980',
    'O Guarani',
    'José de Alencar',
    'https://m.media-amazon.com/images/I/51L7Xbds8bL._SY445_SX342_ControlCacheEqualizer_.jpg',
    'romantismo, literatura brasileira, romance histórico',
    'Romance histórico que narra a história de amor entre Peri e Ceci.',
    NOW(),
    3
  );