import type { Loan, LoanInput, Schedule } from "../types";

const BASE = "/api/loans";

async function jsonFetch<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...(init?.headers ?? {}),
    },
  });
  if (!res.ok) {
    const text = await res.text();
    let message = res.statusText;
    try {
      const parsed = JSON.parse(text);
      if (parsed?.message) message = parsed.message;
    } catch {
      if (text) message = text;
    }
    throw new Error(`${res.status} ${message}`);
  }
  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}

export const loansApi = {
  list: () => jsonFetch<Loan[]>(BASE),
  get: (id: string) => jsonFetch<Loan>(`${BASE}/${id}`),
  create: (input: LoanInput) =>
    jsonFetch<Loan>(BASE, { method: "POST", body: JSON.stringify(input) }),
  update: (id: string, input: LoanInput) =>
    jsonFetch<Loan>(`${BASE}/${id}`, { method: "PUT", body: JSON.stringify(input) }),
  remove: (id: string) =>
    jsonFetch<void>(`${BASE}/${id}`, { method: "DELETE" }),
  schedule: (id: string) => jsonFetch<Schedule>(`${BASE}/${id}/schedule`),
  scheduleCsvUrl: (id: string) => `${BASE}/${id}/schedule.csv`,
};
