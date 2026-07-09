import { Link } from "react-router-dom";
import CabecalhoHome from '../Components/CabecalhoHome';

const ViewHome = () => {

    return (
        <div>
            <CabecalhoHome />
            <div className="hero-shell">
                <div className='w-full max-w-5xl grid grid-cols-1 md:grid-cols-[1.1fr_0.9fr] gap-8 items-center'>
                    <div className="text-white">
                        <p className="uppercase text-sm font-bold tracking-[0.22em] text-[#41B9FF] mb-3">Simpla Meta</p>
                        <h1 className='text-5xl md:text-6xl font-black leading-tight mb-4'>Controle financeiro com foco em crescimento.</h1>
                        <p className='text-xl text-white/75 max-w-2xl'>Organize receitas, despesas e simulações em uma interface clara para acompanhar seu saldo e planejar os próximos passos.</p>
                    </div>
                    <div className='surface-card p-8 flex flex-col gap-5'>
                        <div>
                            <p className='text-sm font-bold uppercase tracking-[0.18em] text-[#3271FF]'>Acesso</p>
                            <h2 className='text-3xl font-black text-[#05070d]'>Entre na sua carteira</h2>
                        </div>
                        <Link to="/login" className='brand-button no-underline text-center px-5 py-3 rounded-xl'>Logar</Link>
                        <Link to="/cadastro" className='no-underline text-center px-5 py-3 rounded-xl border border-[#3271FF] text-[#3271FF] font-bold hover:bg-[#eef6ff]'>Criar cadastro</Link>
                        <p className="text-sm text-[#5d6678] mb-0">Simplifique suas metas financeiras com uma visão objetiva do seu dinheiro.</p>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ViewHome
