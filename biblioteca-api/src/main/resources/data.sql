-- Script SQL para popular dados iniciais (opcional)
-- Este arquivo será executado automaticamente pelo Spring Boot se spring.jpa.hibernate.ddl-auto=create ou create-drop
-- Inserir alunos de exemplo
INSERT INTO
  students (
    matricula,
    nome,
    cpf,
    data_nascimento,
    email,
    telefone,
    reservations_count
  )
VALUES
  (
    '2024001',
    'João Silva',
    '12345678901',
    '2000-05-15',
    'joao.silva@exemplo.com',
    '(11) 99999-1111',
    0
  ),
  (
    '2024002',
    'Maria Santos',
    '98765432100',
    '2001-08-20',
    'maria.santos@exemplo.com',
    '(11) 99999-2222',
    0
  ),
  (
    '2024003',
    'Pedro Oliveira',
    '11122233344',
    '1999-12-10',
    'pedro.oliveira@exemplo.com',
    '(11) 99999-3333',
    0
  );

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

-- Atualizar quantidade de livros (diminuir para criar empréstimo e reserva)
UPDATE
  books
SET
  quantity = 0
WHERE
  isbn = '978-8535914093';

UPDATE
  books
SET
  active_reservations_count = 1
WHERE
  isbn = '978-8535914093';

-- Inserir empréstimo em atraso (para testar notificação de atraso)
-- Empréstimo feito há 20 dias, vencido há 6 dias
INSERT INTO
  loans (
    student_matricula,
    book_isbn,
    loan_date,
    due_date,
    return_date,
    status,
    overdue_days,
    fine_amount,
    created_at
  )
VALUES
  (
    '2024001',
    '978-8535914093',
    NOW() - INTERVAL '20 days',
    NOW() - INTERVAL '6 days',
    NULL,
    'OVERDUE',
    6,
    NULL,
    NOW() - INTERVAL '20 days'
  );

-- Inserir reserva ativa (para testar notificação de reserva disponível)
INSERT INTO
  reservations (
    book_isbn,
    student_matricula,
    reservation_date,
    queue_position,
    status,
    created_at
  )
VALUES
  (
    '978-8535914093',
    '2024002',
    NOW() - INTERVAL '5 days',
    1,
    'ACTIVE',
    NOW() - INTERVAL '5 days'
  );