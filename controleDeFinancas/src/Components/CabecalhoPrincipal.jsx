import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from '../context/AuthProvider';

const CabecalhoPrincipal = () => {

    const {logout} = useAuth();

    const navigate = useNavigate();
    const sair = () => {
        logout();
        navigate("/login");
    }

    return(
        <>
            <Navbar className="brand-gradient py-3 shadow-sm">
                <Container>
                    <Navbar.Brand className='text-white fw-bold'>Simpla Meta</Navbar.Brand>
                    <Navbar.Text className='text-white opacity-90 d-none d-md-block'>
                            Metas, saldo e decisões em um só lugar
                    </Navbar.Text>
                    <Navbar.Toggle />
                    <Navbar.Collapse className="justify-content-end gap-4 text-white">
                        <Nav.Link as={Link} to="/principal">Inicio</Nav.Link>
                        <Nav.Link as={Link} to="/simulacao">Simular</Nav.Link>
                        <Nav.Link as={Link} to="/usuario">Usuario</Nav.Link>
                        <Nav.Link onClick={sair}>Sair</Nav.Link>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    )
}

export default CabecalhoPrincipal
