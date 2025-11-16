import { useState, useEffect, useCallback } from "react";
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
  FileText,
  BookOpen,
  Users,
  ArrowRight,
  Calendar,
  ChevronDown,
  ChevronUp,
  XCircle,
  Loader2,
  RefreshCw,
} from "lucide-react";

export default function Relatorios() {
  const [expandedReport, setExpandedReport] = useState(null);
  const [loading, setLoading] = useState({});
  const [reports, setReports] = useState({
    availability: null,
    studentMetrics: null,
    loanStatistics: null,
    reservationAnalytics: null,
  });
  const [error, setError] = useState({});

  const fetchReport = useCallback(async (reportType) => {
    setLoading((prev) => ({ ...prev, [reportType]: true }));
    setError((prev) => ({ ...prev, [reportType]: null }));

    try {
      const endpointMap = {
        availability: "/reports/availability",
        studentMetrics: "/reports/student-metrics",
        loanStatistics: "/reports/loan-statistics",
        reservationAnalytics: "/reports/reservation-analytics",
      };

      const res = await fetch(`${API_BASE_URL}${endpointMap[reportType]}`, {
        headers: { "Content-Type": "application/json" },
      });

      if (!res.ok) {
        throw new Error(`Erro ao carregar relatório (${res.status})`);
      }

      const data = await res.json();
      setReports((prev) => ({ ...prev, [reportType]: data }));
    } catch (e) {
      setError((prev) => ({
        ...prev,
        [reportType]: e.message || "Erro ao carregar relatório",
      }));
    } finally {
      setLoading((prev) => ({ ...prev, [reportType]: false }));
    }
  }, []);

  const toggleReport = (reportType) => {
    if (expandedReport === reportType) {
      setExpandedReport(null);
    } else {
      setExpandedReport(reportType);
      if (!reports[reportType]) {
        fetchReport(reportType);
      }
    }
  };

  const formatCurrency = (cents) => {
    if (!cents) return "R$ 0,00";
    const reais = cents / 100;
    return `R$ ${reais.toFixed(2).replace(".", ",")}`;
  };

  const formatPercentage = (value) => {
    if (value == null || isNaN(value)) return "0%";
    return `${value.toFixed(2)}%`;
  };

  const reportCards = [
    {
      id: "availability",
      title: "Disponibilidade do Acervo",
      description: "Estatísticas sobre disponibilidade de livros",
      icon: BookOpen,
      color: "blue",
      bgColor: "bg-blue-900/20",
      borderColor: "border-blue-700",
    },
    {
      id: "studentMetrics",
      title: "Métricas de Alunos",
      description: "Estatísticas sobre atividades dos alunos",
      icon: Users,
      color: "emerald",
      bgColor: "bg-emerald-900/20",
      borderColor: "border-emerald-700",
    },
    {
      id: "loanStatistics",
      title: "Estatísticas de Empréstimos",
      description: "Análise completa de empréstimos",
      icon: ArrowRight,
      color: "amber",
      bgColor: "bg-amber-900/20",
      borderColor: "border-amber-700",
    },
    {
      id: "reservationAnalytics",
      title: "Análise de Reservas",
      description: "Padrões e eficácia do sistema de reservas",
      icon: Calendar,
      color: "purple",
      bgColor: "bg-purple-900/20",
      borderColor: "border-purple-700",
    },
  ];

  const renderAvailabilityReport = () => {
    const data = reports.availability;
    if (!data) return null;

    return (
      <div className="space-y-4 mt-4">
        <div className="grid grid-cols-2 gap-3">
          <Card className="border-blue-200 bg-blue-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Total de Títulos
              </div>
              <div className="text-2xl font-bold text-blue-700">
                {data.totalBooks || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-emerald-200 bg-emerald-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Títulos Disponíveis
              </div>
              <div className="text-2xl font-bold text-emerald-700">
                {data.availableBooks || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-rose-200 bg-rose-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Títulos Indisponíveis
              </div>
              <div className="text-2xl font-bold text-rose-700">
                {data.unavailableBooks || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-amber-200 bg-amber-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Percentual de Disponibilidade
              </div>
              <div className="text-2xl font-bold text-amber-700">
                {formatPercentage(data.availabilityPercentage)}
              </div>
            </CardContent>
          </Card>

          <Card className="border-slate-200 bg-slate-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Cópias em Estoque
              </div>
              <div className="text-2xl font-bold text-slate-700">
                {data.totalCopiesInStock || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-blue-200 bg-blue-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Cópias Disponíveis
              </div>
              <div className="text-2xl font-bold text-blue-700">
                {data.totalCopiesAvailable || 0}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  };

  const renderStudentMetricsReport = () => {
    const data = reports.studentMetrics;
    if (!data) return null;

    return (
      <div className="space-y-4 mt-4">
        <div className="grid grid-cols-2 gap-3">
          <Card className="border-blue-200 bg-blue-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Total de Alunos
              </div>
              <div className="text-2xl font-bold text-blue-700">
                {data.totalStudents || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-emerald-200 bg-emerald-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Com Empréstimos Ativos
              </div>
              <div className="text-2xl font-bold text-emerald-700">
                {data.studentsWithActiveLoans || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-rose-200 bg-rose-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Com Empréstimos em Atraso
              </div>
              <div className="text-2xl font-bold text-rose-700">
                {data.studentsWithOverdueLoans || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-slate-200 bg-slate-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Sem Empréstimos
              </div>
              <div className="text-2xl font-bold text-slate-700">
                {data.studentsWithoutLoans || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-amber-200 bg-amber-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Média de Empréstimos/Aluno
              </div>
              <div className="text-2xl font-bold text-amber-700">
                {data.averageLoansPerStudent?.toFixed(2) || "0.00"}
              </div>
            </CardContent>
          </Card>

          <Card className="border-purple-200 bg-purple-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Média de Dias de Atraso
              </div>
              <div className="text-2xl font-bold text-purple-700">
                {data.averageOverdueDaysPerStudent?.toFixed(2) || "0.00"}
              </div>
            </CardContent>
          </Card>

          <Card className="border-emerald-200 bg-emerald-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Total de Empréstimos Ativos
              </div>
              <div className="text-2xl font-bold text-emerald-700">
                {data.totalActiveLoans || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-rose-200 bg-rose-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Total de Empréstimos em Atraso
              </div>
              <div className="text-2xl font-bold text-rose-700">
                {data.totalOverdueLoans || 0}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  };

  const renderLoanStatisticsReport = () => {
    const data = reports.loanStatistics;
    if (!data) return null;

    return (
      <div className="space-y-4 mt-4">
        <div className="grid grid-cols-2 gap-3">
          <Card className="border-blue-200 bg-blue-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Total de Empréstimos
              </div>
              <div className="text-2xl font-bold text-blue-700">
                {data.totalLoans || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-emerald-200 bg-emerald-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Empréstimos Ativos
              </div>
              <div className="text-2xl font-bold text-emerald-700">
                {data.activeLoans || 0}
              </div>
              <div className="text-xs text-emerald-600 mt-1">
                {formatPercentage(data.activeLoansPercentage)}
              </div>
            </CardContent>
          </Card>

          <Card className="border-green-200 bg-green-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Empréstimos Devolvidos
              </div>
              <div className="text-2xl font-bold text-green-700">
                {data.returnedLoans || 0}
              </div>
              <div className="text-xs text-green-600 mt-1">
                {formatPercentage(data.returnedLoansPercentage)}
              </div>
            </CardContent>
          </Card>

          <Card className="border-rose-200 bg-rose-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Empréstimos em Atraso
              </div>
              <div className="text-2xl font-bold text-rose-700">
                {data.overdueLoans || 0}
              </div>
              <div className="text-xs text-rose-600 mt-1">
                {formatPercentage(data.overdueLoansPercentage)}
              </div>
            </CardContent>
          </Card>

          <Card className="border-amber-200 bg-amber-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Valor Médio de Multa
              </div>
              <div className="text-2xl font-bold text-amber-700">
                {formatCurrency(data.averageOverdueValue)}
              </div>
            </CardContent>
          </Card>

          <Card className="border-purple-200 bg-purple-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Total de Multas Coletadas
              </div>
              <div className="text-2xl font-bold text-purple-700">
                {formatCurrency(data.totalFinesCollected)}
              </div>
            </CardContent>
          </Card>

          <Card className="border-slate-200 bg-slate-50/50 col-span-2">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Duração Média dos Empréstimos
              </div>
              <div className="text-2xl font-bold text-slate-700">
                {data.averageLoanDurationDays?.toFixed(2) || "0.00"} dias
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  };

  const renderReservationAnalyticsReport = () => {
    const data = reports.reservationAnalytics;
    if (!data) return null;

    return (
      <div className="space-y-4 mt-4">
        <div className="grid grid-cols-2 gap-3">
          <Card className="border-blue-200 bg-blue-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Total de Reservas
              </div>
              <div className="text-2xl font-bold text-blue-700">
                {data.totalReservations || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-emerald-200 bg-emerald-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Reservas Ativas
              </div>
              <div className="text-2xl font-bold text-emerald-700">
                {data.activeReservations || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-green-200 bg-green-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Reservas Efetivadas
              </div>
              <div className="text-2xl font-bold text-green-700">
                {data.fulfilledReservations || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-rose-200 bg-rose-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Reservas Canceladas
              </div>
              <div className="text-2xl font-bold text-rose-700">
                {data.cancelledReservations || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-amber-200 bg-amber-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Taxa de Efetivação
              </div>
              <div className="text-2xl font-bold text-amber-700">
                {formatPercentage(data.fulfillmentRate)}
              </div>
            </CardContent>
          </Card>

          <Card className="border-purple-200 bg-purple-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Posição Média na Fila
              </div>
              <div className="text-2xl font-bold text-purple-700">
                {data.averageQueuePosition?.toFixed(2) || "0.00"}
              </div>
            </CardContent>
          </Card>

          <Card className="border-slate-200 bg-slate-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Livros com Reservas
              </div>
              <div className="text-2xl font-bold text-slate-700">
                {data.booksWithReservations || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-orange-200 bg-orange-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Livros com Fila Cheia
              </div>
              <div className="text-2xl font-bold text-orange-700">
                {data.booksWithFullQueue || 0}
              </div>
            </CardContent>
          </Card>

          <Card className="border-blue-200 bg-blue-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Tempo Médio de Espera
              </div>
              <div className="text-2xl font-bold text-blue-700">
                {data.averageWaitTimeInDays?.toFixed(2) || "0.00"} dias
              </div>
            </CardContent>
          </Card>

          <Card className="border-indigo-200 bg-indigo-50/50">
            <CardContent className="p-4">
              <div className="text-xs text-muted-foreground mb-1">
                Alunos com Reservas
              </div>
              <div className="text-2xl font-bold text-indigo-700">
                {data.studentsWithReservations || 0}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  };

  const renderReportContent = (reportType) => {
    switch (reportType) {
      case "availability":
        return renderAvailabilityReport();
      case "studentMetrics":
        return renderStudentMetricsReport();
      case "loanStatistics":
        return renderLoanStatisticsReport();
      case "reservationAnalytics":
        return renderReservationAnalyticsReport();
      default:
        return null;
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl sm:text-2xl font-semibold">
          Histórico e Relatórios
        </h1>
        <p className="text-sm sm:text-base text-muted-foreground mt-1">
          Visualize relatórios detalhados e estatísticas do sistema
        </p>
      </div>

      <div className="grid gap-4">
        {reportCards.map((report) => {
          const Icon = report.icon;
          const isExpanded = expandedReport === report.id;
          const isLoading = loading[report.id];
          const reportError = error[report.id];
          const hasData = reports[report.id] !== null;

          return (
            <Card
              key={report.id}
              className={`${report.borderColor} ${report.bgColor} hover:shadow-lg transition-all cursor-pointer`}
            >
              <CardHeader
                onClick={() => toggleReport(report.id)}
                className="cursor-pointer"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div
                      className={`p-2 rounded-lg ${
                        report.color === "blue"
                          ? "bg-blue-100 border border-blue-300"
                          : report.color === "emerald"
                          ? "bg-emerald-100 border border-emerald-300"
                          : report.color === "amber"
                          ? "bg-amber-100 border border-amber-300"
                          : "bg-purple-100 border border-purple-300"
                      }`}
                    >
                      <Icon
                        className={`h-5 w-5 ${
                          report.color === "blue"
                            ? "text-blue-700"
                            : report.color === "emerald"
                            ? "text-emerald-700"
                            : report.color === "amber"
                            ? "text-amber-700"
                            : "text-purple-700"
                        }`}
                      />
                    </div>
                    <div>
                      <CardTitle className="text-base sm:text-lg">
                        {report.title}
                      </CardTitle>
                      <CardDescription className="text-xs sm:text-sm">
                        {report.description}
                      </CardDescription>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {isLoading && (
                      <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
                    )}
                    {isExpanded ? (
                      <ChevronUp className="h-5 w-5 text-foreground" />
                    ) : (
                      <ChevronDown className="h-5 w-5 text-foreground" />
                    )}
                  </div>
                </div>
              </CardHeader>

              {isExpanded && (
                <CardContent>
                  {isLoading && (
                    <div className="flex items-center justify-center py-8">
                      <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
                      <span className="ml-2 text-sm text-muted-foreground">
                        Carregando relatório...
                      </span>
                    </div>
                  )}

                  {reportError && (
                    <div className="flex items-center justify-center py-8">
                      <div className="text-center">
                        <XCircle className="h-8 w-8 text-destructive mx-auto mb-2" />
                        <p className="text-sm text-destructive mb-3">
                          {reportError}
                        </p>
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={(e) => {
                            e.stopPropagation();
                            fetchReport(report.id);
                          }}
                        >
                          <RefreshCw className="h-4 w-4 mr-2" />
                          Tentar novamente
                        </Button>
                      </div>
                    </div>
                  )}

                  {!isLoading && !reportError && hasData && (
                    <>
                      {renderReportContent(report.id)}
                      <div className="mt-4 pt-4 border-t">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={(e) => {
                            e.stopPropagation();
                            fetchReport(report.id);
                          }}
                          className="w-full"
                        >
                          <RefreshCw className="h-4 w-4 mr-2" />
                          Atualizar relatório
                        </Button>
                      </div>
                    </>
                  )}

                  {!isLoading && !reportError && !hasData && (
                    <div className="flex items-center justify-center py-8">
                      <p className="text-sm text-muted-foreground">
                        Clique para carregar o relatório
                      </p>
                    </div>
                  )}
                </CardContent>
              )}
            </Card>
          );
        })}
      </div>
    </div>
  );
}
