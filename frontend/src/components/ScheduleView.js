import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { SCHEDULE_TYPE_LABELS } from "../types";
import { loansApi } from "../api/loans";
function fmt(s) {
    const n = Number(s);
    return new Intl.NumberFormat("en-US", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(n);
}
export function ScheduleView({ schedule, loanLabel }) {
    return (_jsxs("div", { className: "card", children: [_jsxs("div", { className: "toolbar", children: [_jsx("h2", { style: { margin: 0 }, children: "Repayment schedule" }), _jsx("a", { className: "ghost", href: loansApi.scheduleCsvUrl(schedule.loanId), style: {
                            padding: "8px 12px",
                            borderRadius: 8,
                            border: "1px solid var(--border)",
                            textDecoration: "none",
                            fontSize: 13,
                            color: "var(--text)",
                        }, children: "Download CSV" })] }), _jsxs("div", { className: "muted", style: { marginBottom: 12 }, children: [loanLabel, " \u00B7 ", SCHEDULE_TYPE_LABELS[schedule.scheduleType], " \u00B7 ", schedule.installments.length, " periods"] }), _jsxs("div", { className: "totals", children: [_jsxs("div", { className: "stat", children: [_jsx("div", { className: "label", children: "Total principal" }), _jsx("div", { className: "value", children: fmt(schedule.totalPrincipal) })] }), _jsxs("div", { className: "stat", children: [_jsx("div", { className: "label", children: "Total interest" }), _jsx("div", { className: "value", children: fmt(schedule.totalInterest) })] }), _jsxs("div", { className: "stat", children: [_jsx("div", { className: "label", children: "Total paid" }), _jsx("div", { className: "value", children: fmt(schedule.totalPaid) })] })] }), _jsx("div", { style: { maxHeight: 480, overflow: "auto" }, children: _jsxs("table", { children: [_jsx("thead", { children: _jsxs("tr", { children: [_jsx("th", { children: "#" }), _jsx("th", { children: "Due date" }), _jsx("th", { className: "num", children: "Principal" }), _jsx("th", { className: "num", children: "Interest" }), _jsx("th", { className: "num", children: "Payment" }), _jsx("th", { className: "num", children: "Balance" })] }) }), _jsx("tbody", { children: schedule.installments.map((i) => (_jsxs("tr", { children: [_jsx("td", { children: i.periodNumber }), _jsx("td", { children: i.dueDate }), _jsx("td", { className: "num", children: fmt(i.principalAmount) }), _jsx("td", { className: "num", children: fmt(i.interestAmount) }), _jsx("td", { className: "num", children: fmt(i.totalPayment) }), _jsx("td", { className: "num", children: fmt(i.remainingBalance) })] }, i.periodNumber))) })] }) })] }));
}
