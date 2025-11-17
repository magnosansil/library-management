// Usa variável de ambiente em produção, fallback para localhost em desenvolvimento
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
