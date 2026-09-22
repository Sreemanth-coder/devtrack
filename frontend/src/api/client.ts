const API_BASE_URL = (import.meta.env["VITE_API_BASE_URL"] ?? "").replace(/\/$/, "");
const TOKEN_KEY = "devtrack_token";

export type ApiErrorKind = "unauthorized" | "forbidden" | "network" | "server";
export class ApiError extends Error {
  constructor(message: string, public status: number | null, public kind: ApiErrorKind) { super(message); this.name = "ApiError"; }
}
export function getAccessToken() { return typeof window === "undefined" ? null : window.localStorage.getItem(TOKEN_KEY); }
export function setAccessToken(token: string | null) {
  if (typeof window === "undefined") return;
  if (token) window.localStorage.setItem(TOKEN_KEY, token); else window.localStorage.removeItem(TOKEN_KEY);
}
export async function apiGet<T>(path: string): Promise<T> {
  const token = getAccessToken();
  if (!API_BASE_URL) {
    throw new ApiError(
      "The DevTrack API address is not configured yet. Set VITE_API_BASE_URL to your backend URL (for example http://localhost:8081) and reload.",
      null,
      "network",
    );
  }
  try {
    const response = await fetch(`${API_BASE_URL}${path}`, { headers: { Accept: "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}) }, credentials: "include" });
    if (!response.ok) {
      const kind = response.status === 401 ? "unauthorized" : response.status === 403 ? "forbidden" : "server";
      throw new ApiError(response.status === 401 ? "Your session is missing or has expired." : response.status === 403 ? "You do not have access to this data." : "The service is temporarily unavailable.", response.status, kind);
    }
    return (await response.json()) as T;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("Unable to reach the DevTrack service.", null, "network");
  }
}
