import { Link } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import {
  BookOpen,
  ArrowRight,
  Calendar,
  FileText,
  AlertCircle,
} from "lucide-react";

export default function Home() {
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
        <Link to="/acervo">
          <Card className="h-28 sm:h-32 bg-blue-50 dark:bg-blue-950/20 border-blue-200 dark:border-blue-800 hover:shadow-md transition-shadow cursor-pointer">
            <CardContent className="flex flex-col justify-center items-center h-full p-4">
              <BookOpen className="h-6 w-6 sm:h-8 sm:w-8 text-blue-600 dark:text-blue-400 mb-2" />
              <span className="text-sm sm:text-base font-medium text-blue-900 dark:text-blue-100 text-center">
                Acervo
              </span>
            </CardContent>
          </Card>
        </Link>

        <Link to="/emprestimos">
          <Card className="h-28 sm:h-32 bg-green-50 dark:bg-green-950/20 border-green-200 dark:border-green-800 hover:shadow-md transition-shadow cursor-pointer">
            <CardContent className="flex flex-col justify-center items-center h-full p-4">
              <ArrowRight className="h-6 w-6 sm:h-8 sm:w-8 text-green-600 dark:text-green-400 mb-2" />
              <span className="text-sm sm:text-base font-medium text-green-900 dark:text-green-100 text-center">
                Empréstimos
              </span>
            </CardContent>
          </Card>
        </Link>

        <Link to="/reservas">
          <Card className="h-28 sm:h-32 bg-gray-50 dark:bg-gray-800/20 border-gray-200 dark:border-gray-700 hover:shadow-md transition-shadow cursor-pointer">
            <CardContent className="flex flex-col justify-center items-center h-full p-4">
              <Calendar className="h-6 w-6 sm:h-8 sm:w-8 text-gray-600 dark:text-gray-400 mb-2" />
              <span className="text-sm sm:text-base font-medium text-gray-900 dark:text-gray-100 text-center">
                Reservas
              </span>
            </CardContent>
          </Card>
        </Link>

        <Link to="/devolucoes">
          <Card className="h-28 sm:h-32 bg-pink-50 dark:bg-pink-950/20 border-pink-200 dark:border-pink-800 hover:shadow-md transition-shadow cursor-pointer">
            <CardContent className="flex flex-col justify-center items-center h-full p-4">
              <ArrowRight className="h-6 w-6 sm:h-8 sm:w-8 rotate-180 text-pink-600 dark:text-pink-400 mb-2" />
              <span className="text-sm sm:text-base font-medium text-pink-900 dark:text-pink-100 text-center">
                Devoluções
              </span>
            </CardContent>
          </Card>
        </Link>

        <Link to="/historico">
          <Card className="h-28 sm:h-32 bg-yellow-50 dark:bg-yellow-950/20 border-yellow-200 dark:border-yellow-800 hover:shadow-md transition-shadow cursor-pointer">
            <CardContent className="flex flex-col justify-center items-center h-full p-4">
              <FileText className="h-6 w-6 sm:h-8 sm:w-8 text-yellow-600 dark:text-yellow-400 mb-2" />
              <span className="text-xs sm:text-sm font-medium text-yellow-900 dark:text-yellow-100 text-center leading-tight">
                Histórico e relatórios
              </span>
            </CardContent>
          </Card>
        </Link>

        <Link to="/atrasos">
          <Card className="h-28 sm:h-32 bg-red-50 dark:bg-red-950/20 border-red-200 dark:border-red-800 hover:shadow-md transition-shadow cursor-pointer">
            <CardContent className="flex flex-col justify-center items-center h-full p-4">
              <AlertCircle className="h-6 w-6 sm:h-8 sm:w-8 text-red-600 dark:text-red-400 mb-2" />
              <span className="text-xs sm:text-sm font-medium text-red-900 dark:text-red-100 text-center leading-tight">
                Atrasos e Multas
              </span>
            </CardContent>
          </Card>
        </Link>
      </div>
    </div>
  );
}
