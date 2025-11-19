@echo off
echo Initializing Git repository...
git init

echo Adding remote origin...
git remote add origin https://github.com/minhthetroller/Linkedout-Mobile-App.git

echo Adding all files...
git add .

echo Committing files...
git commit -m "Initial commit: Linkedout Android application with authentication and navigation features"

echo Creating main branch...
git branch -M main

echo Pushing to remote repository...
git push -u origin main

echo.
echo Git repository setup complete!
pause

