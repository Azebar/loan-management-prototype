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

function formatMoney(value: string) {
  const numeric = Number(value);
  return new Intl.NumberFormat("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(numeric);
}

export function LoanList({ loans, selectedId, onSelect, onEdit, onDelete }: Readonly<Props>) {
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
          {loans.map((loan) => (
            <tr
              key={loan.id}
              className={"loan-row " + (loan.id === selectedId ? "selected" : "")}
              onClick={() => onSelect(loan)}
            >
              <td>{loan.borrowerName}</td>
              <td><span className="badge">{LOAN_TYPE_LABELS[loan.type]}</span></td>
              <td className="num">{formatMoney(loan.amount)}</td>
              <td className="num">{loan.termMonths} mo</td>
              <td className="num">{Number(loan.annualInterestRatePercent).toFixed(2)}%</td>
              <td><span className="badge">{SCHEDULE_TYPE_LABELS[loan.scheduleType]}</span></td>
              <td onClick={(event) => event.stopPropagation()} style={{ whiteSpace: "nowrap" }}>
                <button className="ghost" onClick={() => onEdit(loan)}>Edit</button>{" "}
                <button className="danger" onClick={() => onDelete(loan)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
