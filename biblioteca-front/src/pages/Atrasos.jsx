import { useEffect, useMemo, useState, useCallback } from "react";
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
} from "@/components/ui/alert-dialog";
import { Mail, RotateCcw } from "lucide-react";
import { toast } from "sonner";

export default function Atrasos() {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [sortOrder, setSortOrder] = useState("DUE_ASC");
  const [notifyingLoanId, setNotifyingLoanId] = useState(null);
  const [returningLoanId, setReturningLoanId] = useState(null);
  const [chargeFine, setChargeFine] = useState(false);
  const [loanToReturn, setLoanToReturn] = useState(null);

  const fetchLoans = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      await fetch(`${API_BASE_URL}/loans/check-overdue`, {
        headers: { "Content-Type": "application/json" },
      });

      const res = await fetch(`${API_BASE_URL}/loans/overdue`, {
        headers: { "Content-Type": "application/json" },
      });
      if (!res.ok)
        throw new Error(
          `Erro ao carregar empréstimos em atraso (${res.status})`
        );
      const data = await res.json();
      setLoans(Array.isArray(data) ? data : []);
    } catch (e) {
      setError(e.message || "Erro ao carregar empréstimos em atraso");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchLoans();
  }, [fetchLoans]);

  const filteredAndSortedLoans = useMemo(() => {
    let list = [...loans];

    list.sort((a, b) => {
      const aDue = new Date(a.dueDate).getTime();
      const bDue = new Date(b.dueDate).getTime();
      if (sortOrder === "DUE_ASC") return aDue - bDue;
      return bDue - aDue;
    });

    return list;
  }, [loans, sortOrder]);

  const formatDate = (iso) => {
    if (!iso) return "-";
    try {
      return new Date(iso).toLocaleDateString("pt-BR");
    } catch {
      return iso;
    }
  };

  const formatCurrency = (cents) => {
    if (!cents) return "R$ 0,00";
    const reais = cents / 100;
    return `R$ ${reais.toFixed(2).replace(".", ",")}`;
  };

  const handleNotifyOverdue = useCallback(async (loanId) => {
    setNotifyingLoanId(loanId);
    setError("");
    try {
      const res = await fetch(`${API_BASE_URL}/notifications/overdue`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ loanId }),
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Erro ao enviar notificação (${res.status})`);
      }
    } catch (e) {
      setError(e.message || "Erro ao enviar notificação");
    } finally {
      setNotifyingLoanId(null);
    }
  }, []);

  const handleReturnLoan = useCallback(
    async (loanId, shouldChargeFine) => {
      setReturningLoanId(loanId);
      setError("");
      const currentLoan = loans.find((l) => l.id === loanId);

      try {
        if (currentLoan?.fineAmount > 0) {
          if (shouldChargeFine) {
            const paidRes = await fetch(
              `${API_BASE_URL}/loans/${loanId}/fine/paid`,
              {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
              }
            );
            if (!paidRes.ok) {
              const text = await paidRes.text();
              throw new Error(
                text || `Erro ao marcar multa como paga (${paidRes.status})`
              );
            }
          } else {
            const forgiveRes = await fetch(
              `${API_BASE_URL}/loans/${loanId}/fine/forgiven`,
              {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
              }
            );
            if (!forgiveRes.ok) {
              const text = await forgiveRes.text();
              throw new Error(
                text || `Erro ao perdoar multa (${forgiveRes.status})`
              );
            }
          }
        }

        const res = await fetch(`${API_BASE_URL}/loans/${loanId}/return`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({}),
        });
        if (!res.ok) {
          const text = await res.text();
          throw new Error(
            text || `Erro ao registrar devolução (${res.status})`
          );
        }

        setChargeFine(false);
        setLoanToReturn(null);

        const bookTitle =
          currentLoan?.bookTitle || currentLoan?.bookIsbn || "livro";
        if (shouldChargeFine && currentLoan?.fineAmount > 0) {
          toast.success("Livro devolvido com sucesso", {
            description: `${bookTitle} foi devolvido. Multa de ${formatCurrency(
              currentLoan.fineAmount
            )} será cobrada. O exemplar foi adicionado ao estoque.`,
          });
        } else if (currentLoan?.fineAmount > 0) {
          toast.success("Livro devolvido com sucesso", {
            description: `${bookTitle} foi devolvido. Multa foi perdoada. O exemplar foi adicionado ao estoque.`,
          });
        } else {
          toast.success("Livro devolvido com sucesso", {
            description: `${bookTitle} foi devolvido. O exemplar foi adicionado ao estoque.`,
          });
        }

        await fetchLoans();
      } catch (e) {
        setError(e.message || "Erro ao registrar devolução");
        toast.error("Erro ao devolver livro", {
          description: e.message || "Ocorreu um erro ao processar a devolução.",
        });
      } finally {
        setReturningLoanId(null);
      }
    },
    [fetchLoans, loans]
  );

  const openReturnDialog = (loan) => {
    setLoanToReturn(loan);
    setChargeFine(true);
  };

  const closeReturnDialog = () => {
    setLoanToReturn(null);
    setChargeFine(false);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl sm:text-2xl font-semibold">Atrasos e Multas</h1>
        <p className="text-sm sm:text-base text-muted-foreground mt-1">
          Visualize empréstimos em atraso e gerencie multas
        </p>
      </div>

      <div className="flex flex-col sm:flex-row gap-3 sm:items-center sm:justify-between">
        <div className="flex flex-wrap items-center gap-3">
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
            <CardDescription>Buscando empréstimos em atraso</CardDescription>
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
            <CardTitle>Nenhum empréstimo em atraso</CardTitle>
            <CardDescription>
              Não há empréstimos em atraso no momento.
            </CardDescription>
          </CardHeader>
        </Card>
      )}

      <div className="grid gap-3">
        {filteredAndSortedLoans.map((loan) => (
          <Card key={loan.id} className="hover:shadow-sm transition">
            <CardContent className="p-4">
              <div className="grid grid-cols-[1fr_auto] gap-4">
                <div className="space-y-2">
                  <div className="text-base sm:text-lg font-semibold text-foreground">
                    {loan.studentName || loan.studentMatricula}
                  </div>
                  <div className="text-sm font-medium text-foreground/90">
                    {loan.bookTitle || loan.bookIsbn}
                  </div>
                  <div className="text-xs text-foreground/70">
                    {loan.bookAuthor || "Autor não informado"}
                  </div>
                  <div className="grid grid-cols-2 gap-2 text-xs text-foreground/80 mt-3">
                    <div>
                      <div className="font-medium">Empréstimo</div>
                      <div>{formatDate(loan.loanDate)}</div>
                    </div>
                    <div>
                      <div className="font-medium">Vencimento</div>
                      <div>{formatDate(loan.dueDate)}</div>
                    </div>
                  </div>
                  <div className="flex items-center gap-4 text-xs mt-2">
                    <div>
                      <div className="font-medium text-rose-700">
                        Dias de atraso
                      </div>
                      <div className="text-rose-600 font-semibold">
                        {loan.overdueDays || 0} dia
                        {loan.overdueDays !== 1 ? "s" : ""}
                      </div>
                    </div>
                    <div>
                      <div className="font-medium text-rose-700">
                        Valor da multa
                      </div>
                      <div className="text-rose-600 font-semibold">
                        {formatCurrency(loan.fineAmount)}
                      </div>
                    </div>
                  </div>
                </div>

                <div className="flex flex-col items-end gap-2">
                  <Button
                    size="sm"
                    className="h-9 px-3 text-xs bg-primary text-primary-foreground hover:bg-primary/90"
                    onClick={() => handleNotifyOverdue(loan.id)}
                    disabled={notifyingLoanId === loan.id}
                  >
                    {notifyingLoanId === loan.id ? (
                      "Enviando..."
                    ) : (
                      <>
                        <Mail className="h-3 w-3 mr-1" />
                        Notificar atraso
                      </>
                    )}
                  </Button>

                  <div>
                    <Button
                      size="sm"
                      className="h-9 px-3 text-xs bg-primary text-primary-foreground hover:bg-primary/90"
                      onClick={() => openReturnDialog(loan)}
                      disabled={returningLoanId === loan.id}
                    >
                      {returningLoanId === loan.id ? (
                        "Devolvendo..."
                      ) : (
                        <>
                          <RotateCcw className="h-3 w-3 mr-1" />
                          Gerar devolução
                        </>
                      )}
                    </Button>

                    {loanToReturn?.id === loan.id && (
                      <AlertDialog
                        open={true}
                        onOpenChange={(open) => !open && closeReturnDialog()}
                      >
                        <AlertDialogContent>
                          <AlertDialogHeader>
                            <AlertDialogTitle>
                              Confirmar devolução
                            </AlertDialogTitle>
                            <AlertDialogDescription className="space-y-3">
                              <div>
                                Tem certeza que deseja registrar a devolução
                                deste livro? O exemplar será devolvido ao
                                estoque automaticamente.
                              </div>
                              {loanToReturn && loanToReturn.fineAmount > 0 && (
                                <div className="flex items-center gap-2 pt-2 border-t">
                                  <input
                                    type="checkbox"
                                    id={`chargeFine-${loan.id}`}
                                    checked={chargeFine}
                                    onChange={(e) =>
                                      setChargeFine(e.target.checked)
                                    }
                                    className="h-4 w-4 rounded border-input"
                                  />
                                  <label
                                    htmlFor={`chargeFine-${loan.id}`}
                                    className="text-sm font-medium cursor-pointer"
                                  >
                                    Cobrar multa de{" "}
                                    {formatCurrency(loanToReturn.fineAmount)}?
                                  </label>
                                </div>
                              )}
                            </AlertDialogDescription>
                          </AlertDialogHeader>
                          <AlertDialogFooter>
                            <AlertDialogCancel onClick={closeReturnDialog}>
                              Cancelar
                            </AlertDialogCancel>
                            <AlertDialogAction
                              onClick={() =>
                                handleReturnLoan(loan.id, chargeFine)
                              }
                              disabled={returningLoanId === loan.id}
                              className="bg-primary text-primary-foreground hover:bg-primary/90"
                            >
                              Confirmar devolução
                            </AlertDialogAction>
                          </AlertDialogFooter>
                        </AlertDialogContent>
                      </AlertDialog>
                    )}
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
