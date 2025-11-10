# 📚 College Library System (Java + SQLite)

A simple **Java Swing + SQLite** application that manages borrowing, returning, and tracking of books in a college library.

---

## 🚀 Features

- 🧾 **Borrow Book:** Add a new book borrowed by a student.
- 🔍 **Search Student by UID:** Look up all books borrowed by a specific student.
- 📚 **View Borrowed Books:** Display a list of all currently borrowed books.
- 🔁 **Return Book:** Remove a record when a book is returned.
- 💾 **SQLite Integration:** Data is stored locally using the `sqlite-jdbc` driver.
- 🪟 **GUI Interface:** Built with Java Swing for a simple, interactive user experience.

---

## 🛠️ Requirements

- **Java Development Kit (JDK 17 or higher)**  
  [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- **SQLite JDBC Driver:** `sqlite-jdbc-3.51.0.0.jar`
- **VS Code** or any Java IDE (Eclipse, IntelliJ IDEA, NetBeans, etc.)

---

## 📂 Project Structure

```
JAVAPROJECT/
│
├── collegelibrarysystem.java
├── sqlite-jdbc-3.51.0.0.jar
└── README.md
```

---

## ⚙️ Setup & Run Instructions

### 1. Clone or create the project
Place both files in the same folder:
```
collegelibrarysystem.java
sqlite-jdbc-3.51.0.0.jar
```

### 2. Open terminal in the project folder
In VS Code:
```
View → Terminal
```

### 3. Compile the Java file

**Windows:**
```bash
javac -cp ".;sqlite-jdbc-3.51.0.0.jar" collegelibrarysystem.java
```

**macOS/Linux:**
```bash
javac -cp ".:sqlite-jdbc-3.51.0.0.jar" collegelibrarysystem.java
```

### 4. Run the program

**Windows:**
```bash
java -cp ".;sqlite-jdbc-3.51.0.0.jar" collegelibrarysystem
```

**macOS/Linux:**
```bash
java -cp ".:sqlite-jdbc-3.51.0.0.jar" collegelibrarysystem
```

---

## 💾 Database Info

- The program automatically creates a database file named:
  ```
  college_libraries.db
  ```
- All book and student records are stored inside this SQLite database.
- No external setup required — everything is handled locally.

---

## 🧱 Technologies Used

| Component | Technology |
|------------|-------------|
| Language | Java |
| Database | SQLite |
| GUI | Java Swing |
| JDBC Driver | sqlite-jdbc-3.51.0.0.jar |

---

## 👨‍💻 Author

**Parag Bajaj**  
📧 *[Your Email or Portfolio Link]*  
💡 *Built for college project / learning purpose.*

---

## 🪪 License

This project is open-source and free to use for educational or personal purposes.
