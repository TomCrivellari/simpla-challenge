import { createContext, useContext, useEffect, useState } from "react";
import { authApi } from "../service/Services";

const AuthContext = createContext(null);
const STORAGE_KEY = "simpla-meta-session";

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [session, setSession] = useState(() => {
    try { return JSON.parse(localStorage.getItem(STORAGE_KEY)); } catch { return null; }
  });
  const [loading, setLoading] = useState(Boolean(session?.token));

  useEffect(() => {
    if (!session?.token) { setLoading(false); return; }
    authApi.me(session.token)
      .then((profile) => setSession((current) => ({ ...current, user: { ...current.user, ...profile } })))
      .catch(() => setSession(null))
      .finally(() => setLoading(false));
  }, [session?.token]);

  useEffect(() => {
    if (session) localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    else localStorage.removeItem(STORAGE_KEY);
  }, [session]);

  const login = async (email, password) => {
    const response = await authApi.login(email, password);
    setSession({ token: response.accessToken, expiresIn: response.expiresIn, user: response.user });
  };

  const logout = () => setSession(null);

  return <AuthContext.Provider value={{ user: session?.user || null, token: session?.token || null, loading, login, logout }}>{children}</AuthContext.Provider>;
};
