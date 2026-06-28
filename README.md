<div align="center">

<img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
<img src="https://img.shields.io/badge/Angular-17-DD0031?style=for-the-badge&logo=angular&logoColor=white" />
<img src="https://img.shields.io/badge/PostgreSQL-Neon-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" />
<img src="https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
<img src="https://img.shields.io/badge/OAuth2-Google%20%7C%20GitHub-4285F4?style=for-the-badge&logo=google&logoColor=white" />
<img src="https://img.shields.io/badge/Deployed-Vercel%20%7C%20Render-000000?style=for-the-badge&logo=vercel&logoColor=white" />
<img src="https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white" />

<br/>
<br/>

# SecureAI Assistant

### A production-grade, full-stack AI chat platform built with enterprise-level security, OAuth2 social login, OTP-based email verification, voice input, and persistent chat history.

**[Live Demo](https://secure-ai-assistant-roan.vercel.app)** · **[Backend API](https://secure-ai-assistant-backend.onrender.com)** · **[GitHub Repository](https://github.com/Raman-8688/secure-ai-assistant)**

</div>

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Tech Stack](#tech-stack)
4. [System Architecture](#system-architecture)
5. [Authentication & Security Flow](#authentication--security-flow)
6. [Database Schema](#database-schema)
7. [API Reference](#api-reference)
8. [Project Structure](#project-structure)
9. [Environment Configuration](#environment-configuration)
10. [Local Development Setup](#local-development-setup)
11. [Deployment Architecture](#deployment-architecture)
12. [Screenshots](#screenshots)

---

## Project Overview

SecureAI Assistant is a full-stack AI-powered chat application engineered to demonstrate enterprise-level security patterns in a modern web stack. It integrates Spring Security with stateless JWT authentication, multi-provider OAuth2 social login (Google & GitHub), OTP-based email verification via SMTP, and a real-time AI conversation interface powered by OpenAI-compatible LLMs.

The frontend is built with Angular 17 (standalone components), featuring a dark/light theme toggle, voice-to-text chat input via the Web Speech API, animated UI transitions, and a persistent sidebar for chat history. The backend exposes a clean RESTful API following layered architecture principles — Controller → Service → Repository → Entity.

This project was built to reflect real-world production patterns: environment-based configuration profiles, secrets externalized via environment variables, Docker containerization, CI/CD-friendly deployments on Render (backend) and Vercel (frontend), and a cloud-hosted PostgreSQL database on Neon.

---

## Key Features

### Security & Authentication
- Stateless JWT authentication with configurable expiration
- BCrypt password hashing via Spring Security's `PasswordEncoder`
- OTP-based email verification on registration (6-digit OTP, time-limited)
- Secure password reset flow with expiring reset tokens and rate limiting
- OAuth2 social login with Google (GitHub-ready) using a custom `OAuth2AuthenticationSuccessHandler`
- Custom `JwtAuthenticationFilter` integrated into the Spring Security filter chain
- Global exception handling with structured `ErrorResponse` payloads

### AI Chat
- Real-time Q&A powered by an OpenAI-compatible API (configurable endpoint)
- Per-user chat history stored in PostgreSQL, retrieved in descending order
- Individual chat history item deletion (ownership-verified before delete)
- Voice-to-text input using the browser's Web Speech API

### Frontend (Angular 17)
- Standalone component architecture (no NgModules)
- Global dark/light theme system with `localStorage` persistence
- Reactive forms with client-side validation
- Book-style animated login panel with feature highlights
- Chat history sidebar with scroll and delete capability
- Responsive layout with Tailwind-inspired utility classes

---

## Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Backend Language | Java 21 | Core application runtime |
| Backend Framework | Spring Boot 3.x | REST API, DI, autoconfiguration |
| Security | Spring Security 6 | Authentication, authorization, filter chain |
| Authentication | JWT (jjwt) | Stateless token-based auth |
| Social Login | OAuth2 (Spring Security) | Google / GitHub login |
| Email | Spring Mail (SMTP) | OTP & password reset emails |
| AI Integration | OpenAI-compatible API | Chat completions (GPT-3.5-turbo) |
| ORM | Spring Data JPA / Hibernate | Database abstraction |
| Database | PostgreSQL (Neon cloud) | Persistent user & chat data |
| Frontend | Angular 17 | SPA framework |
| Frontend Styling | SCSS + CSS Variables | Theme system |
| Voice Input | Web Speech API | Voice-to-text chat |
| Containerization | Docker | Backend packaging |
| Frontend Hosting | Vercel | CDN-based deployment |
| Backend Hosting | Render | Docker container hosting |
| Build Tool | Maven | Backend dependency management |

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                            │
│                                                                 │
│   Angular 17 SPA (Vercel CDN)                                   │
│   ┌────────────┐  ┌─────────────┐  ┌──────────────────────┐    │
│   │ Login/     │  │  Chat UI    │  │  Chat History        │    │
│   │ Register   │  │  + Voice    │  │  Sidebar             │    │
│   └────────────┘  └─────────────┘  └──────────────────────┘    │
└─────────────────────────┬───────────────────────────────────────┘
                          │ HTTPS + Bearer JWT
┌─────────────────────────▼───────────────────────────────────────┐
│                      API GATEWAY LAYER                          │
│                                                                 │
│   Spring Security Filter Chain                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │  JwtAuthenticationFilter → SecurityConfig → CORS        │   │
│   └─────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────────┐
│                     APPLICATION LAYER                           │
│                                                                 │
│  ┌──────────────────┐          ┌───────────────────────┐        │
│  │  AuthController  │          │     AIController      │        │
│  │  /api/auth/**    │          │     /api/ai/**        │        │
│  └────────┬─────────┘          └────────────┬──────────┘        │
│           │                                 │                   │
│  ┌────────▼─────────┐          ┌────────────▼──────────┐        │
│  │   AuthService    │          │       AIService        │        │
│  │   JwtService     │          │  (OpenAI REST client) │        │
│  │   EmailService   │          └───────────────────────┘        │
│  └────────┬─────────┘                                           │
└───────────┼─────────────────────────────────────────────────────┘
            │
┌───────────▼─────────────────────────────────────────────────────┐
│                      DATA LAYER                                 │
│                                                                 │
│   Spring Data JPA → Hibernate → PostgreSQL (Neon)               │
│   ┌──────────────┐          ┌──────────────────────┐            │
│   │  users table │          │  chat_history table  │            │
│   └──────────────┘          └──────────────────────┘            │
└─────────────────────────────────────────────────────────────────┘
            │                           │
            ▼                           ▼
     SMTP (Gmail)              OpenAI-compatible
     Email Service             LLM API endpoint
```

---

## Authentication & Security Flow

### Registration + Email Verification

```
User fills Register Form
        │
        ▼
POST /api/auth/register
        │
        ├── Validate request body (@Valid)
        ├── Check if email already exists
        ├── Hash password with BCrypt
        ├── Generate 6-digit OTP + set expiry (LocalDateTime)
        ├── Save User (emailVerified = false)
        └── Send OTP via SMTP (Spring Mail)
                │
                ▼
User receives OTP email
        │
        ▼
POST /api/auth/verify-email  { email, otp }
        │
        ├── Validate OTP match
        ├── Check OTP not expired
        ├── Set emailVerified = true
        └── Clear OTP fields
```

### JWT Login Flow

```
POST /api/auth/login  { email, password }
        │
        ├── Load user from DB
        ├── Check emailVerified == true
        ├── BCrypt.matches(rawPassword, hashedPassword)
        ├── Update lastLogin timestamp
        └── Generate signed JWT (HS256, configurable expiry)
                │
                ▼
        Client stores JWT
                │
        Every subsequent request:
                │
                ▼
GET/POST /api/**  [Authorization: Bearer <token>]
        │
        ▼
JwtAuthenticationFilter
        │
        ├── Extract token from Authorization header
        ├── Validate signature + expiry (JwtService)
        ├── Load UserDetails from DB
        └── Set SecurityContextHolder → request proceeds
```

### OAuth2 Social Login

```
User clicks "Continue with Google"
        │
        ▼
Spring Security redirects → Google OAuth consent screen
        │
        ▼
Google returns authorization code → /login/oauth2/code/google
        │
        ▼
CustomOAuth2UserService.loadUser()
        │
        ├── Extract email, name, providerId from OAuth2User
        ├── Check if user exists in DB
        │     ├── Exists → update lastLogin, provider fields
        │     └── New → create user (emailVerified=true, no password)
        └── Return CustomUserPrincipal
                │
                ▼
OAuth2AuthenticationSuccessHandler
        │
        ├── Generate JWT for the OAuth2 user
        └── Redirect to frontend with token as query param
```

### Password Reset Flow

```
POST /api/auth/forgot-password  { email }
        │
        ├── Check email registered
        ├── Rate limit check (passwordResetAttempts)
        ├── Generate UUID reset token + set expiry
        └── Send reset link via SMTP
                │
                ▼
GET /api/auth/validate-reset-token?token=xxx
        │
        └── Check token exists + not expired
                │
                ▼
POST /api/auth/reset-password  { token, newPassword }
        │
        ├── Validate token
        ├── Hash new password
        ├── Clear reset token fields
        └── Update lastPasswordReset
```

---

## Database Schema

### `users` table

| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PK | Auto-increment |
| name | VARCHAR | NOT NULL | Display name |
| email | VARCHAR | NOT NULL, UNIQUE | Used as username |
| password | VARCHAR | NULLABLE | Null for OAuth2 users |
| role | VARCHAR | NOT NULL | Default: `USER` |
| email_verified | BOOLEAN | NOT NULL | Default: `false` |
| verification_otp | VARCHAR | NULLABLE | 6-digit OTP |
| otp_expiry_time | TIMESTAMP | NULLABLE | OTP expiration |
| created_at | TIMESTAMP | NOT NULL | Set via `@PrePersist` |
| provider | VARCHAR | NULLABLE | `google`, `github`, or null |
| provider_id | VARCHAR | NULLABLE | OAuth2 provider user ID |
| last_login | TIMESTAMP | NULLABLE | Updated on each login |
| reset_token | VARCHAR | NULLABLE | UUID for password reset |
| reset_token_expiry | TIMESTAMP | NULLABLE | Token expiration |
| last_password_reset | TIMESTAMP | NULLABLE | Audit field |
| password_reset_attempts | INTEGER | NULLABLE | Rate limiting |
| reset_token_generated_at | TIMESTAMP | NULLABLE | Rate limiting |

### `chat_history` table

| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PK | Auto-increment |
| user_email | VARCHAR | NOT NULL | Links to users.email |
| question | TEXT | NOT NULL | User's prompt |
| answer | TEXT | NOT NULL | AI response |
| created_at | TIMESTAMP | NOT NULL | Default: now() |

---

## API Reference

### Authentication Endpoints

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register new user, sends OTP |
| POST | `/api/auth/verify-email` | No | Verify OTP, activate account |
| POST | `/api/auth/login` | No | Login, returns JWT |
| POST | `/api/auth/resend-otp` | No | Resend verification OTP |
| GET | `/api/auth/me` | JWT | Get current user profile |
| POST | `/api/auth/forgot-password` | No | Send password reset link |
| GET | `/api/auth/validate-reset-token` | No | Validate reset token |
| POST | `/api/auth/reset-password` | No | Set new password |

### AI Endpoints

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/api/ai/ask` | JWT | Send a question, get AI response, save to history |
| GET | `/api/ai/history` | JWT | Get current user's chat history (desc order) |
| DELETE | `/api/ai/history/{id}` | JWT | Delete a specific chat history item |

### Request / Response Examples

**POST /api/auth/register**
```json
// Request
{
  "name": "Raman Boya",
  "email": "raman@example.com",
  "password": "securePass123"
}

// Response 200
{ "message": "Registration successful. Please verify your email." }

// Response 400
{ "error": "Email already registered." }
```

**POST /api/auth/login**
```json
// Request
{ "email": "raman@example.com", "password": "securePass123" }

// Response 200
{ "token": "eyJhbGciOiJIUzI1NiJ9...", "message": "Login successful" }

// Response 401
{ "error": "Invalid credentials or email not verified." }
```

**POST /api/ai/ask**
```json
// Request (Authorization: Bearer <token>)
{ "question": "Explain microservices architecture." }

// Response 200
{ "answer": "Microservices is an architectural style that structures an application as..." }
```

---

## Project Structure

```
secure-ai-assistant/
│
├── backend/                                  (Spring Boot - Java 21)
│   └── src/main/java/com/example/aiapp/aiapi/
│       ├── AiapiApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java           # Spring Security, CORS, filter chain
│       │   ├── JwtAuthenticationFilter.java  # JWT extraction & validation filter
│       │   ├── OAuth2AuthenticationSuccessHandler.java
│       │   └── PasswordEncoderConfig.java
│       ├── controller/
│       │   ├── AuthController.java           # /api/auth/**
│       │   └── AIController.java             # /api/ai/**
│       ├── service/
│       │   ├── AuthService.java              # Registration, login, OTP, reset
│       │   ├── JwtService.java               # Token generation & validation
│       │   ├── AIService.java                # OpenAI API client
│       │   ├── EmailService.java             # Email abstraction
│       │   ├── SendGridEmailService.java      # SMTP implementation
│       │   └── CustomOAuth2UserService.java  # OAuth2 user loading
│       ├── entity/
│       │   ├── User.java                     # users table
│       │   └── ChatHistory.java              # chat_history table
│       ├── repository/
│       │   ├── UserRepository.java
│       │   └── ChatHistoryRepository.java
│       ├── dto/
│       │   ├── requests/                     # RegisterRequest, LoginRequest, etc.
│       │   └── response/                     # AuthResponse, AIResponse, etc.
│       ├── exception/
│       │   ├── GlobalExceptionHandler.java
│       │   └── ErrorResponse.java
│       └── resources/
│           ├── application.properties        # Main config (env-var based)
│           ├── application-local.properties  # Local profile
│           └── application-prod.properties   # Production profile
│
├── frontend/                                 (Angular 17)
│   └── src/app/
│       ├── components/
│       │   ├── login/                        # Login + animated book panel
│       │   ├── register/                     # Registration form
│       │   ├── forgot-password/              # Password reset request
│       │   ├── reset-password/               # New password form
│       │   ├── verify-email/                 # OTP entry screen
│       │   └── chat/                         # Chat UI + voice input + history
│       ├── services/
│       │   ├── auth.service.ts               # JWT storage, login/register calls
│       │   └── chat.service.ts               # AI ask, history CRUD
│       ├── guards/
│       │   └── auth.guard.ts                 # Route protection
│       └── app.routes.ts                     # Standalone routing
│
├── Dockerfile                                # Backend container
└── README.md
```

---

## Environment Configuration

All sensitive values are externalized as environment variables. The backend supports Spring profiles (`local`, `prod`) with `application-{profile}.properties`.

### Backend Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://neon.tech/aidb` |
| `DB_USERNAME` | Database username | `aidb_user` |
| `DB_PASSWORD` | Database password | `***` |
| `JWT_SECRET` | HS256 signing secret (min 256-bit) | `your-secret-key` |
| `JWT_EXPIRATION` | Token TTL in milliseconds | `86400000` (24h) |
| `MAIL_USERNAME` | SMTP sender email | `app@gmail.com` |
| `MAIL_PASSWORD` | App-specific SMTP password | `***` |
| `AI_API_URL` | OpenAI-compatible completions URL | `https://api.openai.com/v1/chat/completions` |
| `AI_API_KEY` | LLM provider API key | `sk-...` |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID | `xxx.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | `***` |
| `PORT` | Server port (Render auto-sets this) | `8080` |

### Frontend Environment Variables (Vercel)

| Variable | Description |
|---|---|
| `VITE_API_BASE_URL` | Backend base URL |
| `VITE_OAUTH_REDIRECT_URI` | OAuth2 redirect URI for production |

---

## Local Development Setup

### Prerequisites

- Java 21+
- Node.js 18+
- Angular CLI 17+
- PostgreSQL (local) or Neon account
- Maven 3.9+

### 1. Clone the repository

```bash
git clone https://github.com/Raman-8688/secure-ai-assistant.git
cd secure-ai-assistant
```

### 2. Configure backend

Create `src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/secureai
spring.datasource.username=postgres
spring.datasource.password=yourpassword
jwt.secret=your-local-256-bit-secret
jwt.expiration=86400000
spring.mail.username=your@gmail.com
spring.mail.password=your-app-password
ai.api.url=https://api.openai.com/v1/chat/completions
ai.api.key=sk-your-key
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
```

### 3. Run backend

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
# API available at http://localhost:8080
```

### 4. Run frontend

```bash
cd frontend
npm install
ng serve
# App available at http://localhost:4200
```

### 5. Run with Docker (backend only)

```bash
docker build -t secure-ai-backend .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://... \
  -e JWT_SECRET=... \
  secure-ai-backend
```

---

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      PRODUCTION                             │
│                                                             │
│   ┌─────────────────┐         ┌──────────────────────────┐  │
│   │   Vercel CDN    │         │   Render.com             │  │
│   │                 │  HTTPS  │                          │  │
│   │   Angular 17    │────────▶│   Docker Container       │  │
│   │   Static Build  │         │   Spring Boot + Java 21  │  │
│   │                 │         │   Port: 8080 (via $PORT) │  │
│   └─────────────────┘         └────────────┬─────────────┘  │
│                                            │                │
│                                            │ JDBC/SSL        │
│                                   ┌────────▼──────────┐     │
│                                   │   Neon (Postgres) │     │
│                                   │   Serverless DB   │     │
│                                   └───────────────────┘     │
│                                            │                │
│                             ┌──────────────┴──────────┐     │
│                             │  External Services       │     │
│                             │  - Gmail SMTP            │     │
│                             │  - Google OAuth2         │     │
│                             │  - OpenAI API            │     │
│                             └──────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

**Render Deployment Notes:**
- Backend is deployed as a Docker container using the included `Dockerfile`
- `$PORT` environment variable is respected in `application.properties` via `${PORT:8080}`
- OAuth2 redirect URI is configured to match the Render service URL in Google Cloud Console

**Vercel Deployment Notes:**
- Frontend is deployed from the Angular build output (`ng build --configuration production`)
- OAuth2 callback and API base URL are configured via Vercel environment variables
- `vercel.json` rewrites all routes to `index.html` for Angular routing

---

## Screenshots

### Login Page
> Dark mode — animated left panel with feature highlights, OAuth2 buttons, email/password form.

### Register Page
> Full registration with name, email, password, leading to OTP email verification.

### Forgot Password Page
> Minimal, focused reset flow with back-to-login navigation.

### Chat Interface
> Sidebar with chat history, voice input button, dark/light theme toggle, AI response display.

---

<div align="center">

Built with dedication by **[Raman Boya](https://github.com/Raman-8688)**

[![GitHub](https://img.shields.io/badge/GitHub-Raman--8688-181717?style=flat-square&logo=github)](https://github.com/Raman-8688)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=flat-square&logo=linkedin)](https://linkedin.com/in/ramanjaneyulu-boya)

*Java Full Stack Developer — Hyderabad, India*

</div>