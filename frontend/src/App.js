import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useCallback, useEffect, useState } from "react";
import { loansApi } from "./api/loans";
import { LoanForm } from "./components/LoanForm";
import { LoanList } from "./components/LoanList";
import { ScheduleView } from "./components/ScheduleView";
export function App() {
    const [loans, setLoans] = useState([]);
    const [selected, setSelected] = useState(null);
    const [editing, setEditing] = useState(null);
    const [schedule, setSchedule] = useState(null);
    const [globalError, setGlobalError] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const [loading, setLoading] = useState(true);
    const refresh = useCallback(async () => {
        try {
            const list = await loansApi.list();
            setLoans(list);
            setGlobalError(null);
        }
        catch (err) {
            setGlobalError(err instanceof Error ? err.message : "Failed to load loans");
        }
        finally {
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
            .then((s) => { if (!cancelled)
            setSchedule(s); })
            .catch((err) => {
            if (!cancelled)
                setGlobalError(err instanceof Error ? err.message : "Failed to load schedule");
        });
        return () => { cancelled = true; };
    }, [selected]);
    async function handleCreate(input) {
        setSubmitting(true);
        try {
            const created = await loansApi.create(input);
            await refresh();
            setSelected(created);
        }
        finally {
            setSubmitting(false);
        }
    }
    async function handleUpdate(input) {
        if (!editing)
            return;
        setSubmitting(true);
        try {
            const saved = await loansApi.update(editing.id, input);
            setEditing(null);
            await refresh();
            setSelected(saved);
        }
        finally {
            setSubmitting(false);
        }
    }
    async function handleDelete(loan) {
        if (!window.confirm(`Delete loan for ${loan.borrowerName}?`))
            return;
        try {
            await loansApi.remove(loan.id);
            if (selected?.id === loan.id)
                setSelected(null);
            if (editing?.id === loan.id)
                setEditing(null);
            await refresh();
        }
        catch (err) {
            setGlobalError(err instanceof Error ? err.message : "Failed to delete loan");
        }
    }
    return (_jsxs("div", { className: "app", children: [_jsx("h1", { children: "Loan Management Prototype" }), _jsx("div", { className: "subtitle", children: "Insert loans, generate repayment schedules, export to CSV." }), globalError && _jsx("div", { className: "error", style: { marginBottom: 16 }, children: globalError }), _jsxs("div", { className: "grid", children: [_jsx(LoanForm, { initial: editing, onSubmit: editing ? handleUpdate : handleCreate, onCancelEdit: () => setEditing(null), submitting: submitting }), _jsxs("div", { style: { display: "flex", flexDirection: "column", gap: 24 }, children: [loading ? (_jsx("div", { className: "card", children: _jsx("div", { className: "empty", children: "Loading\u2026" }) })) : (_jsx(LoanList, { loans: loans, selectedId: selected?.id ?? null, onSelect: setSelected, onEdit: (l) => { setEditing(l); setSelected(l); }, onDelete: handleDelete })), selected && schedule && (_jsx(ScheduleView, { schedule: schedule, loanLabel: `${selected.borrowerName} · ${selected.amount} @ ${selected.annualInterestRatePercent}%` }))] })] })] }));
}
