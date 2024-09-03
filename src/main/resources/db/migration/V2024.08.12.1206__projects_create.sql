CREATE TABLE IF NOT EXISTS "projects" (
    "id" UUID PRIMARY KEY,
    "company_id" UUID NOT NULL REFERENCES "companies"("id") ON DELETE CASCADE,
    "name" TEXT NOT NULL,
    "client" TEXT NULL,
    "client_email" TEXT NULL,
    "category" TEXT NULL,
    "omzet" NUMERIC NULL,
    "start_date" DATE NULL,
    "end_date" DATE NULL,
    "status" INT NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);