# Version Control System (Language-Independent Line Tracking Tool)

[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Academic-blue.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Active-success.svg)](https://github.com)

A sophisticated line mapping tool that tracks how lines of code evolve between different versions of files. Based on the LHDiff methodology, this implementation combines multiple similarity metrics to achieve accurate line tracking across various code change scenarios.

## 🎯 Overview

LHDiff is a research implementation of a line tracking algorithm designed for software engineering tasks such as:

- **Version Comparison**: Tracking how code changes between file versions
- **Bug Localization**: Identifying where bugs were introduced
- **Code Review**: Understanding code evolution patterns
- **Refactoring Analysis**: Tracking line movements during restructuring
- **Change Impact Analysis**: Measuring the extent of modifications


## 📦 Setup Instructions

### Step 1: Download Required Libraries

You need 3 JAR files in the `lib/` folder. Click these links to download:

1. [commons-text-1.10.0.jar](https://repo1.maven.org/maven2/org/apache/commons/commons-text/1.10.0/commons-text-1.10.0.jar)
2. [commons-lang3-3.12.0.jar](https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar)
3. [commons-collections4-4.4.jar](https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar)

**Place all 3 JARs in the `lib/` folder.**

### Step 2: Verify Folder Structure

```
VCS-project/
├── lib/
│   ├── commons-text-1.10.0.jar          ✓ Downloaded
│   ├── commons-lang3-3.12.0.jar         ✓ Downloaded
│   └── commons-collections4-4.4.jar     ✓ Downloaded
├── Code/
│   └── CompleteLHDiff.java
└── Dataset/
    ├── prog1.java
    ├── prog1new.java
    └── ...
```

### Step 3: Compile the Code

**Windows:**
```bash
cd Code
javac -cp ".;../lib/*" CompleteLHDiff.java
```

**Linux/Mac:**
```bash
cd Code
javac -cp ".:../lib/*" CompleteLHDiff.java
```

## 🚀 Running the Code

### Option 1: Compare Two Files

**Windows:**
```bash
java -cp ".;../lib/*" CompleteLHDiff oldFile.java newFile.java output.txt
```

**Linux/Mac:**
```bash
java -cp ".:../lib/*" CompleteLHDiff oldFile.java newFile.java output.txt
```

**Example:**
```bash
java -cp ".;../lib/*" CompleteLHDiff ../Dataset/prog1.java ../Dataset/prog1new.java result.txt
```

## 👥 Authors

****
- **Harmit**  
- **Pramil** 
- **Khalid** 

**Course:** COMP3110 - INTRO. TO SOFTWARE ENGINEERING
**Institution:** University Of Windsor


---

**Last Updated:** December 2025 
**Version:** 1.0.0  
**Status:** ✅ Complete and Tested

---

Made with ❤️ by HKP labs!


