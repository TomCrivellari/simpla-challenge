import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import CabecalhoHome from '../Components/CabecalhoHome';
import { useState } from 'react';
import { useAuth } from '../context/AuthProvider';
import { useNavigate } from 'react-router';

const ViewLogin = () => {
    const [nome, setNome] = useState();
    const [senha, setSenha] = useState();
    const [mensagem, setMensagem] = useState("");
    const {login} = useAuth();

    const navigate = useNavigate();

    const logar = async () => {
        const result = await login(nome, senha)
        if (result) {
            setNome("")
            setSenha("")
            navigate("/principal")
        }
        else {
            setMensagem("Usuario ou senha invalidos!")
        }
    }
    return (
        <div>
            <CabecalhoHome />
            <div className='hero-shell'>
                <Form className='surface-card w-full max-w-md p-8 flex flex-col'>
                    <p className="text-sm font-bold uppercase tracking-[0.18em] text-[#3271FF] mb-2">Simpla Meta</p>
                    <h1 className="text-3xl font-black mb-6">Login</h1>
                    <Form.Group className="mb-3 w-full" controlId="formBasicEmail">
                        <Form.Label>Usuario</Form.Label>
                        <Form.Control type="text" value={nome} onChange={(e) => setNome(e.target.value)}/>
                    </Form.Group>
                    <Form.Group className="mb-3 w-full" controlId="formBasicPassword">
                        <Form.Label>Senha</Form.Label>
                        <Form.Control type="password" value={senha} onChange={(e) => setSenha(e.target.value)}/>
                    </Form.Group>
                    <Form.Text className="text-danger mb-3 min-h-6">
                        {mensagem}
                    </Form.Text>
                    <Button variant="success" className="w-full py-2" onClick={logar}>
                        Logar
                    </Button>
                </Form>
            </div>
        </div>
    )
}

export default ViewLogin
