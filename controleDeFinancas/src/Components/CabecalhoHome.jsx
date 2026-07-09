import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import { Link } from "react-router-dom";

const CabecalhoHome = () => {


    return(
        <>
            <Navbar className="brand-gradient py-3 shadow-sm">
                <Container>
                    <Navbar.Brand as={Link} to="/" className='text-white fw-bold tracking-wide'>Simpla Meta</Navbar.Brand>
                    <Navbar.Toggle />
                    <Navbar.Collapse className="justify-content-end">
                    <Navbar.Text className='text-white opacity-90'>
                        Estratégia para suas metas financeiras
                    </Navbar.Text>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    )
}

export default CabecalhoHome
