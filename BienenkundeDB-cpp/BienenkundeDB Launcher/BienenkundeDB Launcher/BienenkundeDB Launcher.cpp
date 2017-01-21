// BienenkundeDB Launcher.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include "BienenkundeDB Launcher.h"
#include <cstdio>
#include <string>
#include <stdexcept>
#include <memory>
#include <iostream>
#include <fstream>

#include <ShlObj.h>
#define MAX_LOADSTRING 100

#include <io.h>
#define access _access_s

// Global Variables:
//HINSTANCE hInst;                                // current instance

int versionNumber = 1;

enum exitCode {
	default,
	launchDBDFailed,
	getFolderPathFailed,
	restartLauncherFailed,
	restartBDBFailed,
	needRestartBDB,
	needRestartLauncher,
};

int launchBDB(bool updated)
{
	char jarFile[MAX_PATH];
	if (SUCCEEDED(SHGetFolderPathA(NULL, CSIDL_APPDATA, NULL, 0, jarFile))) {
		std::string _userDirStr(jarFile);
		std::string concatted_stdstr1 = "if exist " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB_neu.jar" + " del " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB.jar";
		std::string concatted_stdstr2 = "if exist " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB_neu.jar" + " ren " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB_neu.jar BienenkundeDB.jar";
		LPSTR cmd1 = const_cast<LPSTR>(concatted_stdstr1.c_str());
		LPSTR cmd2 = const_cast<LPSTR>(concatted_stdstr2.c_str());
		system(cmd1);
		system(cmd2);
	}
	// Get the AppData directory as CreateProcess needs the full path to the installation folder
	wchar_t userDir[MAX_PATH];
	if (SUCCEEDED(SHGetFolderPath(NULL, CSIDL_APPDATA, NULL, 0, userDir))) {
		STARTUPINFO si;
		PROCESS_INFORMATION pi;
		DWORD exit_code;
		int spi = sizeof(pi);
		int ssi = sizeof(si);
		ZeroMemory(&si, sizeof(si));
		si.cb = sizeof(STARTUPINFO); //sizeof(si);
		ZeroMemory(&pi, sizeof(pi));

		// Determine the java installation path
		//std::string whereJavaw = exec("where javaw");
		//std::string pathJavaw = exec("for %i in (javaw.exe) do @echo. %~$PATH:i");
		
		// lets hope java always creates those symlinks in "C:\ProgramData\Oracle\Java\javapath"
		// the following function is just used to correctly determine the ProgramData path as it is not always the C:\ drive
		wchar_t programdataDir[MAX_PATH];
		if (!SUCCEEDED(SHGetFolderPath(NULL, CSIDL_COMMON_APPDATA, NULL, 0, programdataDir))) {
			return exitCode::getFolderPathFailed;
		}
		std::wstring w_progDataStr(programdataDir);
		std::wstring concatted_progstr = w_progDataStr + L"\\Oracle\\Java\\javapath\\javaw.exe";
		LPWSTR javaPath = const_cast<LPWSTR>(concatted_progstr.c_str());

		// Create the cmd statement for launching the jar file
		std::wstring w_userDirStr(userDir);
		std::wstring concatted_stdstr = L" -jar " + w_userDirStr + L"\\BienenkundeDB\\BienenkundeDB.jar" + L" launcherVersion " + std::to_wstring(versionNumber);
		LPWSTR cmd = const_cast<LPWSTR>(concatted_stdstr.c_str());

		// Start the child process.
		if(!CreateProcess(javaPath,
			cmd,	 // Command line.
			NULL,			 // Process handle not inheritable.
			NULL,			 // Thread handle not inheritable.
			0,			// Set handle inheritance to FALSE.
			NORMAL_PRIORITY_CLASS,				// No creation flags.
			NULL,			 // Use parent's environment block.
			NULL,			 // Use parent's starting directory.
			&si,			  // Pointer to STARTUPINFO structure.
			&pi))			// Pointer to PROCESS_INFORMATION structure.
		{
			if (updated) {
				return exitCode::restartBDBFailed;
			}
			else {
				return exitCode::launchDBDFailed;
			}
		}
		WaitForSingleObject(pi.hProcess, INFINITE);

		GetExitCodeProcess(pi.hProcess, &exit_code);

		CloseHandle(pi.hProcess);
		CloseHandle(pi.hThread);

		return exit_code;
	}
	else {
		return exitCode::getFolderPathFailed;
	}
}

