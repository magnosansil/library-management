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
import { Settings, Plus, Edit } from "lucide-react";

export default function Emprestimos() {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [sortOrder, setSortOrder] = useState("DUE_ASC");

  const fetchLoans = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API_BASE_URL}/loans`, {
        headers: { "Content-Type": "application/json" },
      });
      if (!res.ok)
        throw new Error(`Erro ao carregar empréstimos (${res.status})`);
      const data = await res.json();
      setLoans(Array.isArray(data) ? data : []);
    } catch (e) {
      setError(e.message || "Erro ao carregar empréstimos");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchLoans();
  }, [fetchLoans]);

  const filteredAndSortedLoans = useMemo(() => {
    let list = [...loans];

    if (statusFilter !== "ALL") {
      list = list.filter((l) => l.status === statusFilter);
    }

    list.sort((a, b) => {
      const aDue = new Date(a.dueDate).getTime();
      const bDue = new Date(b.dueDate).getTime();
      if (sortOrder === "DUE_ASC") return aDue - bDue;
      return bDue - aDue;
    });

    return list;
  }, [loans, statusFilter, sortOrder]);

  const formatDate = (iso) => {
    if (!iso) return "-";
    try {
      return new Date(iso).toLocaleDateString("pt-BR");
    } catch {
      return iso;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold">Empréstimos</h1>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <Link to="/emprestimos/novo">
            <Button className="bg-primary text-sm text-primary-foreground">
              <Plus className="h-4 w-4 mr-2" /> Novo empréstimo
            </Button>
          </Link>
          <Link to="/configuracoes">
            <Button variant="secondary" className="text-sm">
              <Settings className="h-4 w-4 mr-2" /> Configurações
            </Button>
          </Link>
        </div>
      </div>

      <div className="flex flex-col sm:flex-row gap-3 sm:items-center sm:justify-between">
        <div className="flex flex-wrap items-center gap-3">
          <select
            className="h-9 rounded-md border bg-background px-3 text-xs"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">Todos os status</option>
            <option value="ACTIVE">Ativos</option>
            <option value="OVERDUE">Em atraso</option>
            <option value="RETURNED">Devolvidos</option>
          </select>

          <select
            className="h-9 rounded-md border bg-background px-3 text-xs"
            value={sortOrder}
            onChange={(e) => setSortOrder(e.target.value)}
          >
            <option value="DUE_ASC">Vencimento: mais antigo</option>
            <option value="DUE_DESC">Vencimento: mais novo</option>
          </select>
        </div>
      </div>

      {loading && (
        <Card>
          <CardHeader>
            <CardTitle>Carregando...</CardTitle>
            <CardDescription>Buscando dados de empréstimos</CardDescription>
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

      {!loading && !error && filteredAndSortedLoans.length === 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Nenhum empréstimo encontrado</CardTitle>
            <CardDescription>
              Ajuste os filtros ou cadastre um novo empréstimo.
            </CardDescription>
          </CardHeader>
        </Card>
      )}

      <div className="grid gap-3">
        {filteredAndSortedLoans.map((loan) => (
          <Card key={loan.id} className="hover:shadow-sm transition">
            <CardContent className="p-4">
              <div className="grid grid-cols-[1fr_auto] gap-3">
                <div className="min-w-0">
                  <div className="text-base font-semibold truncate">
                    {loan.studentName || loan.studentMatricula}
                  </div>
                  <div className="text-sm text-foreground/80 truncate">
                    {loan.bookTitle || loan.bookIsbn}
                  </div>

                  <div className="mt-2 grid grid-cols-2 gap-3 text-xs text-foreground/80">
                    <div>
                      <div className="font-medium">Empréstimo</div>
                      <div>{formatDate(loan.loanDate)}</div>
                    </div>
                    <div>
                      <div className="font-medium">Vencimento</div>
                      <div>{formatDate(loan.dueDate)}</div>
                    </div>
                  </div>
                </div>
                <div className="flex flex-col items-end justify-between">
                  <span
                    className={`text-xs font-medium px-2 py-1 rounded-md border ${
                      loan.status === "OVERDUE"
                        ? "bg-rose-100"
                        : loan.status === "RETURNED"
                        ? "bg-emerald-100"
                        : "bg-amber-100"
                    }`}
                    title={loan.status}
                  >
                    {loan.status === "OVERDUE"
                      ? "Em atraso"
                      : loan.status === "RETURNED"
                      ? "Devolvido"
                      : "Ativo"}
                  </span>

                  <Link to={`/emprestimos/${loan.id}/editar`}>
                    <Button
                      size="sm"
                      className="mt-3 h-9 w-9 p-0 bg-primary text-primary-foreground hover:bg-primary/90"
                    >
                      <Edit className="h-4 w-4" />
                    </Button>
                  </Link>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
