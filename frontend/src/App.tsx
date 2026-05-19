import { useCallback, useEffect, useState } from "react";
import { loansApi } from "./api/loans";
import { LoanForm } from "./components/LoanForm";
import { LoanList } from "./components/LoanList";
import { ScheduleView } from "./components/ScheduleView";
import type { Loan, LoanInput, Schedule } from "./types";

export function App() {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [selected, setSelected] = useState<Loan | null>(null);
  const [editing, setEditing] = useState<Loan | null>(null);
  const [schedule, setSchedule] = useState<Schedule | null>(null);
  const [globalError, setGlobalError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(true);

  const refresh = useCallback(async () => {
    try {
      const list = await loansApi.list();
      setLoans(list);
      setGlobalError(null);
    } catch (err) {
      setGlobalError(err instanceof Error ? err.message : "Failed to load loans");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { void refresh(); }, [refresh]);

  useEffect(() => {
    if (!selected) {
      setSchedule(null);
      return;
    }
    let cancelled = false;
    loansApi.schedule(selected.id)
      .then((s) => { if (!cancelled) setSchedule(s); })
      .catch((err) => {
        if (!cancelled) setGlobalError(err instanceof Error ? err.message : "Failed to load schedule");
      });
    return () => { cancelled = true; };
  }, [selected]);

  async function handleCreate(input: LoanInput) {
    setSubmitting(true);
    try {
      const created = await loansApi.create(input);
      await refresh();
      setSelected(created);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleUpdate(input: LoanInput) {
    if (!editing) return;
    setSubmitting(true);
    try {
      const saved = await loansApi.update(editing.id, input);
      setEditing(null);
      await refresh();
      setSelected(saved);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDelete(loan: Loan) {
    if (!window.confirm(`Delete loan for ${loan.borrowerName}?`)) return;
    try {
      await loansApi.remove(loan.id);
      if (selected?.id === loan.id) setSelected(null);
      if (editing?.id === loan.id) setEditing(null);
      await refresh();
    } catch (err) {
      setGlobalError(err instanceof Error ? err.message : "Failed to delete loan");
    }
  }

  return (
    <div className="app">
      <h1>Loan Management Prototype</h1>
      <div className="subtitle">
        Insert loans, generate repayment schedules, export to CSV.
      </div>

      {globalError && <div className="error" style={{ marginBottom: 16 }}>{globalError}</div>}

      <div className="grid">
        <LoanForm
          initial={editing}
          onSubmit={editing ? handleUpdate : handleCreate}
          onCancelEdit={() => setEditing(null)}
          submitting={submitting}
        />

        <div style={{ display: "flex", flexDirection: "column", gap: 24 }}>
          {loading ? (
            <div className="card"><div className="empty">Loading…</div></div>
          ) : (
            <LoanList
              loans={loans}
              selectedId={selected?.id ?? null}
              onSelect={setSelected}
              onEdit={(l) => { setEditing(l); setSelected(l); }}
              onDelete={handleDelete}
            />
          )}

          {selected && schedule && (
            <ScheduleView
              schedule={schedule}
              loanLabel={`${selected.borrowerName} · ${selected.amount} @ ${selected.annualInterestRatePercent}%`}
            />
          )}
        </div>
      </div>
    </div>
  );
}
