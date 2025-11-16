import { useCallback, useMemo, useState, useEffect, useRef } from "react";
import { useNavigate, Link } from "react-router-dom";
import { API_BASE_URL } from "@/config/api";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { CheckCircle2, XCircle, Search } from "lucide-react";

export default function NovoEmprestimo() {
  const navigate = useNavigate();

  const [selectedBook, setSelectedBook] = useState(null);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [loanDate, setLoanDate] = useState("");

  const [bookQuery, setBookQuery] = useState("");
  const [bookResults, setBookResults] = useState([]);
  const [showBookList, setShowBookList] = useState(false);

  const [studentQuery, setStudentQuery] = useState("");
  const [studentResults, setStudentResults] = useState([]);
  const [showStudentList, setShowStudentList] = useState(false);

  const [availability, setAvailability] = useState(null);
  const [canBorrow, setCanBorrow] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const canSubmit = useMemo(() => {
    return !!(selectedBook && selectedStudent);
  }, [selectedBook, selectedStudent]);

  const allBooksRef = useRef([]);
  const allStudentsRef = useRef([]);
  const [loaded, setLoaded] = useState({ books: false, students: false });

  useEffect(() => {
    let aborted = false;
    (async () => {
      try {
        const [booksRes, studentsRes] = await Promise.all([
          fetch(`${API_BASE_URL}/books`),
          fetch(`${API_BASE_URL}/students`),
        ]);
        if (!booksRes.ok || !studentsRes.ok) return;
        const [books, students] = await Promise.all([
          booksRes.json(),
          studentsRes.json(),
        ]);
        if (aborted) return;
        allBooksRef.current = (books || []).map((b) => ({
          isbn: b.isbn || b.ISBN || b.id || "",
          title: b.title || b.titulo || b.name || "",
          author: b.author || b.autor || "",
        }));
        allStudentsRef.current = (students || []).map((s) => ({
          matricula: s.matricula || s.id || "",
          nome: s.nome || s.name || "",
          email: s.email || "",
        }));
        setLoaded({ books: true, students: true });
      } catch {}
    })();
    return () => {
      aborted = true;
    };
  }, []);

  // Filter helpers
  useEffect(() => {
    const q = bookQuery.trim().toLowerCase();
    if (!q) {
      setBookResults([]);
      return;
    }
    const results = allBooksRef.current
      .filter(
        (b) =>
          (b.title || "").toLowerCase().includes(q) ||
          (b.author || "").toLowerCase().includes(q)
      )
      .slice(0, 8);
    setBookResults(results);
  }, [bookQuery]);

  useEffect(() => {
    const q = studentQuery.trim().toLowerCase();
    if (!q) {
      setStudentResults([]);
      return;
    }
    const results = allStudentsRef.current
      .filter(
        (s) =>
          (s.nome || "").toLowerCase().includes(q) ||
          (s.email || "").toLowerCase().includes(q)
      )
      .slice(0, 8);
    setStudentResults(results);
  }, [studentQuery]);

  const checkAvailability = useCallback(async () => {
    if (!selectedBook?.isbn) return;
    try {
      const res = await fetch(
        `${API_BASE_URL}/loans/books/${encodeURIComponent(
          selectedBook.isbn
        )}/availability`
      );
      const ok = res.ok ? await res.json() : false;
      setAvailability(!!ok);
    } catch {
      setAvailability(false);
    }
  }, [selectedBook]);

  const checkCanBorrow = useCallback(async () => {
    if (!selectedStudent?.matricula) return;
    try {
      const res = await fetch(
        `${API_BASE_URL}/loans/students/${encodeURIComponent(
          selectedStudent.matricula
        )}/can-borrow`
      );
      const ok = res.ok ? await res.json() : false;
      setCanBorrow(!!ok);
    } catch {
      setCanBorrow(false);
    }
  }, [selectedStudent]);

  const handleSubmit = useCallback(
    async (e) => {
      e.preventDefault();
      if (!canSubmit || submitting) return;
      setSubmitting(true);
      setError("");
      try {
        const body = {
          studentMatricula: selectedStudent.matricula,
          bookIsbn: selectedBook.isbn,
        };
        if (loanDate) {
          body.loanDate = `${loanDate}T00:00:00`;
        }
        const res = await fetch(`${API_BASE_URL}/loans`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(body),
        });
        if (!res.ok) {
          const text = await res.text();
          throw new Error(text || `Erro ao criar empréstimo (${res.status})`);
        }
        navigate("/emprestimos");
      } catch (err) {
        setError(err.message || "Erro ao criar empréstimo");
      } finally {
        setSubmitting(false);
      }
    },
    [canSubmit, loanDate, navigate, selectedBook, selectedStudent, submitting]
  );

  const bookBoxRef = useRef(null);
  const studentBoxRef = useRef(null);
  useEffect(() => {
    function onDocClick(e) {
      if (bookBoxRef.current && !bookBoxRef.current.contains(e.target)) {
        setShowBookList(false);
      }
      if (studentBoxRef.current && !studentBoxRef.current.contains(e.target)) {
        setShowStudentList(false);
      }
    }
    document.addEventListener("click", onDocClick);
    return () => document.removeEventListener("click", onDocClick);
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold">Novo Empréstimo</h1>
        <p className="text-sm text-muted-foreground mt-1">
          Registre um novo empréstimo
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

      <Card>
        <CardHeader>
          <CardTitle>Dados do empréstimo</CardTitle>
          <CardDescription>
            Selecione o livro, o aluno e a data (opcional)
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid gap-2" ref={bookBoxRef}>
              <label className="text-sm font-medium">Livro</label>
              <div className="relative">
                <div className="flex items-center gap-2">
                  <div className="relative flex-1">
                    <Search className="absolute left-2 top-1/2 -translate-y-1/2 h-4 w-4 text-foreground/60" />
                    <input
                      className="w-full h-10 rounded-md border bg-background pl-8 pr-3 text-sm"
                      placeholder={
                        loaded.books
                          ? "Busque por título ou autor"
                          : "Carregando livros..."
                      }
                      value={selectedBook ? `${selectedBook.title}` : bookQuery}
                      onChange={(e) => {
                        setSelectedBook(null);
                        setBookQuery(e.target.value);
                        setShowBookList(true);
                        setAvailability(null);
                      }}
                    />
                  </div>
                  {availability === true && (
                    <CheckCircle2 className="h-5 w-5 text-emerald-600" />
                  )}
                  {availability === false && (
                    <XCircle className="h-5 w-5 text-rose-600" />
                  )}
                </div>
                {showBookList && bookResults.length > 0 && (
                  <div className="absolute z-10 mt-1 w-full rounded-md border bg-background shadow-sm max-h-56 overflow-auto">
                    {bookResults.map((b) => (
                      <button
                        type="button"
                        key={b.isbn}
                        className="w-full text-left px-3 py-2 text-sm hover:bg-muted/50"
                        onClick={() => {
                          setSelectedBook({ isbn: b.isbn, title: b.title });
                          setBookQuery("");
                          setShowBookList(false);
                          setAvailability(null);
                          setTimeout(() => {
                            checkAvailability();
                          }, 0);
                        }}
                      >
                        <div className="font-medium">{b.title}</div>
                        <div className="text-foreground/70 text-xs">
                          ISBN {b.isbn}
                          {b.author ? ` • ${b.author}` : ""}
                        </div>
                      </button>
                    ))}
                  </div>
                )}
              </div>
              <button
                type="button"
                className="text-xs underline text-foreground/80 self-start"
                onClick={checkAvailability}
                disabled={!selectedBook}
              >
                Verificar disponibilidade
              </button>
            </div>

            <div className="grid gap-2" ref={studentBoxRef}>
              <label className="text-sm font-medium">Aluno</label>
              <div className="relative">
                <div className="flex items-center gap-2">
                  <div className="relative flex-1">
                    <Search className="absolute left-2 top-1/2 -translate-y-1/2 h-4 w-4 text-foreground/60" />
                    <input
                      className="w-full h-10 rounded-md border bg-background pl-8 pr-3 text-sm"
                      placeholder={
                        loaded.students
                          ? "Busque por nome ou e-mail"
                          : "Carregando alunos..."
                      }
                      value={
                        selectedStudent
                          ? `${selectedStudent.nome}`
                          : studentQuery
                      }
                      onChange={(e) => {
                        setSelectedStudent(null);
                        setStudentQuery(e.target.value);
                        setShowStudentList(true);
                        setCanBorrow(null);
                      }}
                    />
                  </div>
                  {canBorrow === true && (
                    <CheckCircle2 className="h-5 w-5 text-emerald-600" />
                  )}
                  {canBorrow === false && (
                    <XCircle className="h-5 w-5 text-rose-600" />
                  )}
                </div>
                {showStudentList && studentResults.length > 0 && (
                  <div className="absolute z-10 mt-1 w-full rounded-md border bg-background shadow-sm max-h-56 overflow-auto">
                    {studentResults.map((s) => (
                      <button
                        type="button"
                        key={s.matricula}
                        className="w-full text-left px-3 py-2 text-sm hover:bg-muted/50"
                        onClick={() => {
                          setSelectedStudent({
                            matricula: s.matricula,
                            nome: s.nome,
                          });
                          setStudentQuery("");
                          setShowStudentList(false);
                          setCanBorrow(null);
                          setTimeout(() => {
                            checkCanBorrow();
                          }, 0);
                        }}
                      >
                        <div className="font-medium">{s.nome}</div>
                        <div className="text-foreground/70 text-xs">
                          Matrícula {s.matricula}
                          {s.email ? ` • ${s.email}` : ""}
                        </div>
                      </button>
                    ))}
                  </div>
                )}
              </div>
              <button
                type="button"
                className="text-xs underline text-foreground/80 self-start"
                onClick={checkCanBorrow}
                disabled={!selectedStudent}
              >
                Verificar permissão de empréstimo
              </button>
            </div>

            <div className="grid gap-2">
              <label className="text-sm font-medium">
                Data do empréstimo (opcional)
              </label>
              <input
                type="date"
                className="h-10 rounded-md border bg-background px-3 text-sm"
                value={loanDate}
                max={new Date().toISOString().slice(0, 10)}
                onChange={(e) => setLoanDate(e.target.value)}
              />
            </div>

            <div className="flex items-center gap-2 pt-2">
              <Button
                type="submit"
                disabled={!canSubmit || submitting}
                className="bg-primary text-primary-foreground"
              >
                {submitting ? "Salvando..." : "Registrar empréstimo"}
              </Button>
              <Link to="/emprestimos">
                <Button type="button" variant="secondary">
                  Cancelar
                </Button>
              </Link>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
