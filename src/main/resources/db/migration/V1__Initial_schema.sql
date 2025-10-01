DROP TABLE IF EXISTS employer CASCADE;

DROP TABLE IF EXISTS employee CASCADE;

DROP TABLE IF EXISTS address CASCADE;

DROP TABLE IF EXISTS AGENT CASCADE;

DROP TABLE IF EXISTS EMPLOYMENT_CONTRACT CASCADE;

DROP TABLE IF EXISTS WP_46 CASCADE;

DROP TABLE IF EXISTS WORK CASCADE;

DROP TABLE IF EXISTS WORK_DETAIL CASCADE;

DROP TABLE IF EXISTS BILL;

DROP TYPE IF EXISTS bloodtype CASCADE;

DROP TYPE IF EXISTS document_type CASCADE;

DROP TYPE IF EXISTS highest_education CASCADE;

DROP TYPE IF EXISTS bill_status CASCADE;

CREATE TYPE blood_type AS enum (
    'A',
    'B',
    'O',
    'AB'
);

CREATE TYPE document_type AS enum (
    'ใบรับรองแพทย์',
    'ใบอนุญาตทำงาน',
    'ประกันสุขภาพ',
    'เอกสาร CI',
    'บัตรชมพู'
);

CREATE TYPE highest_education AS enum (
    'ประถมศึกษา',
    'มัธยมศึกษา',
    'ปริญญาตรี',
    'ปริญญาโท',
    'ปริญญาเอก'
);

CREATE TYPE bill_status AS enum (
    'PAID',
    'NOT_PAID'
);

CREATE TABLE ADDRESS (
    Id varchar(13) PRIMARY KEY,
    Address_detail_th varchar(255),
    Address_detail_en varchar(255),
    District_th varchar(255),
    District_en varchar(255),
    Sub_district_th varchar(255),
    Sub_district_en varchar(255),
    Province varchar(255),
    Postel_code varchar(10)
);

CREATE TABLE EMPLOYER (
    Id varchar(13) PRIMARY KEY,
    Firstname varchar(255),
    Lastname varchar(255),
    Company_name varchar(255),
    Business_type varchar(255),
    Status varchar(255),
    Address_id varchar(13),

    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE EMPLOYEE (
    Passport_number varchar(13) PRIMARY KEY,
    Firstname varchar(255),
    Lastname varchar(255),
    Nationality varchar(255),
    Blood_type blood_type,
    Status varchar(255),
    Address_id varchar(13),

    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE AGENT (
    Id varchar(13) PRIMARY KEY,
    Firstname varchar(255),
    Lastname varchar(255),
    Email varchar(255),
    Hashed_password varchar(255),
    Status varchar(255),
    Address_id varchar(13),

    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE DOCUMENT (
    Id varchar(13) PRIMARY KEY,
    Type document_type,
    Expiry_date date
);

CREATE TABLE EMPLOYMENT_CONTRACT (
    Id varchar(13) PRIMARY KEY,
    Type_of_work_th varchar(255),
    Type_of_work_en varchar(255),
    Income_per_day int,
    Paid_income_at int,
    Period_of_employment_th varchar(255),
    Period_of_employment_en varchar(255),
    Working_hour_limit int,
    Working_day_per_week int,
    Day_off_weekly_th varchar(255),
    Day_off_weekly_en varchar(255),
    Day_off_holiday_th varchar(255),
    Day_off_holiday_en varchar(255),
    Days_annual_leave_th varchar(255),
    Days_annual_leave_en varchar(255),
    Overtime_rate_th varchar(255),
    Overtime_rate_en varchar(255),
    Holiday_overtime_rate_th varchar(255),
    Holiday_overtime_rate_en varchar(255),
    Employee varchar(13),
    Employer varchar(13),

    CONSTRAINT fk_employer_id FOREIGN KEY (Employer) REFERENCES Employer (Id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_passport_number FOREIGN KEY (Employee) REFERENCES Employee (Passport_number) ON DELETE CASCADE
);

CREATE TABLE WP_46 (
    Id varchar(13) PRIMARY KEY,
    Financial_status_year int,
    Financial_status_income int,
    Financial_status_tax int,
    Current_income int,
    Income_duration int,
    Type_of_work varchar(255),
    Nature_of_work varchar(255),
    Period_of_employment_year int,
    Period_of_employment_month int,
    Period_of_employment_day int,
    Employment_valid_until date,
    Income_per_day int,
    Benefit_per_day int,
    Highes_education highest_education,
    Work_experience int,
    Reason_for_not_employing_thai_person varchar(255),
    EMP_contract_id varchar(13),

    CONSTRAINT fk_emp_contract_id FOREIGN KEY (EMP_contract_id) REFERENCES EMPLOYMENT_CONTRACT (Id) ON DELETE CASCADE
);

CREATE TABLE WORK (
    Id varchar(13) PRIMARY KEY,
    Step varchar(255),
    -- Detail varchar(255),
    under_resp_agent varchar(13),

    CONSTRAINT fk_resp_agent_id FOREIGN KEY (under_resp_agent) REFERENCES AGENT (Id) ON DELETE CASCADE
);

CREATE TABLE WORK_DETAIL (
    Work_id varchar(13),
    Employee_id varchar(13),
    PRIMARY KEY (Work_id, Employee_id),
    Detail varchar(255),

    CONSTRAINT fk_work_id FOREIGN KEY (Work_id) REFERENCES WORK (Id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_passport_number FOREIGN KEY (Employee_id) REFERENCES EMPLOYEE (Passport_number) ON DELETE CASCADE
);

CREATE TABLE BILL (
    Id varchar(13) PRIMARY KEY,
    Price float,
    Status bill_status,
    Created_at timestamp,
    Paid_at timestamp,
    Work_id varchar(13),

    CONSTRAINT fk_work_id FOREIGN KEY (Work_id) REFERENCES WORK (Id) ON DELETE CASCADE
);