# Security Policy and Scanning

**Project**: RadioAWA - Lossless Audio Streaming Platform
**Last Updated**: December 2024
**Security Contact**: Sujit K Singh

---

## Table of Contents

1. [Why Security Scanning?](#why-security-scanning)
2. [Security Stack Overview](#security-stack-overview)
3. [Security Tools Evaluated](#security-tools-evaluated)
4. [Our Security Approach](#our-security-approach)
5. [Running Security Scans](#running-security-scans)
6. [Automated Security Checks](#automated-security-checks)
7. [Dependency Management](#dependency-management)
8. [Vulnerability Disclosure](#vulnerability-disclosure)
9. [Security Best Practices](#security-best-practices)

---

## Why Security Scanning?

### The Need for Security

RadioAWA is a web application that:
- **Accepts user input** (song ratings, user IDs)
- **Connects to a database** (PostgreSQL)
- **Serves content over HTTP** (API endpoints)
- **Uses third-party dependencies** (100+ npm/Maven packages)
- **Runs in Docker containers** (multiple base images)

Each of these introduces potential security vulnerabilities:

| Risk Category | Examples | Impact |
|---------------|----------|---------|
| **Dependency Vulnerabilities** | Known CVEs in npm/Maven packages | RCE, data theft, DoS |
| **Container Vulnerabilities** | Outdated base images, OS packages | System compromise |
| **Secret Exposure** | API keys, passwords in code | Unauthorized access |
| **Code Vulnerabilities** | SQL injection, XSS, CSRF | Data breach, account takeover |

### Business Impact

Without security scanning:
- **Legal liability**: GDPR, CCPA violations for data breaches
- **Reputation damage**: Loss of user trust
- **Service disruption**: Exploits leading to downtime
- **Financial loss**: Recovery costs, potential fines
- **Development delays**: Emergency patching instead of planned work

### Industry Statistics

- **83%** of applications have at least one vulnerability in dependencies (Veracode 2023)
- **Average 60 days** to patch critical vulnerabilities (Snyk 2023)
- **$4.45M** average cost of a data breach (IBM 2023)
- **79%** of breaches involve compromised credentials (Verizon 2023)

---

## Security Stack Overview

### Implemented Security Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    RADIOAWA SECURITY STACK                   │
├─────────────────────────────────────────────────────────────┤
│  Layer 1: Dependency Scanning                               │
│  ├─ Trivy (Maven + npm vulnerabilities)                     │
│  ├─ npm audit (Frontend CVE database)                       │
│  └─ Dependabot (Automated updates)                          │
├─────────────────────────────────────────────────────────────┤
│  Layer 2: Container Scanning                                │
│  └─ Trivy (Docker image vulnerabilities)                    │
├─────────────────────────────────────────────────────────────┤
│  Layer 3: Secret Detection                                  │
│  └─ Trivy (Exposed credentials, API keys)                   │
├─────────────────────────────────────────────────────────────┤
│  Layer 4: Automated Enforcement                             │
│  ├─ GitHub Actions (CI/CD pipeline)                         │
│  ├─ Pull Request checks (Block merges)                      │
│  └─ Daily scheduled scans                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## Security Tools Evaluated

Before selecting our security stack, we evaluated multiple tools across four categories:

### 1. Dependency Vulnerability Scanning

| Tool | Backend | Frontend | Docker | Cost | Decision |
|------|---------|----------|--------|------|----------|
| **Trivy** ⭐ | ✅ Java/Maven | ✅ JavaScript/npm | ✅ Images | Free | **SELECTED** |
| OWASP Dependency-Check | ✅ Maven | ❌ | ❌ | Free | Optional addon |
| npm audit | ❌ | ✅ Built-in | ❌ | Free | Included |
| Snyk | ✅ | ✅ | ✅ | Free tier limited | Not selected |
| Dependabot | ✅ | ✅ | ❌ | Free | **SELECTED** |

**Why Trivy?**
- ✅ All-in-one solution (dependencies + containers + secrets)
- ✅ Fast execution (< 2 minutes in CI)
- ✅ No account/registration required
- ✅ Comprehensive CVE database (NVD, GitHub Advisory, etc.)
- ✅ SARIF output for GitHub Security tab integration
- ✅ Works offline
- ✅ Actively maintained (Aqua Security)

**Why Dependabot?**
- ✅ Automated pull requests for vulnerable dependencies
- ✅ Zero configuration for Maven and npm
- ✅ GitHub native integration
- ✅ Prevents vulnerabilities before they're merged
- ✅ Groups related updates to reduce PR noise

### 2. Static Application Security Testing (SAST)

| Tool | Supported | Cost | Decision |
|------|-----------|------|----------|
| CodeQL | Java, JavaScript | Free (public repos) | Future consideration |
| SonarCloud | Java, JavaScript | Free (public repos) | Future consideration |
| Semgrep | Java, JavaScript | Free | Future consideration |
| SpotBugs + FindSecBugs | Java only | Free | Future consideration |

**Status**: Not implemented in Tier 1. Can add CodeQL later for deep semantic analysis.

### 3. Container Image Scanning

| Tool | Decision |
|------|----------|
| **Trivy** ⭐ | **SELECTED** - Already chosen for dependencies |
| Docker Scout | Not needed (Trivy covers this) |
| Snyk Container | Not needed (Trivy covers this) |
| Anchore Grype | Not needed (Trivy covers this) |

### 4. Secret Scanning

| Tool | Decision |
|------|----------|
| **Trivy** ⭐ | **SELECTED** - Built-in secret detection |
| GitHub Secret Scanning | Automatic (public repos) |
| TruffleHog | Not needed (Trivy covers this) |
| GitLeaks | Not needed (Trivy covers this) |

---

## Our Security Approach

### Tier 1: Core Security (Implemented) ✅

This is our **production security stack**:

1. **Trivy** - All-in-one scanning
   - Backend dependencies (Maven)
   - Frontend dependencies (npm)
   - Docker images
   - Secret detection

2. **Dependabot** - Automated dependency updates
   - Maven dependencies
   - npm dependencies
   - GitHub Actions
   - Docker base images

3. **npm audit** - Built-in frontend vulnerability checking

### Tier 2: Enhanced Security (Future)

Optional enhancements we can add later:

4. **CodeQL** - Deep SAST analysis for logic bugs
5. **OWASP Dependency-Check** - Additional Maven-specific checks
6. **SonarCloud** - Code quality + security metrics

### Why This Stack?

| Criteria | Our Choice | Rationale |
|----------|------------|-----------|
| **Coverage** | 90% of security needs | Dependencies, containers, secrets covered |
| **Performance** | ~2 min CI time | Fast enough for every PR |
| **Cost** | $0 | All tools are free and open source |
| **Maintenance** | Low | Minimal configuration, auto-updates |
| **Learning Curve** | Easy | Simple CLI, clear reports |
| **GitHub Integration** | Native | SARIF uploads, Security tab |

---

## Running Security Scans

### Prerequisites

Install Trivy (one-time setup):

```bash
# macOS
make security-install
# or
brew install aquasecurity/trivy/trivy

# Linux (Ubuntu/Debian)
make security-install
# or manually:
wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" | sudo tee -a /etc/apt/sources.list.d/trivy.list
sudo apt-get update
sudo apt-get install trivy
```

### Quick Start

Run all security scans:

```bash
make security-scan
```

This executes:
1. Backend dependency scan
2. Frontend dependency scan (npm audit + Trivy)
3. Docker image scan
4. Secret detection scan

### Individual Scans

Run specific scans as needed:

```bash
# Scan backend dependencies only
make security-backend

# Scan frontend dependencies only
make security-frontend

# Scan Docker images only
make security-docker

# Scan for exposed secrets only
make security-secrets
```

### Understanding Results

Trivy severity levels:

| Severity | Action Required | Example |
|----------|----------------|---------|
| **CRITICAL** | Fix immediately | RCE, authentication bypass |
| **HIGH** | Fix within 7 days | SQL injection, XSS |
| **MEDIUM** | Fix within 30 days | DoS, information disclosure |
| **LOW** | Fix when convenient | Minor issues |

### Sample Output

```bash
$ make security-backend

Scanning backend dependencies for vulnerabilities...

backend/pom.xml (maven)
═══════════════════════════════════════════════════════════════
Total: 2 (HIGH: 1, CRITICAL: 1)

┌─────────────────────────┬────────────────┬──────────┬───────────────────┬───────────────┬────────────────────────────────────────┐
│         Library         │ Vulnerability  │ Severity │ Installed Version │ Fixed Version │                 Title                  │
├─────────────────────────┼────────────────┼──────────┼───────────────────┼───────────────┼────────────────────────────────────────┤
│ org.postgresql:postgresql│ CVE-2024-1597  │ HIGH     │ 42.5.0            │ 42.5.5        │ postgresql: SQL Injection in ResultSet │
│ com.fasterxml.jackson   │ CVE-2024-1234  │ CRITICAL │ 2.15.0            │ 2.15.3        │ jackson: Deserialization RCE          │
└─────────────────────────┴────────────────┴──────────┴───────────────────┴───────────────┴────────────────────────────────────────┘
```

### Fixing Vulnerabilities

1. **Update dependencies**:
   ```bash
   # Backend
   cd backend
   # Update pom.xml with fixed version
   mvn clean install

   # Frontend
   cd frontend
   npm update <package-name>
   # or
   npm audit fix
   ```

2. **Wait for Dependabot**:
   - Dependabot will automatically create PRs for vulnerable dependencies
   - Review and merge the PR
   - Security scan runs automatically on the PR

3. **Manual override** (if no fix available):
   - Document the reason in `.trivyignore`
   - Add compensating controls
   - Monitor for future updates

---

## Automated Security Checks

### GitHub Actions Integration

Our security workflow runs automatically:

**Triggers**:
- ✅ Every pull request
- ✅ Push to `main` or `develop` branches
- ✅ Daily at 2 AM UTC (scheduled scan)
- ✅ Manual trigger via GitHub Actions UI

**What it scans**:
1. Backend Maven dependencies (Trivy)
2. Frontend npm dependencies (npm audit + Trivy)
3. Docker images (backend + frontend)
4. Secrets in code (Trivy)

**Results**:
- 📊 Uploaded to **GitHub Security** tab (SARIF format)
- 💬 Comment on PR with summary
- 📦 Artifacts downloadable for 30 days
- ❌ Fails PR if CRITICAL or HIGH vulnerabilities found

### Viewing Results

**In GitHub UI**:
1. Go to **Security** tab → **Code scanning**
2. Filter by tool: `Trivy`
3. View vulnerability details, affected files, fix recommendations

**In Pull Requests**:
- Security scan comment shows pass/fail status
- Click workflow run for detailed logs
- Download SARIF artifacts for offline analysis

### Enforcement Policy

| Severity | PR Merge | Action |
|----------|----------|--------|
| CRITICAL | ❌ BLOCKED | Must fix before merge |
| HIGH | ❌ BLOCKED | Must fix before merge |
| MEDIUM | ⚠️ WARNING | Fix preferred, but can merge |
| LOW | ✅ ALLOWED | Fix when convenient |

**Override**: Repository admins can bypass checks for urgent hotfixes (not recommended).

---

## Dependency Management

### Dependabot Configuration

**Update Schedule**: Every Monday at 9 AM UTC

**Managed Ecosystems**:
- Maven (backend dependencies)
- npm (frontend dependencies)
- GitHub Actions (workflow dependencies)
- Docker (base images)

**PR Limits**:
- 5 PRs max for Maven
- 5 PRs max for npm
- 3 PRs max for GitHub Actions
- 3 PRs max for Docker

**Grouping Strategy**:
- Spring Boot packages grouped together
- React packages grouped together
- Testing libraries grouped together
- Patch updates ignored for `@types/*` and `eslint*` (reduces noise)

### Reviewing Dependabot PRs

When Dependabot creates a PR:

1. **Check the changelog**: What changed in the new version?
2. **Review security advisory**: Is it a security fix?
3. **Run tests**: Does `make test` pass?
4. **Check compatibility**: Any breaking changes?
5. **Merge**: If all checks pass, merge immediately

**Priority**: Security fixes should be merged within 24 hours.

### Manual Updates

Update all dependencies quarterly:

```bash
# Backend
cd backend
mvn versions:display-dependency-updates

# Frontend
cd frontend
npm outdated
npm update
```

---

## Vulnerability Disclosure

### Reporting a Vulnerability

If you discover a security vulnerability in RadioAWA:

**DO NOT** open a public GitHub issue.

Instead:
1. Email: [your-email@example.com] with subject "RadioAWA Security"
2. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if available)

**Response Time**:
- Acknowledgment: Within 24 hours
- Initial assessment: Within 3 days
- Fix timeline: Based on severity
  - CRITICAL: 24-48 hours
  - HIGH: 7 days
  - MEDIUM: 30 days
  - LOW: 90 days

### Security Advisories

We publish security advisories via:
- GitHub Security Advisories
- Release notes
- CHANGELOG.md

### CVE Process

For critical vulnerabilities:
1. Request CVE ID via GitHub Security Advisories
2. Coordinate disclosure with security researchers
3. Release patched version
4. Publish advisory

---

## Security Best Practices

### For Contributors

**Before committing**:
- ✅ Run `make security-secrets` to check for exposed secrets
- ✅ Never commit `.env` files with real credentials
- ✅ Use environment variables for sensitive data
- ✅ Run `make security-scan` before creating PR

**During development**:
- ✅ Keep dependencies up-to-date (`npm update`, `mvn versions:use-latest-releases`)
- ✅ Review Dependabot PRs promptly
- ✅ Use parameterized queries (never string concatenation for SQL)
- ✅ Validate all user input
- ✅ Follow OWASP Top 10 guidelines

**Code review checklist**:
- [ ] No hardcoded secrets
- [ ] Input validation present
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention (output encoding)
- [ ] CSRF tokens for state-changing operations
- [ ] Rate limiting for public APIs
- [ ] Error messages don't leak sensitive info

### For Maintainers

**Monthly tasks**:
- [ ] Review GitHub Security tab for new alerts
- [ ] Check for outdated dependencies (`npm outdated`, `mvn versions:display-dependency-updates`)
- [ ] Review Dependabot PRs
- [ ] Run full security scan: `make security-scan`

**Quarterly tasks**:
- [ ] Review and update `.trivyignore` (remove resolved issues)
- [ ] Audit user permissions and access controls
- [ ] Review security policies and procedures
- [ ] Update base Docker images

**Annually**:
- [ ] Security assessment by external party
- [ ] Penetration testing
- [ ] Review and update security documentation

### Production Deployment

**Before deploying**:
```bash
# 1. Run all tests
make test

# 2. Run security scans
make security-scan

# 3. Build production images
make prod-build

# 4. Scan production images
make security-docker
```

**After deploying**:
- Monitor logs for suspicious activity
- Set up alerts for failed authentication attempts
- Enable GitHub Advanced Security (if available)
- Configure WAF rules (if using CDN)

---

## Known Limitations

### Current Security Gaps

RadioAWA currently **does NOT** implement:

| Security Feature | Status | Impact | Mitigation |
|-----------------|--------|--------|------------|
| HTTPS/TLS | ❌ Not implemented | Traffic interception | Use reverse proxy (nginx/CloudFlare) |
| Authentication | ❌ Not implemented | Unauthorized access | Rate limiting, IP blocking |
| CSRF Protection | ❌ Not implemented | Cross-site attacks | Add CSRF tokens |
| API Rate Limiting | ⚠️ Partial (voting only) | DoS attacks | Extend to all endpoints |
| Input Sanitization | ⚠️ Basic validation | XSS vulnerabilities | Add comprehensive sanitization |
| SQL Injection | ✅ Protected (JPA) | N/A | Continue using parameterized queries |

### Future Enhancements

Planned security improvements:

1. **Phase 2** (Q1 2025):
   - Add CodeQL SAST scanning
   - Implement CSRF protection
   - Add comprehensive input sanitization

2. **Phase 3** (Q2 2025):
   - Add user authentication (OAuth 2.0)
   - Implement JWT tokens
   - Add API rate limiting to all endpoints

3. **Phase 4** (Q3 2025):
   - Add HTTPS enforcement
   - Implement Content Security Policy (CSP)
   - Add security headers (HSTS, X-Frame-Options, etc.)

---

## Additional Resources

### Documentation
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Trivy Documentation](https://aquasecurity.github.io/trivy/)
- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [GitHub Security Best Practices](https://docs.github.com/en/code-security)

### Tools
- [Trivy GitHub Action](https://github.com/aquasecurity/trivy-action)
- [npm audit](https://docs.npmjs.com/cli/v9/commands/npm-audit)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)

### Security Standards
- [CWE Top 25](https://cwe.mitre.org/top25/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)
- [ISO 27001](https://www.iso.org/isoiec-27001-information-security.html)

---

**Remember**: Security is not a one-time task, it's a continuous process. Stay vigilant, keep dependencies updated, and monitor for new vulnerabilities regularly.

---

**Last Updated**: December 2024
**Maintained By**: Sujit K Singh
**Version**: 1.0
