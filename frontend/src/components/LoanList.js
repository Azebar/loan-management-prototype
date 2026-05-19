import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { LOAN_TYPE_LABELS, SCHEDULE_TYPE_LABELS, } from "../types";
function formatMoney(s) {
    const n = Number(s);
    return new Intl.NumberFormat("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(n);
}
export function LoanList({ loans, selectedId, onSelect, onEdit, onDelete }) {
    if (loans.length === 0) {
        return _jsx("div", { className: "card", children: _jsx("div", { className: "empty", children: "No loans yet. Create one on the left." }) });
    }
    return (_jsxs("div", { className: "card", children: [_jsx("h2", { children: "Loans" }), _jsxs("table", { children: [_jsx("thead", { children: _jsxs("tr", { children: [_jsx("th", { children: "Borrower" }), _jsx("th", { children: "Type" }), _jsx("th", { className: "num", children: "Amount" }), _jsx("th", { className: "num", children: "Term" }), _jsx("th", { className: "num", children: "Rate" }), _jsx("th", { children: "Schedule" }), _jsx("th", {})] }) }), _jsx("tbody", { children: loans.map((l) => (_jsxs("tr", { className: "loan-row " + (l.id === selectedId ? "selected" : ""), onClick: () => onSelect(l), children: [_jsx("td", { children: l.borrowerName }), _jsx("td", { children: _jsx("span", { className: "badge", children: LOAN_TYPE_LABELS[l.type] }) }), _jsx("td", { className: "num", children: formatMoney(l.amount) }), _jsxs("td", { className: "num", children: [l.termMonths, " mo"] }), _jsxs("td", { className: "num", children: [Number(l.annualInterestRatePercent).toFixed(2), "%"] }), _jsx("td", { children: _jsx("span", { className: "badge", children: SCHEDULE_TYPE_LABELS[l.scheduleType] }) }), _jsxs("td", { onClick: (e) => e.stopPropagation(), style: { whiteSpace: "nowrap" }, children: [_jsx("button", { className: "ghost", onClick: () => onEdit(l), children: "Edit" }), " ", _jsx("button", { className: "danger", onClick: () => onDelete(l), children: "Delete" })] })] }, l.id))) })] })] }));
}
