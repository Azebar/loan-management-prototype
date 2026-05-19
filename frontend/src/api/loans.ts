import type { Loan, LoanInput, Schedule } from "../types";

const BASE = "/api/loans";

async function jsonFetch<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...init?.headers,
    },
  });
  if (!response.ok) {
    const text = await response.text();
    let message = response.statusText;
    try {
      const parsed = JSON.parse(text);
      if (parsed?.message) message = parsed.message;
    } catch {
      if (text) message = text;
    }
    throw new Error(`${response.status} ${message}`);
  }
  if (response.status === 204) return undefined as T;
  return (await response.json()) as T;
}

export const loansApi = {
  list: () => jsonFetch<Loan[]>(BASE),
  create: (input: LoanInput) =>
    jsonFetch<Loan>(BASE, { method: "POST", body: JSON.stringify(input) }),
  update: (loanId: string, input: LoanInput) =>
    jsonFetch<Loan>(`${BASE}/${loanId}`, { method: "PUT", body: JSON.stringify(input) }),
  remove: (loanId: string) =>
    jsonFetch<void>(`${BASE}/${loanId}`, { method: "DELETE" }),
  schedule: (loanId: string) => jsonFetch<Schedule>(`${BASE}/${loanId}/schedule`),
  scheduleCsvUrl: (loanId: string) => `${BASE}/${loanId}/schedule.csv`,
};
