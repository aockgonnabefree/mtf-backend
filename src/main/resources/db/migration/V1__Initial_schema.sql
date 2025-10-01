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
    Address_detail_th varchar(255)  NOT NULL,
    Address_detail_en varchar(255) NOT NULL,
    District_th varchar(255) NOT NULL,
    District_en varchar(255) NOT NULL,
    Sub_district_th varchar(255) NOT NULL,
    Sub_district_en varchar(255) NOT NULL,
    Province varchar(255) NOT NULL,
    Postel_code varchar(10) NOT NULL
);

CREATE TABLE EMPLOYER (
    Id varchar(13) PRIMARY KEY,
    Firstname varchar(255) NOT NULL,
    Lastname varchar(255) NOT NULL,
    Business_type varchar(255) NOT NULL,
    Status varchar(255) NOT NULL,
    Address_id varchar(13) NOT NULL,

    Company_name varchar(255),

    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE EMPLOYEE (
    Passport_number varchar(13) PRIMARY KEY,
    Firstname varchar(255) NOT NULL,
    Lastname varchar(255) NOT NULL,
    Nationality varchar(255) NOT NULL,
    Blood_type blood_type NOT NULL,
    Status varchar(255) NOT NULL,
    Address_id varchar(13) NOT NULL,

    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE AGENT (
    Id varchar(13) PRIMARY KEY,
    Firstname varchar(255) NOT NULL,
    Lastname varchar(255) NOT NULL,
    Email varchar(255) NOT NULL,
    Hashed_password varchar(255) NOT NULL,
    Status varchar(255) NOT NULL,
    Address_id varchar(13) NOT NULL,

    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE DOCUMENT (
    Id varchar(13) PRIMARY KEY,
    Type document_type NOT NULL,
    Expiry_date date NOT NULL
);

CREATE TABLE EMPLOYMENT_CONTRACT (
    Id varchar(13) PRIMARY KEY,
    Type_of_work_th varchar(255) NOT NULL,
    Type_of_work_en varchar(255) NOT NULL,
    Income_per_day int NOT NULL,
    Paid_income_at int NOT NULL,
    Period_of_employment_th varchar(255) NOT NULL,
    Period_of_employment_en varchar(255) NOT NULL,
    Working_hour_limit int NOT NULL,
    Working_day_per_week int NOT NULL,
    Day_off_weekly_th varchar(255) NOT NULL,
    Day_off_weekly_en varchar(255) NOT NULL,
    Day_off_holiday_th varchar(255) NOT NULL,
    Day_off_holiday_en varchar(255) NOT NULL,
    Days_annual_leave_th varchar(255) NOT NULL,
    Days_annual_leave_en varchar(255) NOT NULL,
    Overtime_rate_th varchar(255) NOT NULL,
    Overtime_rate_en varchar(255) NOT NULL,
    Holiday_overtime_rate_th varchar(255) NOT NULL,
    Holiday_overtime_rate_en varchar(255) NOT NULL,
    Employee varchar(13) NOT NULL,
    Employer varchar(13) NOT NULL,

    CONSTRAINT fk_employer_id FOREIGN KEY (Employer) REFERENCES Employer (Id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_passport_number FOREIGN KEY (Employee) REFERENCES Employee (Passport_number) ON DELETE CASCADE
);

CREATE TABLE WP_46 (
    Id varchar(13) PRIMARY KEY,
    Financial_status_year int NOT NULL,
    Financial_status_income int NOT NULL,
    Financial_status_tax int NOT NULL,
    Current_income int NOT NULL,
    Income_duration int NOT NULL,
    Type_of_work varchar(255) NOT NULL,
    Nature_of_work varchar(255) NOT NULL,
    Period_of_employment_year int NOT NULL,
    Period_of_employment_month int NOT NULL,
    Period_of_employment_day int NOT NULL,
    Employment_valid_until date NOT NULL,
    Income_per_day int NOT NULL,
    Benefit_per_day int NOT NULL,
    Highes_education highest_education NOT NULL,
    Work_experience int NOT NULL,
    Reason_for_not_employing_thai_person varchar(255) NOT NULL,
    EMP_contract_id varchar(13) NOT NULL,

    CONSTRAINT fk_emp_contract_id FOREIGN KEY (EMP_contract_id) REFERENCES EMPLOYMENT_CONTRACT (Id) ON DELETE CASCADE
);

CREATE TABLE WORK (
    Id varchar(13) PRIMARY KEY,
    Step varchar(255) NOT NULL,
    Detail varchar(255) NOT NULL,
    under_resp_agent varchar(13) NOT NULL,

    CONSTRAINT fk_resp_agent_id FOREIGN KEY (under_resp_agent) REFERENCES AGENT (Id) ON DELETE CASCADE
);

CREATE TABLE WORK_DETAIL (
    Work_id varchar(13) NOT NULL,
    Employee_id varchar(13) NOT NULL,
    Detail varchar(255) NOT NULL,

    PRIMARY KEY (Work_id, Employee_id),
    CONSTRAINT fk_work_id FOREIGN KEY (Work_id) REFERENCES WORK (Id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_passport_number FOREIGN KEY (Employee_id) REFERENCES EMPLOYEE (Passport_number) ON DELETE CASCADE
);

CREATE TABLE BILL (
    Id varchar(13) PRIMARY KEY,
    Price float NOT NULL,
    Status bill_status NOT NULL,
    Created_at timestamp NOT NULL,
    Paid_at timestamp NOT NULL,
    Work_id varchar(13) NOT NULL,

    CONSTRAINT fk_work_id FOREIGN KEY (Work_id) REFERENCES WORK (Id) ON DELETE CASCADE
);