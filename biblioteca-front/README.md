# Biblioteca Front-end

Front-end do Sistema de Gerenciamento de Biblioteca desenvolvido com React, Tailwind CSS 4.1 e shadcn/ui.

## 🚀 Tecnologias

- **React 19** - Biblioteca JavaScript para construção de interfaces
- **Vite** - Build tool e dev server
- **Tailwind CSS 4.1** - Framework CSS utility-first
- **shadcn/ui** - Componentes UI reutilizáveis
- **React Router** - Roteamento para aplicações React
- **Lucide React** - Ícones modernos

## 📦 Instalação

```bash
npm install
```

## 🏃 Executar em Desenvolvimento

```bash
npm run dev
```

A aplicação estará disponível em `http://localhost:5173`

## 🏗️ Build para Produção

```bash
npm run build
```

## 📁 Estrutura do Projeto

```
biblioteca-front/
├── src/
│   ├── components/
│   │   ├── ui/          # Componentes shadcn/ui
│   │   └── Layout.jsx   # Layout principal com navegação
│   ├── pages/
│   │   ├── Home.jsx              # Página inicial
│   │   ├── Acervo.jsx            # Lista de livros
│   │   ├── CadastroLivro.jsx     # Cadastro de novo livro
│   │   └── EdicaoLivro.jsx       # Edição de livro
│   ├── lib/
│   │   └── utils.js     # Utilitários (cn function)
│   ├── App.jsx          # Componente principal com rotas
│   ├── main.jsx         # Entry point
│   └── index.css       # Estilos globais e tema Tailwind
├── components.json      # Configuração shadcn/ui
└── vite.config.js      # Configuração Vite
```

## 🎨 Design Mobile First

O projeto foi desenvolvido com abordagem mobile first, garantindo uma experiência otimizada em dispositivos móveis e adaptável para telas maiores.

## 🔌 Integração com API

A aplicação consome a API REST localizada em `http://localhost:8080/api`. Certifique-se de que a API está rodando antes de usar o front-end.

### Endpoints Utilizados

- `GET /api/books` - Listar todos os livros
- `GET /api/books/{isbn}` - Buscar livro por ISBN
- `POST /api/books` - Criar novo livro
- `PUT /api/books/{isbn}` - Atualizar livro
- `DELETE /api/books/{isbn}` - Excluir livro

## 📱 Rotas

- `/` - Página inicial
- `/acervo` - Lista de livros
- `/livros/novo` - Cadastro de novo livro
- `/livros/:isbn/editar` - Edição de livro

## 🎯 Próximos Passos

- [ ] Páginas de gerenciamento de alunos
- [ ] Páginas de empréstimos
- [ ] Páginas de reservas
- [ ] Sistema de notificações
- [ ] Dashboard com estatísticas
