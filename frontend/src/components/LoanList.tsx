import {
  LOAN_TYPE_LABELS,
  SCHEDULE_TYPE_LABELS,
  type Loan,
} from "../types";

interface Props {
  loans: Loan[];
  selectedId: string | null;
  onSelect: (loan: Loan) => void;
  onEdit: (loan: Loan) => void;
  onDelete: (loan: Loan) => void;
}

function formatMoney(s: string) {
  const n = Number(s);
  return new Intl.NumberFormat("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(n);
}

export function LoanList({ loans, selectedId, onSelect, onEdit, onDelete }: Props) {
  if (loans.length === 0) {
    return <div className="card"><div className="empty">No loans yet. Create one on the left.</div></div>;
  }
  return (
    <div className="card">
      <h2>Loans</h2>
      <table>
        <thead>
          <tr>
            <th>Borrower</th>
            <th>Type</th>
            <th className="num">Amount</th>
            <th className="num">Term</th>
            <th className="num">Rate</th>
            <th>Schedule</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {loans.map((l) => (
            <tr
              key={l.id}
              className={"loan-row " + (l.id === selectedId ? "selected" : "")}
              onClick={() => onSelect(l)}
            >
              <td>{l.borrowerName}</td>
              <td><span className="badge">{LOAN_TYPE_LABELS[l.type]}</span></td>
              <td className="num">{formatMoney(l.amount)}</td>
              <td className="num">{l.termMonths} mo</td>
              <td className="num">{Number(l.annualInterestRatePercent).toFixed(2)}%</td>
              <td><span className="badge">{SCHEDULE_TYPE_LABELS[l.scheduleType]}</span></td>
              <td onClick={(e) => e.stopPropagation()} style={{ whiteSpace: "nowrap" }}>
                <button className="ghost" onClick={() => onEdit(l)}>Edit</button>{" "}
                <button className="danger" onClick={() => onDelete(l)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
