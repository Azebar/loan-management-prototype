package com.lhv.loans.presentation.rest;

import com.lhv.loans.application.service.LoanApplicationService;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.presentation.rest.dto.CreateLoanRequest;
import com.lhv.loans.presentation.rest.dto.LoanResponse;
import com.lhv.loans.presentation.rest.dto.ScheduleResponse;
import com.lhv.loans.presentation.rest.dto.UpdateLoanRequest;
import com.lhv.loans.presentation.rest.mapper.LoanWebMapper;
import com.opencsv.CSVWriter;
import jakarta.validation.Valid;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanApplicationService loans;

    @GetMapping
    public List<LoanResponse> list() {
        return loans.list().stream().map(LoanWebMapper::toResponse).toList();
    }

    @GetMapping("/{loanId}")
    public LoanResponse get(@PathVariable UUID loanId) {
        return LoanWebMapper.toResponse(loans.get(loanId));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody CreateLoanRequest request) {
        var loan = loans.create(LoanWebMapper.toCommand(request));
        return ResponseEntity
                .created(URI.create("/api/loans/" + loan.id()))
                .body(LoanWebMapper.toResponse(loan));
    }

    @PutMapping("/{loanId}")
    public LoanResponse update(@PathVariable UUID loanId, @Valid @RequestBody UpdateLoanRequest request) {
        return LoanWebMapper.toResponse(loans.update(loanId, LoanWebMapper.toCommand(request)));
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> delete(@PathVariable UUID loanId) {
        loans.delete(loanId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{loanId}/schedule")
    public ScheduleResponse schedule(@PathVariable UUID loanId) {
        return LoanWebMapper.toResponse(loans.schedule(loanId));
    }

    @GetMapping(value = "/{loanId}/schedule.csv", produces = "text/csv")
    public ResponseEntity<String> scheduleCsv(@PathVariable UUID loanId) {
        RepaymentSchedule schedule = loans.schedule(loanId);
        StringWriter csvBuffer = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(csvBuffer)) {
            csvWriter.writeNext(new String[]{
                    "period", "dueDate", "principal", "interest", "totalPayment", "remainingBalance"
            });
            for (var installment : schedule.installments()) {
                csvWriter.writeNext(new String[]{
                        Integer.toString(installment.periodNumber()),
                        installment.dueDate().toString(),
                        installment.principalAmount().toPlainString(),
                        installment.interestAmount().toPlainString(),
                        installment.totalPayment().toPlainString(),
                        installment.remainingBalance().toPlainString()
                });
            }
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Failed to render CSV", exception);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header("Content-Disposition",
                        "attachment; filename=\"schedule-" + loanId + ".csv\"")
                .body(csvBuffer.toString());
    }
}
