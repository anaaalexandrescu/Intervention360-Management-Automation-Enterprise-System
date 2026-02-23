# Intervention360 - Management & Automation Enterprise System

## 1. Overview
Intervention360 is a comprehensive enterprise web application built using Java and the Spring Boot framework. It is designed to digitalize, automate, and streamline the workflow of service companies. The system acts as a centralized hub for tracking technical interventions, managing client relationships, monitoring material inventory, generating statistics, and automating the billing process.

## 2. System Architecture
The application follows a robust Model-View-Controller (MVC) architectural pattern:
* *Backend Framework*: Spring Boot handles the core configuration, dependency injection, and RESTful/MVC routing.
* *Data Persistence*: Spring Data JPA and Hibernate map Java objects to relational database tables, abstracting complex SQL queries.
* *Frontend Rendering*: Thymeleaf serves as the server-side template engine, dynamically rendering HTML pages using data injected by the Spring controllers.
* *Build Tool*: Apache Maven manages project dependencies and the build lifecycle.

## 3. Key Features and Technical Implementation

### Authentication and Role Management
The system implements secure access control to differentiate between staff members and customers:
* *Login & Authentication*: Managed by the LoginController and AuthService, which validate credentials and initialize user sessions.
* *Role-Based Routing*: Employees and administrators are directed to the main operational dashboard, whereas clients are routed to a restricted client_dashboard providing a read-only view of their specific intervention history.

### Intervention Lifecycle Management
At the core of the application is the intervention tracking system:
* *Creation and Assignment*: Staff can register new service operations via the adauga_interventie interface, linking them to specific clients and assigning necessary tasks.
* *Services and Materials Mapping*: An intervention is not a static record; it dynamically aggregates multiple services (Serviciu entity) and physical items used (Material entity).
* *Composite Key Relationships*: The connection between interventions and materials is handled via the InterventieMateriale entity, which uses a composite primary key (InterventieMaterialeId) to accurately track the exact quantity of specific materials consumed per job.

### Client Relationship Management (CRM)
The application acts as a CRM tailored for service providers:
* *Client Database*: The vizualizare_clienti view allows administrators to manage customer profiles, retrieve contact details, and view the historical log of operations associated with each client.
* *Dedicated Client Portal*: Through the ClientDashboardController, end-users can log in to check the status, details, and costs of the interventions performed at their locations.

### Inventory and Resource Tracking
* *Material Management*: The system tracks the stock of physical components used during field operations. The edit_materials functionality allows technicians or managers to update the bill of materials consumed for an ongoing or completed intervention, ensuring inventory accuracy.

### Analytics and Financial Operations
* *Business Intelligence Statistics*: The StatisticiController aggregates data from the repositories to generate real-time metrics and reports. These insights are visualized on the statistici dashboard, helping management track operational efficiency and revenue.
* *Automated Invoicing*: Upon completion of an intervention, the system dynamically generates a financial document (invoice.html). The invoice aggregates the base costs of the labor (services) and the unit prices of the consumed materials, providing a fully automated billing solution ready for the client.
