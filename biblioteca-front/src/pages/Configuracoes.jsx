import { useCallback, useEffect, useState } from "react";
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

export default function Configuracoes() {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [loanPeriodDays, setLoanPeriodDays] = useState(14);
  const [maxLoansPerStudent, setMaxLoansPerStudent] = useState(3);
  const [finePerDay, setFinePerDay] = useState(100);
  const [finePerDayDisplay, setFinePerDayDisplay] = useState("R$ 1.00");

  const fetchSettings = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API_BASE_URL}/settings`);
      if (!res.ok)
        throw new Error(`Erro ao carregar configurações (${res.status})`);
      const data = await res.json();
      const lp = data.loanPeriodDays ?? 14;
      const ml = data.maxLoansPerStudent ?? 3;
      const fp = data.finePerDay ?? 100;
      setLoanPeriodDays(lp);
      setMaxLoansPerStudent(ml);
      setFinePerDay(fp);
    } catch (e) {
      setError(e.message || "Erro ao carregar configurações");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const formatted = `R$ ${(Number(finePerDay || 0) / 100).toFixed(2)}`;
    setFinePerDayDisplay(formatted);
  }, [finePerDay]);

  useEffect(() => {
    fetchSettings();
  }, [fetchSettings]);

  const handleSave = useCallback(async () => {
    try {
      setSaving(true);
      setError("");
      setSuccess("");
      const res = await fetch(`${API_BASE_URL}/settings`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          loanPeriodDays,
          maxLoansPerStudent,
          finePerDay,
        }),
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Erro ao salvar configurações (${res.status})`);
      }
      setSuccess("Configurações salvas com sucesso");
    } catch (e) {
      setError(e.message || "Erro ao salvar configurações");
    } finally {
      setSaving(false);
    }
  }, [loanPeriodDays, maxLoansPerStudent, finePerDay]);

  const handleFineDisplayChange = useCallback((e) => {
    const raw = e.target.value || "";
    const digits = raw.replace(/\D/g, "");
    const cents = digits ? Number(digits) : 0;
    setFinePerDay(cents);
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between gap-3">
        <h1 className="text-2xl sm:text-3xl font-bold">Configurações</h1>
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
          <CardTitle>Empréstimos</CardTitle>
          <CardDescription>Defina os limites e prazos</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-2 max-w-xs">
            <label className="text-sm font-medium">
              Prazo de devolução (dias)
            </label>
            <input
              type="number"
              min={1}
              className="h-10 rounded-md border bg-background px-3 text-sm"
              value={loanPeriodDays}
              onChange={(e) =>
                setLoanPeriodDays(Math.max(1, Number(e.target.value || 0)))
              }
              disabled={loading}
            />
          </div>

          <div className="grid gap-2 max-w-xs">
            <label className="text-sm font-medium">
              Máx. empréstimos por aluno
            </label>
            <input
              type="number"
              min={1}
              className="h-10 rounded-md border bg-background px-3 text-sm"
              value={maxLoansPerStudent}
              onChange={(e) =>
                setMaxLoansPerStudent(Math.max(1, Number(e.target.value || 0)))
              }
              disabled={loading}
            />
          </div>

          <div className="grid gap-2 max-w-xs">
            <label className="text-sm font-medium">Multa por dia (R$)</label>
            <input
              type="text"
              inputMode="numeric"
              className="h-10 rounded-md border bg-background px-3 text-sm"
              value={finePerDayDisplay}
              onChange={handleFineDisplayChange}
              disabled={loading}
            />
          </div>

          <div className="pt-2">
            <Button
              onClick={handleSave}
              disabled={loading || saving}
              className="bg-primary text-primary-foreground"
            >
              {saving ? "Salvando..." : "Salvar configurações"}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
