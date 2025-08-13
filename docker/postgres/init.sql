-- CONNECTION: connect to jobseekerdb 
\c jobseekerdb;

-- uuid-ossp extension: functions to generate UUIDs (primary keys)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- custom schemas
-- CREATE SCHEMA IF NOT EXISTS jobseeker_auth;
-- CREATE SCHEMA IF NOT EXISTS jobseeker_jobs;

-- default datas
-- Example: Create default roles for authentication
-- INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_RECRUITER');

-- Example: Create a default admin user
-- INSERT INTO users (username, email, password, created_at) 
-- VALUES ('admin', 'admin@jobseeker.com', '$2a$10$...', NOW());

-- permission grants
GRANT ALL PRIVILEGES ON DATABASE jobseekerdb TO "secret-regulator";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "secret-regulator";

-- for auto-increment IDs
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO "secret-regulator";

-- future tables/sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO "secret-regulator";
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO "secret-regulator";

\echo 'Database initialization completed successfully!'