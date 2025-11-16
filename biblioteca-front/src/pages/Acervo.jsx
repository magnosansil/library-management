import { useState, useEffect, useMemo } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Search, Plus, Edit, BookOpen } from "lucide-react";
import { API_BASE_URL } from "@/config/api";

export default function Acervo() {
  const [livros, setLivros] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState("title_asc");

  useEffect(() => {
    fetchLivros();
  }, []);

  const fetchLivros = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/books`);
      if (response.ok) {
        const data = await response.json();
        setLivros(data);
      }
    } catch (error) {
      console.error("Erro ao buscar livros:", error);
    } finally {
      setLoading(false);
    }
  };

  const filteredLivros = useMemo(() => {
    const base = livros.filter((livro) =>
      [livro.title, livro.author, livro.isbn, livro.keywords]
        .filter(Boolean)
        .join(" ")
        .toLowerCase()
        .includes(searchTerm.toLowerCase())
    );

    const compare = (a, b) => {
      const qa = a.quantity ?? 0;
      const qb = b.quantity ?? 0;
      switch (sortBy) {
        case "title_asc":
          return (a.title || "").localeCompare(b.title || "", "pt-BR");
        case "title_desc":
          return (b.title || "").localeCompare(a.title || "", "pt-BR");
        case "author_asc":
          return (a.author || "").localeCompare(b.author || "", "pt-BR");
        case "author_desc":
          return (b.author || "").localeCompare(a.author || "", "pt-BR");
        case "quantity_desc":
          return qb - qa;
        case "quantity_asc":
          return qa - qb;
        default:
          return 0;
      }
    };

    return [...base].sort(compare);
  }, [livros, searchTerm, sortBy]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-muted-foreground">Carregando livros...</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="text-xl sm:text-2xl font-semibold">Acervo</h1>
          <p className="text-xs sm:text-sm text-muted-foreground mt-1">
            {livros.length}{" "}
            {livros.length === 1 ? "livro cadastrado" : "livros cadastrados"}
          </p>
        </div>
        <Link to="/livros/novo">
          <Button className="h-9 px-4">
            <Plus className="h-4 w-4 mr-2" />
            Adicionar
          </Button>
        </Link>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          type="text"
          placeholder="Buscar por título, livro, ISBN ou palavras-chave..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      <div className="flex items-center justify-between">
        <span className="text-sm font-light">Livros disponíveis no acervo</span>
        <select
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          className="h-9 rounded-md border border-input bg-background px-3 text-sm"
          aria-label="Ordenar"
        >
          <option value="title_asc">Título (A → Z)</option>
          <option value="title_desc">Título (Z → A)</option>
          <option value="author_asc">Autor (A → Z)</option>
          <option value="author_desc">Autor (Z → A)</option>
          <option value="quantity_desc">Exemplares (dec)</option>
          <option value="quantity_asc">Exemplares (cre)</option>
        </select>
      </div>

      {filteredLivros.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-12 border rounded-xl">
          <BookOpen className="h-12 w-12 text-muted-foreground mb-4" />
          <p className="text-muted-foreground text-center">
            {searchTerm
              ? "Nenhum livro encontrado"
              : "Nenhum livro cadastrado ainda"}
          </p>
        </div>
      ) : (
        <div className="space-y-2">
          {filteredLivros.map((livro) => (
            <div
              key={livro.isbn}
              className="grid grid-cols-[64px_1fr_auto] gap-3 items-center border rounded-xl p-3"
            >
              <div className="w-16 overflow-hidden rounded-md border bg-muted/30">
                {livro.coverImageUrl ? (
                  <img
                    src={livro.coverImageUrl}
                    alt={livro.title}
                    loading="lazy"
                    decoding="async"
                    className="h-full w-full object-cover"
                  />
                ) : (
                  <div className="flex h-full w-full items-center justify-center">
                    <BookOpen className="h-6 w-6 text-muted-foreground" />
                  </div>
                )}
              </div>

              <div className="min-w-0 self-start">
                <div className="text-sm sm:text-base font-semibold truncate">
                  {livro.title}
                </div>
                <div className="text-xs sm:text-sm text-muted-foreground truncate">
                  {livro.author}
                </div>
                <div className="text-xs text-muted-foreground truncate">
                  ISBN: {livro.isbn}
                </div>
              </div>

              <div className="flex flex-col items-end justify-between h-full gap-2">
                <Link
                  to={`/livros/${livro.isbn}/editar`}
                  className="inline-flex items-center justify-center h-9 w-9 rounded-md bg-primary text-primary-foreground hover:bg-primary/90"
                  aria-label={`Editar ${livro.title}`}
                >
                  <Edit className="h-4 w-4" />
                </Link>
                <div className="text-xs sm:text-sm text-muted-foreground">
                  Exemplares:{" "}
                  <span className="font-medium text-foreground">
                    {livro.quantity ?? 0}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
