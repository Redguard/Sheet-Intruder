# Use an official Python runtime as a parent image
FROM python:3.11-alpine

# Set the working directory in the container
WORKDIR /app

# Copy only the necessary files into the container
COPY server.py requirements.txt /app/

# Create a non-root user to run the application
RUN addgroup -S appuser && adduser -S appuser -G appuser && \
    chown -R appuser:appuser /app

# Switch to the non-root user
USER appuser

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 5000

# Run the command to start the Flask application
CMD ["python3", "server.py"]
