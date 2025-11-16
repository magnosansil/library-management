import { useCallback, useEffect, useMemo, useState } from "react";
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
import { Trash2 } from "lucide-react";

export default function EditarEmprestimo() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [loan, setLoan] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [returnDate, setReturnDate] = useState("");
  const [savingReturn, setSavingReturn] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const loadLoan = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API_BASE_URL}/loans/${id}`);
      if (!res.ok) throw new Error(`Empréstimo não encontrado (${res.status})`);
      const data = await res.json();
      setLoan(data);
    } catch (e) {
      setError(e.message || "Erro ao carregar empréstimo");
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    loadLoan();
  }, [loadLoan]);

  const statusText = useMemo(() => {
    if (!loan) return "-";
    if (loan.status === "OVERDUE") return "Em atraso";
    if (loan.status === "RETURNED") return "Devolvido";
    return "Ativo";
  }, [loan]);

  const handleReturn = useCallback(async () => {
    if (savingReturn) return;
    setSavingReturn(true);
    setError("");
    try {
      const body = returnDate ? { returnDate: `${returnDate}T00:00:00` } : {};
      const res = await fetch(`${API_BASE_URL}/loans/${id}/return`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: Object.keys(body).length ? JSON.stringify(body) : null,
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Erro ao registrar devolução (${res.status})`);
      }
      await loadLoan();
      setReturnDate("");
    } catch (e) {
      setError(e.message || "Erro ao registrar devolução");
    } finally {
      setSavingReturn(false);
    }
  }, [API_BASE_URL, id, loadLoan, returnDate, savingReturn]);

  const handleDelete = useCallback(async () => {
    if (deleting) return;
    setDeleting(true);
    setError("");
    try {
      const res = await fetch(`${API_BASE_URL}/loans/${id}`, {
        method: "DELETE",
      });
      if (!res.ok && res.status !== 204) {
        const text = await res.text();
        throw new Error(text || `Erro ao excluir (${res.status})`);
      }
      navigate("/emprestimos");
    } catch (e) {
      setError(e.message || "Erro ao excluir empréstimo");
    } finally {
      setDeleting(false);
    }
  }, [id, navigate, deleting]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold">Editar Empréstimo</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Visualize, devolva ou exclua o empréstimo
        </p>
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

      {loading && (
        <Card>
          <CardHeader>
            <CardTitle>Carregando...</CardTitle>
            <CardDescription>Buscando dados do empréstimo</CardDescription>
          </CardHeader>
        </Card>
      )}

      {!loading && loan && (
        <>
          <Card>
            <CardHeader>
              <CardTitle>Detalhes</CardTitle>
              <CardDescription>Informações do empréstimo</CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="grid grid-cols-2 gap-3 text-sm">
                <div>
                  <div className="font-medium">Aluno</div>
                  <div className="text-foreground/90">
                    {loan.studentName || loan.studentMatricula}
                  </div>
                </div>
                <div>
                  <div className="font-medium">Livro</div>
                  <div className="text-foreground/90">
                    {loan.bookTitle || loan.bookIsbn}
                  </div>
                </div>
                <div>
                  <div className="font-medium">Empréstimo</div>
                  <div className="text-foreground/90">
                    {new Date(loan.loanDate).toLocaleDateString("pt-BR")}
                  </div>
                </div>
                <div>
                  <div className="font-medium">Vencimento</div>
                  <div className="text-foreground/90">
                    {new Date(loan.dueDate).toLocaleDateString("pt-BR")}
                  </div>
                </div>
                <div>
                  <div className="font-medium">Status</div>
                  <div className="text-foreground/90">{statusText}</div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Registrar devolução</CardTitle>
              <CardDescription>
                Informe a data (opcional) ou deixe em branco para usar hoje
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="grid gap-2 max-w-xs">
                <label className="text-sm font-medium">
                  Data de devolução (opcional)
                </label>
                <input
                  type="date"
                  className="h-10 rounded-md border bg-background px-3 text-sm"
                  value={returnDate}
                  max={new Date().toISOString().slice(0, 10)}
                  onChange={(e) => setReturnDate(e.target.value)}
                  disabled={loan.status === "RETURNED"}
                />
              </div>
              <div className="flex items-center gap-2">
                <Button
                  onClick={handleReturn}
                  disabled={savingReturn || loan.status === "RETURNED"}
                  className="bg-primary text-primary-foreground"
                >
                  {savingReturn ? "Salvando..." : "Confirmar devolução"}
                </Button>
                <Link to="/emprestimos">
                  <Button variant="secondary">Voltar</Button>
                </Link>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Excluir empréstimo</CardTitle>
              <CardDescription>Esta ação é irreversível</CardDescription>
            </CardHeader>
            <CardContent>
              <AlertDialog>
                <AlertDialogTrigger asChild>
                  <Button variant="destructive" className="gap-2">
                    <Trash2 className="h-4 w-4" /> Excluir empréstimo
                  </Button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Tem certeza que deseja excluir?
                    </AlertDialogTitle>
                    <AlertDialogDescription>
                      O registro será removido permanentemente. Se o empréstimo
                      ainda estiver ativo, o exemplar será devolvido ao estoque.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel>Cancelar</AlertDialogCancel>
                    <AlertDialogAction
                      onClick={handleDelete}
                      disabled={deleting}
                    >
                      {deleting ? "Excluindo..." : "Excluir"}
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </CardContent>
          </Card>
        </>
      )}
    </div>
  );
}
