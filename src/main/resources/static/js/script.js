// Module Navigation
function showModule(moduleId) {
    const modules = document.querySelectorAll('.module');
    modules.forEach(m => m.classList.remove('active'));
    document.getElementById(moduleId).classList.add('active');
}

// ===== Speaking Module =====
const words = ["Hello", "World", "Good", "Morning", "University", "Student"];
let currentIndex = 0;
let score = 0;

const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
recognition.lang = 'en-US';

function startSpeaking() {
    recognition.start();
}

recognition.onresult = function (event) {
    const spokenText = event.results[0][0].transcript;
    document.getElementById("spokenText").innerText = spokenText;

    const expected = words[currentIndex].toLowerCase();
    const spoken = spokenText.toLowerCase().trim();

    if (spoken === expected) {
        score++;
        document.getElementById("feedback").innerText = "✅ Perfect!";
        speakText("Good job!");
    } else {
        document.getElementById("feedback").innerText = `❌ Try Again! Correct: "${words[currentIndex]}"`;
        speakText(`Please try again. The correct answer is ${words[currentIndex]}`);
    }

    document.getElementById("score").innerText = score;
    nextWord();
};

recognition.onerror = function (event) {
    console.error(event.error);
};

function speakText(text) {
    const utterance = new SpeechSynthesisUtterance(text);
    speechSynthesis.speak(utterance);
}

function nextWord() {
    currentIndex = (currentIndex + 1) % words.length;
    document.getElementById("word").innerText = words[currentIndex];
    document.getElementById("spokenText").innerText = "---";
    document.getElementById("feedback").innerText = "";
}

// ===== Writing Module =====
let timer;
function startWriting(topic) {
    document.getElementById('writingTopic').innerText = topic;
    document.getElementById('writingInput').value = '';
    // 5 minute timer
    timer = setTimeout(() => submitWriting(), 5 * 60 * 1000);
}

async function submitWriting() {
    clearTimeout(timer);
    const text = document.getElementById('writingInput').value;

    const response = await fetch('/api/writing/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text })
    });
    const result = await response.json();
    document.getElementById('writingFeedback').innerText = result.feedback;
    document.getElementById('writingScore').innerText = result.score;
}

// ===== Reading Module =====
function checkReading() {
    const input = document.getElementById("readingInput").value.trim();
    const keywords = ["Lorem", "ipsum", "dolor"]; // Example keywords
    let correct = 0;

    keywords.forEach(word => {
        if (input.includes(word)) correct++;
    });

    document.getElementById("readingFeedback").innerText = `You found ${correct} keyword(s).`;
    document.getElementById("readingInput").value = "";
}

// ===== Listening Module =====
function checkListening() {
    const input = document.getElementById("listeningInput").value.trim();
    const expectedText = "This is a sample sentence"; // example
    if (input.toLowerCase() === expectedText.toLowerCase()) {
        document.getElementById("listeningFeedback").innerText = "✅ Correct!";
    } else {
        document.getElementById("listeningFeedback").innerText = `❌ Try again! Correct: "${expectedText}"`;
    }
    document.getElementById("listeningInput").value = "";
}

// Show home by default
showModule('home');
