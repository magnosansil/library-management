import FeatureCard from "@/components/FeatureCard";
import {
  BookOpen,
  ArrowRight,
  Calendar,
  FileText,
  AlertCircle,
} from "lucide-react";

export default function Home() {
  const features = [
    {
      to: "/acervo",
      icon: BookOpen,
      title: "Acervo",
      bgColor: "bg-blue-900/20",
      borderColor: "border-blue-700",
    },
    {
      to: "/emprestimos",
      icon: ArrowRight,
      title: "Empréstimos",
      bgColor: "bg-emerald-900/20",
      borderColor: "border-emerald-700",
    },
    {
      to: "/reservas",
      icon: Calendar,
      title: "Reservas",
      bgColor: "bg-slate-800/40",
      borderColor: "border-slate-700",
    },
    {
      to: "/devolucoes",
      icon: ArrowRight,
      title: "Devoluções",
      bgColor: "bg-purple-900/20",
      borderColor: "border-purple-700",
      iconRotate: 180,
    },
    {
      to: "/historico",
      icon: FileText,
      title: "Histórico e relatórios",
      bgColor: "bg-amber-900/20",
      borderColor: "border-amber-700",
    },
    {
      to: "/atrasos",
      icon: AlertCircle,
      title: "Atrasos e Multas",
      bgColor: "bg-rose-900/20",
      borderColor: "border-rose-700",
    },
  ];

  return (
    <div className="pb-4">
      <div className="flex items-center justify-between gap-15 mb-6">
        <div>
          <h1 className="text-xl sm:text-3xl mb-2 font-semibold tracking-tight text-foreground">
            Wisely
          </h1>
          <p className="text-xl sm:text-2xl font-light">
            Sua biblioteca na palma da mão!
          </p>
        </div>
        <img
          src="/wisely-logo.png"
          alt="Wisely Logo"
          className="w-27.5 h-27.5 sm:w-20 sm:h-20 rounded-full border-2 border-purple-300 object-cover"
        />
      </div>

      <div className="grid grid-cols-2 gap-3 sm:gap-4">
        {features.map((feature) => (
          <FeatureCard key={feature.to} {...feature} />
        ))}
      </div>
    </div>
  );
}
