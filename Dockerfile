# Dockerfile for building NSO Client 217 (J2ME Project)
FROM eclipse-temurin:8-jdk

# Install Apache Ant
RUN apt-get update && \
    apt-get install -y ant wget && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Expose build directory as volume
VOLUME ["/app/dist"]

# Default command: build JAR using simplified build file (skip clean to avoid permission issues)
CMD ["ant", "-f", "build-docker.xml", "jar"]
