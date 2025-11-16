import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import { API_BASE_URL } from "@/config/api";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { Mail, CheckCircle2, XCircle, BookOpen, ArrowLeft } from "lucide-react";

export default function DetalhesReserva() {
  const navigate = useNavigate();
  const { isbn } = useParams();

  const [book, setBook] = useState(null);
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const bookRes = await fetch(`${API_BASE_URL}/books/${isbn}`);
      if (!bookRes.ok)
        throw new Error(`Livro não encontrado (${bookRes.status})`);
      const bookData = await bookRes.json();
      setBook(bookData);

      const resRes = await fetch(`${API_BASE_URL}/reservations/book/${isbn}`);
      if (!resRes.ok)
        throw new Error(`Erro ao carregar reservas (${resRes.status})`);
      const resData = await resRes.json();
      setReservations(Array.isArray(resData) ? resData : []);
    } catch (e) {
      setError(e.message || "Erro ao carregar dados");
    } finally {
      setLoading(false);
    }
  }, [isbn]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleNotify = useCallback(async (reservationId) => {
    try {
      setError("");
      setSuccess("");
      const res = await fetch(
        `${API_BASE_URL}/notifications/reservation-available`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ reservationId }),
        }
      );
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Erro ao enviar notificação (${res.status})`);
      }
      setSuccess("Notificação enviada com sucesso");
    } catch (e) {
      setError(e.message || "Erro ao enviar notificação");
    }
  }, []);

  const handleFulfill = useCallback(
    (reservation) => {
      const params = new URLSearchParams({
        reservationId: reservation.id.toString(),
        bookIsbn: reservation.bookIsbn,
        studentMatricula: reservation.studentMatricula,
      });
      navigate(`/emprestimos/novo?${params.toString()}`);
    },
    [navigate]
  );

  const handleCancel = useCallback(
    async (reservationId) => {
      try {
        setError("");
        setSuccess("");
        const res = await fetch(
          `${API_BASE_URL}/reservations/${reservationId}`,
          {
            method: "DELETE",
          }
        );
        if (!res.ok && res.status !== 204) {
          const text = await res.text();
          throw new Error(text || `Erro ao cancelar reserva (${res.status})`);
        }
        setSuccess("Reserva cancelada com sucesso");
        await loadData();
      } catch (e) {
        setError(e.message || "Erro ao cancelar reserva");
      }
    },
    [loadData]
  );

  const formatDate = (iso) => {
    if (!iso) return "-";
    try {
      return new Date(iso).toLocaleDateString("pt-BR");
    } catch {
      return iso;
    }
  };

  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Carregando...</CardTitle>
          <CardDescription>Buscando dados das reservas</CardDescription>
        </CardHeader>
      </Card>
    );
  }

  if (!book) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-destructive">
            Livro não encontrado
          </CardTitle>
          <CardDescription>
            <Link to="/reservas">
              <Button variant="secondary" size="sm">
                Voltar
              </Button>
            </Link>
          </CardDescription>
        </CardHeader>
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold">
            Detalhes das Reservas
          </h1>
          <p className="text-sm text-muted-foreground mt-1">{book.title}</p>
        </div>
        <Link to="/reservas">
          <Button variant="secondary">
            <ArrowLeft className="h-4 w-4 mr-2" /> Voltar
          </Button>
        </Link>
      </div>

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

      {success && (
        <Card className="border-emerald-300">
          <CardHeader>
            <CardTitle className="text-emerald-700">Sucesso</CardTitle>
            <CardDescription className="text-emerald-700">
              {success}
            </CardDescription>
          </CardHeader>
        </Card>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Informações do Livro</CardTitle>
          <CardDescription>Dados do livro reservado</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-[64px_1fr] gap-3">
            <div className="w-16 aspect-2/3 overflow-hidden rounded-md border bg-muted/30">
              {book.coverImageUrl ? (
                <img
                  src={book.coverImageUrl}
                  alt={book.title}
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
            <div>
              <div className="text-base font-semibold">{book.title}</div>
              <div className="text-sm text-foreground/80">{book.author}</div>
              <div className="text-xs text-foreground/70">
                ISBN: {book.isbn}
              </div>
              <div className="text-xs text-foreground/70 mt-1">
                Exemplares disponíveis: {book.quantity ?? 0}
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Fila de Reservas</CardTitle>
          <CardDescription>
            {reservations.length === 0
              ? "Nenhuma reserva ativa para este livro"
              : `${reservations.length} reserva(s) ativa(s)`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {reservations.length === 0 ? (
            <p className="text-sm text-muted-foreground text-center py-4">
              Não há reservas ativas para este livro.
            </p>
          ) : (
            <div className="space-y-3">
              {reservations.map((reservation) => (
                <div
                  key={reservation.id}
                  className="border rounded-md p-3 space-y-2"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-medium">
                          Posição {reservation.queuePosition}:
                        </span>
                        <span className="text-sm font-semibold">
                          {reservation.studentName ||
                            reservation.studentMatricula}
                        </span>
                      </div>
                      <div className="text-xs text-foreground/70 mt-1">
                        Data da reserva:{" "}
                        {formatDate(reservation.reservationDate)}
                      </div>
                      {reservation.studentMatricula && (
                        <div className="text-xs text-foreground/70">
                          Matrícula: {reservation.studentMatricula}
                        </div>
                      )}
                    </div>
                    <div className="flex flex-col gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleNotify(reservation.id)}
                        className="gap-1"
                      >
                        <Mail className="h-3 w-3" /> Notificar
                      </Button>
                      <Button
                        size="sm"
                        className="bg-primary text-primary-foreground gap-1"
                        onClick={() => handleFulfill(reservation)}
                      >
                        <CheckCircle2 className="h-3 w-3" /> Efetivar
                      </Button>
                      <AlertDialog>
                        <AlertDialogTrigger asChild>
                          <Button
                            size="sm"
                            variant="destructive"
                            className="gap-1"
                          >
                            <XCircle className="h-3 w-3" /> Cancelar
                          </Button>
                        </AlertDialogTrigger>
                        <AlertDialogContent>
                          <AlertDialogHeader>
                            <AlertDialogTitle>
                              Tem certeza que deseja cancelar?
                            </AlertDialogTitle>
                            <AlertDialogDescription>
                              Esta ação cancelará a reserva do aluno{" "}
                              <strong>
                                {reservation.studentName ||
                                  reservation.studentMatricula}
                              </strong>
                              . A fila será reorganizada automaticamente.
                            </AlertDialogDescription>
                          </AlertDialogHeader>
                          <AlertDialogFooter>
                            <AlertDialogCancel>Cancelar ação</AlertDialogCancel>
                            <AlertDialogAction
                              onClick={() => handleCancel(reservation.id)}
                            >
                              Confirmar cancelamento
                            </AlertDialogAction>
                          </AlertDialogFooter>
                        </AlertDialogContent>
                      </AlertDialog>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
