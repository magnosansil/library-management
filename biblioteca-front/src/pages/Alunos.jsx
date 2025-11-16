import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { API_BASE_URL } from "@/config/api";
import { Users, Search, Edit, User, Trash2 } from "lucide-react";
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

export default function Alunos() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [editingMatricula, setEditingMatricula] = useState(null);
  const [editForm, setEditForm] = useState({
    matricula: "",
    nome: "",
    cpf: "",
    dataNascimento: "",
    email: "",
    telefone: "",
  });
  const [history, setHistory] = useState({ reservations: [], loans: [] });
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchStudents();
  }, []);

  const fetchStudents = async () => {
    try {
      setLoading(true);
      const res = await fetch(`${API_BASE_URL}/students`);
      if (res.ok) {
        const data = await res.json();
        setStudents(data);
      }
    } catch (e) {
      console.error("Erro ao buscar alunos:", e);
    } finally {
      setLoading(false);
    }
  };

  const filtered = useMemo(() => {
    return students
      .filter((s) =>
        [s.matricula, s.nome, s.cpf, s.email]
          .filter(Boolean)
          .join(" ")
          .toLowerCase()
          .includes(searchTerm.toLowerCase())
      )
      .sort((a, b) => (a.nome || "").localeCompare(b.nome || "", "pt-BR"));
  }, [students, searchTerm]);

  const openEdit = async (student) => {
    const willOpen = editingMatricula !== student.matricula;

    if (willOpen && editingMatricula !== null) {
      setHistory({ reservations: [], loans: [] });
    }

    setEditingMatricula(willOpen ? student.matricula : null);
    setEditForm({
      matricula: student.matricula || "",
      nome: student.nome || "",
      cpf: student.cpf || "",
      dataNascimento: (student.dataNascimento || "").slice(0, 10),
      email: student.email || "",
      telefone: student.telefone || "",
    });

    if (!willOpen) {
      setHistory({ reservations: [], loans: [] });
      return;
    }

    try {
      setLoadingHistory(true);
      const [resvRes, loanRes] = await Promise.all([
        fetch(`${API_BASE_URL}/reservations/student/${student.matricula}`),
        fetch(`${API_BASE_URL}/loans`),
      ]);
      const reservations = resvRes.ok ? await resvRes.json() : [];
      const allLoans = loanRes.ok ? await loanRes.json() : [];
      const loans = allLoans.filter(
        (loan) =>
          loan.studentMatricula === student.matricula &&
          (loan.status === "ACTIVE" || loan.status === "OVERDUE")
      );
      setHistory({ reservations, loans });
    } catch (e) {
      console.error("Erro ao buscar histórico:", e);
      setHistory({ reservations: [], loans: [] });
    } finally {
      setLoadingHistory(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEditForm((p) => ({ ...p, [name]: value }));
  };

  const cancelEdit = () => {
    setEditingMatricula(null);
    setHistory({ reservations: [], loans: [] });
    setLoadingHistory(false);
  };

  const saveStudent = async () => {
    try {
      setSaving(true);

      if (!editForm.email || editForm.email.trim() === "") {
        alert("E-mail é obrigatório");
        return;
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(editForm.email.trim())) {
        alert("E-mail deve ter um formato válido");
        return;
      }

      const res = await fetch(
        `${API_BASE_URL}/students/${editForm.matricula}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            matricula: editForm.matricula,
            nome: editForm.nome,
            cpf: editForm.cpf,
            dataNascimento: editForm.dataNascimento,
            email: editForm.email.trim(),
            telefone: editForm.telefone || null,
          }),
        }
      );

      if (res.ok) {
        await fetchStudents();
        setEditingMatricula(null);
      } else {
        let errorMsg = "Erro ao salvar aluno";
        try {
          const text = await res.text();
          if (text) {
            errorMsg = text;
          } else if (res.status === 409) {
            errorMsg = "E-mail já está em uso por outro aluno";
          } else if (res.status === 400) {
            errorMsg =
              "Dados inválidos. Verifique se todos os campos estão preenchidos corretamente";
          } else if (res.status === 404) {
            errorMsg = "Aluno não encontrado";
          }
        } catch (e) {
          if (res.status === 409) {
            errorMsg = "E-mail já está em uso por outro aluno";
          }
        }
        alert(errorMsg);
      }
    } catch (e) {
      console.error("Erro ao salvar aluno:", e);
      alert("Erro ao salvar aluno. Verifique sua conexão e tente novamente.");
    } finally {
      setSaving(false);
    }
  };

  const deleteStudent = async (matricula) => {
    try {
      setDeleting(true);
      const res = await fetch(`${API_BASE_URL}/students/${matricula}`, {
        method: "DELETE",
      });
      if (res.ok) {
        await fetchStudents();
        if (editingMatricula === matricula) cancelEdit();
      } else {
        const msg = await res.text();
        alert(`Erro ao excluir aluno: ${msg}`);
      }
    } catch (e) {
      console.error("Erro ao excluir aluno:", e);
      alert("Erro ao excluir aluno");
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-muted-foreground">Carregando alunos...</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="text-xl sm:text-2xl font-semibold">Alunos</h1>
          <p className="text-xs sm:text-sm text-muted-foreground mt-1">
            {students.length}{" "}
            {students.length === 1 ? "aluno cadastrado" : "alunos cadastrados"}
          </p>
        </div>
        <Link to="/alunos/novo">
          <Button className="h-9 px-4">
            <Users className="h-4 w-4 mr-2" />
            Adicionar
          </Button>
        </Link>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          type="text"
          placeholder="Buscar por nome, matrícula, CPF ou e-mail..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      {filtered.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-12 border rounded-xl">
          <Users className="h-12 w-12 text-muted-foreground mb-4" />
          <p className="text-muted-foreground text-center">
            Nenhum aluno encontrado
          </p>
        </div>
      ) : (
        <div className="space-y-2">
          {filtered.map((s) => (
            <div key={s.matricula} className="border rounded-xl p-3 space-y-3">
              <div className="grid grid-cols-[48px_1fr_auto] gap-3 items-center">
                <div className="h-12 w-12 rounded-full bg-muted/50 border flex items-center justify-center">
                  <User className="h-5 w-5 text-muted-foreground" />
                </div>
                <div className="min-w-0">
                  <div className="text-sm sm:text-base font-semibold truncate">
                    {s.nome}
                  </div>
                  <div className="text-xs sm:text-sm text-muted-foreground truncate">
                    Matrícula: {s.matricula} • CPF: {s.cpf}
                  </div>
                  <div className="text-xs text-muted-foreground truncate">
                    E-mail: {s.email}
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    className="h-9 w-9 p-0 bg-primary text-primary-foreground hover:bg-primary/90"
                    onClick={() => openEdit(s)}
                    aria-label={`Editar ${s.nome}`}
                  >
                    <Edit className="h-4 w-4" />
                  </Button>
                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button
                        variant="destructive"
                        className="h-9 w-9 p-0"
                        aria-label={`Excluir ${s.nome}`}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Excluir aluno</AlertDialogTitle>
                        <AlertDialogDescription>
                          Esta ação não pode ser desfeita. O cadastro do aluno
                          será removido.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Cancelar</AlertDialogCancel>
                        <AlertDialogAction
                          onClick={() => deleteStudent(s.matricula)}
                          disabled={deleting}
                        >
                          Excluir
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </div>
              </div>

              {editingMatricula === s.matricula && (
                <div className="mt-2 grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-3">
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      <div className="space-y-1">
                        <label className="text-xs text-muted-foreground">
                          Matrícula
                        </label>
                        <Input
                          value={editForm.matricula}
                          name="matricula"
                          disabled
                          className="bg-muted rounded-md"
                        />
                      </div>
                      <div className="space-y-1">
                        <label className="text-xs text-muted-foreground">
                          CPF
                        </label>
                        <Input
                          value={editForm.cpf}
                          name="cpf"
                          onChange={handleChange}
                          className="rounded-md"
                        />
                      </div>
                    </div>
                    <div className="space-y-1">
                      <label className="text-xs text-muted-foreground">
                        Nome
                      </label>
                      <Input
                        value={editForm.nome}
                        name="nome"
                        onChange={handleChange}
                        className="rounded-md"
                      />
                    </div>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      <div className="space-y-1">
                        <label className="text-xs text-muted-foreground">
                          Data de Nascimento
                        </label>
                        <Input
                          type="date"
                          value={editForm.dataNascimento}
                          name="dataNascimento"
                          onChange={handleChange}
                          max={new Date().toISOString().slice(0, 10)}
                          className="rounded-md"
                        />
                      </div>
                      <div className="space-y-1">
                        <label className="text-xs text-muted-foreground">
                          Telefone
                        </label>
                        <Input
                          value={editForm.telefone}
                          name="telefone"
                          onChange={handleChange}
                          className="rounded-md"
                        />
                      </div>
                    </div>
                    <div className="space-y-1">
                      <label className="text-xs text-muted-foreground">
                        E-mail
                      </label>
                      <Input
                        value={editForm.email}
                        name="email"
                        onChange={handleChange}
                        className="rounded-md"
                      />
                    </div>
                    <div className="flex justify-end gap-2">
                      <Button variant="outline" onClick={cancelEdit}>
                        Cancelar
                      </Button>
                      <Button onClick={saveStudent} disabled={saving}>
                        {saving ? "Salvando..." : "Salvar"}
                      </Button>
                    </div>
                  </div>

                  <div className="space-y-3">
                    <div>
                      <div className="text-sm font-medium mb-1">Reservas</div>
                      {loadingHistory ? (
                        <p className="text-xs text-muted-foreground">
                          Carregando...
                        </p>
                      ) : history.reservations.length === 0 ? (
                        <p className="text-xs text-muted-foreground">
                          Sem reservas
                        </p>
                      ) : (
                        <ul className="space-y-1 text-sm">
                          {history.reservations.map((r) => (
                            <li
                              key={r.id}
                              className="flex justify-between border rounded-md px-3 py-2"
                            >
                              <span className="truncate mr-2">
                                {r.bookTitle} • {r.bookIsbn}
                              </span>
                              <span className="text-xs text-muted-foreground">
                                Fila: {r.queuePosition}
                              </span>
                            </li>
                          ))}
                        </ul>
                      )}
                    </div>
                    <div>
                      <div className="text-sm font-medium mb-1">
                        Empréstimos
                      </div>
                      {loadingHistory ? (
                        <p className="text-xs text-muted-foreground">
                          Carregando...
                        </p>
                      ) : history.loans.length === 0 ? (
                        <p className="text-xs text-muted-foreground">
                          Sem empréstimos ativos ou em atraso
                        </p>
                      ) : (
                        <ul className="space-y-1 text-sm">
                          {history.loans.map((l) => {
                            const isOverdue = l.status === "OVERDUE";
                            return (
                              <li
                                key={l.id}
                                className={`flex justify-between border rounded-md px-3 py-2 ${
                                  isOverdue ? "bg-rose-50 border-rose-300" : ""
                                }`}
                              >
                                <span
                                  className={`truncate mr-2 ${
                                    isOverdue ? "text-rose-700 font-medium" : ""
                                  }`}
                                >
                                  {l.bookTitle} • {l.bookIsbn}
                                </span>
                                <span
                                  className={`text-xs whitespace-nowrap ${
                                    isOverdue
                                      ? "text-rose-700 font-medium"
                                      : "text-muted-foreground"
                                  }`}
                                >
                                  {isOverdue ? "⚠️ Em atraso • " : ""}Vence:{" "}
                                  {new Date(l.dueDate).toLocaleDateString(
                                    "pt-BR"
                                  )}
                                </span>
                              </li>
                            );
                          })}
                        </ul>
                      )}
                    </div>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
