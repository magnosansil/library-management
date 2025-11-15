import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Calendar } from "lucide-react";

export default function Reservas() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold">Reservas</h1>
        <p className="text-sm sm:text-base text-muted-foreground mt-1">
          Gerencie as reservas de livros
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calendar className="h-5 w-5" />
            Reservas
          </CardTitle>
          <CardDescription>Funcionalidade em desenvolvimento</CardDescription>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">
            Em breve você poderá gerenciar reservas de livros.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
