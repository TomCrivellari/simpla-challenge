import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import CabecalhoHome from '../Components/CabecalhoHome';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginService, saveUser } from '../service/Services';
import { useEffect } from 'react';

const ViewCadastro = () => {

    const navigate = useNavigate()
    const [token, setToken] = useState()

    useEffect(() => {
        const cadastrarAdmin = async () => {
            let resp = await loginService("admin", "123456")
            setToken(resp.access_token)
        }

        cadastrarAdmin()
        
    }, [])

    const [dados, setDados] = useState({
        "username": "",
        "password": "",
        "saldo": 0
    })

    const buttonCadastrar = async () => {
        console.log(token)
        await saveUser(dados, token)
        navigate("/login")
    }


    return (
        <div>
            <CabecalhoHome />
            <div className='hero-shell'>
                <Form className='surface-card w-full max-w-md p-8 flex flex-col'>
                    <p className="text-sm font-bold uppercase tracking-[0.18em] text-[#3271FF] mb-2">Simpla Meta</p>
                    <h1 className="text-3xl font-black mb-6">Cadastro</h1>
                    <Form.Group className="mb-3 w-full" controlId="formBasicEmail">
                        <Form.Label>Usuario</Form.Label>
                        <Form.Control type="text" value={dados.username} onChange={(e)=>{setDados({...dados, "username": e.target.value})}}/>
                    </Form.Group>
                    <Form.Group className="mb-4 w-full" controlId="formBasicPassword">
                        <Form.Label>Senha</Form.Label>
                        <Form.Control type="password" value={dados.password} onChange={(e)=>{setDados({...dados, "password": e.target.value})}}/>
                    </Form.Group>
                    <Button variant="success" className="w-full py-2" onClick={buttonCadastrar}>
                        Cadastrar
                    </Button>
                </Form>
            </div>
        </div>
    )
}

export default ViewCadastro
