export type LoanType = "CONSUMER" | "CAR_LEASING" | "MORTGAGE" | "STUDENT" | "BUSINESS";

export type ScheduleType = "ANNUITY" | "EQUAL_PRINCIPAL" | "BULLET";

export interface Loan {
  id: string;
  borrowerName: string;
  type: LoanType;
  amount: string;
  termMonths: number;
  annualInterestRatePercent: string;
  scheduleType: ScheduleType;
  startDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface LoanInput {
  borrowerName: string;
  type: LoanType;
  amount: string;
  termMonths: number;
  annualInterestRatePercent: string;
  scheduleType: ScheduleType;
  startDate: string;
}

export interface Installment {
  periodNumber: number;
  dueDate: string;
  principalAmount: string;
  interestAmount: string;
  totalPayment: string;
  remainingBalance: string;
}

export interface Schedule {
  loanId: string;
  scheduleType: ScheduleType;
  totalPrincipal: string;
  totalInterest: string;
  totalPaid: string;
  installments: Installment[];
}

export const LOAN_TYPES: LoanType[] = [
  "CONSUMER",
  "CAR_LEASING",
  "MORTGAGE",
  "STUDENT",
  "BUSINESS",
];

export const SCHEDULE_TYPES: ScheduleType[] = ["ANNUITY", "EQUAL_PRINCIPAL", "BULLET"];

export const LOAN_TYPE_LABELS: Record<LoanType, string> = {
  CONSUMER: "Consumer",
  CAR_LEASING: "Car leasing",
  MORTGAGE: "Mortgage",
  STUDENT: "Student",
  BUSINESS: "Business",
};

export const SCHEDULE_TYPE_LABELS: Record<ScheduleType, string> = {
  ANNUITY: "Annuity",
  EQUAL_PRINCIPAL: "Equal principal",
  BULLET: "Bullet",
};
