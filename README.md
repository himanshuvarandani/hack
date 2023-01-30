# HACK
HACK is an application created for HACK Employees and HR. This application let them check their profile and project details. Also, they can give their daily updates.

This is an assignment for full stack program. Here HACK comes from teammates first letter.

### HACK Main Features
 - Provides authentication and authorization on specific pages
 - Provides rights to Admin to initialize data for pprojects and their respective HR's (one Project has only one HR).
 - Shows HR project details and daily updates to HR. HR can give daily updates.
 - Provides rights to HR to initialize Employees data for their project and create accounts.
 - Shows Employee project details, HR details and daily updates to Employee. Employee can give daily updates.
 - Shows all project employee details to HR and also their daily updates.
 
### Prerequisites
 - Java version 19
 - Mysql running on port 3306
 
### Installation
 #### Import on Spring Tool Suite
  - Go to File -> Import
  - Choose Git -> Projects from Git (smart import) and click on Next
  - Choose CloneURI and click on Next
  - Paste this URL (https://github.com/himanshuvarandani/hack.git) and click on Next -> Next -> Next -> Finish
 
 #### Database
  - Start Mysql on 3306 port
  - Create database named 'hack'
  - Update your database 'username' and 'password' in 'application.properties' file in 'src/main/repository'
 
 #### Run Application
  - Run application as 'Java Application'
  - Tables will be created on your database
 
 #### Initialize Admin Data
  - Run below sql command on your database to add admin credentials
    ```
      INSERT INTO USER VALUES (5, 'admin@gmail.com', '$2a$10$yEtk7yiD9lTVWz9.wT8KtuXjwCHjTE8KkTT6IqJhZ3D3I4B8Uw3O6', 'ROLE_ADMIN', 'admin');
    ```
 
 #### Login as Admin and Initialize Application Data
  - Go to 'http://localhost:8080/login' and give below credentials
    ```
      Username: admin
      Password: password
    ```
  - Go to 'Initialize Data' and upload 'HACKInitialData.xlsx' from 'src/main/resources/static/assets' and click on submit
  - Update data as you want and check the passwords for HR
  - Click on logout
 
 #### Login as HR
  - Go to Login Page and give HR credentials as initialized above
  - Check all features
  - Go to 'AddEmployees' Link on '/hr' page
  - Upload 'HACKProjectEmployeeData.xlsx' from 'src/main/resources/static/assets' and click on submit
  - Update data as you want and check the passwords for Employee
  - Click on logout
 
 #### Login as Employee
  - Go to Login Page and give Employee credentials as initialized above
  - Check all features
 
