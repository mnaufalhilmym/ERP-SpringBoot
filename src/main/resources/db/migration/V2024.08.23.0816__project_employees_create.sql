CREATE TABLE IF NOT EXISTS "project_employees" (
    "project_id" UUID NOT NULL REFERENCES "projects"("id") ON DELETE CASCADE,
    "employee_id" UUID NOT NULL REFERENCES "employees"("id") ON DELETE CASCADE,
    PRIMARY KEY ("project_id", "employee_id")
);