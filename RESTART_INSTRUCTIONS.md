# IMPORTANT: Restart Required for Environment Variables

## Problem
The "Failed to fetch" error occurs because the React dev server was started **before** the `.env` file was created. React only reads environment variables at startup.

## Solution

### 1. Stop the React Dev Server
Press `Ctrl+C` in the terminal running the React app, or:
```powershell
Get-Process -Name "node" | Where-Object {$_.StartTime -lt (Get-Date).AddHours(-1)} | Stop-Process -Force
```

### 2. Restart React with Environment Variables
```powershell
cd "C:\Users\joshu\OneDrive\Desktop\PROJECT\Fall2025\SWE\COSC-412-untitled-project\calendar"
npm start
```

### 3. Start the Backend (in a separate terminal)
```powershell
cd "C:\Users\joshu\OneDrive\Desktop\PROJECT\Fall2025\SWE\syllabus-copilot-backend"
.\mvnw.cmd spring-boot:run
```

## Verification

### Test Backend:
```powershell
curl http://localhost:8081/
```
Expected: Welcome message with API documentation

### Test Frontend:
1. Open browser: http://localhost:3000
2. Go to Syllabus Manager
3. Try uploading a PDF
4. Check browser console (F12) - should see API calls to `http://localhost:8081`

## Troubleshooting

### If still getting "Failed to fetch":
1. Check browser console (F12) for actual API URL being used
2. Verify `.env` file exists in `calendar/` folder
3. Restart React dev server (it must be restarted after creating `.env`)
4. Check backend is running: `curl http://localhost:8081/api/syllabi/test`

### Backend keeps shutting down:
- Don't run backend commands in the same terminal as curl/test commands
- Use separate terminal windows for backend and testing
- Backend should run continuously in background

## Quick Commands

### Kill all Node processes (restart React):
```powershell
Stop-Process -Name "node" -Force -ErrorAction SilentlyContinue
```

### Kill all Java processes (restart backend):
```powershell
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
```

### Start both services:
```powershell
# Terminal 1 - Backend
cd "C:\Users\joshu\OneDrive\Desktop\PROJECT\Fall2025\SWE\syllabus-copilot-backend"
.\mvnw.cmd spring-boot:run

# Terminal 2 - Frontend
cd "C:\Users\joshu\OneDrive\Desktop\PROJECT\Fall2025\SWE\COSC-412-untitled-project\calendar"
npm start
```
