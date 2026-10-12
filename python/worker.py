"""Stateless HTTP scheduler (ADR-009), deployed with Python for Annex J."""
import json
import logging
import os
import threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import urlsplit
from urllib.request import Request, urlopen


class HttpJob:
    def __init__(self, endpoint, opener=urlopen):
        parsed = urlsplit(endpoint)
        if parsed.scheme not in ("http", "https") or not parsed.hostname or parsed.username or parsed.password:
            raise ValueError("An absolute HTTP endpoint without embedded credentials is required")
        self.endpoint = endpoint
        self.opener = opener

    def execute(self):
        try:
            with self.opener(Request(self.endpoint, method="POST"), timeout=10) as response:
                return 200 <= response.status < 300
        except (OSError, ValueError):
            logging.warning("Domain job unavailable; retry on next schedule")
            return False


def schedule(job, interval, stopped):
    while not stopped.wait(interval):
        job.execute()


class HealthHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != "/api/v1/notifications/health":
            self.send_error(404)
            return
        data = json.dumps({"status": "UP", "service": "educk-worker", "runtime": "python"}).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def log_message(self, *_):
        pass


def main():
    stopped = threading.Event()
    if os.getenv("ATTENDANCE_TIMEOUT_ENABLED", "false").lower() == "true":
        job = HttpJob(os.environ["ATTENDANCE_TIMEOUT_URL"])
        interval = float(os.getenv("WORKER_INTERVAL_SECONDS", "1800"))
        if not 1 <= interval <= 86400:
            raise ValueError("Worker interval must be between 1 and 86400 seconds")
        threading.Thread(target=schedule, args=(job, interval, stopped), daemon=True).start()
    server = ThreadingHTTPServer(("0.0.0.0", int(os.getenv("PORT", "8084"))), HealthHandler)
    try:
        server.serve_forever()
    finally:
        stopped.set()
        server.server_close()


if __name__ == "__main__":
    main()
