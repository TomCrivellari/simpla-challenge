const API_URL = import.meta.env.VITE_API_URL || "/api/v1";

export class ApiError extends Error {
  constructor(message, status, fields = null) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fields = fields;
  }
}

const request = async (path, { token, body, ...options } = {}) => {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      Accept: "application/json",
      ...(body !== undefined ? { "Content-Type": "application/json" } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
    ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
  });

  if (!response.ok) {
    let error = {};
    try { error = await response.json(); } catch { /* resposta sem JSON */ }
    throw new ApiError(error.message || "Não foi possível concluir a operação.", response.status, error.fields);
  }

  if (response.status === 204) return null;
  return response.json();
};

export const authApi = {
  register: (data) => request("/auth/register", { method: "POST", body: data }),
  login: (email, password) => request("/auth/login", { method: "POST", body: { email, password } }),
  me: (token) => request("/auth/me", { token }),
};

export const financeApi = {
  dashboard: (token) => request("/dashboard", { token }),
  listTransactions: (token) => request("/transactions", { token }),
  getTransaction: (token, id) => request(`/transactions/${id}`, { token }),
  createTransaction: (token, data) => request("/transactions", { method: "POST", token, body: data }),
  updateTransaction: (token, id, data) => request(`/transactions/${id}`, { method: "PUT", token, body: data }),
  deleteTransaction: (token, id) => request(`/transactions/${id}`, { method: "DELETE", token }),
};

export const goalsApi = {
  list: (token) => request("/goals", { token }),
  get: (token, id) => request(`/goals/${id}`, { token }),
  create: (token, data) => request("/goals", { method: "POST", token, body: data }),
  update: (token, id, data) => request(`/goals/${id}`, { method: "PUT", token, body: data }),
  delete: (token, id) => request(`/goals/${id}`, { method: "DELETE", token }),
  listContributions: (token, goalId) => request(`/goals/${goalId}/contributions`, { token }),
  createContribution: (token, goalId, data) => request(`/goals/${goalId}/contributions`, { method: "POST", token, body: data }),
  deleteContribution: (token, goalId, id) => request(`/goals/${goalId}/contributions/${id}`, { method: "DELETE", token }),
};

export const aiApi = {
  chat: (token, messages) => request("/ai/chat", { method: "POST", token, body: { messages } }),
};
