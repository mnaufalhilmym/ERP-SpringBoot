CREATE TABLE IF NOT EXISTS "subscriptions" (
    "id" UUID PRIMARY KEY,
    "active_from" TIMESTAMPTZ NOT NULL,
    "active_until" TIMESTAMPTZ NOT NULL,
    "type" INT NOT NULL,
    "company_id" UUID NOT NULL REFERENCES "companies"("id") ON DELETE CASCADE,
    "payment_id" UUID NULL REFERENCES "payments"("id") ON DELETE CASCADE
);