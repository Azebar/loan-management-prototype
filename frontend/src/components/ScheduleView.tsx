import { SCHEDULE_TYPE_LABELS, type Schedule } from "../types";
import { loansApi } from "../api/loans";

interface Props {
  schedule: Schedule;
  loanLabel: string;
}

function formatMoney(value: string) {
  const numeric = Number(value);
  return new Intl.NumberFormat("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(numeric);
}

export function ScheduleView({ schedule, loanLabel }: Readonly<Props>) {
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
          <div className="value">{formatMoney(schedule.totalPrincipal)}</div>
        </div>
        <div className="stat">
          <div className="label">Total interest</div>
          <div className="value">{formatMoney(schedule.totalInterest)}</div>
        </div>
        <div className="stat">
          <div className="label">Total paid</div>
          <div className="value">{formatMoney(schedule.totalPaid)}</div>
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
            {schedule.installments.map((installment) => (
              <tr key={installment.periodNumber}>
                <td>{installment.periodNumber}</td>
                <td>{installment.dueDate}</td>
                <td className="num">{formatMoney(installment.principalAmount)}</td>
                <td className="num">{formatMoney(installment.interestAmount)}</td>
                <td className="num">{formatMoney(installment.totalPayment)}</td>
                <td className="num">{formatMoney(installment.remainingBalance)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
