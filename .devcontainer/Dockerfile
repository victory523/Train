FROM mcr.microsoft.com/vscode/devcontainers/base:bullseye

ARG USERNAME=vscode
ARG USER_HOME=/home/${USERNAME}
ARG WORKSPACE_HOME=/workspaces/training-log-pro

RUN apt-get update && apt-get -y install lsb-release

# Download the Eclipse Adoptium GPG key:
RUN mkdir -p /etc/apt/keyrings
RUN wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | tee /etc/apt/keyrings/adoptium.asc

# Configure the Eclipse Adoptium apt repository.
RUN echo \
  "deb [signed-by=/etc/apt/keyrings/adoptium.asc] https://packages.adoptium.net/artifactory/deb \
   $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list

# Add Docker’s official GPG key
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Configure the Docker’s official apt repository.
RUN echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
  $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

# Configure NodeSource
RUN curl -fsSL -o get_node.sh https://deb.nodesource.com/setup_18.x \
    && chmod 700 get_node.sh \
    && ./get_node.sh

# Configure the PostgreSQL apt repository.
RUN curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc | gpg --dearmor -o /etc/apt/trusted.gpg.d/apt.postgresql.org.gpg
RUN echo \
  "deb [signed-by=/etc/apt/trusted.gpg.d/apt.postgresql.org.gpg] http://apt.postgresql.org/pub/repos/apt \
  $(lsb_release -cs)-pgdg main" | tee /etc/apt/sources.list.d/pgdg.list > /dev/null

RUN apt-get update && apt-get -y install --no-install-recommends \
    sshpass \
    chromium \
    chromium-sandbox \
    chromium-driver \
    temurin-17-jdk \
    maven \
    docker-ce-cli \
    docker-compose-plugin \
    nodejs \
    python3 \
    python3-pip \
    postgresql-client-15

# Apt cleanup
RUN apt-get autoremove -y; \
    apt-get clean -y; \
    rm -rf /var/lib/apt/lists/*

# Install python setuptools
RUN python3 -m pip install --upgrade setuptools

# [Optional] Allow the vscode user to pip install globally w/o sudo
ENV PIP_TARGET=/usr/local/pip-global
ENV PYTHONPATH=${PIP_TARGET}:${PYTHONPATH}
ENV PATH=${PIP_TARGET}/bin:${PATH}
RUN if ! cat /etc/group | grep -e "^pip-global:" > /dev/null 2>&1; then groupadd -r pip-global; fi \
    && usermod -a -G pip-global vscode \
    && umask 0002 && mkdir -p ${PIP_TARGET} \
    && chown :pip-global ${PIP_TARGET} \
    && ( [ ! -f "/etc/profile.d/00-restore-env.sh" ] || sed -i -e "s/export PATH=/export PATH=\/usr\/local\/pip-global:/" /etc/profile.d/00-restore-env.sh )

# Install kubectl
RUN curl -sSL -o /usr/local/bin/kubectl https://dl.k8s.io/release/v1.25.3/bin/linux/amd64/kubectl \
    && chmod 0755 /usr/local/bin/kubectl

# Install Helm
RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 \
    && chmod 700 get_helm.sh \
    && ./get_helm.sh --version v3.11.1

ENV ANSIBLE_CONFIG=${WORKSPACE_HOME}/ansible.cfg
ENV KUBECONFIG=${WORKSPACE_HOME}/.kube/config
ENV CHROME_BIN=/usr/bin/chromium
COPY .bashrc ${USER_HOME}

# Install Angular CLI
RUN npm install -g @angular/cli yarn
