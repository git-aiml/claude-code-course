# CI/CD Pipeline Documentation

**Project**: RadioAWA - Lossless Audio Streaming Platform
**Author**: Sujit K Singh
**Last Updated**: December 2024

---

## Table of Contents

1. [Overview](#overview)
2. [Purpose and Benefits](#purpose-and-benefits)
3. [Pipeline Architecture](#pipeline-architecture)
4. [When Does It Run?](#when-does-it-run)
5. [What Does It Check?](#what-does-it-check)
6. [Understanding Pipeline Results](#understanding-pipeline-results)
7. [Developer Workflow](#developer-workflow)
8. [Troubleshooting Common Issues](#troubleshooting-common-issues)
9. [Configuration Details](#configuration-details)
10. [Best Practices](#best-practices)

---

## Overview

RadioAWA uses **GitHub Actions** for continuous integration and continuous deployment (CI/CD). Every push and pull request automatically triggers a comprehensive pipeline that:

- ✅ Runs all unit tests (backend and frontend)
- 🔒 Scans for security vulnerabilities
- 🐳 Validates Docker images
- 🔍 Detects exposed secrets
- ⚡ Enforces code quality standards

**Location**: `.github/workflows/ci.yml` (at repository root)

---

## Purpose and Benefits

### Why We Need CI/CD

1. **Catch Bugs Early**: Automated tests run on every commit, catching issues before they reach production
2. **Security First**: Automatic vulnerability scanning protects against known CVEs
3. **Code Quality**: Linting and formatting checks maintain consistent code standards
4. **Confidence in Deployments**: Green pipeline = safe to deploy
5. **Team Collaboration**: PRs can't be merged if tests fail, ensuring code quality
6. **Documentation**: Pipeline results serve as proof of code quality

### Real-World Impact

| Without CI/CD | With CI/CD |
|---------------|------------|
| Manual testing on developer machines | Automated testing on every commit |
| "It works on my machine" syndrome | Consistent environment for all tests |
| Security vulnerabilities discovered in production | Vulnerabilities caught before merge |
| Breaking changes slip through code review | Breaking tests block the PR |
| Inconsistent code style across team | Automated linting enforces standards |

---

## Pipeline Architecture

The CI/CD pipeline is organized into **parallel jobs** that run simultaneously for speed, followed by a **quality gate** that enforces all checks must pass.

```
┌─────────────────────────────────────────────────────────────┐
│                    Trigger: Push or PR                       │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              │                               │
         Testing Jobs                  Security Jobs
              │                               │
    ┌─────────┼─────────┐          ┌─────────┼─────────┐
    │         │         │          │         │         │
 Backend  Frontend   Lint    Backend    Frontend   Docker
  Tests    Tests            Security    Security   Scan
    │         │         │          │         │         │
    └─────────┴─────────┴──────────┴─────────┴─────────┘
                              │
                      ┌───────┴───────┐
                      │  Quality Gate │
                      │ (All must pass)│
                      └───────┬───────┘
                              │
                      ┌───────┴───────┐
                      │    Summary    │
                      │  + PR Comment │
                      └───────────────┘
```

---

## When Does It Run?

### Automatic Triggers

1. **On Push to Main/Develop**
   ```bash
   git push origin main
   # ✅ Pipeline runs automatically
   ```

2. **On Pull Request Creation/Update**
   ```bash
   gh pr create --title "Add new feature"
   # ✅ Pipeline runs automatically
   # ✅ Results appear as PR checks
   ```

3. **Manual Trigger** (via GitHub UI)
   - Navigate to Actions tab → CI/CD Pipeline → Run workflow

### What Gets Tested

- **Every commit** to protected branches
- **Every PR** targeting main or develop
- **All files** in the RadioAWA project subdirectory

---

## What Does It Check?

### 1. Testing Jobs (Run in Parallel)

#### Backend Tests (Java 17)
- **What**: JUnit unit tests with PostgreSQL test database
- **Duration**: ~1-2 minutes
- **Coverage**: JaCoCo code coverage report
- **Artifacts**: Test results, coverage reports (30-day retention)

**Command equivalent**:
```bash
cd radioawa/backend
mvn clean test
```

#### Frontend Tests (Node 18.x & 20.x Matrix)
- **What**: Vitest unit tests (tested on 2 Node.js versions)
- **Duration**: ~30 seconds per version
- **Coverage**: Vitest coverage report
- **Artifacts**: Test results, coverage reports (30-day retention)

**Command equivalent**:
```bash
cd radioawa/frontend
npm ci
npm run test
npm run test:coverage
```

#### Code Linting (ESLint)
- **What**: JavaScript/React code quality checks
- **Duration**: ~15 seconds
- **Standards**: React best practices, hooks rules
- **Continues on error**: Won't block pipeline (informational only)

**Command equivalent**:
```bash
cd radioawa/frontend
npm run lint
```

---

### 2. Security Scanning Jobs (Run in Parallel)

#### Backend Security (Trivy + Maven)
- **Scans**: Maven dependencies (pom.xml)
- **Severity**: Blocks on CRITICAL/HIGH vulnerabilities
- **Output**: SARIF report → GitHub Security tab
- **Example CVEs**: CVE-2025-24813 (Tomcat RCE), CVE-2024-1597 (PostgreSQL)

**Command equivalent**:
```bash
make security-backend
```

#### Frontend Security (npm audit + Trivy)
- **Scans**: npm dependencies (package-lock.json)
- **Tools**: npm audit + Trivy scanner
- **Severity**: Blocks on CRITICAL/HIGH vulnerabilities
- **Output**: SARIF report → GitHub Security tab

**Command equivalent**:
```bash
make security-frontend
```

#### Docker Image Security (Trivy)
- **Scans**: Both backend and frontend Docker images
- **Checks**: Base image vulnerabilities, OS packages
- **Matrix**: Tests both images in parallel
- **Severity**: Blocks on CRITICAL/HIGH vulnerabilities

**Command equivalent**:
```bash
make security-docker
```

#### Secret Detection (Trivy)
- **Scans**: Entire codebase for exposed secrets
- **Detects**: API keys, tokens, passwords, credentials
- **Scope**: Full git history (fetch-depth: 0)
- **Severity**: Blocks if any secrets found

**Command equivalent**:
```bash
make security-secrets
```

---

### 3. Quality Gate

The quality gate is a **mandatory checkpoint** that:

- ✅ Waits for ALL test and security jobs to complete
- ✅ Enforces ALL checks must pass (no failures allowed)
- ❌ Blocks PR merge if any job fails
- 📊 Generates comprehensive status summary

**Pass Criteria**:
- Backend tests: ✅ Passed
- Frontend tests (both Node versions): ✅ Passed
- Linting: ✅ Passed (or skipped)
- Backend security: ✅ No CRITICAL/HIGH vulnerabilities
- Frontend security: ✅ No CRITICAL/HIGH vulnerabilities
- Docker security: ✅ No CRITICAL/HIGH vulnerabilities
- Secret detection: ✅ No secrets found

---

## Understanding Pipeline Results

### GitHub UI - Actions Tab

1. **Navigate to Actions**: `https://github.com/git-aiml/claude-code-course/actions`
2. **Find your run**: Click on the commit message or PR
3. **View results**: See all jobs with ✅ (pass) or ❌ (fail) indicators

### GitHub UI - Pull Request Checks

When you create a PR, you'll see:

```
CI/CD Pipeline — In progress...
✅ Backend Tests (Java 17)
✅ Frontend Tests (Node 18.x)
✅ Frontend Tests (Node 20.x)
✅ Code Linting
✅ Security Scan - Backend
✅ Security Scan - Frontend
✅ Security Scan - Docker Images
✅ Security Scan - Secret Detection
✅ Quality Gate
```

### Command Line - Using GitHub CLI

```bash
# List recent workflow runs
gh run list --limit 10

# View specific run details
gh run view <run-id>

# View failed logs
gh run view <run-id> --log-failed

# Watch live run
gh run watch
```

### Interpreting Results

| Status | Icon | Meaning | Action Required |
|--------|------|---------|-----------------|
| Success | ✅ | All checks passed | Safe to merge PR |
| Failure | ❌ | One or more checks failed | Fix issues and push again |
| In Progress | 🟡 | Tests still running | Wait for completion |
| Skipped | ⚪ | Job was skipped | Check conditions |

---

## Developer Workflow

### Daily Development

#### 1. Before Creating a PR

Run tests locally to catch issues early:

```bash
# Run all tests locally (recommended)
make test

# Or run specific tests
make test-backend    # Backend only
make test-frontend   # Frontend only
```

#### 2. Creating a Pull Request

```bash
# Create feature branch
git checkout -b feature/my-new-feature

# Make your changes
# ... code, code, code ...

# Run tests locally
make test

# Commit changes
git add .
git commit -m "Add new feature"

# Push to GitHub
git push origin feature/my-new-feature

# Create PR
gh pr create --title "Add new feature" --body "Description..."

# ✅ CI/CD pipeline runs automatically
```

#### 3. Responding to CI/CD Failures

If the pipeline fails:

1. **Check which job failed**:
   ```bash
   gh run view --log-failed
   ```

2. **Fix the issue locally**:
   ```bash
   # If backend tests failed
   cd radioawa/backend
   mvn clean test  # See the error locally

   # If security scan failed
   make security-backend  # Reproduce the vulnerability scan
   ```

3. **Push the fix**:
   ```bash
   git add .
   git commit -m "Fix failing tests"
   git push
   # ✅ Pipeline runs again automatically
   ```

---

### Security Vulnerability Workflow

If the security scan finds vulnerabilities:

#### Step 1: Identify the Vulnerability

```bash
# View security scan logs
gh run view <run-id> --log-failed

# Example output:
# CVE-2025-24813 (CRITICAL): Apache Tomcat RCE
# Package: tomcat-embed-core 10.1.17
# Fixed in: 10.1.45
```

#### Step 2: Update Dependencies

**Backend (Maven)**:
```bash
cd radioawa/backend

# Edit pom.xml to update version
# Add or update in <properties> section:
<tomcat.version>10.1.45</tomcat.version>

# Test locally
make security-backend

# Run tests to ensure nothing broke
mvn clean test
```

**Frontend (npm)**:
```bash
cd radioawa/frontend

# Update specific package
npm update <package-name>

# Or audit fix
npm audit fix

# Test locally
make security-frontend

# Run tests
npm test
```

#### Step 3: Commit and Verify

```bash
git add .
git commit -m "Fix CVE-2025-24813: Update Tomcat to 10.1.45"
git push

# ✅ Pipeline runs and should pass now
```

---

## Troubleshooting Common Issues

### Issue 1: "Backend Tests Failed"

**Symptom**: Backend Tests job shows ❌

**Common Causes**:
1. JUnit test failures
2. PostgreSQL connection issues
3. Maven dependency resolution errors

**Solutions**:
```bash
# Run tests locally to see detailed errors
cd radioawa/backend
mvn clean test

# Check for compilation errors
mvn clean compile

# Verify Java version
java -version  # Should be 17+
```

---

### Issue 2: "Frontend Tests Failed"

**Symptom**: Frontend Tests job shows ❌

**Common Causes**:
1. Vitest test failures
2. Missing test files
3. Component rendering errors

**Solutions**:
```bash
# Run tests locally
cd radioawa/frontend
npm ci
npm run test

# Check test configuration
cat vitest.config.js

# Run tests in watch mode for debugging
npm run test:watch
```

---

### Issue 3: "Security Scan Blocked - Vulnerabilities Found"

**Symptom**: Security job shows ❌ with CRITICAL/HIGH vulnerabilities

**Solution**:
```bash
# For backend vulnerabilities
cd radioawa/backend
make security-backend  # See the vulnerabilities

# Update pom.xml with secure versions
# Then test locally before pushing

# For frontend vulnerabilities
cd radioawa/frontend
npm audit  # See the vulnerabilities
npm audit fix  # Auto-fix if possible
```

---

### Issue 4: "Secret Detection Failed"

**Symptom**: Secret scanner found exposed credentials

**⚠️ CRITICAL**: Never commit real secrets!

**Solution**:
```bash
# Find the offending file
gh run view <run-id> --log-failed

# Remove the secret from the file
# If already committed, use:
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch path/to/secret/file" \
  --prune-empty --tag-name-filter cat -- --all

# Or use BFG Repo-Cleaner for large repos
# https://rtyley.github.io/bfg-repo-cleaner/
```

**Prevention**:
- Use `.env` files (already in `.gitignore`)
- Never hardcode API keys in code
- Use environment variables in CI/CD

---

### Issue 5: "Quality Gate Failed"

**Symptom**: All individual jobs pass, but Quality Gate fails

**Solution**:
This shouldn't happen if all jobs pass. Check:

```bash
# View quality gate logs
gh run view <run-id> --log

# Look for dependency issues
# Usually one job silently failed but wasn't caught
```

---

## Configuration Details

### Workflow File Location

```
claude-code-course/              # Repository root
└── .github/
    └── workflows/
        └── ci.yml               # ⬅️ CI/CD pipeline configuration
```

**Important**: Workflow MUST be at repository root, not in `radioawa/.github/workflows/`

### Workflow Triggers (Excerpt from ci.yml)

```yaml
on:
  push:
    branches: [main, develop]    # Auto-run on push to these branches
  pull_request:
    branches: [main, develop]    # Auto-run on PRs to these branches
  workflow_dispatch:              # Allow manual trigger
```

### Concurrency Control

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true       # Cancel in-progress runs for same branch
```

This saves CI/CD minutes by canceling old runs when you push new commits.

---

### Environment Requirements

#### Backend Tests
- Java 17 (Temurin distribution)
- PostgreSQL 16 (service container)
- Maven cache enabled

#### Frontend Tests
- Node.js 18.x and 20.x (matrix testing)
- npm cache enabled
- Vitest test runner

#### Security Scans
- Trivy latest version
- SARIF upload to GitHub Security tab
- Permissions: `contents: read`, `security-events: write`

---

## Best Practices

### For Developers

1. **Run Tests Locally First**
   ```bash
   make test  # Before every commit
   ```

2. **Keep Commits Small**
   - Small commits = easier to debug when pipeline fails
   - One feature per PR = clearer pipeline results

3. **Monitor Security Scans**
   - Check GitHub Security tab weekly
   - Update dependencies regularly
   - Never ignore CRITICAL vulnerabilities

4. **Don't Skip CI/CD**
   - Never use `[skip ci]` in commit messages
   - Pipeline is there to protect you

5. **Read Failure Logs**
   - Use `gh run view --log-failed` to understand failures
   - Don't just "try again" without fixing

---

### For Maintainers

1. **Branch Protection Rules**
   ```
   Settings → Branches → main
   ✅ Require status checks to pass before merging
   ✅ Require branches to be up to date before merging
   ✅ CI/CD Pipeline (quality-gate)
   ```

2. **Security Tab Monitoring**
   - Review Dependabot alerts weekly
   - Check SARIF upload results
   - Address CRITICAL/HIGH vulnerabilities within 7 days

3. **Artifact Retention**
   - Test results: 30 days (configurable)
   - Security reports: 30 days (configurable)
   - Adjust in workflow file if needed

4. **Performance Optimization**
   - Jobs run in parallel where possible
   - Caching enabled for Maven and npm
   - Concurrency control cancels stale runs

---

## Comparison: Local vs CI/CD Testing

| Aspect | Local Testing (`make test`) | CI/CD Pipeline |
|--------|----------------------------|----------------|
| **Environment** | Docker containers on your machine | GitHub Actions runners (Ubuntu) |
| **Execution** | Manual (you run the command) | Automatic (every push/PR) |
| **Speed** | Faster (local resources) | Slower (shared runners) |
| **Use Case** | Daily development, quick feedback | Merge verification, team collaboration |
| **Database** | PostgreSQL in Docker | PostgreSQL service container |
| **Node Version** | Whatever you have installed | 18.x AND 20.x (matrix) |
| **Security Scans** | Optional (manual `make security-*`) | Mandatory (automatic) |
| **Coverage** | Local report only | Uploaded to Codecov |

**Recommendation**: Use **both**!
- Local: Fast feedback during development
- CI/CD: Final verification before merge

---

## Frequently Asked Questions

### Q: Can I skip CI/CD for a quick hotfix?

**A**: No. Branch protection rules enforce CI/CD checks. This protects production from breaking changes.

### Q: Why does the pipeline test on multiple Node versions?

**A**: To ensure compatibility. Users might have different Node versions, and CI/CD catches version-specific bugs.

### Q: How long should the pipeline take?

**A**:
- Fastest: ~2 minutes (all jobs parallel)
- Typical: 3-5 minutes
- Slow: 5-10 minutes (if Docker cache miss)

### Q: What if the pipeline is always green but production still breaks?

**A**: This means:
1. Tests don't cover the broken scenario (add more tests!)
2. Environment mismatch (check Docker configs)
3. External dependency changed (pin versions)

### Q: Can I run the pipeline locally?

**A**: Partially. Use:
```bash
make test             # Runs tests
make security-scan    # Runs security scans
```

For the full GitHub Actions experience, use [act](https://github.com/nektos/act).

---

## Related Documentation

- [TESTING-FRAMEWORK.md](./TESTING-FRAMEWORK.md) - Detailed testing guide
- [SECURITY.md](./SECURITY.md) - Security vulnerability handling
- [QUICKSTART.md](./QUICKSTART.md) - Makefile commands and workflows
- [CLAUDE.md](./CLAUDE.md) - AI assistant guidelines (includes CI/CD section)

---

## Changelog

| Date | Change |
|------|--------|
| 2024-12-27 | Initial CI/CD pipeline integration |
| 2024-12-27 | Fixed workflow location and path issues |
| 2024-12-27 | Added comprehensive documentation |

---

**Maintained By**: Sujit K Singh
**Last Reviewed**: December 27, 2024
**Version**: 1.0
