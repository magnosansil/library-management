import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import Home from "./pages/Home";
import Acervo from "./pages/Acervo";
import CadastroLivro from "./pages/CadastroLivro";
import EdicaoLivro from "./pages/EdicaoLivro";
import Busca from "./pages/Busca";
import Alunos from "./pages/Alunos";
import Emprestimos from "./pages/Emprestimos";
import Reservas from "./pages/Reservas";
import Devolucoes from "./pages/Devolucoes";
import Historico from "./pages/Historico";
import Atrasos from "./pages/Atrasos";
import NovoAluno from "./pages/NovoAluno";
import NovoEmprestimo from "./pages/NovoEmprestimo";
import EditarEmprestimo from "./pages/EditarEmprestimo";

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/acervo" element={<Acervo />} />
          <Route path="/livros/novo" element={<CadastroLivro />} />
          <Route path="/livros/:isbn/editar" element={<EdicaoLivro />} />
          <Route path="/busca" element={<Busca />} />
          <Route path="/alunos" element={<Alunos />} />
          <Route path="/alunos/novo" element={<NovoAluno />} />
          <Route path="/emprestimos" element={<Emprestimos />} />
          <Route path="/emprestimos/novo" element={<NovoEmprestimo />} />
          <Route
            path="/emprestimos/:id/editar"
            element={<EditarEmprestimo />}
          />
          <Route path="/reservas" element={<Reservas />} />
          <Route path="/devolucoes" element={<Devolucoes />} />
          <Route path="/historico" element={<Historico />} />
          <Route path="/atrasos" element={<Atrasos />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
