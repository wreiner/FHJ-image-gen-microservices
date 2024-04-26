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
COPY classification_service.py /app
COPY requirements-cpuonly.txt /app

# install only CPU version of PyTorch
# source: https://stackoverflow.com/a/75988106
RUN pip install -r /app/requirements-cpuonly.txt --extra-index-url https://download.pytorch.org/whl/cpu
# use this if cuda is needed
# RUN pip install -r /app/requirements-withcuda.txt

EXPOSE 8000

ENV NAME ClassificationService
ENV GUNICORN_CMD_ARGS="--access-logfile - --error-logfile - --log-level info -w 2 -b 0.0.0.0:8000"

CMD ["gunicorn", "classification_service:app"]
