import { Link, useLocation, useNavigate } from "react-router-dom";
import { Home, Search, Users, ArrowLeft } from "lucide-react";
import { cn } from "@/lib/utils";

export default function Layout({ children }) {
  const location = useLocation();
  const navigate = useNavigate();

  const isActive = (path) => location.pathname === path;
  const showBack = location.pathname !== "/";

  return (
    <div className="min-h-screen bg-background pb-16 mx-auto">
      <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60">
        <div className="px-4 max-w-3xl mx-auto">
          <div className="flex h-14 items-center justify-between">
            <div className="flex items-center">
              {showBack && (
                <button
                  aria-label="Voltar"
                  onClick={() => navigate(-1)}
                  className="flex items-center justify-center h-9 w-9 rounded-full hover:bg-accent text-foreground"
                >
                  <ArrowLeft className="h-5 w-5" />
                </button>
              )}
            </div>
            <Link to="/" className="flex items-center gap-2 font-semibold">
              <span className="text-base sm:text-lg">Wisely</span>
              <img
                src="/wisely-logo.png"
                alt="Wisely"
                className="h-8 w-8 rounded-full border border-purple-300 object-cover"
              />
            </Link>
          </div>
        </div>
      </header>

      <main className="px-4 py-4 sm:py-6 max-w-3xl mx-auto">{children}</main>

      <nav className="fixed bottom-0 left-0 right-0 z-50 border-t bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60">
        <div className="grid grid-cols-3 h-16 max-w-3xl mx-auto">
          <Link
            to="/"
            className={cn(
              "flex flex-col items-center justify-center gap-1 transition-colors",
              isActive("/")
                ? "text-primary"
                : "text-muted-foreground hover:text-foreground"
            )}
          >
            <Home className="h-5 w-5" />
            <span className="text-xs font-medium">Home</span>
          </Link>

          <Link
            to="/busca"
            className={cn(
              "flex flex-col items-center justify-center gap-1 transition-colors",
              isActive("/busca")
                ? "text-primary"
                : "text-muted-foreground hover:text-foreground"
            )}
          >
            <Search className="h-5 w-5" />
            <span className="text-xs font-medium">Busca</span>
          </Link>

          <Link
            to="/alunos"
            className={cn(
              "flex flex-col items-center justify-center gap-1 transition-colors",
              isActive("/alunos")
                ? "text-primary"
                : "text-muted-foreground hover:text-foreground"
            )}
          >
            <Users className="h-5 w-5" />
            <span className="text-xs font-medium">Alunos</span>
          </Link>
        </div>
      </nav>
    </div>
  );
}
