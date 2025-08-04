'use client'

import { useState } from 'react';
import { LoginCredentials } from '@/types'
interface LoginPageProps { 
    onLogin : (credentials :LoginCredentials) => void; 
}

export default function LoginPage ( {onLogin} : LoginPageProps ) {
    const [username, setUsername] = useState(''); 
    const [password, setPassword] = useState(''); 

    const handleLogin = (e: React.FormEvent) => {
        e.preventDefault(); 
        onLogin({
            username,
            password
        });
    }

    return (
        <section> 
            <form onSubmit={handleLogin}>
                <input 
                    name="username"
                    type="text"
                    placeholder="username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />

                <input 
                    name="password"
                    type="password"
                    placeholder="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button type="submit">Login</button>
            </form>
        </section>
    )
}