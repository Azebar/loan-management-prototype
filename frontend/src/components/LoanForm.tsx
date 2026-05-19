import { useEffect, useState } from "react";
import {
  LOAN_TYPES,
  LOAN_TYPE_LABELS,
  SCHEDULE_TYPES,
  SCHEDULE_TYPE_LABELS,
  type Loan,
  type LoanInput,
} from "../types";

interface Props {
  initial?: Loan | null;
  onSubmit: (input: LoanInput) => Promise<void>;
  onCancelEdit?: () => void;
  submitting?: boolean;
}

const empty: LoanInput = {
  borrowerName: "",
  type: "CONSUMER",
  amount: "10000.00",
  termMonths: 12,
  annualInterestRatePercent: "8.5",
  scheduleType: "ANNUITY",
  startDate: new Date().toISOString().slice(0, 10),
};

export function LoanForm({ initial, onSubmit, onCancelEdit, submitting }: Props) {
  const [form, setForm] = useState<LoanInput>(empty);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (initial) {
      setForm({
        borrowerName: initial.borrowerName,
        type: initial.type,
        amount: initial.amount,
        termMonths: initial.termMonths,
        annualInterestRatePercent: initial.annualInterestRatePercent,
        scheduleType: initial.scheduleType,
        startDate: initial.startDate,
      });
    } else {
      setForm(empty);
    }
    setError(null);
  }, [initial]);

  function update<K extends keyof LoanInput>(key: K, value: LoanInput[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    try {
      await onSubmit(form);
      if (!initial) setForm(empty);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to save loan");
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <h2>{initial ? "Edit loan" : "New loan"}</h2>

      <div className="field">
        <label htmlFor="borrower">Borrower</label>
        <input
          id="borrower"
          type="text"
          value={form.borrowerName}
          onChange={(e) => update("borrowerName", e.target.value)}
          required
        />
      </div>

      <div className="field">
        <label htmlFor="type">Loan type</label>
        <select
          id="type"
          value={form.type}
          onChange={(e) => update("type", e.target.value as LoanInput["type"])}
        >
          {LOAN_TYPES.map((t) => (
            <option key={t} value={t}>{LOAN_TYPE_LABELS[t]}</option>
          ))}
        </select>
      </div>

      <div className="row">
        <div className="field">
          <label htmlFor="amount">Amount (EUR)</label>
          <input
            id="amount"
            type="number"
            min="0.01"
            step="0.01"
            value={form.amount}
            onChange={(e) => update("amount", e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="months">Term (months)</label>
          <input
            id="months"
            type="number"
            min="1"
            max="600"
            value={form.termMonths}
            onChange={(e) => update("termMonths", Number(e.target.value))}
            required
          />
        </div>
      </div>

      <div className="row">
        <div className="field">
          <label htmlFor="rate">Annual rate (%)</label>
          <input
            id="rate"
            type="number"
            min="0"
            step="0.01"
            value={form.annualInterestRatePercent}
            onChange={(e) => update("annualInterestRatePercent", e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="start">Start date</label>
          <input
            id="start"
            type="date"
            value={form.startDate}
            onChange={(e) => update("startDate", e.target.value)}
            required
          />
        </div>
      </div>

      <div className="field">
        <label htmlFor="schedule">Repayment schedule</label>
        <select
          id="schedule"
          value={form.scheduleType}
          onChange={(e) => update("scheduleType", e.target.value as LoanInput["scheduleType"])}
        >
          {SCHEDULE_TYPES.map((s) => (
            <option key={s} value={s}>{SCHEDULE_TYPE_LABELS[s]}</option>
          ))}
        </select>
      </div>

      {error && <div className="error" style={{ marginBottom: 12 }}>{error}</div>}

      <div className="row">
        <button type="submit" className="primary" disabled={submitting}>
          {submitting ? "Saving…" : initial ? "Save changes" : "Create loan"}
        </button>
        {initial && onCancelEdit && (
          <button type="button" className="ghost" onClick={onCancelEdit}>
            Cancel
          </button>
        )}
      </div>
    </form>
  );
}
