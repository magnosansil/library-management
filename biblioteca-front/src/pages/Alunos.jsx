import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Users } from "lucide-react";

export default function Alunos() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold">Alunos</h1>
        <p className="text-sm sm:text-base text-muted-foreground mt-1">
          Gerencie os estudantes cadastrados no sistema
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Users className="h-5 w-5" />
            Gerenciamento de Alunos
          </CardTitle>
          <CardDescription>Funcionalidade em desenvolvimento</CardDescription>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">
            Em breve você poderá cadastrar, editar e gerenciar os alunos da
            biblioteca.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
