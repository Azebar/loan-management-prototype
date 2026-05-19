package com.lhv.loans.presentation.rest.csv;

import com.lhv.loans.domain.model.RepaymentSchedule;
import com.opencsv.CSVWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.springframework.stereotype.Component;

@Component
public class ScheduleCsvWriter {

    private static final String[] HEADER = {
            "period", "dueDate", "principal", "interest", "totalPayment", "remainingBalance"
    };

    public String write(RepaymentSchedule schedule) {
        StringWriter csvBuffer = new StringWriter();
        try (CSVWriter writer = new CSVWriter(csvBuffer)) {
            writer.writeNext(HEADER);
            for (var installment : schedule.installments()) {
                writer.writeNext(new String[]{
                        Integer.toString(installment.periodNumber()),
                        installment.dueDate().toString(),
                        installment.principalAmount().toPlainString(),
                        installment.interestAmount().toPlainString(),
                        installment.totalPayment().toPlainString(),
                        installment.remainingBalance().toPlainString()
                });
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to render CSV", exception);
        }
        return csvBuffer.toString();
    }
}
