import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from "react";
import { LOAN_TYPES, LOAN_TYPE_LABELS, SCHEDULE_TYPES, SCHEDULE_TYPE_LABELS, } from "../types";
const empty = {
    borrowerName: "",
    type: "CONSUMER",
    amount: "10000.00",
    termMonths: 12,
    annualInterestRatePercent: "8.5",
    scheduleType: "ANNUITY",
    startDate: new Date().toISOString().slice(0, 10),
};
export function LoanForm({ initial, onSubmit, onCancelEdit, submitting }) {
    const [form, setForm] = useState(empty);
    const [error, setError] = useState(null);
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
        }
        else {
            setForm(empty);
        }
        setError(null);
    }, [initial]);
    function update(key, value) {
        setForm((prev) => ({ ...prev, [key]: value }));
    }
    async function handleSubmit(e) {
        e.preventDefault();
        setError(null);
        try {
            await onSubmit(form);
            if (!initial)
                setForm(empty);
        }
        catch (err) {
            setError(err instanceof Error ? err.message : "Failed to save loan");
        }
    }
    return (_jsxs("form", { className: "card", onSubmit: handleSubmit, children: [_jsx("h2", { children: initial ? "Edit loan" : "New loan" }), _jsxs("div", { className: "field", children: [_jsx("label", { htmlFor: "borrower", children: "Borrower" }), _jsx("input", { id: "borrower", type: "text", value: form.borrowerName, onChange: (e) => update("borrowerName", e.target.value), required: true })] }), _jsxs("div", { className: "field", children: [_jsx("label", { htmlFor: "type", children: "Loan type" }), _jsx("select", { id: "type", value: form.type, onChange: (e) => update("type", e.target.value), children: LOAN_TYPES.map((t) => (_jsx("option", { value: t, children: LOAN_TYPE_LABELS[t] }, t))) })] }), _jsxs("div", { className: "row", children: [_jsxs("div", { className: "field", children: [_jsx("label", { htmlFor: "amount", children: "Amount (EUR)" }), _jsx("input", { id: "amount", type: "number", min: "0.01", step: "0.01", value: form.amount, onChange: (e) => update("amount", e.target.value), required: true })] }), _jsxs("div", { className: "field", children: [_jsx("label", { htmlFor: "months", children: "Term (months)" }), _jsx("input", { id: "months", type: "number", min: "1", max: "600", value: form.termMonths, onChange: (e) => update("termMonths", Number(e.target.value)), required: true })] })] }), _jsxs("div", { className: "row", children: [_jsxs("div", { className: "field", children: [_jsx("label", { htmlFor: "rate", children: "Annual rate (%)" }), _jsx("input", { id: "rate", type: "number", min: "0", step: "0.01", value: form.annualInterestRatePercent, onChange: (e) => update("annualInterestRatePercent", e.target.value), required: true })] }), _jsxs("div", { className: "field", children: [_jsx("label", { htmlFor: "start", children: "Start date" }), _jsx("input", { id: "start", type: "date", value: form.startDate, onChange: (e) => update("startDate", e.target.value), required: true })] })] }), _jsxs("div", { className: "field", children: [_jsx("label", { htmlFor: "schedule", children: "Repayment schedule" }), _jsx("select", { id: "schedule", value: form.scheduleType, onChange: (e) => update("scheduleType", e.target.value), children: SCHEDULE_TYPES.map((s) => (_jsx("option", { value: s, children: SCHEDULE_TYPE_LABELS[s] }, s))) })] }), error && _jsx("div", { className: "error", style: { marginBottom: 12 }, children: error }), _jsxs("div", { className: "row", children: [_jsx("button", { type: "submit", className: "primary", disabled: submitting, children: submitting ? "Saving…" : initial ? "Save changes" : "Create loan" }), initial && onCancelEdit && (_jsx("button", { type: "button", className: "ghost", onClick: onCancelEdit, children: "Cancel" }))] })] }));
}
