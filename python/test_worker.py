import threading
import unittest
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.request import urlopen
from worker import HealthHandler, HttpJob


class WorkerTest(unittest.TestCase):
    def test_health_starts_without_database(self):
        server = ThreadingHTTPServer(("127.0.0.1", 0), HealthHandler)
        thread = threading.Thread(target=server.serve_forever, daemon=True)
        thread.start()
        try:
            with urlopen(f"http://127.0.0.1:{server.server_port}/api/v1/notifications/health") as response:
                self.assertEqual(200, response.status)
                self.assertIn(b'"runtime": "python"', response.read())
        finally:
            server.shutdown()
            server.server_close()
            thread.join()

    def test_retries_after_domain_http_failure(self):
        class Handler(BaseHTTPRequestHandler):
            calls = 0
            def do_POST(self):
                Handler.calls += 1
                self.send_response(503 if Handler.calls == 1 else 204)
                self.end_headers()
            def log_message(self, *_):
                pass
        server = ThreadingHTTPServer(("127.0.0.1", 0), Handler)
        thread = threading.Thread(target=server.serve_forever, daemon=True)
        thread.start()
        try:
            job = HttpJob(f"http://127.0.0.1:{server.server_port}/job")
            self.assertFalse(job.execute())
            self.assertTrue(job.execute())
            self.assertEqual(2, Handler.calls)
        finally:
            server.shutdown()
            server.server_close()
            thread.join()

    def test_rejects_invalid_configuration(self):
        for endpoint in ("", "file:///tmp/data", "relative/path", "http://user:password@localhost"):
            with self.subTest(endpoint=endpoint), self.assertRaises(ValueError):
                HttpJob(endpoint)


if __name__ == "__main__":
    unittest.main()
