FROM python:3.11-slim

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
  build-essential \
  ca-certificates \
  ccache \
  curl \
  libssl-dev ca-certificates make \
  git python3-pip && \
  rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY generation_service.py /app
COPY generation_service_animagine.py /app
COPY requirements-cpuonly.txt /app
COPY requirements-withcuda.txt /app

# install only CPU version of PyTorch
# source: https://stackoverflow.com/a/75988106
RUN pip install -r /app/requirements-cpuonly.txt --extra-index-url https://download.pytorch.org/whl/cpu
# use this if cuda is needed
# RUN pip install -r /app/requirements-withcuda.txt

EXPOSE 8000

ENV NAME GenerationService

CMD ["python", "/app/generation_service.py"]
