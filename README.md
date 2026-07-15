EnglishMaster - AI-Powered IELTS Writing Evaluator

EnglishMaster is an advanced web application designed to help IELTS candidates improve their speaking, writing, listening, and reading skills. By leveraging the power of Google's Gemini API, EnglishMaster provides instant, comprehensive feedback on essays, covering grammar, spelling, vocabulary, and sentence structure, ultimately assigning an estimated IELTS Band Score (0-9).

🚀 Key Features

AI-Driven Evaluation: Uses the Gemini API to act as an official IELTS Writing Examiner.

Instant Feedback: Receive detailed analysis within seconds after submission.

Structured Analysis:

Grammar & Spelling: Identification of specific mistakes.

Coherence & Structure: Suggestions to improve logical flow.

Vocabulary Improvement: Recommendations for higher-level words.

Band Score Calculation: An estimated score out of 9.0 based on IELTS criteria.

User-Friendly Interface: Clean, distraction-free environment for writing practice.

🛠️ Tech Stack

Backend: Java Spring Boot

Frontend: HTML5, Tailwind CSS, JavaScript (Fetch API)

AI Engine: Google Gemini API

Data Format: JSON Schema for structured feedback

📋 Prerequisites

Before running the application, ensure you have:

Java Development Kit (JDK) 17 or higher.

Maven.

A valid Google Gemini API Key.

⚙️ Installation & Setup

Clone the repository:

git clone https://github.com/your-username/EnglishMaster.git
cd EnglishMaster


Run the application:

mvn spring-boot:run


Access the application:
Open your browser and navigate to http://localhost:8080/.

🧠 How It Works

Prompt Engineering: When a user submits an essay, EnglishMaster sends a structured prompt to the Gemini API, instructing it to act as an IELTS examiner.

JSON Response: The API returns a structured JSON object containing the Band Score, Strengths, Weaknesses, and Improvement Suggestions.

Rendering: The frontend parses this JSON and dynamically displays the feedback in a clean, readable dashboard.
