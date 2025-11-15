import { useState, useEffect, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ArrowLeft, Save, Trash2 } from "lucide-react";

const API_BASE_URL = "http://localhost:8080/api";

export default function EdicaoLivro() {
  const navigate = useNavigate();
  const { isbn } = useParams();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    isbn: "",
    title: "",
    author: "",
    coverImageUrl: "",
    keywords: "",
    synopsis: "",
    quantity: 0,
  });

  const fetchLivro = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/books/${isbn}`);
      if (response.ok) {
        const data = await response.json();
        setFormData({
          isbn: data.isbn || "",
          title: data.title || "",
          author: data.author || "",
          coverImageUrl: data.coverImageUrl || "",
          keywords: data.keywords || "",
          synopsis: data.synopsis || "",
          quantity: data.quantity || 0,
        });
      } else {
        alert("Livro não encontrado");
        navigate("/acervo");
      }
    } catch (error) {
      console.error("Erro ao buscar livro:", error);
      alert("Erro ao carregar livro. Verifique se a API está rodando.");
    } finally {
      setLoading(false);
    }
  }, [isbn, navigate]);

  useEffect(() => {
    fetchLivro();
  }, [fetchLivro]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "quantity" ? parseInt(value) || 0 : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);

    try {
      const response = await fetch(`${API_BASE_URL}/books/${isbn}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        navigate("/acervo");
      } else {
        const error = await response.text();
        alert(`Erro ao atualizar livro: ${error}`);
      }
    } catch (error) {
      console.error("Erro ao atualizar livro:", error);
      alert("Erro ao atualizar livro. Verifique se a API está rodando.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm("Tem certeza que deseja excluir este livro?")) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/books/${isbn}`, {
        method: "DELETE",
      });

      if (response.ok) {
        navigate("/acervo");
      } else {
        const error = await response.text();
        alert(`Erro ao excluir livro: ${error}`);
      }
    } catch (error) {
      console.error("Erro ao excluir livro:", error);
      alert("Erro ao excluir livro. Verifique se a API está rodando.");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-muted-foreground">Carregando livro...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={() => navigate("/acervo")}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold">Editar Livro</h1>
          <p className="text-sm sm:text-base text-muted-foreground mt-1">
            Atualize as informações do livro
          </p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Informações do Livro</CardTitle>
          <CardDescription>
            ISBN: {formData.isbn} (não pode ser alterado)
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4 sm:space-y-6">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="isbn">ISBN</Label>
                <Input
                  id="isbn"
                  name="isbn"
                  value={formData.isbn}
                  disabled
                  className="bg-muted"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="quantity">Quantidade *</Label>
                <Input
                  id="quantity"
                  name="quantity"
                  type="number"
                  min="0"
                  value={formData.quantity}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="title">Título *</Label>
              <Input
                id="title"
                name="title"
                value={formData.title}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="author">Autor *</Label>
              <Input
                id="author"
                name="author"
                value={formData.author}
                onChange={handleChange}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="coverImageUrl">URL da Capa</Label>
              <Input
                id="coverImageUrl"
                name="coverImageUrl"
                type="url"
                value={formData.coverImageUrl}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="keywords">Palavras-chave</Label>
              <Input
                id="keywords"
                name="keywords"
                value={formData.keywords}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="synopsis">Sinopse</Label>
              <textarea
                id="synopsis"
                name="synopsis"
                value={formData.synopsis}
                onChange={handleChange}
                rows="4"
                className="flex min-h-[80px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
              />
            </div>

            <div className="flex flex-col sm:flex-row gap-3 sm:justify-between">
              <Button
                type="button"
                variant="destructive"
                onClick={handleDelete}
                className="w-full sm:w-auto"
              >
                <Trash2 className="h-4 w-4 mr-2" />
                Excluir Livro
              </Button>
              <div className="flex flex-col sm:flex-row gap-3">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => navigate("/acervo")}
                  className="w-full sm:w-auto"
                >
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  disabled={saving}
                  className="w-full sm:w-auto"
                >
                  {saving ? (
                    "Salvando..."
                  ) : (
                    <>
                      <Save className="h-4 w-4 mr-2" />
                      Salvar Alterações
                    </>
                  )}
                </Button>
              </div>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
