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

    @GetMapping("/{id}")
    public LoanResponse get(@PathVariable UUID id) {
        return LoanWebMapper.toResponse(loans.get(id));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody CreateLoanRequest req) {
        var loan = loans.create(LoanWebMapper.toCommand(req));
        return ResponseEntity
                .created(URI.create("/api/loans/" + loan.id()))
                .body(LoanWebMapper.toResponse(loan));
    }

    @PutMapping("/{id}")
    public LoanResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateLoanRequest req) {
        return LoanWebMapper.toResponse(loans.update(id, LoanWebMapper.toCommand(req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        loans.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/schedule")
    public ScheduleResponse schedule(@PathVariable UUID id) {
        return LoanWebMapper.toResponse(loans.schedule(id));
    }

    @GetMapping(value = "/{id}/schedule.csv", produces = "text/csv")
    public ResponseEntity<String> scheduleCsv(@PathVariable UUID id) {
        RepaymentSchedule s = loans.schedule(id);
        StringWriter sw = new StringWriter();
        try (CSVWriter csv = new CSVWriter(sw)) {
            csv.writeNext(new String[]{
                    "period", "dueDate", "principal", "interest", "totalPayment", "remainingBalance"
            });
            for (var i : s.installments()) {
                csv.writeNext(new String[]{
                        Integer.toString(i.periodNumber()),
                        i.dueDate().toString(),
                        i.principalAmount().toPlainString(),
                        i.interestAmount().toPlainString(),
                        i.totalPayment().toPlainString(),
                        i.remainingBalance().toPlainString()
                });
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Failed to render CSV", e);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header("Content-Disposition",
                        "attachment; filename=\"schedule-" + id + ".csv\"")
                .body(sw.toString());
    }
}
