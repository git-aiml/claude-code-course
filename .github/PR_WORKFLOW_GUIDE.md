# Pull Request Workflow Guide

This guide outlines the complete workflow for creating, reviewing, and merging pull requests in this repository.

---

## Table of Contents

1. [Quick Reference](#quick-reference)
2. [Creating a Pull Request](#creating-a-pull-request)
3. [Merging a Pull Request](#merging-a-pull-request)
4. [Post-Merge Cleanup](#post-merge-cleanup)
5. [Best Practices](#best-practices)
6. [Troubleshooting](#troubleshooting)

---

## Quick Reference

```bash
# Create and push feature branch
git checkout -b feature/your-feature-name
git add .
git commit -m "Your commit message"
git push -u origin feature/your-feature-name

# Create PR using GitHub CLI
gh pr create --title "PR Title" --body "PR Description"

# Merge PR
gh pr merge <PR_NUMBER> --merge

# Cleanup after merge
git checkout main
git pull
git branch -d feature/your-feature-name
git push origin --delete feature/your-feature-name  # If needed
```

---

## Creating a Pull Request

### Step 1: Create a Feature Branch

Always work on a feature branch, never directly on `main`.

```bash
# Make sure you're on main and up to date
git checkout main
git pull origin main

# Create and switch to new feature branch
git checkout -b feature/descriptive-name
```

**Branch Naming Conventions:**
- `feature/` - New features (e.g., `feature/add-user-auth`)
- `fix/` - Bug fixes (e.g., `fix/rating-bug`)
- `docs/` - Documentation updates (e.g., `docs/update-readme`)
- `refactor/` - Code refactoring (e.g., `refactor/optimize-queries`)
- `test/` - Test additions/updates (e.g., `test/add-rating-tests`)

### Step 2: Make Your Changes

```bash
# Edit files as needed
# Check status frequently
git status

# View changes
git diff
```

### Step 3: Commit Your Changes

```bash
# Stage specific files
git add path/to/file1 path/to/file2

# Or stage all changes
git add .

# Commit with descriptive message
git commit -m "$(cat <<'EOF'
Short summary of changes (50 chars or less)

Detailed explanation of what changed and why. Include:
- What was added/modified/removed
- Why the change was necessary
- Any important implementation details

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>
EOF
)"
```

**Commit Message Best Practices:**
- First line: Clear, concise summary (imperative mood)
- Blank line after summary
- Detailed description explaining what and why
- Reference issue numbers if applicable

### Step 4: Push to Remote

```bash
# First push - set upstream tracking
git push -u origin feature/descriptive-name

# Subsequent pushes
git push
```

### Step 5: Create the Pull Request

**Option 1: Using GitHub CLI (Recommended)**

```bash
gh pr create --title "Descriptive PR Title" --body "$(cat <<'EOF'
## Summary
- First key change
- Second key change
- Third key change

## Changes Made

### Added
- New feature X
- New component Y

### Modified
- Updated service Z

### Removed
- Deprecated function A

## Purpose
Explain why these changes were made and what problem they solve.

## Type of Change
- [x] New feature
- [ ] Bug fix
- [ ] Documentation update

## Testing

### Test Plan
- [x] Unit tests added/updated
- [x] Manual testing performed
- [x] All existing tests pass

### Manual Testing Steps
1. Step one
2. Step two
3. Step three

## Checklist
- [x] Code follows project conventions
- [x] Self-review completed
- [x] Documentation updated
- [x] Branch is up to date with base branch

🤖 Generated with [Claude Code](https://claude.com/claude-code)
EOF
)"
```

**Option 2: Using GitHub Web UI**

1. Go to your repository on GitHub
2. Click "Pull requests" tab
3. Click "New pull request"
4. Select your feature branch
5. Fill out the PR template (auto-populated)
6. Click "Create pull request"

**Option 3: Using the Link from Push Output**

When you push, Git provides a URL like:
```
remote: Create a pull request for 'feature/name' on GitHub by visiting:
remote:   https://github.com/username/repo/pull/new/feature/name
```
Click that link and fill out the form.

### Step 6: Review PR Status

```bash
# Check your PR status
gh pr status

# View specific PR details
gh pr view <PR_NUMBER>

# View PR in browser
gh pr view <PR_NUMBER> --web
```

---

## Merging a Pull Request

### Before Merging Checklist

- [ ] All CI/CD checks pass
- [ ] Code review completed (if required)
- [ ] All requested changes addressed
- [ ] Branch is up to date with base branch
- [ ] No merge conflicts
- [ ] Tests are passing

### Update Branch if Needed

If your branch is behind `main`:

```bash
# Switch to your feature branch
git checkout feature/your-feature-name

# Pull latest main
git fetch origin main

# Merge or rebase
git merge origin/main
# OR
git rebase origin/main

# Push updated branch
git push
```

### Merge Methods

**Method 1: Merge Commit (Default)**

Creates a merge commit preserving full history.

```bash
gh pr merge <PR_NUMBER> --merge
```

**Method 2: Squash and Merge**

Combines all commits into one.

```bash
gh pr merge <PR_NUMBER> --squash
```

**Method 3: Rebase and Merge**

Replays commits on top of base branch.

```bash
gh pr merge <PR_NUMBER> --rebase
```

**Recommendation:** Use `--merge` for this project to preserve commit history.

### Auto-Delete Branch Option

```bash
# Merge and auto-delete remote branch
gh pr merge <PR_NUMBER> --merge --delete-branch
```

---

## Post-Merge Cleanup

### Step 1: Switch to Main Branch

```bash
git checkout main
```

### Step 2: Pull Latest Changes

```bash
git pull origin main
```

### Step 3: Delete Local Feature Branch

```bash
# Delete local branch
git branch -d feature/your-feature-name

# Force delete if needed (use carefully)
git branch -D feature/your-feature-name
```

### Step 4: Delete Remote Branch (if not auto-deleted)

```bash
# Delete remote branch
git push origin --delete feature/your-feature-name
```

### Step 5: Verify Cleanup

```bash
# List all branches
git branch -a

# Should only show:
# * main
# remotes/origin/HEAD -> origin/main
# remotes/origin/main
```

### Step 6: Prune Stale Remote Branches

```bash
# Remove stale remote tracking branches
git remote prune origin

# Or use fetch with prune
git fetch --prune
```

---

## Best Practices

### General Guidelines

1. **Keep PRs Small and Focused**
   - One feature/fix per PR
   - Easier to review and merge
   - Reduces merge conflicts

2. **Write Descriptive Titles**
   - Good: "Add user authentication with JWT"
   - Bad: "Updates"

3. **Use the PR Template**
   - Fill out all relevant sections
   - Provide context for reviewers
   - Include test results

4. **Keep Branch Updated**
   - Regularly sync with main
   - Resolve conflicts early
   - Rebase or merge main frequently

5. **Self-Review Before Creating PR**
   - Review your own diff
   - Check for debug code
   - Verify tests pass locally

### Commit Guidelines

1. **Commit Often**
   - Small, logical commits
   - Easy to review and revert
   - Clear commit history

2. **Write Clear Messages**
   - Explain what and why
   - Reference issues
   - Follow conventional commits format

3. **Don't Commit Sensitive Data**
   - No API keys or passwords
   - No `.env` files
   - Use `.gitignore` properly

### Code Review Guidelines

1. **Be Responsive**
   - Address feedback promptly
   - Ask questions if unclear
   - Thank reviewers

2. **Make Requested Changes**
   - Create new commits for changes
   - Don't force push after review starts
   - Mark conversations as resolved

3. **Test Changes**
   - Re-test after making changes
   - Ensure CI passes
   - Update tests if needed

---

## Troubleshooting

### Merge Conflicts

If you encounter merge conflicts:

```bash
# Update your branch with latest main
git checkout feature/your-feature-name
git fetch origin main
git merge origin/main

# Resolve conflicts in your editor
# Look for conflict markers: <<<<<<<, =======, >>>>>>>

# After resolving, stage changes
git add .

# Complete the merge
git commit -m "Resolve merge conflicts with main"

# Push updated branch
git push
```

### Failed CI Checks

```bash
# View check status
gh pr checks <PR_NUMBER>

# View detailed logs
gh run view <RUN_ID>

# Re-run failed checks (if transient failure)
gh run rerun <RUN_ID>
```

### Accidentally Pushed to Main

```bash
# DON'T PANIC! Create a branch from current main
git branch backup-branch

# Reset main to previous commit
git checkout main
git reset --hard origin/main

# Push your work from backup branch
git checkout backup-branch
git push -u origin backup-branch

# Create PR from backup branch
gh pr create
```

### Forgot to Create Feature Branch

```bash
# Create branch from current state
git checkout -b feature/forgot-to-branch

# Push to remote
git push -u origin feature/forgot-to-branch

# Reset main to match origin
git checkout main
git reset --hard origin/main
```

### Need to Update PR After Review

```bash
# Make changes on your feature branch
git checkout feature/your-feature-name

# Make edits, then commit
git add .
git commit -m "Address review feedback: clarify error handling"

# Push updates
git push

# PR automatically updates!
```

### Delete Wrong Branch

```bash
# Restore deleted local branch (if not pushed)
git reflog
git checkout -b feature/restored <commit-hash>

# Restore deleted remote branch
git push origin <commit-hash>:refs/heads/feature/restored
```

---

## Common Scenarios

### Scenario 1: Simple Feature Addition

```bash
# 1. Create branch
git checkout -b feature/add-new-button
git add src/components/Button.jsx
git commit -m "Add new button component"
git push -u origin feature/add-new-button

# 2. Create PR
gh pr create --title "Add new button component" --fill

# 3. Merge after approval
gh pr merge 5 --merge

# 4. Cleanup
git checkout main
git pull
git branch -d feature/add-new-button
```

### Scenario 2: Bug Fix with Testing

```bash
# 1. Create branch
git checkout -b fix/rating-bug

# 2. Make changes and test
# Edit code...
# Run tests...

# 3. Commit
git add .
git commit -m "Fix rating bug where votes weren't counted correctly"

# 4. Push and create PR
git push -u origin fix/rating-bug
gh pr create --title "Fix rating count bug" --fill

# 5. After CI passes and review, merge
gh pr merge 6 --merge --delete-branch

# 6. Cleanup
git checkout main
git pull
git branch -d fix/rating-bug
```

### Scenario 3: Multiple Commits in One PR

```bash
# 1. Create branch
git checkout -b feature/user-profile

# 2. Make multiple commits
git commit -m "Add user profile model"
git commit -m "Add profile API endpoints"
git commit -m "Add profile UI component"

# 3. Push all commits
git push -u origin feature/user-profile

# 4. Create PR (all commits included)
gh pr create --title "Add user profile feature" --fill

# 5. Merge (creates one merge commit)
gh pr merge 7 --merge

# 6. Cleanup
git checkout main
git pull
git branch -d feature/user-profile
```

---

## GitHub CLI Commands Reference

```bash
# Install gh CLI
brew install gh  # macOS
# See https://cli.github.com/ for other platforms

# Authenticate
gh auth login

# PR Commands
gh pr create                    # Create PR interactively
gh pr create --fill            # Auto-fill from commits
gh pr list                     # List all PRs
gh pr status                   # Show your PR status
gh pr view <NUMBER>            # View PR details
gh pr view <NUMBER> --web      # Open PR in browser
gh pr checkout <NUMBER>        # Checkout PR branch
gh pr merge <NUMBER>           # Merge PR
gh pr close <NUMBER>           # Close PR
gh pr reopen <NUMBER>          # Reopen PR
gh pr review <NUMBER>          # Review PR
gh pr checks <NUMBER>          # View CI checks

# Repository Commands
gh repo view                   # View repo details
gh repo view --web            # Open repo in browser
```

---

## Additional Resources

- [GitHub Flow](https://guides.github.com/introduction/flow/)
- [Pull Request Best Practices](https://github.com/blog/1943-how-to-write-the-perfect-pull-request)
- [GitHub CLI Documentation](https://cli.github.com/manual/)
- [Git Branching Model](https://nvie.com/posts/a-successful-git-branching-model/)
- [Conventional Commits](https://www.conventionalcommits.org/)

---

## Project-Specific Notes

- **Main Branch**: `main` (default for PRs)
- **Required Reviews**: 0 (currently no required reviews)
- **CI/CD**: GitHub Actions workflows run on PR creation
- **Branch Protection**: None (currently)
- **Merge Strategy**: Prefer merge commits (`--merge`)
- **Auto-Delete**: Branches are NOT auto-deleted (manual cleanup required)

---

**Last Updated**: December 2024
**Maintained By**: Sujit K Singh

For questions or issues with this workflow, please open an issue or contact the repository maintainer.