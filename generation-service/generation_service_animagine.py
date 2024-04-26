from os import environ
import torch
from diffusers import DiffusionPipeline
import json
import pika

# RabbitMQ connection details
RABBITMQ_HOST = environ.get('RABBITMQ_HOST', 'localhost')
RABBITMQ_PORT = environ.get('RABBITMQ_PORT', 5672)
RABBITMQ_USER = environ.get('RABBITMQ_USER', 'guest')
RABBITMQ_PASS = environ.get('RABBITMQ_PASS', 'guest')
QUEUE_NAME = environ.get('QUEUE_NAME', 'generation_request')

# S3 connection details
S3_ENDPOINT = environ.get('S3_ENDPOINT', 's3.openshift-storage.svc')
S3_ACCESS_KEY = environ.get('S3_ACCESS_KEY', None)
if S3_ACCESS_KEY is None:
    print("S3_ACCESS_KEY is not set")
    sys.exit(1)

S3_SECRET_KEY = environ.get('S3_SECRET', None)
if S3_SECRET_KEY is None:
    print("S3_SECRET_KEY is not set")
    sys.exit(1)

S3_BUCKET = environ.get('S3_BUCKET', None)
if S3_BUCKET is None:
    print("S3_BUCKET is not set")
    sys.exit(1)

environ['AWS_CA_BUNDLE'] = ['/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt']


# Replace with your RabbitMQ connection details
connection_params = pika.ConnectionParameters(
    host=RABBITMQ_HOST,
    port=RABBITMQ_PORT,
    credentials=pika.PlainCredentials(RABBITMQ_USER, RABBITMQ_PASS)
)

# directory to store models
cache_directory = "/models"
# directory to temporarily store generated images
output_directory = "/output"

def generate_image(prompt, uuid):
    print(f"generating image for uuid: {uuid} with prompt: {prompt}")

    pipe = DiffusionPipeline.from_pretrained(
        "cagliostrolab/animagine-xl-3.1",
        torch_dtype=torch.float32,
        use_safetensors=True,
        cache_dir=cache_directory
    )
    pipe.to('cpu')

    negative_prompt = "nsfw, lowres, (bad), text, error, fewer, extra, missing, worst quality, jpeg artifacts, low quality, watermark, unfinished, displeasing, oldest, early, chromatic aberration, signature, extra digits, artistic error, username, scan, [abstract]"

    image = pipe(
        prompt,
        negative_prompt=negative_prompt,
        width=832,
        height=1216,
        guidance_scale=7,
        num_inference_steps=28
    ).images[0]

    image.save(f"/{output_directory}/{uuid.png}")
    print(f"image saved for uuid: {uuid}")

def save_image_to_s3(uuid):
    print(f"uploading image to S3 for uuid: {uuid}")
    upload_file = f"/{output_directory}/{uuid}.png"

    s3 = boto3.resource('s3',
                    endpoint_url=S3_ENDPOINT,
                    aws_access_key_id=S3_ACCESS_KEY,
                    aws_secret_access_key=S3_SECRET_KEY,
                    config=Config(signature_version='s3v4'),
                    region_name='us-east-1')

    s3.Bucket(S3_BUCKET).upload_file(upload_file, upload_file)
    print(f"image uploaded to S3 for uuid: {uuid}")

def process_message(channel, method, properties, body):
    # Parse the JSON message
    data = json.loads(body)
    uuid = data.get('uuid')
    prompt = data.get('prompt')

    generate_image(prompt, uuid)
    save_image_to_s3(uuid)

    # Acknowledge the message processing
    channel.basic_ack(delivery_tag=method.delivery_tag)

def main():
    connection = pika.BlockingConnection(connection_params)
    channel = connection.channel()

    channel.queue_declare(queue=QUEUE_NAME, durable=True)

    # Start consuming messages
    channel.basic_consume(queue=QUEUE_NAME, on_message_callback=process_message, auto_ack=False)
    channel.start_consuming()

if __name__ == '__main__':
    main()




