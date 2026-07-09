import { useEffect, useState } from "react";
import CabecalhoPrincipal from "../Components/CabecalhoPrincipal"
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { useAuth } from "../context/AuthProvider";
import { buscaUserAPI } from "../service/Services";


const ViewUsuario = () => {

    const {user} = useAuth()
    const [novaSenha, setNovaSenha] = useState("")
    const [novoUsuario, setNovoUsuario] = useState("")
    const [mensagem, setMensagem] = useState("")

    useEffect(() =>{
        const buscarUser = async () => {
            if (user == null) {
                return
            }

            try {
                let userBuscado = await buscaUserAPI(user.nome, user.token)
                setNovoUsuario(userBuscado[0].username || "")
            } catch {
                return
            }
            
        }
        buscarUser()
    }, [])

    const alterarUsuario = () => {
        if (novoUsuario === "" || novaSenha === "") {
            setMensagem(`Campos obrigatórios`)
        }
    }

    return(
        <>
            <CabecalhoPrincipal />
            <div className="app-shell items-center justify-center flex p-6">
                <Form className='surface-card w-full max-w-md p-8 flex flex-col'>
                    <p className="text-sm font-bold uppercase tracking-[0.18em] text-[#3271FF] mb-2">Conta</p>
                    <h1 className="text-3xl font-black mb-6">Dados do usuario</h1>
                    <Form.Group className="mb-3 w-full" controlId="formBasicEmail">
                        <Form.Label>Usuario</Form.Label>
                        <Form.Control type="text" value={novoUsuario} onChange={(e) => setNovoUsuario(e.target.value)}/>
                    </Form.Group>
                    <Form.Group className="mb-3 w-full" controlId="formBasicPassword">
                        <Form.Label>Senha</Form.Label>
                        <Form.Control type="password" value={novaSenha} onChange={(e) => setNovaSenha(e.target.value)}/>
                    </Form.Group>
                    <Form.Text className="text-danger mb-3 min-h-6">
                        {mensagem}
                    </Form.Text>
                    <Button variant="success" className="w-full py-2" onClick={alterarUsuario}>
                        Alterar
                    </Button>
                </Form>

            </div>
        </>
    )
}

export default ViewUsuario
