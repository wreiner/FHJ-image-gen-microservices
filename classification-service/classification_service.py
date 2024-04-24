from flask import Flask, request, jsonify
from transformers import pipeline, AutoModelForSequenceClassification, AutoTokenizer

app = Flask(__name__)

# directory where to store models
cache_directory = "/models"

# Load the model and tokenizer with the specified cache directory
model = AutoModelForSequenceClassification.from_pretrained(
    "eliasalbouzidi/distilbert-nsfw-text-classifier",
    cache_dir=cache_directory
)
tokenizer = AutoTokenizer.from_pretrained(
    "eliasalbouzidi/distilbert-nsfw-text-classifier",
    cache_dir=cache_directory
)

# Set up the sentiment analysis pipeline using the loaded model and tokenizer
sentiment_task = pipeline(
    "text-classification",
    model=model,
    tokenizer=tokenizer
)

@app.route('/analyze', methods=['POST'])
def analyze_text():
    if not request.json or 'prompt' not in request.json:
        return jsonify({'error': 'No prompt provided'}), 400

    prompt = request.json['prompt']
    result = sentiment_task(prompt)
    return jsonify(result)

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
