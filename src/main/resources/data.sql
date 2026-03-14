-- 1. Initialize Roles
MERGE INTO roles (role_id, role_name) KEY(role_name) VALUES (1, 'ADMIN');
MERGE INTO roles (role_id, role_name) KEY(role_name) VALUES (2, 'CUSTOMER');
MERGE INTO roles (role_id, role_name) KEY(role_name) VALUES (3, 'UNDERWRITER');
MERGE INTO roles (role_id, role_name) KEY(role_name) VALUES (4, 'CLAIMS_OFFICER');

-- 2. Initialize Admin User (Password: admin123)
MERGE INTO users (user_id, company_name, email, full_name, is_active, password, phone) 
KEY(email) 
VALUES (1, 'RK Events', 'admin@eventguard.com', 'Rahul Kumar', true, '$2a$10$ELopHLaLJ2sTob3MamrG4eGjirL0lldKBjBkwGxFl53kGAmGVmgei', '9876543210');

-- 3. Link Admin to Role
MERGE INTO user_role (user_id, role_id) KEY(user_id, role_id) VALUES (1, 1);

-- 4. Initialize 10 Professional Policies (explicit columns to prevent errors)
-- MUSIC POLICIES
MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (1, 3.0, 'Small gig liability for local cafe or pub performances.', 'OUTDOOR_MUSIC_CONCERT', true, 50000.0, 'Micro Gigs (Music)', false, false, true, false);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (2, 5.0, 'Basic liability for local park concerts or small venues.', 'OUTDOOR_MUSIC_CONCERT', true, 200000.0, 'Silver Guard (Music)', false, false, true, false);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (3, 8.0, 'Balanced coverage including instrument and equipment theft.', 'OUTDOOR_MUSIC_CONCERT', true, 1000000.0, 'Gold Secure (Music)', false, true, true, false);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (4, 12.0, 'Comprehensive protection for major outdoor festivals.', 'OUTDOOR_MUSIC_CONCERT', true, 5000000.0, 'Platinum Elite (Music)', true, true, true, true);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (5, 15.0, 'The ultimate stadium-level shield including stage failure.', 'OUTDOOR_MUSIC_CONCERT', true, 10000000.0, 'Starlight Protection (Music)', true, true, true, true);

-- CORPORATE POLICIES
MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (6, 2.5, 'Basic seminar liability for internal company workshops.', 'CORPORATE_TECH_CONFERENCE', true, 100000.0, 'Micro Seminar (Corp)', false, false, false, false);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (7, 4.0, 'Standard liability for industry conferences and networking events.', 'CORPORATE_TECH_CONFERENCE', true, 500000.0, 'Silver Guard (Corp)', false, false, true, false);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (8, 7.0, 'High-value equipment and venue damage protection.', 'CORPORATE_TECH_CONFERENCE', true, 2500000.0, 'Gold Secure (Corp)', true, false, true, false);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (9, 10.0, 'Full protection for international tech expos and keynotes.', 'CORPORATE_TECH_CONFERENCE', true, 10000000.0, 'Platinum Elite (Corp)', true, true, true, true);

MERGE INTO policies (policy_id, base_rate, description, domain, is_active, max_coverage_amount, policy_name, covers_cancelation, covers_fire, covers_theft, covers_weather) 
KEY(policy_name) VALUES (10, 14.0, 'Maximum enterprise coverage including data and IP security.', 'CORPORATE_TECH_CONFERENCE', true, 25000000.0, 'Global Expo Shield (Corp)', true, true, true, true);

-- 5. Sync Sequences
ALTER TABLE users ALTER COLUMN user_id RESTART WITH (SELECT MAX(user_id) + 1 FROM users);
ALTER TABLE roles ALTER COLUMN role_id RESTART WITH (SELECT MAX(role_id) + 1 FROM roles);
ALTER TABLE policies ALTER COLUMN policy_id RESTART WITH (SELECT MAX(policy_id) + 1 FROM policies);
