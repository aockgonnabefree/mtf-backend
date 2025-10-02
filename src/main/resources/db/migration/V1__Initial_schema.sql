CREATE TYPE blood_type AS enum (
    'A',
    'B',
    'O',
    'AB'
);

CREATE TYPE active_status_type AS enum (
    'ACTIVE',
    'INACTIVE'
);

CREATE TYPE nationality AS enum (
    'เมียนมา',
    'กัมพูชา',
    'ลาว'
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
    District_th varchar(255) NOT NULL,
    Sub_district_th varchar(255) NOT NULL,
    Province_th varchar(255) NOT NULL,
    Postal_code varchar(5) NOT NULL,

    Address_detail_en varchar(255),
    District_en varchar(255),
    Sub_district_en varchar(255),
    Province_en varchar(255)
);

CREATE TABLE EMPLOYER (
    Id varchar(13) PRIMARY KEY,
    Firstname varchar(255) NOT NULL,
    Lastname varchar(255) NOT NULL,
    Business_type varchar(255) NOT NULL,
    Financial_status_year int NOT NULL,
    Financial_status_income int NOT NULL,
    Financial_status_tax int NOT NULL,
    Current_income int NOT NULL,
    Income_duration int NOT NULL,
    Status active_status_type NOT NULL,

    Company_name varchar(255),

    Address_id varchar(13) NOT NULL,
    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE EMPLOYEE (
    Passport_number varchar(13) PRIMARY KEY,
    Firstname varchar(255) NOT NULL,
    Lastname varchar(255) NOT NULL,
    Nationality nationality NOT NULL,
    Blood_type blood_type NOT NULL,
    Status active_status_type NOT NULL,

    Address_id varchar(13) NOT NULL,
    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE AGENT (
    Id varchar(13) PRIMARY KEY,
    Firstname varchar(255) NOT NULL,
    Lastname varchar(255) NOT NULL,
    Email varchar(255) NOT NULL,
    Hashed_password varchar(255) NOT NULL,
    Status active_status_type NOT NULL,

    Address_id varchar(13) NOT NULL,
    CONSTRAINT fk_address_id FOREIGN KEY (Address_id) REFERENCES ADDRESS (Id) ON DELETE CASCADE
);

CREATE TABLE DOCUMENT (
    Id varchar(13) PRIMARY KEY,
    Type document_type NOT NULL,
    Expiry_date date NOT NULL,

    Employee_id varchar(13) NOT NULL,
    CONSTRAINT fk_employee_passport_number FOREIGN KEY (Employee_id) REFERENCES Employee (Passport_number) ON DELETE CASCADE
);

CREATE TABLE EMPLOYMENT (
    Employer_id varchar(13),
    Employee_id varchar(13),
    Status active_status_type NOT NULL,

    PRIMARY KEY (Employer_id, Employee_id),
    CONSTRAINT fk_employer_id FOREIGN KEY (Employer_id) REFERENCES Employer (Id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_passport_number FOREIGN KEY (Employee_id) REFERENCES Employee (Passport_number) ON DELETE CASCADE
);

CREATE TABLE EMPLOYMENT_CONTRACT (
    Id varchar(13) PRIMARY KEY,
    Type_of_work_th varchar(255) NOT NULL,
    Type_of_work_en varchar(255) NOT NULL,
    Income_per_day int NOT NULL,
    Paid_income_at int NOT NULL,
    Working_hour_limit int NOT NULL,
    Working_day_per_week int NOT NULL,
    Employment_period_month int NOT NULL,
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

    Employer_id varchar(13) NOT NULL,
    Employee_id varchar(13) NOT NULL,
    CONSTRAINT fk_employment_contract_employment FOREIGN KEY (Employer_id, Employee_id) REFERENCES EMPLOYMENT (Employer_id, Employee_id) ON DELETE CASCADE
);

CREATE TABLE WP_46 (
    Id varchar(13) PRIMARY KEY,
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
    Created_at timestamp NOT NULL,

    Employer_id varchar(13) NOT NULL,
    Employee_id varchar(13) NOT NULL,
    CONSTRAINT fk_employment_wp_46 FOREIGN KEY (Employer_id, Employee_id) REFERENCES EMPLOYMENT (Employer_id, Employee_id) ON DELETE CASCADE
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