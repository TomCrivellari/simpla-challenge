import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthProvider";

const CabecalhoPrincipal = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const sair = () => { logout(); navigate("/login"); };

  return (
    <Navbar expand="md" className="brand-gradient py-3 shadow-sm" data-bs-theme="dark">
      <Container>
        <Navbar.Brand as={Link} to="/principal" className="text-white fw-bold">Simpla Meta</Navbar.Brand>
        <Navbar.Toggle aria-controls="main-navigation" />
        <Navbar.Collapse id="main-navigation" className="justify-content-end">
          <Nav className="align-items-md-center gap-md-2">
            <Nav.Link as={Link} to="/principal">Visão geral</Nav.Link>
            <Nav.Link as={Link} to="/transacoes">Transações</Nav.Link>
            <Nav.Link as={Link} to="/metas">Metas</Nav.Link>
            <Nav.Link as={Link} to="/assistente">Assistente IA</Nav.Link>
            <Navbar.Text className="px-md-3 text-white">{user?.fullName}</Navbar.Text>
            <Nav.Link onClick={sair}>Sair</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default CabecalhoPrincipal;
