import { useEffect, useMemo, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { API_BASE_URL } from "@/config/api";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Plus, BookOpen, ChevronRight } from "lucide-react";

export default function Reservas() {
  const [reservations, setReservations] = useState([]);
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [sortOrder, setSortOrder] = useState("TITLE_ASC");

  const fetchReservations = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API_BASE_URL}/reservations`, {
        headers: { "Content-Type": "application/json" },
      });
      if (!res.ok) throw new Error(`Erro ao carregar reservas (${res.status})`);
      const data = await res.json();
      setReservations(Array.isArray(data) ? data : []);
    } catch (e) {
      setError(e.message || "Erro ao carregar reservas");
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchBooks = useCallback(async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/books`, {
        headers: { "Content-Type": "application/json" },
      });
      if (!res.ok) return;
      const data = await res.json();
      setBooks(Array.isArray(data) ? data : []);
    } catch {
      // ignore
    }
  }, []);

  useEffect(() => {
    fetchReservations();
    fetchBooks();
  }, [fetchReservations, fetchBooks]);

  const groupedReservations = useMemo(() => {
    const filtered = reservations.filter((r) => r.status === "ACTIVE");

    const grouped = {};
    filtered.forEach((r) => {
      const isbn = r.bookIsbn;
      if (!grouped[isbn]) {
        grouped[isbn] = {
          isbn,
          title: r.bookTitle,
          author: r.bookAuthor,
          reservations: [],
        };
      }
      grouped[isbn].reservations.push(r);
    });

    Object.values(grouped).forEach((g) => {
      g.reservations.sort(
        (a, b) => (a.queuePosition || 0) - (b.queuePosition || 0)
      );
    });

    let list = Object.values(grouped);
    list.sort((a, b) => {
      const titleA = (a.title || "").toLowerCase();
      const titleB = (b.title || "").toLowerCase();
      if (sortOrder === "TITLE_ASC")
        return titleA.localeCompare(titleB, "pt-BR");
      return titleB.localeCompare(titleA, "pt-BR");
    });

    return list;
  }, [reservations, sortOrder]);

  const getBookImage = (isbn) => {
    const book = books.find((b) => b.isbn === isbn);
    return book?.coverImageUrl || null;
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3">
        <div className="flex items-center justify-between gap-3">
          <h1 className="text-xl sm:text-2xl font-semibold">Reservas Ativas</h1>
          <Link to="/reservas/nova">
            <Button className="bg-primary text-sm text-primary-foreground">
              <Plus className="h-4 w-4 mr-2" /> Nova Reserva
            </Button>
          </Link>
        </div>
      </div>

      <div className="flex flex-col sm:flex-row gap-3 sm:items-center sm:justify-between">
        <div className="flex flex-wrap items-center gap-3">
          <select
            className="h-9 rounded-md border bg-background px-3 text-xs"
            value={sortOrder}
            onChange={(e) => setSortOrder(e.target.value)}
          >
            <option value="TITLE_ASC">Título: A → Z</option>
            <option value="TITLE_DESC">Título: Z → A</option>
          </select>
        </div>
      </div>

      {loading && (
        <Card>
          <CardHeader>
            <CardTitle>Carregando...</CardTitle>
            <CardDescription>Buscando dados de reservas</CardDescription>
          </CardHeader>
        </Card>
      )}

      {error && (
        <Card className="border-destructive">
          <CardHeader>
            <CardTitle className="text-destructive">Erro</CardTitle>
            <CardDescription className="text-destructive">
              {error}
            </CardDescription>
          </CardHeader>
        </Card>
      )}

      {!loading && !error && groupedReservations.length === 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Nenhuma reserva encontrada</CardTitle>
            <CardDescription>
              Ajuste os filtros ou cadastre uma nova reserva.
            </CardDescription>
          </CardHeader>
        </Card>
      )}

      <div className="grid gap-3">
        {groupedReservations.map((group) => {
          const activeReservations = group.reservations.filter(
            (r) => r.status === "ACTIVE"
          );
          const firstTwo = activeReservations.slice(0, 2);
          const coverImage = getBookImage(group.isbn);

          return (
            <Card key={group.isbn} className="hover:shadow-sm transition">
              <CardContent className="p-4">
                <div className="grid grid-cols-[64px_1fr_auto] gap-3">
                  <div className="w-16 aspect-2/3 overflow-hidden rounded-md border bg-muted/30">
                    {coverImage ? (
                      <img
                        src={coverImage}
                        alt={group.title}
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

                  <div className="min-w-0">
                    <div className="text-base font-semibold truncate">
                      {group.title}
                    </div>
                    <div className="text-sm text-foreground/80 truncate">
                      {group.author}
                    </div>

                    <div className="mt-2">
                      <div className="text-xs font-medium text-foreground/90">
                        Fila de Reserva:
                      </div>
                      {firstTwo.length === 0 ? (
                        <div className="text-xs text-foreground/70 mt-1">
                          Nenhuma reserva ativa
                        </div>
                      ) : (
                        <div className="mt-1 space-y-1">
                          {firstTwo.map((reservation) => (
                            <div
                              key={reservation.id}
                              className="text-xs text-foreground/80"
                            >
                              {reservation.queuePosition}.{" "}
                              {reservation.studentName ||
                                reservation.studentMatricula}
                            </div>
                          ))}
                          {activeReservations.length > 2 && (
                            <div className="text-xs text-foreground/70 italic">
                              +{activeReservations.length - 2} mais
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="flex flex-col items-end justify-between gap-2">
                    <Link to={`/reservas/${group.isbn}/detalhes`}>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-xs gap-1"
                      >
                        Ver mais <ChevronRight className="h-3 w-3" />
                      </Button>
                    </Link>
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>
    </div>
  );
}
