create table loans (
    id                              uuid           primary key,
    borrower_name                   varchar(200)   not null,
    type                            varchar(32)    not null,
    amount                          numeric(19, 2) not null,
    term_months                     integer        not null,
    annual_interest_rate_percent    numeric(7, 4)  not null,
    schedule_type                   varchar(32)    not null,
    start_date                      date           not null,
    created_at                      timestamptz    not null,
    updated_at                      timestamptz    not null
);

create index loans_created_at_idx on loans (created_at desc);
