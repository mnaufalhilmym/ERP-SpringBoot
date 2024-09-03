CREATE TABLE IF NOT EXISTS "storage" (
    "id" UUID PRIMARY KEY,
    "parent_folder_id" UUID NULL REFERENCES "storage"("id") ON DELETE CASCADE,
    "project_id" UUID NULL REFERENCES "projects"("id") ON DELETE CASCADE,
    "type" INT NOT NULL,
    "name" TEXT NOT NULL,
    "mime_type" TEXT NULL,
    "size" BIGINT NOT NULL,
    "user_id" UUID NOT NULL REFERENCES "users"("id") ON DELETE CASCADE,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);