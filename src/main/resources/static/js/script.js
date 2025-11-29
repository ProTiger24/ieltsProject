<<<<<<< HEAD

let mediaRecorder, audioChunks = [], startTime;
const maxTime = 3 * 60 * 1000; // 3 minutes
let recordingTimeout, countdownInterval, isRecording = false;
let timer;

// ===== Module Navigation =====
function showModule(moduleId) {
    const modules = document.querySelectorAll('.module');
    modules.forEach(m => m.classList.remove('active'));

    const moduleElement = document.getElementById(moduleId);
    if (moduleElement) {
        moduleElement.classList.add('active');
    } else {
        console.warn(`⚠️ Module with id "${moduleId}" not found in DOM.`);
    }
}

// ===== Speaking Module =====
async function startRecording() {
    try {
        const stream = await navigator.mediaDevices.getUserMedia({
            audio: {
                echoCancellation: true,
                noiseSuppression: true,
                sampleRate: 44100
            }
        });

        // MediaRecorder initialization
        mediaRecorder = new MediaRecorder(stream, {
            mimeType: 'audio/webm;codecs=opus'
        });

        audioChunks = [];

        mediaRecorder.ondataavailable = (event) => {
            if (event.data.size > 0) {
                audioChunks.push(event.data);
            }
        };

        mediaRecorder.onstop = async () => {
            // Stream cleanup
            stream.getTracks().forEach(track => track.stop());
            await processAudioRecording();
        };

        // Start recording
        mediaRecorder.start(1000); // Collect data every second
        isRecording = true;
        startTime = Date.now();

        // UI updates
        document.getElementById('startRecordBtn').disabled = true;
        document.getElementById('stopRecordBtn').disabled = false;
        document.getElementById('speakingStatus').innerText = "🎤 Recording... Speak now!";
        document.getElementById('speakingFeedback').innerHTML = "";

        // Start timer
        startSpeakingTimer();

        console.log("Recording started");

    } catch (error) {
        console.error("Error starting recording:", error);
        document.getElementById('speakingStatus').innerText = "❌ Error: Cannot access microphone. Please check permissions.";
    }
}

async function stopRecording() {
    if (mediaRecorder && isRecording) {
        mediaRecorder.stop();
        isRecording = false;

        // UI updates
        document.getElementById('stopRecordBtn').disabled = true;
        document.getElementById('speakingStatus').innerText = "⏳ Processing your recording...";

        // Stop timer
        stopSpeakingTimer();

        console.log("Recording stopped");
    }
}

function startSpeakingTimer() {
    const timerElement = document.getElementById('speakingTimer');
    let timeLeft = 180; // 3 minutes in seconds

    timerElement.textContent = "03:00";
    timerElement.style.color = '#333';

    countdownInterval = setInterval(() => {
        timeLeft--;
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

        // Change color when time is running out
        if (timeLeft <= 30) {
            timerElement.style.color = '#dc3545';
        }

        if (timeLeft <= 0) {
            stopRecording();
        }
    }, 1000);
}

function stopSpeakingTimer() {
    if (countdownInterval) {
        clearInterval(countdownInterval);
        countdownInterval = null;
    }
}

