import os
import json
import torch

import boto3
from botocore.client import Config
from diffusers import StableDiffusionXLPipeline, UNet2DConditionModel, EulerDiscreteScheduler
from huggingface_hub import hf_hub_download
from safetensors.torch import load_file
import pika

# RabbitMQ connection details
RABBITMQ_HOST = os.environ.get('RABBITMQ_HOST', 'localhost')
RABBITMQ_PORT = os.environ.get('RABBITMQ_PORT', 5672)
RABBITMQ_USER = os.environ.get('RABBITMQ_USER', 'guest')
RABBITMQ_PASS = os.environ.get('RABBITMQ_PASS', 'guest')
REQUEST_QUEUE_NAME = os.environ.get('QUEUE_NAME', 'generation_request')
RESPONSE_QUEUE_NAME = os.environ.get('QUEUE_NAME', 'generation_response')

# S3 connection details
S3_ENDPOINT = os.environ.get('S3_ENDPOINT', 's3.openshift-storage.svc')
S3_ACCESS_KEY = os.environ.get('S3_ACCESS_KEY', None)
if S3_ACCESS_KEY is None:
    print("S3_ACCESS_KEY is not set")
    sys.exit(1)

S3_SECRET_KEY = os.environ.get('S3_SECRET', None)
if S3_SECRET_KEY is None:
    print("S3_SECRET_KEY is not set")
    sys.exit(1)

S3_BUCKET = os.environ.get('S3_BUCKET', None)
if S3_BUCKET is None:
    print("S3_BUCKET is not set")
    sys.exit(1)

if os.environ.get('AWS_CA_BUNDLE') is None:
    print("setting AWS_CA_BUNDLE.. ")
    os.environ['AWS_CA_BUNDLE'] = '/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt'


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

base = "stabilityai/stable-diffusion-xl-base-1.0"
repo = "ByteDance/SDXL-Lightning"
ckpt = "sdxl_lightning_2step_unet.safetensors"  # Use the correct ckpt for your step setting!


def generate_image(prompt, uuid):
    print(f"generating image for uuid: {uuid} with prompt: {prompt}")

    unet = UNet2DConditionModel.from_config(
        base,
        subfolder="unet",
        cache_dir=cache_directory  # Specify custom cache directory here
    ).to("cpu")

    unet.load_state_dict(load_file(
        hf_hub_download(repo, ckpt, cache_dir=cache_directory),  # Specify custom cache directory for checkpoint
        device="cpu"
    ))

    pipe = StableDiffusionXLPipeline.from_pretrained(
        base,
        unet=unet,
        torch_dtype=torch.float32,
        cache_dir=cache_directory  # Specify custom cache directory here
    ).to("cpu")

    # Ensure sampler uses "trailing" timesteps.
    pipe.scheduler = EulerDiscreteScheduler.from_config(
        pipe.scheduler.config,
        timestep_spacing="trailing",
        cache_dir=cache_directory  # Specify custom cache directory here
    )

    # Ensure using the same inference steps as the loaded model and CFG set to 0.
    image = pipe(prompt, num_inference_steps=4, guidance_scale=0).images[0]
    image.save(f"/{output_directory}/{uuid}.png")
    print(f"image temporarily saved for uuid: {uuid}")

def save_image_to_s3(uuid):
    print(f"uploading image to S3 for uuid: {uuid}")
    upload_file = f"/{output_directory}/{uuid}.png"
    remote_filename = f"{uuid}.png"

    s3 = boto3.resource('s3',
                        endpoint_url=S3_ENDPOINT,
                        aws_access_key_id=S3_ACCESS_KEY,
                        aws_secret_access_key=S3_SECRET_KEY,
                        config=Config(signature_version='s3v4'),
                        region_name='us-east-1')

    s3.Bucket(S3_BUCKET).upload_file(upload_file, remote_filename)
    print(f"image uploaded to S3 for uuid: {uuid}")

def remove_temporary_image(uuid):
    os.remove(f"/{output_directory}/{uuid}.png")

def send_generation_response(uuid):
    print(f"sending response for uuid: {uuid}")

    connection = pika.BlockingConnection(connection_params)
    channel = connection.channel()

    # Declare the queue (create if it doesn't exist)
    channel.queue_declare(queue=RESPONSE_QUEUE_NAME, durable=True)

    # Prepare your JSON data as a string
    data = {'uuid': uuid, 'status': 'GENERATED'}
    json_data = json.dumps(data)

    # Publish the JSON string to the queue
    channel.basic_publish(exchange='',
                          routing_key=RESPONSE_QUEUE_NAME,
                          body=json_data,
                          properties=pika.BasicProperties(
                              content_type='text/plain',
                              delivery_mode=2,  # make message persistent
                          ))

    print(f"Response sent for uuid: {uuid}")

def process_message(channel, method, properties, body):
    print(f"received message {body}, parsing ..")

    # Acknowledge the message processing
    channel.basic_ack(delivery_tag=method.delivery_tag)

    # Parse the JSON message
    data = json.loads(body)
    uuid = data.get('uuid')
    prompt = data.get('prompt')

    print(f"parsed uuid: {uuid} with prompt: {prompt}")

    generate_image(prompt, uuid)
    save_image_to_s3(uuid)
    remove_temporary_image(uuid)
    send_generation_response(uuid)


def main():
    connection = pika.BlockingConnection(connection_params)
    channel = connection.channel()

    channel.queue_declare(queue=REQUEST_QUEUE_NAME, durable=True)

    # Start consuming messages
    channel.basic_consume(queue=REQUEST_QUEUE_NAME, on_message_callback=process_message, auto_ack=False)
    channel.start_consuming()

if __name__ == '__main__':
    main()
