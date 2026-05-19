import { SCHEDULE_TYPE_LABELS, type Schedule } from "../types";
import { loansApi } from "../api/loans";

interface Props {
  schedule: Schedule;
  loanLabel: string;
}

function fmt(s: string) {
  const n = Number(s);
  return new Intl.NumberFormat("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(n);
}

export function ScheduleView({ schedule, loanLabel }: Props) {
  return (
    <div className="card">
      <div className="toolbar">
        <h2 style={{ margin: 0 }}>Repayment schedule</h2>
        <a
          className="ghost"
          href={loansApi.scheduleCsvUrl(schedule.loanId)}
          style={{
            padding: "8px 12px",
            borderRadius: 8,
            border: "1px solid var(--border)",
            textDecoration: "none",
            fontSize: 13,
            color: "var(--text)",
          }}
        >
          Download CSV
        </a>
      </div>
      <div className="muted" style={{ marginBottom: 12 }}>
        {loanLabel} · {SCHEDULE_TYPE_LABELS[schedule.scheduleType]} · {schedule.installments.length} periods
      </div>

      <div className="totals">
        <div className="stat">
          <div className="label">Total principal</div>
          <div className="value">{fmt(schedule.totalPrincipal)}</div>
        </div>
        <div className="stat">
          <div className="label">Total interest</div>
          <div className="value">{fmt(schedule.totalInterest)}</div>
        </div>
        <div className="stat">
          <div className="label">Total paid</div>
          <div className="value">{fmt(schedule.totalPaid)}</div>
        </div>
      </div>

      <div style={{ maxHeight: 480, overflow: "auto" }}>
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Due date</th>
              <th className="num">Principal</th>
              <th className="num">Interest</th>
              <th className="num">Payment</th>
              <th className="num">Balance</th>
            </tr>
          </thead>
          <tbody>
            {schedule.installments.map((i) => (
              <tr key={i.periodNumber}>
                <td>{i.periodNumber}</td>
                <td>{i.dueDate}</td>
                <td className="num">{fmt(i.principalAmount)}</td>
                <td className="num">{fmt(i.interestAmount)}</td>
                <td className="num">{fmt(i.totalPayment)}</td>
                <td className="num">{fmt(i.remainingBalance)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
