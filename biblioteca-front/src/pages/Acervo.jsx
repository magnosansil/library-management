import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Search, Plus, Edit, BookOpen } from "lucide-react";

const API_BASE_URL = "http://localhost:8080/api";

export default function Acervo() {
  const [livros, setLivros] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");

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

  const filteredLivros = livros.filter(
    (livro) =>
      livro.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      livro.author?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      livro.isbn?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-muted-foreground">Carregando livros...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold">Acervo</h1>
          <p className="text-sm sm:text-base text-muted-foreground mt-1">
            {livros.length}{" "}
            {livros.length === 1 ? "livro cadastrado" : "livros cadastrados"}
          </p>
        </div>
        <Link to="/livros/novo">
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            Novo Livro
          </Button>
        </Link>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          type="text"
          placeholder="Buscar por título, autor ou ISBN..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      {filteredLivros.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <BookOpen className="h-12 w-12 text-muted-foreground mb-4" />
            <p className="text-muted-foreground text-center">
              {searchTerm
                ? "Nenhum livro encontrado"
                : "Nenhum livro cadastrado ainda"}
            </p>
            {!searchTerm && (
              <Link to="/livros/novo" className="block mt-4">
                <Button>
                  <Plus className="h-4 w-4 mr-2" />
                  Cadastrar Primeiro Livro
                </Button>
              </Link>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
          {filteredLivros.map((livro) => (
            <Card key={livro.isbn} className="flex flex-col">
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <CardTitle className="text-lg sm:text-xl line-clamp-2">
                      {livro.title}
                    </CardTitle>
                    <CardDescription className="mt-1">
                      {livro.author}
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="flex-1 space-y-4">
                <div className="space-y-2 text-sm">
                  <div>
                    <span className="font-medium">ISBN: </span>
                    <span className="text-muted-foreground">{livro.isbn}</span>
                  </div>
                  <div>
                    <span className="font-medium">Quantidade: </span>
                    <span className="text-muted-foreground">
                      {livro.quantity || 0}
                    </span>
                  </div>
                </div>
                <Link to={`/livros/${livro.isbn}/editar`} className="block">
                  <Button variant="outline" className="w-full">
                    <Edit className="h-4 w-4 mr-2" />
                    Editar
                  </Button>
                </Link>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