async function processAudioRecording() {
    const feedbackEl = document.getElementById("speakingFeedback");
    const statusEl = document.getElementById("speakingStatus");

    try {
        const audioBlob = new Blob(audioChunks, { type: "audio/webm" });
        const formData = new FormData();
        formData.append("audioFile", audioBlob, "recording.webm");

        console.log("Sending audio to server...");

        const response = await fetch("/api/speaking/submit-audio", { // ✅ Corrected endpoint
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();

        feedbackEl.innerHTML = `
            <div class="feedback-item">
                <strong>Score:</strong> ${result.score || 'N/A'}/9.0
            </div>
            <div class="feedback-item">
                <strong>Strengths:</strong> ${result.strengths || 'N/A'}
            </div>
            <div class="feedback-item">
                <strong>Weaknesses:</strong> ${result.weaknesses || 'N/A'}
            </div>
            <div class="feedback-item">
                <strong>Suggestions:</strong> ${result.suggestions || 'N/A'}
            </div>
        `;

        statusEl.innerText = "✅ Evaluation completed!";
        document.getElementById('startRecordBtn').disabled = false;

    } catch (err) {
        console.error("Processing error:", err);
        feedbackEl.innerHTML = `
            <div style="color: #dc3545;">
                <strong>Error:</strong> ${err.message}
            </div>
        `;
        statusEl.innerText = "❌ Evaluation failed";
        document.getElementById('startRecordBtn').disabled = false;
    }
}

// ===== Writing Module =====
async function submitWriting() {
    const text = document.getElementById('writingInput').value.trim();
    const feedbackContainer = document.getElementById('writingFeedback');

    if (!text) {
        if (feedbackContainer) {
            feedbackContainer.innerHTML = "<div style='color: #dc3545;'>⚠️ Please write something before submitting.</div>";
        }
        return;
    }

    // Show loading
    if (feedbackContainer) {
        feedbackContainer.innerHTML = "<div>⏳ Evaluating your writing...</div>";
    }

    try {
        const response = await fetch('/api/writing/submit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ text: text })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();

        if (feedbackContainer) {
            feedbackContainer.innerHTML = `
                <div class="feedback-item">
                    <strong>Score:</strong> ${result.score || 'N/A'}/9.0
                </div>
                <div class="feedback-item">
                    <strong>Strengths:</strong> ${result.strengths || 'N/A'}
                </div>
                <div class="feedback-item">
                    <strong>Weaknesses:</strong> ${result.weaknesses || 'N/A'}
                </div>
                <div class="feedback-item">
                    <strong>Suggestions:</strong> ${result.suggestions || 'N/A'}
                </div>
            `;
        }

    } catch (err) {
        console.error("❌ Writing request failed:", err);
        if (feedbackContainer) {
            feedbackContainer.innerHTML = `
                <div style="color: #dc3545;">
                    <strong>Error:</strong> ${err.message}
                    <br>Please check if the server is running.
                </div>
            `;
        }
    }
=======
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
>>>>>>> 3166e765e07f8d60d37eb9486f5f60608864602a
}

// ===== Reading Module =====
function checkReading() {
    const input = document.getElementById("readingInput").value.trim();
<<<<<<< HEAD
    const keywords = ["Lorem", "ipsum", "dolor", "sample", "text"];
    let correct = 0;

    if (!input) {
        document.getElementById("readingFeedback").innerHTML = "<div style='color: #dc3545;'>⚠️ Please enter some text.</div>";
        return;
    }

    keywords.forEach(word => {
        if (input.toLowerCase().includes(word.toLowerCase())) correct++;
    });

    const percentage = Math.round((correct / keywords.length) * 100);

    document.getElementById("readingFeedback").innerHTML = `
        <div class="feedback-item">
            <strong>Result:</strong> You found ${correct} out of ${keywords.length} keywords (${percentage}%)
        </div>
        <div class="feedback-item">
            <strong>Keywords:</strong> ${keywords.join(', ')}
        </div>
    `;
=======
    const keywords = ["Lorem", "ipsum", "dolor"]; // Example keywords
    let correct = 0;

    keywords.forEach(word => {
        if (input.includes(word)) correct++;
    });

    document.getElementById("readingFeedback").innerText = `You found ${correct} keyword(s).`;
    document.getElementById("readingInput").value = "";
>>>>>>> 3166e765e07f8d60d37eb9486f5f60608864602a
}

// ===== Listening Module =====
function checkListening() {
    const input = document.getElementById("listeningInput").value.trim();
<<<<<<< HEAD
    const expectedText = "This is a sample sentence for listening practice";

    if (!input) {
        document.getElementById("listeningFeedback").innerHTML = "<div style='color: #dc3545;'>⚠️ Please enter what you heard.</div>";
        return;
    }

    const isCorrect = input.toLowerCase() === expectedText.toLowerCase();

    if (isCorrect) {
        document.getElementById("listeningFeedback").innerHTML = `
            <div style="color: #28a745;">✅ Correct! Well done!</div>
        `;
    } else {
        document.getElementById("listeningFeedback").innerHTML = `
            <div style="color: #dc3545;">❌ Not quite right.</div>
            <div class="feedback-item">
                <strong>You entered:</strong> "${input}"
            </div>
            <div class="feedback-item">
                <strong>Correct answer:</strong> "${expectedText}"
            </div>
        `;
    }
}

// ===== Utility Functions =====
function clearWriting() {
    document.getElementById('writingInput').value = '';
    document.getElementById('writingFeedback').innerHTML = '';
}

function clearSpeaking() {
    document.getElementById('speakingFeedback').innerHTML = '';
    document.getElementById('speakingStatus').innerText = 'Click "Start Recording" to begin';
    document.getElementById('speakingTimer').textContent = '03:00';
    document.getElementById('speakingTimer').style.color = '#333';
}

// ===== Initialize Default Module =====
document.addEventListener('DOMContentLoaded', function () {
    showModule('home');

    // Button event listeners
    const startBtn = document.getElementById('startRecordBtn');
    const stopBtn = document.getElementById('stopRecordBtn');

    if (startBtn) startBtn.addEventListener('click', startRecording);
    if (stopBtn) stopBtn.addEventListener('click', stopRecording);
});
=======
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
>>>>>>> 3166e765e07f8d60d37eb9486f5f60608864602a