int restartLauncher() {
	char jarFile[MAX_PATH];
	if (SUCCEEDED(SHGetFolderPathA(NULL, CSIDL_APPDATA, NULL, 0, jarFile))) {
		std::string _userDirStr(jarFile);
		std::string concatted_stdstr1 = "if exist " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB_neu.jar" + " del " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB.jar";
		std::string concatted_stdstr2 = "if exist " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB_neu.jar" + " ren " + _userDirStr + "\\BienenkundeDB\\BienenkundeDB_neu.jar BienenkundeDB.jar";
		LPSTR cmd1 = const_cast<LPSTR>(concatted_stdstr1.c_str());
		LPSTR cmd2 = const_cast<LPSTR>(concatted_stdstr2.c_str());
		system(cmd1);
		system(cmd2);
	}
		HMODULE hModule = NULL;
		GetModuleHandleEx(
			GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS,
			(LPCTSTR)restartLauncher,
			&hModule);
		wchar_t buffer[MAX_PATH];
		GetModuleFileName(hModule, buffer, MAX_PATH);

		STARTUPINFO si;
		PROCESS_INFORMATION pi;
		int spi = sizeof(pi);
		int ssi = sizeof(si);
		ZeroMemory(&si, sizeof(si));
		si.cb = sizeof(STARTUPINFO); //sizeof(si);
		ZeroMemory(&pi, sizeof(pi));

		if (!CreateProcess(buffer,
			NULL,	 // Command line.
			NULL,			 // Process handle not inheritable.
			NULL,			 // Thread handle not inheritable.
			0,			// Set handle inheritance to FALSE.
			NORMAL_PRIORITY_CLASS,				// No creation flags.
			NULL,			 // Use parent's environment block.
			NULL,			 // Use parent's starting directory.
			&si,			  // Pointer to STARTUPINFO structure.
			&pi))
		{
			return exitCode::restartLauncherFailed;
		}

		ExitProcess(0);
}

int handleExitCodes(int ret) {
	switch (ret)
	{
	case exitCode::default:
		return exitCode::default;
	case exitCode::launchDBDFailed:
		MessageBoxW(NULL, L"BienenkundeDB konnte nicht gestartet werden. Stellen Sie bitte sicher, dass Java auf" \
			" diesem PC installiert ist und dass BienenkundeDB korrekt installiert ist.\n\n" \
			"Bei Problemen Kontaktieren Sie mich bitte unter markus.hofmann97@gmx.de", L"Achtung", MB_OK | MB_ICONERROR);
		return ret;
	case exitCode::getFolderPathFailed:
		MessageBoxW(NULL, L"Dieses Programm funktioniert nur unter Windows und Java muss korrekt installiert sein!\n\n" \
			"Bei Problemen Kontaktieren Sie mich bitte unter markus.hofmann97@gmx.de", L"Achtung", MB_OK | MB_ICONERROR);
		return ret;
	case exitCode::restartLauncherFailed:
		MessageBoxW(NULL, L"Der Launcher konnte nach einem Update nicht neugestartet werden!\n\n" \
			"Bei Problemen Kontaktieren Sie mich bitte unter markus.hofmann97@gmx.de", L"Achtung", MB_OK | MB_ICONERROR);
		return ret;
	case exitCode::restartBDBFailed:
		MessageBoxW(NULL, L"BienenkundeDB konnte nach einem Update nicht neu gestartet werden" \
			"bitte stellen Sie sicher, dass unter %appdata%\\BienenkundeDB\\ die Datei BienenkundeDB.jar vorhanden ist.\n\n" \
			"Bei Problemen Kontaktieren Sie mich bitte unter markus.hofmann97@gmx.de",
			L"Achtung", MB_OK | MB_ICONERROR);
		return ret;
	case exitCode::needRestartBDB:
		return handleExitCodes(launchBDB(true));
	case exitCode::needRestartLauncher:
		return handleExitCodes(restartLauncher());
	default:
		return ret;
	}
}

int APIENTRY wWinMain(_In_ HINSTANCE hInstance,
                     _In_opt_ HINSTANCE hPrevInstance,
                     _In_ LPWSTR    lpCmdLine,
                     _In_ int       nCmdShow)
{
    UNREFERENCED_PARAMETER(hPrevInstance);
    UNREFERENCED_PARAMETER(lpCmdLine);

	if (wcscmp(lpCmdLine, L"getVersionInfo") == 0) {
		wchar_t userDir[MAX_PATH];
		if (SUCCEEDED(SHGetFolderPath(NULL, CSIDL_APPDATA, NULL, 0, userDir))) {
			std::wstring w_userDirStr(userDir);
			std::wstring concatted_stdstr = w_userDirStr + L"\\BienenkundeDB\\launcherVerInfo.txt";
			LPWSTR dir = const_cast<LPWSTR>(concatted_stdstr.c_str());
			std::ofstream myfile(dir);
			myfile << versionNumber;
			myfile.close();
		}
		return 0;
	}

	int ret = launchBDB(false);
	return handleExitCodes(ret);
}